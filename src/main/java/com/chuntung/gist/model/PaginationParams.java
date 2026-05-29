package com.chuntung.gist.model;

public class PaginationParams {

    private Integer perPage;
    private Integer page;

    public PaginationParams() {}

    private PaginationParams(Builder builder) {
        this.perPage = builder.perPage;
        this.page = builder.page;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getPerPage() { return perPage; }
    public Integer getPage() { return page; }

    public static final class Builder {
        private Integer perPage;
        private Integer page;

        private Builder() {}

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

        public PaginationParams build() {
            return new PaginationParams(this);
        }
    }
}
