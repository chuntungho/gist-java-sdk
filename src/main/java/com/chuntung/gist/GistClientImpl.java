package com.chuntung.gist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chuntung.gist.exception.GistException;
import com.chuntung.gist.model.CreateGistRequest;
import com.chuntung.gist.model.Gist;
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
    private static final String API_VERSION_HEADER = "2022-11-28";

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
        return executeListRequest(request);
    }

    @Override
    public Gist createGist(CreateGistRequest body) {
        if (body == null) throw new IllegalArgumentException("request must not be null");
        HttpRequest request = buildPostRequest(baseUrl + "/gists", serialize(body));
        return executeSingleRequest(request);
    }

    @Override
    public List<Gist> listPublicGists(ListGistsParams params) {
        String url = buildUrl("/gists/public", params);
        HttpRequest request = buildGetRequest(url);
        return executeListRequest(request);
    }

    @Override
    public List<Gist> listStarredGists(ListGistsParams params) {
        String url = buildUrl("/gists/starred", params);
        HttpRequest request = buildGetRequest(url);
        return executeListRequest(request);
    }

    @Override
    public Gist getGist(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildGetRequest(baseUrl + "/gists/" + gistId);
        return executeSingleRequest(request);
    }

    @Override
    public Gist updateGist(String gistId, UpdateGistRequest body) {
        requireNonBlank(gistId, "gistId");
        if (body == null) throw new IllegalArgumentException("request must not be null");
        HttpRequest request = buildPatchRequest(baseUrl + "/gists/" + gistId, serialize(body));
        return executeSingleRequest(request);
    }

    @Override
    public void deleteGist(String gistId) {
        requireNonBlank(gistId, "gistId");
        HttpRequest request = buildDeleteRequest(baseUrl + "/gists/" + gistId);
        executeVoidRequest(request);
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

    private List<Gist> executeListRequest(HttpRequest request) {
        HttpResponse<String> response = send(request);
        checkStatus(response);
        try {
            return objectMapper.readValue(response.body(), new TypeReference<List<Gist>>() {});
        } catch (IOException e) {
            throw new GistException("Failed to deserialize response", e);
        }
    }

    private Gist executeSingleRequest(HttpRequest request) {
        HttpResponse<String> response = send(request);
        checkStatus(response);
        try {
            return objectMapper.readValue(response.body(), Gist.class);
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
