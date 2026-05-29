package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GistComment {

    private long id;

    @JsonProperty("node_id")
    private String nodeId;

    private String url;
    private String body;
    private GistOwner user;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("author_association")
    private String authorAssociation;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public GistOwner getUser() { return user; }
    public void setUser(GistOwner user) { this.user = user; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getAuthorAssociation() { return authorAssociation; }
    public void setAuthorAssociation(String authorAssociation) { this.authorAssociation = authorAssociation; }
}
