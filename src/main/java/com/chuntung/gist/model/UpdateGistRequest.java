package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateGistRequest {

    private String description;
    private Map<String, GistFileContent> files;

    public UpdateGistRequest() {}

    private UpdateGistRequest(Builder builder) {
        this.description = builder.description;
        this.files = builder.files;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Map<String, GistFileContent> getFiles() { return files; }
    public void setFiles(Map<String, GistFileContent> files) { this.files = files; }

    public static final class Builder {
        private String description;
        private Map<String, GistFileContent> files;

        private Builder() {}

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder files(Map<String, GistFileContent> files) {
            this.files = files;
            return this;
        }

        public UpdateGistRequest build() {
            return new UpdateGistRequest(this);
        }
    }
}
