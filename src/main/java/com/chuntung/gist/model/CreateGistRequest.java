package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateGistRequest {

    private Map<String, GistFileContent> files;
    private String description;

    @JsonProperty("public")
    private Boolean isPublic;

    public CreateGistRequest() {}

    private CreateGistRequest(Builder builder) {
        this.files = builder.files;
        this.description = builder.description;
        this.isPublic = builder.isPublic;
    }

    public static Builder builder(Map<String, GistFileContent> files) {
        return new Builder(files);
    }

    public Map<String, GistFileContent> getFiles() { return files; }
    public void setFiles(Map<String, GistFileContent> files) { this.files = files; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getPublic() { return isPublic; }
    public void setPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public static final class Builder {
        private final Map<String, GistFileContent> files;
        private String description;
        private Boolean isPublic;

        private Builder(Map<String, GistFileContent> files) {
            if (files == null || files.isEmpty()) {
                throw new IllegalArgumentException("files must not be null or empty");
            }
            this.files = files;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public CreateGistRequest build() {
            return new CreateGistRequest(this);
        }
    }
}
