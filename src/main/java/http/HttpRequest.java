package http;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class HttpRequest {
    private final URI uri;
    private final HttpMethod method;
    private final byte[] body;
    private final HttpHeaders headers;

    public HttpRequest(URI uri, HttpMethod method, byte[] body, HttpHeaders headers) {
        this.uri = uri;
        this.method = method;
        this.body = body;
        this.headers = headers;
    }

    public URI getUri() {
        return uri;
    }

    public final HttpMethod getMethod() {
        return method;
    }

    public final byte[] getBody() {
        return body;
    }

    public final HttpHeaders getHeaders() {
        return headers;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("HttpRequest[");
        buf.append("method = ").append(method);
        buf.append(", uri = ").append(uri);
        buf.append(", headers = ").append(headers);
        buf.append("]");
        return buf.toString();
    }

    public static class HttpRequestBuilder {
        private URI uri;
        private HttpMethod method = HttpMethod.GET;
        private byte[] body;
        private HttpHeaders headers = new HttpHeaders();

        public HttpRequest build() {
            return new HttpRequest(uri, method, body, headers);
        }

        public HttpRequestBuilder uri(URI uri) {
            this.uri = uri;
            return this;
        }
        
        public HttpRequestBuilder uri(String uri) {
            this.uri = URI.create(uri);
            return this;
        }

        public HttpRequestBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder body(byte[] body) {
            this.body = body;
            return this;
        }

        public void setHeader(String name, String value) {
            this.headers.set(name, value);
        }

        public void addHeader(String name, String value) {
            this.headers.add(name, value);
        }

        public void addHeader(String name, String... values) {
            this.headers.add(name, Arrays.asList(values));
        }

        public void addHeader(String name, List<String> values) {
            this.headers.add(name, values);
        }
    }
}
