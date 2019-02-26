package http;

public class HttpStatus {
    
    public static final HttpStatus CONTINUE = newStatus(100, "Continue");

    public static final HttpStatus SWITCHING_PROTOCOLS = newStatus(101, "Switching Protocols");
    
    public static final HttpStatus PROCESSING = newStatus(102, "Processing");

    public static final HttpStatus OK = newStatus(200, "OK");

    public static final HttpStatus CREATED = newStatus(201, "Created");
    
    public static final HttpStatus ACCEPTED = newStatus(202, "Accepted");

    public static final HttpStatus NON_AUTHORITATIVE_INFORMATION =
            newStatus(203, "Non-Authoritative Information");

    public static final HttpStatus NO_CONTENT = newStatus(204, "No Content");
    
    public static final HttpStatus RESET_CONTENT = newStatus(205, "Reset Content");

    public static final HttpStatus PARTIAL_CONTENT = newStatus(206, "Partial Content");

    public static final HttpStatus MULTI_STATUS = newStatus(207, "Multi-Status");

    public static final HttpStatus MULTIPLE_CHOICES = newStatus(300, "Multiple Choices");

    public static final HttpStatus MOVED_PERMANENTLY = newStatus(301, "Moved Permanently");

    public static final HttpStatus FOUND = newStatus(302, "Found");
    
    public static final HttpStatus SEE_OTHER = newStatus(303, "See Other");

    public static final HttpStatus NOT_MODIFIED = newStatus(304, "Not Modified");

    public static final HttpStatus USE_PROXY = newStatus(305, "Use Proxy");
    
    public static final HttpStatus TEMPORARY_REDIRECT = newStatus(307, "Temporary Redirect");

    public static final HttpStatus PERMANENT_REDIRECT = newStatus(308, "Permanent Redirect");
    
    public static final HttpStatus BAD_REQUEST = newStatus(400, "Bad Request");

    public static final HttpStatus UNAUTHORIZED = newStatus(401, "Unauthorized");

    public static final HttpStatus PAYMENT_REQUIRED = newStatus(402, "Payment Required");
    
    public static final HttpStatus FORBIDDEN = newStatus(403, "Forbidden");

    public static final HttpStatus NOT_FOUND = newStatus(404, "Not Found");

    public static final HttpStatus METHOD_NOT_ALLOWED = newStatus(405, "Method Not Allowed");
    
    public static final HttpStatus NOT_ACCEPTABLE = newStatus(406, "Not Acceptable");

    public static final HttpStatus PROXY_AUTHENTICATION_REQUIRED =
            newStatus(407, "Proxy Authentication Required");

    public static final HttpStatus REQUEST_TIMEOUT = newStatus(408, "Request Timeout");
    
    public static final HttpStatus CONFLICT = newStatus(409, "Conflict");

    public static final HttpStatus GONE = newStatus(410, "Gone");
    
    public static final HttpStatus LENGTH_REQUIRED = newStatus(411, "Length Required");
    
    public static final HttpStatus PRECONDITION_FAILED = newStatus(412, "Precondition Failed");

    public static final HttpStatus REQUEST_ENTITY_TOO_LARGE =
            newStatus(413, "Request Entity Too Large");

    public static final HttpStatus REQUEST_URI_TOO_LONG = newStatus(414, "Request-URI Too Long");

    public static final HttpStatus UNSUPPORTED_MEDIA_TYPE = newStatus(415, "Unsupported Media Type");
    
    public static final HttpStatus REQUESTED_RANGE_NOT_SATISFIABLE =
            newStatus(416, "Requested Range Not Satisfiable");

    public static final HttpStatus EXPECTATION_FAILED = newStatus(417, "Expectation Failed");
    
    public static final HttpStatus MISDIRECTED_REQUEST = newStatus(421, "Misdirected Request");

    public static final HttpStatus UNPROCESSABLE_ENTITY = newStatus(422, "Unprocessable Entity");

    public static final HttpStatus LOCKED = newStatus(423, "Locked");

    public static final HttpStatus FAILED_DEPENDENCY = newStatus(424, "Failed Dependency");

    public static final HttpStatus UNORDERED_COLLECTION = newStatus(425, "Unordered Collection");

    public static final HttpStatus UPGRADE_REQUIRED = newStatus(426, "Upgrade Required");

    public static final HttpStatus PRECONDITION_REQUIRED = newStatus(428, "Precondition Required");

    public static final HttpStatus TOO_MANY_REQUESTS = newStatus(429, "Too Many Requests");
    
    public static final HttpStatus REQUEST_HEADER_FIELDS_TOO_LARGE =
            newStatus(431, "Request Header Fields Too Large");

    public static final HttpStatus INTERNAL_SERVER_ERROR = newStatus(500, "Internal Server Error");
    
    public static final HttpStatus NOT_IMPLEMENTED = newStatus(501, "Not Implemented");
    
    public static final HttpStatus BAD_GATEWAY = newStatus(502, "Bad Gateway");

    public static final HttpStatus SERVICE_UNAVAILABLE = newStatus(503, "Service Unavailable");
    
    public static final HttpStatus GATEWAY_TIMEOUT = newStatus(504, "Gateway Timeout");

    
    public static final HttpStatus HTTP_VERSION_NOT_SUPPORTED =
            newStatus(505, "HTTP Version Not Supported");

    public static final HttpStatus VARIANT_ALSO_NEGOTIATES = newStatus(506, "Variant Also Negotiates");

    public static final HttpStatus INSUFFICIENT_STORAGE = newStatus(507, "Insufficient Storage");

    public static final HttpStatus NOT_EXTENDED = newStatus(510, "Not Extended");

    public static final HttpStatus NETWORK_AUTHENTICATION_REQUIRED =
            newStatus(511, "Network Authentication Required");
    
    private static HttpStatus newStatus(int statusCode, String reasonPhrase) {
        return new HttpStatus(statusCode, reasonPhrase);
    }
    
    private final int code;
    private final String reasonPhrase;

    public HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }
    
    public final int getCode() {
        return code;
    }

    public final String getReasonPhrase() {
        return reasonPhrase;
    }

    public final boolean is1xxInformational() {
        return seriesCode() == 1;
    }

    public final boolean is2xxSuccessful() {
        return seriesCode() == 2;
    }

    public final boolean is3xxRedirection() {
        return seriesCode() == 3;
    }

    public final boolean is4xxClientError() {
        return seriesCode() == 4;
    }

    public final boolean is5xxServerError() {
        return seriesCode() == 5;
    }
    
    private int seriesCode() {
        return code / 100;
    }
    
    @Override
    public String toString() {
        return code + " " + reasonPhrase;
    }
}