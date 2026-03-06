package com.bankapp.common.dto;

public class TokenValidationRequest {
    private String token;
    private String requestPath;
    private String httpMethod;

    public TokenValidationRequest() {}
    public TokenValidationRequest(String token, String requestPath, String httpMethod) {
        this.token = token;
        this.requestPath = requestPath;
        this.httpMethod = httpMethod;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRequestPath() { return requestPath; }
    public void setRequestPath(String requestPath) { this.requestPath = requestPath; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
}
