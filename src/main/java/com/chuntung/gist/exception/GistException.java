package com.chuntung.gist.exception;

public class GistException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public GistException(int statusCode, String responseBody) {
        super("GitHub Gists API error: HTTP " + statusCode + " – " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public GistException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseBody = null;
    }

    public int getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
}
