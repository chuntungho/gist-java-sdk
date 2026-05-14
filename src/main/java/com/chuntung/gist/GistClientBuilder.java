package com.chuntung.gist;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Builds a {@link GistClient} instance.
 *
 * <pre>{@code
 * GistClient client = GistClientBuilder.builder()
 *     .token("ghp_your_personal_access_token")
 *     .build();
 * }</pre>
 */
public final class GistClientBuilder {

    private String token;
    private String baseUrl;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    private GistClientBuilder() {}

    public static GistClientBuilder builder() {
        return new GistClientBuilder();
    }

    /**
     * GitHub personal access token or OAuth token used for authentication.
     * Required for write operations and private gist access.
     */
    public GistClientBuilder token(String token) {
        this.token = token;
        return this;
    }

    /**
     * Override the GitHub API base URL (default: {@code https://api.github.com}).
     * Useful for GitHub Enterprise Server or testing.
     */
    public GistClientBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Provide a custom {@link HttpClient}. If not set, a default client with
     * a 30-second connect timeout is used.
     */
    public GistClientBuilder httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    /**
     * Provide a custom Jackson {@link ObjectMapper}. If not set, a sensible
     * default is used.
     */
    public GistClientBuilder objectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public GistClient build() {
        HttpClient client = httpClient != null ? httpClient : defaultHttpClient();
        ObjectMapper mapper = objectMapper != null ? objectMapper : defaultObjectMapper();
        return new GistClientImpl(client, mapper, baseUrl, token);
    }

    private static HttpClient defaultHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    private static ObjectMapper defaultObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
