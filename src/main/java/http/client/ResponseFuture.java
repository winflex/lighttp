package http.client;

import java.io.IOException;
import java.util.Set;

import http.HttpRequest;
import http.util.ConcurrentHashSet;
import http.util.future.DefaultPromise;
import http.util.future.IPromise;
import io.netty.channel.Channel;

final class ResponseFuture extends DefaultPromise {

    static final long serialVersionUID = 6167454397202234154L;

    private final long DEADLINE;
    private final HttpRequest request;
    private volatile Channel channel;

    private volatile boolean requestSent; // 请求是否已发送

    ResponseFuture(HttpRequest request, int timeout) {
        this.request = request;
        this.DEADLINE = System.currentTimeMillis() + timeout;
        watchedFutures.add(this);
    }
    
    void setChannel(Channel channel) {
        this.channel = channel;
    }
    
    Channel getChannel() {
        return this.channel;
    }
    
    void requestSent() {
        this.requestSent = true;
    }

    boolean isRequestSent() {
        return this.requestSent;
    }

    HttpRequest getRequest() {
        return request;
    }

    @Override
    public IPromise setSuccess(Object result) {
        watchedFutures.remove(this);
        return super.setSuccess(result);
    }

    @Override
    public IPromise setFailure(Throwable cause) {
        watchedFutures.remove(this);
        return super.setFailure(cause);
    }

    boolean isTimedout() {
        return System.currentTimeMillis() > DEADLINE;
    }

    static final Set<ResponseFuture> watchedFutures = new ConcurrentHashSet<ResponseFuture>();
    static {
        Thread watchdogThread = new Thread(new Watchdog());
        watchdogThread.setName("ATS-Monitor-HttpResponseFuture-Watchdog");
        watchdogThread.setDaemon(true);
        watchdogThread.start();
    }

    private static final class Watchdog implements Runnable {

        @Override
        public void run() {
            while (true) {
                for (ResponseFuture future : watchedFutures) {
                    if (future.isTimedout()) {
                        String whichSide = future.isRequestSent() ? "server" : "client";
                        // assert it's an IO exception
                        Throwable cause = new IOException("Request timed out at "
                                + whichSide + " side");
                        future.setFailure(cause);
                        watchedFutures.remove(future);
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }
}