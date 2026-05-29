package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GistCommit {

    private String url;
    private String version;
    private GistOwner user;

    @JsonProperty("change_status")
    private GistChangeStatus changeStatus;

    @JsonProperty("committed_at")
    private String committedAt;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public GistOwner getUser() { return user; }
    public void setUser(GistOwner user) { this.user = user; }

    public GistChangeStatus getChangeStatus() { return changeStatus; }
    public void setChangeStatus(GistChangeStatus changeStatus) { this.changeStatus = changeStatus; }

    public String getCommittedAt() { return committedAt; }
    public void setCommittedAt(String committedAt) { this.committedAt = committedAt; }
}
