package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GistCommentRequest {

    private String body;

    public GistCommentRequest() {}

    private GistCommentRequest(String body) {
        this.body = body;
    }

    public static GistCommentRequest of(String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("body must not be null or blank");
        }
        return new GistCommentRequest(body);
    }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
