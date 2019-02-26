package http.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;

/**
 * 连接池
 *
 * @author lixiaohui
 * @date 2018年11月15日 上午11:07:17
 * @version 1.0
 *
 */
public class ChannelPool {

	private static final Logger logger = LoggerFactory.getLogger(ChannelPool.class);

	/** key(IP+Port) -> ChannelPool */
	private final ConcurrentHashMap<Server, FixedChannelPool> server2Pools = new ConcurrentHashMap<>();

	private final Bootstrap bootstrap;

	private final int maxConnectionsEachServer;

	public ChannelPool(Bootstrap bootstrap, int maxConnectionsEachServer) {
		this.bootstrap = bootstrap;
		this.maxConnectionsEachServer = maxConnectionsEachServer;
	}

	/**
	 * 获取指定ip和端口的连接
	 * 
	 * @param ip   ip地址
	 * @param port 端口
	 */
	public Channel acquire(String ip, int port) throws IOException {
		final Server server = Server.of(ip, port);
		FixedChannelPool pool = server2Pools.get(server);
		if (pool == null) {
			Bootstrap b = this.bootstrap.clone();
			b.remoteAddress(ip, port);
			FixedChannelPool newPool = new FixedChannelPool(b, LOGGING_CHANNEL_POOL_HANDLER, maxConnectionsEachServer);
			FixedChannelPool oldPool = server2Pools.putIfAbsent(server, newPool);
			pool = oldPool == null ? newPool : oldPool;
		}
		Future<Channel> future = pool.acquire();
		future.awaitUninterruptibly();
		if (future.isSuccess()) {
			return future.getNow();
		} else {
			Throwable e = future.cause();
			if (e instanceof IOException) {
				throw (IOException) e;
			} else {
				throw new IOException(e);
			}
		}
	}

	/**
	 * <pre>
	 * 归还Channel
	 * 
	 * 归还动作是异步的, 方法返回后连接并不一定立马能被其它线程获取到
	 * </pre>
	 *
	 * @param channel 要归还的连接
	 */
	public void release(Channel channel) {
		final Server server = Server.of(channel);
		FixedChannelPool pool = server2Pools.get(server);
		if (pool == null) {
			throw new RuntimeException("Channel " + channel + " is not belong to this pool");
		}
		pool.release(channel); // 归还是异步的
	}

	public void close() {
		for (FixedChannelPool pool : server2Pools.values()) {
			pool.close();
		}
		server2Pools.clear();
	}

	/**
	 * 以ip和端口来区别Channel
	 */
	static class Server {

		static Server of(String ip, int port) {
			return new Server(ip, port);
		}

		static Server of(Channel channel) {
			InetSocketAddress addr = (InetSocketAddress) channel.remoteAddress();
			return new Server(addr.getAddress().getHostAddress(), addr.getPort());
		}

		final String ip; // 这是IP地址, 不是域名或主机名
		final int port;

		private Server(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ip == null) ? 0 : ip.hashCode());
			result = prime * result + port;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Server other = (Server) obj;
			if (ip == null) {
				if (other.ip != null)
					return false;
			} else if (!ip.equals(other.ip))
				return false;
			if (port != other.port)
				return false;
			return true;
		}
	}

	private final ChannelPoolHandler LOGGING_CHANNEL_POOL_HANDLER = new ChannelPoolHandler() {

		@Override
		public void channelAcquired(Channel ch) throws Exception {
			logger.debug("Channel acquired {}", ch);
		}

		@Override
		public void channelReleased(Channel ch) throws Exception {
			logger.debug("Channel released {}", ch);
		}

		@Override
		public void channelCreated(Channel ch) throws Exception {
			// 由于Netty的SimpleChannelPool会重新设置bootstrap.handler(), 因此我们需要给channel初始化pipeline
			BootstrapConfig config = bootstrap.config();
			config.handler().handlerAdded(ch.pipeline().firstContext());
		}
	};
}
