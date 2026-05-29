package com.chuntung.gist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.chuntung.gist.exception.GistException;
import com.chuntung.gist.model.CreateGistRequest;
import com.chuntung.gist.model.Gist;
import com.chuntung.gist.model.GistComment;
import com.chuntung.gist.model.GistCommentRequest;
import com.chuntung.gist.model.GistCommit;
import com.chuntung.gist.model.ListGistsParams;
import com.chuntung.gist.model.UpdateGistRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class GistClientImpl implements GistClient {

    private static final String DEFAULT_BASE_URL = "https://api.github.com";
    private static final String ACCEPT_HEADER = "application/vnd.github+json";
    private static final String API_VERSION_HEADER = "2026-03-10";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String token;

    GistClientImpl(HttpClient httpClient, ObjectMapper objectMapper, String baseUrl, String token) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl != null ? baseUrl : DEFAULT_BASE_URL;
        this.token = token;
    }

    @Override
    public List<Gist> listGists(ListGistsParams params) {
        String url = buildUrl("/user/gists", params);
        HttpRequest request = buildGetRequest(url);
        return executeListRequest(request, Gist.class);
    }

    @Override
    public Gist createGist(CreateGistRequest body) {
        if (body == null) throw new IllegalArgumentException("request must not be null");
        HttpRequest request = buildPostRequest(baseUrl + "/gists", serialize(body));
        return executeSingleRequest(request, Gist.class);
    }

    @Override
    public List<Gist> listPublicGists(ListGistsParams params) {
        String url = buildUrl("/gists/public", params);
        HttpRequest request = buildGetRequest(url);
        return executeListRequest(request, Gist.class);
    }

    @Override
    public List<Gist> listStarredGists(ListGistsParams params) {
        String url = buildUrl("/gists/starred", params);
        HttpRequest request = buildGetRequest(url);
        return executeListRequest(request, Gist.class);
    }

    @Override
    public Gist getGist(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId);
        return executeSingleRequest(request, Gist.class);
    }

    @Override
    public Gist updateGist(String gistId, UpdateGistRequest body) {
        requireNonBlank(gistId, "gistId");
        if (body == null) throw new IllegalArgumentException("request must not be null");
        HttpRequest request = buildPatchRequest(baseUrl + "/gists/" + gistId, serialize(body));
        return executeSingleRequest(request, Gist.class);
    }

    @Override
    public void deleteGist(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildDeleteRequest(baseUrl + "/gists/" + gistId);
        executeVoidRequest(request);
    }

    @Override
    public List<GistCommit> listGistCommits(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId + "/commits");
        return executeListRequest(request, GistCommit.class);
    }

    @Override
    public List<Gist> listGistForks(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId + "/forks");
        return executeListRequest(request, Gist.class);
    }

    @Override
    public Gist forkGist(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildPostEmptyRequest(baseUrl + "/gists/" + gistId + "/forks");
        return executeSingleRequest(request, Gist.class);
    }

    @Override
    public boolean isGistStarred(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId + "/star");
        HttpResponse<String> response = send(request);
        int status = response.statusCode();
        if (status == 204) return true;
        if (status == 404) return false;
        checkStatus(response);
        return false;
    }

    @Override
    public void starGist(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildPutRequest(baseUrl + "/gists/" + gistId + "/star");
        executeVoidRequest(request);
    }

    @Override
    public void unstarGist(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildDeleteRequest(baseUrl + "/gists/" + gistId + "/star");
        executeVoidRequest(request);
    }

    @Override
    public List<GistComment> listGistComments(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId + "/comments");
        return executeListRequest(request, GistComment.class);
    }

    @Override
    public GistComment createGistComment(String gistId, GistCommentRequest body) {
        requireNonBlank(gistId, "gistId");
        if (body == null) throw new IllegalArgumentException("request must not be null");
        HttpRequest request = buildPostRequest(baseUrl + "/gists/" + gistId + "/comments", serialize(body));
        return executeSingleRequest(request, GistComment.class);
    }

    @Override
    public GistComment getGistComment(String gistId, long commentId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId + "/comments/" + commentId);
        return executeSingleRequest(request, GistComment.class);
    }

    @Override
    public GistComment updateGistComment(String gistId, long commentId, GistCommentRequest body) {
        requireNonBlank(gistId, "gistId");
        if (body == null) throw new IllegalArgumentException("request must not be null");
        HttpRequest request = buildPatchRequest(baseUrl + "/gists/" + gistId + "/comments/" + commentId, serialize(body));
        return executeSingleRequest(request, GistComment.class);
    }

    @Override
    public void deleteGistComment(String gistId, long commentId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildDeleteRequest(baseUrl + "/gists/" + gistId + "/comments/" + commentId);
        executeVoidRequest(request);
    }

    @Override
    public List<Gist> listUserGists(String username, ListGistsParams params) {
        requireNonBlank(username, "username");
        String url = buildUrl("/users/" + encode(username) + "/gists", params);
        HttpRequest request = buildGetRequest(url);
        return executeListRequest(request, Gist.class);
    }

    @Override
    public Gist getGistRevision(String gistId, String sha) {
        requireNonBlank(gistId, "gistId");
        requireNonBlank(sha, "sha");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId + "/" + sha);
        return executeSingleRequest(request, Gist.class);
    }

    // --- HTTP helpers ---

    private HttpRequest buildGetRequest(String url) {
        return baseRequestBuilder(url)
                .GET()
                .build();
    }

    private HttpRequest buildPostRequest(String url, String json) {
        return baseRequestBuilder(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private HttpRequest buildPostEmptyRequest(String url) {
        return baseRequestBuilder(url)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private HttpRequest buildPutRequest(String url) {
        return baseRequestBuilder(url)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private HttpRequest buildPatchRequest(String url, String json) {
        return baseRequestBuilder(url)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
    }

    private HttpRequest buildDeleteRequest(String url) {
        return baseRequestBuilder(url)
                .DELETE()
                .build();
    }

    private HttpRequest.Builder baseRequestBuilder(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", ACCEPT_HEADER)
                .header("X-GitHub-Api-Version", API_VERSION_HEADER);
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    // --- Response handlers ---

    private <T> List<T> executeListRequest(HttpRequest request, Class<T> elementClass) {
        HttpResponse<String> response = send(request);
        checkStatus(response);
        try {
            var collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementClass);
            return objectMapper.readValue(response.body(), collectionType);
        } catch (IOException e) {
            throw new GistException("Failed to deserialize response", e);
        }
    }

    private <T> T executeSingleRequest(HttpRequest request, Class<T> clazz) {
        HttpResponse<String> response = send(request);
        checkStatus(response);
        try {
            return objectMapper.readValue(response.body(), clazz);
        } catch (IOException e) {
            throw new GistException("Failed to deserialize response", e);
        }
    }

    private void executeVoidRequest(HttpRequest request) {
        HttpResponse<String> response = send(request);
        checkStatus(response);
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new GistException("I/O error communicating with GitHub API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GistException("Request interrupted", e);
        }
    }

    private void checkStatus(HttpResponse<String> response) {
        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new GistException(status, response.body());
        }
    }

    // --- URL building ---

    private String buildUrl(String path, ListGistsParams params) {
        if (params == null) {
            return baseUrl + path;
        }
        List<String> parts = new ArrayList<>();
        if (params.getSince() != null) {
            parts.add("since=" + encode(params.getSince()));
        }
        if (params.getPerPage() != null) {
            parts.add("per_page=" + params.getPerPage());
        }
        if (params.getPage() != null) {
            parts.add("page=" + params.getPage());
        }
        String query = String.join("&", parts);
        return baseUrl + path + (query.isEmpty() ? "" : "?" + query);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    // --- Serialization ---

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new GistException("Failed to serialize request body", e);
        }
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
    }
}
