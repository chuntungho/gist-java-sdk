package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Gist {

    private String id;

    @JsonProperty("node_id")
    private String nodeId;

    private String url;

    @JsonProperty("forks_url")
    private String forksUrl;

    @JsonProperty("commits_url")
    private String commitsUrl;

    @JsonProperty("git_pull_url")
    private String gitPullUrl;

    @JsonProperty("git_push_url")
    private String gitPushUrl;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("comments_url")
    private String commentsUrl;

    private Map<String, GistFile> files;

    @JsonProperty("public")
    private boolean isPublic;

    private String description;

    private int comments;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private GistOwner owner;
    private GistOwner user;
    private boolean truncated;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getForksUrl() { return forksUrl; }
    public void setForksUrl(String forksUrl) { this.forksUrl = forksUrl; }

    public String getCommitsUrl() { return commitsUrl; }
    public void setCommitsUrl(String commitsUrl) { this.commitsUrl = commitsUrl; }

    public String getGitPullUrl() { return gitPullUrl; }
    public void setGitPullUrl(String gitPullUrl) { this.gitPullUrl = gitPullUrl; }

    public String getGitPushUrl() { return gitPushUrl; }
    public void setGitPushUrl(String gitPushUrl) { this.gitPushUrl = gitPushUrl; }

    public String getHtmlUrl() { return htmlUrl; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }

    public String getCommentsUrl() { return commentsUrl; }
    public void setCommentsUrl(String commentsUrl) { this.commentsUrl = commentsUrl; }

    public Map<String, GistFile> getFiles() { return files; }
    public void setFiles(Map<String, GistFile> files) { this.files = files; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getComments() { return comments; }
    public void setComments(int comments) { this.comments = comments; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public GistOwner getOwner() { return owner; }
    public void setOwner(GistOwner owner) { this.owner = owner; }

    public GistOwner getUser() { return user; }
    public void setUser(GistOwner user) { this.user = user; }

    public boolean isTruncated() { return truncated; }
    public void setTruncated(boolean truncated) { this.truncated = truncated; }
}
