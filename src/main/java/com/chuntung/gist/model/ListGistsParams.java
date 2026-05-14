package com.chuntung.gist.model;

public class ListGistsParams {

    private String since;
    private Integer perPage;
    private Integer page;

    public ListGistsParams() {}

    private ListGistsParams(Builder builder) {
        this.since = builder.since;
        this.perPage = builder.perPage;
        this.page = builder.page;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSince() { return since; }
    public Integer getPerPage() { return perPage; }
    public Integer getPage() { return page; }

    public static final class Builder {
        private String since;
        private Integer perPage;
        private Integer page;

        private Builder() {}

        /** ISO 8601 timestamp – only return gists updated after this time. */
        public Builder since(String since) {
            this.since = since;
            return this;
        }

        /** Results per page (1–100, default 30). */
        public Builder perPage(int perPage) {
            if (perPage < 1 || perPage > 100) {
                throw new IllegalArgumentException("perPage must be between 1 and 100");
            }
            this.perPage = perPage;
            return this;
        }

        /** Page number of results to return (default 1). */
        public Builder page(int page) {
            if (page < 1) {
                throw new IllegalArgumentException("page must be >= 1");
            }
            this.page = page;
            return this;
        }

        public ListGistsParams build() {
            return new ListGistsParams(this);
        }
    }
}
