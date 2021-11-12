package com.jwtfulldefence.constant;

public class SecurityConstant {
    /*JWT Constants*/
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 432_000_000; // 5 days (432_000_000) in milliseconds - 1 hour (3600000) in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String COMPANY_ISSUER = "Company,Greece";
    public static final String COMPANY_ADMINISTRATION = "Company Administration";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to Login";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission , access denied";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";


    /*HTTP ONLY COOKIE Constants*/
    public static final String TOKEN_COOKIE_NAME = "accessToken";

    /* Allowed Urls Without JWT */
    public static final String[] PUBLIC_URLS = {"/", "/index.html", "/api/user/login", "/api/user/register", "/api/token/refresh"};
//    public static final String[] PUBLIC_URLS = {"**"};

    /* Allowed Cors */
    public static final String[] ALLOWED_CORS = {"http://localhost:4200", "http://localhost:8081"};

    /* Allowed Methods */
    public static final String[] ALLOWED_HTTP_METHOD_CALLS = {"PUT", "DELETE", "GET", "POST"};

    /*Allowed Headers */
    public static final String[] ALLOWED_HEADERS = {
            "Origin",
            "Access-Control-Allow-Origin",
            "Content-Type",
            "Accept",
            "Jwt-Token",
            "Authorization",
            "Origin, Accept",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
    };
    public static final String[] EXPOSED_HEADERS = {
            "Origin",
            "Content-Type",
            "Accept",
            "Jwt-Token",
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Access-Control-Allow-Credentials",
    };

    


    /*HttpResponse Message*/

    public static final String RESET_PASSWORD_MSG = "An email with a new password was sent to: ";
    public static final String USER_DELETED_MESSAGE = "User was deleted successfully";


}
