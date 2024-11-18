package com.nk.base.constants;

public class ErrorCodeCommon {
    private ErrorCodeCommon() {throw new com.nk.base.exception.IllegalStateException("Utility class");}

    // Success codes (2xx)
    public static final String OK = "OK";
    public static final String CREATED = "CREATED";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String NO_CONTENT = "NO_CONTENT";

    // Client error codes (4xx)
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
    public static final String CONFLICT = "CONFLICT";
    public static final String UNPROCESSABLE_ENTITY = "UNPROCESSABLE_ENTITY";

    // Server error codes (5xx)
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
    public static final String BAD_GATEWAY = "BAD_GATEWAY";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String GATEWAY_TIMEOUT = "GATEWAY_TIMEOUT";
    public static final String VERSION_NOT_SUPPORTED = "VERSION_NOT_SUPPORTED";

    // Other error codes
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
}
