package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GistOwner {

    private String login;
    private long id;

    @JsonProperty("node_id")
    private String nodeId;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("html_url")
    private String htmlUrl;

    private String type;

    @JsonProperty("site_admin")
    private boolean siteAdmin;

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getHtmlUrl() { return htmlUrl; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isSiteAdmin() { return siteAdmin; }
    public void setSiteAdmin(boolean siteAdmin) { this.siteAdmin = siteAdmin; }
}
