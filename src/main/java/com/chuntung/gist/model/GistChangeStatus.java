package com.chuntung.gist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GistChangeStatus {

    private int total;
    private int additions;
    private int deletions;

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getAdditions() { return additions; }
    public void setAdditions(int additions) { this.additions = additions; }

    public int getDeletions() { return deletions; }
    public void setDeletions(int deletions) { this.deletions = deletions; }
}
