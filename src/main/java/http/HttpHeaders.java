package http;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import http.util.LinkedMultiValueMap;
import http.util.MultiValueMap;

public final class HttpHeaders implements Iterable<Entry<String, List<String>>>{
    
    private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    
    public void set(String name, String value) {
        headers.set(name, value);
    }
    
    public void add(String name, String value){
        headers.add(name, value);
    }
    
    public void add(String name, List<String> values) {
        headers.addAll(name, values);
    }
    
    public List<String> get(String name) {
        return headers.get(name);
    }
    
    public Set<Entry<String, List<String>>> entrySet() {
        return headers.entrySet();
    }
    
    @Override
    public Iterator<Entry<String, List<String>>> iterator() {
        return headers.entrySet().iterator();
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        boolean first = true;
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(entry.getKey()).append("=");
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(entry.getValue().get(i));
            }
        }
        buf.append("]");
        return buf.toString();
    }
    
    public static final class HttpHeaderNames {
        
        public static final String ACCEPT = "accept";
        
        /**
         * Accept-Charset: iso-8859-1
         * Accept-Charset: utf-8, iso-8859-1;q=0.5
         * Accept-Charset: utf-8, iso-8859-1;q=0.5, *;q=0.1
         */
        public static final String ACCEPT_CHARSET = "accept-charset";
        
        public static final String ACCEPT_ENCODING = "accept-encoding";
        
        public static final String ACCEPT_LANGUAGE = "accept-language";
        
        public static final String ACCEPT_RANGES = "accept-ranges";
        
        public static final String ACCEPT_PATCH = "accept-patch";
        
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS =
                "access-control-allow-credentials";
        
        public static final String ACCESS_CONTROL_ALLOW_HEADERS =
                "access-control-allow-headers";
        
        public static final String ACCESS_CONTROL_ALLOW_METHODS =
                "access-control-allow-methods";
        
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN =
                "access-control-allow-origin";
        
        public static final String ACCESS_CONTROL_EXPOSE_HEADERS =
                "access-control-expose-headers";
        
        public static final String ACCESS_CONTROL_MAX_AGE = "access-control-max-age";
        
        public static final String ACCESS_CONTROL_REQUEST_HEADERS =
                "access-control-request-headers";
        
        public static final String ACCESS_CONTROL_REQUEST_METHOD =
                "access-control-request-method";
        
        public static final String AGE = "age";
        
        public static final String ALLOW = "allow";
        
        public static final String AUTHORIZATION = "authorization";
        
        public static final String CACHE_CONTROL = "cache-control";
        
        public static final String CONNECTION = "connection";
        
        public static final String CONTENT_BASE = "content-base";
        
        public static final String CONTENT_ENCODING = "content-encoding";
        
        public static final String CONTENT_LANGUAGE = "content-language";
        
        public static final String CONTENT_LENGTH = "content-length";
        
        public static final String CONTENT_LOCATION = "content-location";
        
        public static final String CONTENT_TRANSFER_ENCODING = "content-transfer-encoding";
        
        public static final String CONTENT_DISPOSITION = "content-disposition";
        
        public static final String CONTENT_MD5 = "content-md5";
        
        public static final String CONTENT_RANGE = "content-range";
        
        public static final String CONTENT_TYPE = "content-type";
        
        public static final String COOKIE = "cookie";
        
        public static final String DATE = "date";
        
        public static final String ETAG = "etag";
        
        public static final String EXPECT = "expect";
        
        public static final String EXPIRES = "expires";
        
        public static final String FROM = "from";
        
        public static final String HOST = "host";
        
        public static final String IF_MATCH = "if-match";
        
        public static final String IF_MODIFIED_SINCE = "if-modified-since";
        
        public static final String IF_NONE_MATCH = "if-none-match";
        
        public static final String IF_RANGE = "if-range";
        
        public static final String IF_UNMODIFIED_SINCE = "if-unmodified-since";
        
        @Deprecated
        public static final String KEEP_ALIVE = "keep-alive";
        
        public static final String LAST_MODIFIED = "last-modified";
        
        public static final String LOCATION = "location";
        
        public static final String MAX_FORWARDS = "max-forwards";
        
        public static final String ORIGIN = "origin";
        
        public static final String PRAGMA = "pragma";
        
        public static final String PROXY_AUTHENTICATE = "proxy-authenticate";
        
        public static final String PROXY_AUTHORIZATION = "proxy-authorization";
        
        @Deprecated
        public static final String PROXY_CONNECTION = "proxy-connection";
        
        public static final String RANGE = "range";
        
        public static final String REFERER = "referer";
        
        public static final String RETRY_AFTER = "retry-after";
        
        public static final String SEC_WEBSOCKET_KEY1 = "sec-websocket-key1";
        
        public static final String SEC_WEBSOCKET_KEY2 = "sec-websocket-key2";
        
        public static final String SEC_WEBSOCKET_LOCATION = "sec-websocket-location";
        
        public static final String SEC_WEBSOCKET_ORIGIN = "sec-websocket-origin";
        
        public static final String SEC_WEBSOCKET_PROTOCOL = "sec-websocket-protocol";
        
        public static final String SEC_WEBSOCKET_VERSION = "sec-websocket-version";
        
        public static final String SEC_WEBSOCKET_KEY = "sec-websocket-key";
        
        public static final String SEC_WEBSOCKET_ACCEPT = "sec-websocket-accept";
        
        public static final String SEC_WEBSOCKET_EXTENSIONS = "sec-websocket-extensions";
        
        public static final String SERVER = "server";
        
        public static final String SET_COOKIE = "set-cookie";
        
        public static final String SET_COOKIE2 = "set-cookie2";
        
        public static final String TE = "te";
        
        public static final String TRAILER = "trailer";
        
        public static final String TRANSFER_ENCODING = "transfer-encoding";
        
        public static final String UPGRADE = "upgrade";
        
        public static final String USER_AGENT = "user-agent";
        
        public static final String VARY = "vary";
        
        public static final String VIA = "via";
        
        public static final String WARNING = "warning";
        
        public static final String WEBSOCKET_LOCATION = "websocket-location";
        
        public static final String WEBSOCKET_ORIGIN = "websocket-origin";
        
        public static final String WEBSOCKET_PROTOCOL = "websocket-protocol";
        
        public static final String WWW_AUTHENTICATE = "www-authenticate";

        private HttpHeaderNames() { }
    }
    
    public static final class HttpHeaderValues {
        
        public static final String APPLICATION_JSON = "application/json";
        
        public static final String APPLICATION_X_WWW_FORM_URLENCODED =
                "application/x-www-form-urlencoded";
        
        public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
        
        public static final String ATTACHMENT = "attachment";
        
        public static final String BASE64 = "base64";
        
        public static final String BINARY = "binary";
        
        public static final String BOUNDARY = "boundary";
        
        public static final String BYTES = "bytes";
        
        public static final String CHARSET = "charset";
        
        public static final String CHUNKED = "chunked";
        
        public static final String CLOSE = "close";
        
        public static final String COMPRESS = "compress";
        
        public static final String CONTINUE = "100-continue";
        
        public static final String DEFLATE = "deflate";
        
        public static final String X_DEFLATE = "x-deflate";
        
        public static final String FILE = "file";
        
        public static final String FILENAME = "filename";
        
        public static final String FORM_DATA = "form-data";
        
        public static final String GZIP = "gzip";
        
        public static final String GZIP_DEFLATE = "gzip,deflate";
        
        public static final String X_GZIP = "x-gzip";
        
        public static final String IDENTITY = "identity";
        
        public static final String KEEP_ALIVE = "keep-alive";
        
        public static final String MAX_AGE = "max-age";
        
        public static final String MAX_STALE = "max-stale";
        
        public static final String MIN_FRESH = "min-fresh";
        
        public static final String MULTIPART_FORM_DATA = "multipart/form-data";
        
        public static final String MULTIPART_MIXED = "multipart/mixed";
        
        public static final String MUST_REVALIDATE = "must-revalidate";
        
        public static final String NAME = "name";
        
        public static final String NO_CACHE = "no-cache";
        
        public static final String NO_STORE = "no-store";
        
        public static final String NO_TRANSFORM = "no-transform";
        
        public static final String NONE = "none";
        
        public static final String ZERO = "0";
        
        public static final String ONLY_IF_CACHED = "only-if-cached";
        
        public static final String PRIVATE = "private";
        
        public static final String PROXY_REVALIDATE = "proxy-revalidate";
        
        public static final String PUBLIC = "public";
        
        public static final String QUOTED_PRINTABLE = "quoted-printable";
        
        public static final String S_MAXAGE = "s-maxage";
        
        public static final String TEXT_PLAIN = "text/plain";
        
        public static final String TRAILERS = "trailers";
        
        public static final String UPGRADE = "upgrade";
        
        public static final String WEBSOCKET = "websocket";

        public static final String applicationJsonWithCharset(String charset) {
            return APPLICATION_JSON + "; charset=" + charset;
        }
        private HttpHeaderValues() { }
    }
}
