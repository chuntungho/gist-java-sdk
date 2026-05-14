package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GistFileContent {

    private String content;

    @JsonProperty("filename")
    private String newFilename;

    public GistFileContent() {}

    public GistFileContent(String content) {
        this.content = content;
    }

    public static GistFileContent of(String content) {
        return new GistFileContent(content);
    }

    public static GistFileContent rename(String newFilename, String content) {
        GistFileContent fc = new GistFileContent(content);
        fc.newFilename = newFilename;
        return fc;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getNewFilename() { return newFilename; }
    public void setNewFilename(String newFilename) { this.newFilename = newFilename; }
}
