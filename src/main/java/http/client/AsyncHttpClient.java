package http.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.HttpHeaders.HttpHeaderNames;
import http.HttpHeaders.HttpHeaderValues;
import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpResponse.HttpResponseBuilder;
import http.HttpStatus;
import http.util.ConcurrentHashSet;
import http.util.NamedThreadFactory;
import http.util.future.IFuture;
import http.util.future.IFutureListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;

/**
 * 异步HTTP客户端
 *
 * @author lixiaohui
 * @date 2018年11月15日 上午10:27:57
 * @version 1.0
 *
 */
public class AsyncHttpClient implements Closeable {

	private static final Logger logger = LoggerFactory.getLogger(AsyncHttpClient.class);

	private static final AttributeKey<ResponseFuture> ATTR_KEY_FUTURE = AttributeKey
			.newInstance("HTTP_RESPONSE_FUTURE");

	private final AsyncHttpClientConfig config;

	private final ChannelPool channelPool;
	private final EventLoopGroup eventLoopGroup; // 共享io线程池
	private final Bootstrap bootstrap;

	private final ConcurrentHashSet<ResponseFuture> sentFutures = new ConcurrentHashSet<>();

	private final AtomicBoolean closed = new AtomicBoolean();

	public AsyncHttpClient(AsyncHttpClientConfig config) {
		this.config = config;
		this.eventLoopGroup = new NioEventLoopGroup(config.getIoThreads(),
				new NamedThreadFactory("ATS-Monitor-Http-IOWorker"));
		this.bootstrap = createBootstrap(eventLoopGroup, config);
		this.channelPool = new ChannelPool(bootstrap, config.getMaxConnectionsEachServer());
	}

	private Bootstrap createBootstrap(EventLoopGroup bossGroup, AsyncHttpClientConfig config) {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(bossGroup);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {

			@Override
			protected void initChannel(NioSocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("HTTP_ENCODER", new HttpRequestEncoder());
				pipeline.addLast("HTTP_DECODER", new HttpResponseDecoder());
				pipeline.addLast("HTTP_AGGREGATOR", new HttpObjectAggregator(Integer.MAX_VALUE));
				pipeline.addLast("HTTP_PROTOCOL_ADAPTER", new ProtocolAdapter());
				pipeline.addLast("RESPONSE_HANDLER", new ResponseHandler());
			}
		});
		return bootstrap;
	}

	/**
	 * 同步发送HTTP请求
	 */
	public HttpResponse request(HttpRequest request) throws IOException {
		IFuture future = execute(request);
		future.awaitUninterruptibly();
		if (future.isSuccessful()) {
			return (HttpResponse) future.getNow();
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
	 * 异步发送HTTP请求
	 */
	public IFuture execute(HttpRequest request) {
		final ResponseFuture future = new ResponseFuture(request, config.getRequestTimeout());
		future.addListener(new IFutureListener() {

			@Override
			public void operationCompleted(IFuture f) throws Exception {
				sentFutures.remove(future);// 从sentFutures中移除
				Channel channel = future.getChannel();
				if (channel != null) { // 归还连接
					channelPool.release(channel);
				}
			}
		});
		sentFutures.add(future);

		Channel channel = null;
		try {
			channel = getChannel(request);
			future.setChannel(channel);
		} catch (IOException e) {
			future.setFailure(e);
			return future;
		}

		channel.attr(ATTR_KEY_FUTURE).set(future); // 绑定future
		channel.writeAndFlush(request).addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				if (f.isSuccess()) {
					future.requestSent();
				} else {
					future.setFailure(f.cause());
					f.channel().attr(ATTR_KEY_FUTURE).set(null);
				}
			}
		});
		return future;
	}

	private Channel getChannel(HttpRequest request) throws IOException {
		String host = request.getUri().getHost();
		int port = request.getUri().getPort();
		return channelPool.acquire(host, port);
	}

	@Override
	public void close() {
		if (!closed.compareAndSet(false, true)) {
			return;
		}

		if (sentFutures.size() > 0) {
			final Throwable e = new IOException("The async http client has been closed");
			for (ResponseFuture future : sentFutures) {
				future.setFailure(e);
			}
		}

		channelPool.close();
		eventLoopGroup.shutdownGracefully();
	}

	static final class ProtocolAdapter extends MessageToMessageCodec<FullHttpResponse, HttpRequest> {

		static final String USER_AGENT = "ATS Monitor Http Client";

		@Override
		protected void encode(ChannelHandlerContext ctx, HttpRequest request, List<Object> out) throws Exception {
			final DefaultFullHttpRequest nettyRequest;
			if (request.getBody() == null) {
				nettyRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, toNettyHttpMethod(request.getMethod()),
						request.getUri().getPath());
			} else {
				nettyRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, toNettyHttpMethod(request.getMethod()),
						request.getUri().getPath(), Unpooled.wrappedBuffer(request.getBody()));
			}

			// copy headers
			for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
				nettyRequest.headers().add(entry.getKey(), entry.getValue());
			}
			// 填充必要的头
			nettyRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			if (request.getMethod() != HttpMethod.GET && request.getBody() != null) {
				nettyRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.getBody().length);
			}
			if (!nettyRequest.headers().contains(HttpHeaderNames.HOST)) {
				String host = request.getUri().getHost() + ":" + request.getUri().getPort();
				nettyRequest.headers().add(HttpHeaderNames.HOST, host);
			}
			nettyRequest.headers().set(HttpHeaderNames.USER_AGENT, USER_AGENT);
			out.add(nettyRequest);
		}

		@Override
		protected void decode(ChannelHandlerContext ctx, FullHttpResponse nettyResponse, List<Object> out)
				throws Exception {
			HttpResponseBuilder builder = HttpResponse.builder();
			// copy status
			HttpStatus status = new HttpStatus(nettyResponse.status().code(), nettyResponse.status().reasonPhrase());
			builder.status(status);
			// copy body
			ByteBuffer data = ByteBuffer.allocate(nettyResponse.content().readableBytes());
			nettyResponse.content().readBytes(data);
			builder.body(data.array());
			for (Entry<String, String> entry : nettyResponse.headers().entries()) {
				builder.addHeader(entry.getKey(), entry.getValue());
			}
			out.add(builder.build());
		}

		private io.netty.handler.codec.http.HttpMethod toNettyHttpMethod(HttpMethod method) {
			return io.netty.handler.codec.http.HttpMethod.valueOf(method.name());
		}
	}

	static final class ResponseHandler extends SimpleChannelInboundHandler<HttpResponse> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, HttpResponse response) throws Exception {
			ResponseFuture future = ctx.channel().attr(ATTR_KEY_FUTURE).getAndSet(null);
			if (future == null || future.isDone()) {
				logger.warn("Recieved a response {}, but no correlated request future found or the future is done.",
						response);
				return;
			}
			future.setSuccess(response);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
			String host = addr.getAddress().getHostAddress();
			int port = addr.getPort();
			logger.info("disconnected with {}:{}, channelId = {}", host, port, ctx.channel().id());
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
			String host = addr.getAddress().getHostAddress();
			int port = addr.getPort();
			logger.info("connected to {}:{}, channelId = {}", host, port, ctx.channel().id());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			logger.error("Unexpected exception caught: {}", cause.getMessage(), cause);
		}
	}
}
