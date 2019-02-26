package http;

import java.util.Arrays;
import java.util.List;


public class HttpResponse {
    private final HttpStatus status;
    private final byte[] body;
    private final HttpHeaders headers;


    public HttpResponse(HttpStatus status, byte[] body, HttpHeaders headers) {
        this.status = status;
        this.body = body;
        this.headers = headers;
    }
    
    public final HttpStatus getStatus() {
        return status;
    }

    public final byte[] getBody() {
        return body;
    }

    public final byte[] getBody(byte[] def) {
        return body == null ? def : body;
    }
    
    public final HttpHeaders getHeaders() {
        return headers;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("HttpResponse[");
        buf.append("code = ").append(status.getCode());
        buf.append(", reasonPhrase = ").append(status.getReasonPhrase());
        buf.append(", headers = ").append(headers);
        buf.append("]");
        return buf.toString();
    }
    
    public static class HttpResponseBuilder {
        private HttpStatus status;
        private byte[] body;
        private HttpHeaders headers = new HttpHeaders();

        public HttpResponse build() {
            return new HttpResponse(status, body, headers);
        }

        public HttpResponseBuilder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponseBuilder status(HttpStatus status) {
            this.status = status;
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
