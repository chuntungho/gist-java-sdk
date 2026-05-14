package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GistFile {

    private String filename;
    private String type;
    private String language;

    @JsonProperty("raw_url")
    private String rawUrl;

    private long size;
    private boolean truncated;
    private String content;

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getRawUrl() { return rawUrl; }
    public void setRawUrl(String rawUrl) { this.rawUrl = rawUrl; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public boolean isTruncated() { return truncated; }
    public void setTruncated(boolean truncated) { this.truncated = truncated; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
