/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 * 
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package http.client;

/**
 *
 *
 * @author lixiaohui
 * @date 2018年11月15日 上午11:41:03
 * @version 1.0
 *
 */
public class AsyncHttpClientConfig {
    
    /** 创建连接超时时间 */
    private int connectTimeout;
    
    /** http请求超时时间 */
    private int requestTimeout;
    
    /** IO线程数 */
    private int ioThreads;
    
    /** {@link AsyncHttpClient}会根据IP和Port缓存连接, 该参数指定一个IP+Port对应的最大连接数*/
    private int maxConnectionsEachServer;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getIoThreads() {
        return ioThreads;
    }

    public void setIoThreads(int ioThreads) {
        this.ioThreads = ioThreads;
    }

    public int getMaxConnectionsEachServer() {
        return maxConnectionsEachServer;
    }

    public void setMaxConnectionsEachServer(int maxConnectionsEachServer) {
        this.maxConnectionsEachServer = maxConnectionsEachServer;
    }
}
