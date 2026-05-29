package com.chuntung.gist;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chuntung.gist.exception.GistException;
import com.chuntung.gist.model.CreateGistRequest;
import com.chuntung.gist.model.Gist;
import com.chuntung.gist.model.GistComment;
import com.chuntung.gist.model.GistCommentRequest;
import com.chuntung.gist.model.GistCommit;
import com.chuntung.gist.model.GistFileContent;
import com.chuntung.gist.model.ListGistsParams;
import com.chuntung.gist.model.UpdateGistRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GistClientTest {

    private HttpClient mockHttpClient;
    private GistClient client;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String GIST_ID = "abc123";
    private static final String GIST_JSON = """
            {
              "id": "abc123",
              "description": "Hello World",
              "public": true,
              "html_url": "https://gist.github.com/abc123",
              "created_at": "2024-01-01T00:00:00Z",
              "updated_at": "2024-01-02T00:00:00Z",
              "files": {
                "hello.txt": {
                  "filename": "hello.txt",
                  "type": "text/plain",
                  "size": 5,
                  "content": "hello"
                }
              },
              "owner": {
                "login": "octocat",
                "id": 1
              }
            }
            """;

    private static final String GIST_LIST_JSON = "[" + GIST_JSON + "]";

    private static final String COMMENT_JSON = """
            {
              "id": 1,
              "node_id": "MDExOkdpc3RDb21tZW50MQ==",
              "url": "https://api.github.com/gists/abc123/comments/1",
              "body": "nice gist!",
              "user": { "login": "octocat", "id": 1 },
              "created_at": "2024-01-01T00:00:00Z",
              "updated_at": "2024-01-01T00:00:00Z",
              "author_association": "NONE"
            }
            """;

    private static final String COMMENT_LIST_JSON = "[" + COMMENT_JSON + "]";

    private static final String COMMIT_JSON = """
            {
              "url": "https://api.github.com/gists/abc123/1a2b3c",
              "version": "1a2b3c",
              "committed_at": "2024-01-01T00:00:00Z",
              "change_status": { "total": 5, "additions": 3, "deletions": 2 },
              "user": { "login": "octocat", "id": 1 }
            }
            """;

    private static final String COMMIT_LIST_JSON = "[" + COMMIT_JSON + "]";

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        mockHttpClient = mock(HttpClient.class);
        client = GistClientBuilder.builder()
                .token("test-token")
                .httpClient(mockHttpClient)
                .build();
    }

    // --- listGists ---

    @Test
    void listGists_returnsGists() throws Exception {
        stubResponse(200, GIST_LIST_JSON);

        List<Gist> gists = client.listGists(null);

        assertNotNull(gists);
        assertEquals(1, gists.size());
        assertEquals(GIST_ID, gists.get(0).getId());
        assertEquals("Hello World", gists.get(0).getDescription());
        assertTrue(gists.get(0).isPublic());
    }

    @Test
    void listGists_withParams_buildsCorrectUrl() throws Exception {
        stubResponse(200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        ListGistsParams params = ListGistsParams.builder()
                .since("2024-01-01T00:00:00Z")
                .perPage(10)
                .page(2)
                .build();
        client.listGists(params);

        verify(mockHttpClient).send(captor.capture(), any());
        String url = captor.getValue().uri().toString();
        assertTrue(url.contains("/user/gists"));
        assertTrue(url.contains("per_page=10"));
        assertTrue(url.contains("page=2"));
        assertTrue(url.contains("since="));
    }

    @Test
    void listGists_setsAuthorizationHeader() throws Exception {
        stubResponse(200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.listGists(null);

        verify(mockHttpClient).send(captor.capture(), any());
        HttpRequest request = captor.getValue();
        assertEquals("Bearer test-token", request.headers().firstValue("Authorization").orElse(null));
        assertEquals("application/vnd.github+json", request.headers().firstValue("Accept").orElse(null));
    }

    // --- createGist ---

    @Test
    void createGist_returnsCreatedGist() throws Exception {
        stubResponse(201, GIST_JSON);

        CreateGistRequest req = CreateGistRequest.builder(
                Map.of("hello.txt", GistFileContent.of("hello")))
                .description("Hello World")
                .isPublic(true)
                .build();

        Gist created = client.createGist(req);

        assertNotNull(created);
        assertEquals(GIST_ID, created.getId());
    }

    @Test
    void createGist_sendsPostToGistsEndpoint() throws Exception {
        stubResponse(201, GIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.createGist(CreateGistRequest.builder(
                Map.of("f.txt", GistFileContent.of("content"))).build());

        verify(mockHttpClient).send(captor.capture(), any());
        HttpRequest request = captor.getValue();
        assertEquals("POST", request.method());
        assertTrue(request.uri().toString().endsWith("/gists"));
    }

    @Test
    void createGist_nullRequest_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> client.createGist(null));
    }

    // --- listPublicGists ---

    @Test
    void listPublicGists_usesCorrectEndpoint() throws Exception {
        stubResponse(200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.listPublicGists(null);

        verify(mockHttpClient).send(captor.capture(), any());
        assertTrue(captor.getValue().uri().toString().contains("/gists/public"));
    }

    // --- listStarredGists ---

    @Test
    void listStarredGists_usesCorrectEndpoint() throws Exception {
        stubResponse(200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.listStarredGists(null);

        verify(mockHttpClient).send(captor.capture(), any());
        assertTrue(captor.getValue().uri().toString().contains("/gists/starred"));
    }

    // --- getGist ---

    @Test
    void getGist_returnsGist() throws Exception {
        stubResponse(200, GIST_JSON);

        Gist gist = client.getGist(GIST_ID);

        assertNotNull(gist);
        assertEquals(GIST_ID, gist.getId());
        assertNotNull(gist.getFiles());
        assertNotNull(gist.getFiles().get("hello.txt"));
        assertEquals("hello", gist.getFiles().get("hello.txt").getContent());
    }

    @Test
    void getGist_sendsGetToGistEndpoint() throws Exception {
        stubResponse(200, GIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.getGist(GIST_ID);

        verify(mockHttpClient).send(captor.capture(), any());
        HttpRequest request = captor.getValue();
        assertEquals("GET", request.method());
        assertTrue(request.uri().toString().endsWith("/gists/" + GIST_ID));
    }

    @Test
    void getGist_blankId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> client.getGist(""));
        assertThrows(IllegalArgumentException.class, () -> client.getGist(null));
    }

    // --- updateGist ---

    @Test
    void updateGist_returnsUpdatedGist() throws Exception {
        stubResponse(200, GIST_JSON);

        UpdateGistRequest req = UpdateGistRequest.builder()
                .description("Updated description")
                .files(Map.of("hello.txt", GistFileContent.of("updated content")))
                .build();

        Gist updated = client.updateGist(GIST_ID, req);

        assertNotNull(updated);
        assertEquals(GIST_ID, updated.getId());
    }

    @Test
    void updateGist_sendsPatchRequest() throws Exception {
        stubResponse(200, GIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.updateGist(GIST_ID, UpdateGistRequest.builder().description("desc").build());

        verify(mockHttpClient).send(captor.capture(), any());
        HttpRequest request = captor.getValue();
        assertEquals("PATCH", request.method());
        assertTrue(request.uri().toString().endsWith("/gists/" + GIST_ID));
    }

    // --- deleteGist ---

    @Test
    void deleteGist_sends204AndSucceeds() throws Exception {
        stubResponse(204, "");

        assertDoesNotThrow(() -> client.deleteGist(GIST_ID));
    }

    @Test
    void deleteGist_sendsDeleteRequest() throws Exception {
        stubResponse(204, "");
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.deleteGist(GIST_ID);

        verify(mockHttpClient).send(captor.capture(), any());
        HttpRequest request = captor.getValue();
        assertEquals("DELETE", request.method());
        assertTrue(request.uri().toString().endsWith("/gists/" + GIST_ID));
    }

    // --- listGistCommits ---

    @Test
    void listGistCommits_usesCorrectEndpoint() throws Exception {
        stubResponse(200, COMMIT_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        List<GistCommit> commits = client.listGistCommits(GIST_ID);

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("GET", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/commits"));
        assertEquals(1, commits.size());
        assertEquals("1a2b3c", commits.get(0).getVersion());
        assertEquals(5, commits.get(0).getChangeStatus().getTotal());
    }

    @Test
    void listGistCommits_blankId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> client.listGistCommits(""));
        assertThrows(IllegalArgumentException.class, () -> client.listGistCommits(null));
    }

    // --- listGistForks ---

    @Test
    void listGistForks_usesCorrectEndpoint() throws Exception {
        stubResponse(200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        List<Gist> forks = client.listGistForks(GIST_ID);

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("GET", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/forks"));
        assertEquals(1, forks.size());
    }

    // --- forkGist ---

    @Test
    void forkGist_sendsPostAndReturnsGist() throws Exception {
        stubResponse(201, GIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        Gist forked = client.forkGist(GIST_ID);

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("POST", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/forks"));
        assertNotNull(forked);
        assertEquals(GIST_ID, forked.getId());
    }

    @Test
    void forkGist_blankId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> client.forkGist(""));
    }

    // --- isGistStarred ---

    @Test
    void isGistStarred_returnsTrue_when204() throws Exception {
        stubResponse(204, "");

        assertTrue(client.isGistStarred(GIST_ID));
    }

    @Test
    void isGistStarred_returnsFalse_when404() throws Exception {
        stubResponse(404, "{\"message\":\"Not Found\"}");

        assertFalse(client.isGistStarred(GIST_ID));
    }

    @Test
    void isGistStarred_usesCorrectEndpoint() throws Exception {
        stubResponse(204, "");
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.isGistStarred(GIST_ID);

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("GET", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/star"));
    }

    // --- starGist ---

    @Test
    void starGist_sendsPutRequest() throws Exception {
        stubResponse(204, "");
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        assertDoesNotThrow(() -> client.starGist(GIST_ID));

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("PUT", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/star"));
    }

    // --- unstarGist ---

    @Test
    void unstarGist_sendsDeleteToStarEndpoint() throws Exception {
        stubResponse(204, "");
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        assertDoesNotThrow(() -> client.unstarGist(GIST_ID));

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("DELETE", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/star"));
    }

    // --- listGistComments ---

    @Test
    void listGistComments_usesCorrectEndpoint() throws Exception {
        stubResponse(200, COMMENT_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        List<GistComment> comments = client.listGistComments(GIST_ID);

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("GET", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/comments"));
        assertEquals(1, comments.size());
        assertEquals("nice gist!", comments.get(0).getBody());
    }

    // --- createGistComment ---

    @Test
    void createGistComment_returnsCreatedComment() throws Exception {
        stubResponse(201, COMMENT_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        GistComment comment = client.createGistComment(GIST_ID, GistCommentRequest.of("nice gist!"));

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("POST", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/comments"));
        assertNotNull(comment);
        assertEquals(1L, comment.getId());
        assertEquals("nice gist!", comment.getBody());
    }

    @Test
    void createGistComment_nullRequest_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> client.createGistComment(GIST_ID, null));
    }

    // --- getGistComment ---

    @Test
    void getGistComment_usesCorrectEndpoint() throws Exception {
        stubResponse(200, COMMENT_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        GistComment comment = client.getGistComment(GIST_ID, 1L);

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("GET", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/comments/1"));
        assertEquals(1L, comment.getId());
    }

    // --- updateGistComment ---

    @Test
    void updateGistComment_sendsPatchRequest() throws Exception {
        stubResponse(200, COMMENT_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.updateGistComment(GIST_ID, 1L, GistCommentRequest.of("updated body"));

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("PATCH", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/comments/1"));
    }

    // --- deleteGistComment ---

    @Test
    void deleteGistComment_sendsDeleteRequest() throws Exception {
        stubResponse(204, "");
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        assertDoesNotThrow(() -> client.deleteGistComment(GIST_ID, 1L));

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("DELETE", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/comments/1"));
    }

    // --- listUserGists ---

    @Test
    void listUserGists_usesCorrectEndpoint() throws Exception {
        stubResponse(200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        List<Gist> gists = client.listUserGists("octocat", null);

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("GET", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().contains("/users/octocat/gists"));
        assertEquals(1, gists.size());
    }

    @Test
    void listUserGists_withParams_buildsCorrectUrl() throws Exception {
        stubResponse(200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        client.listUserGists("octocat", ListGistsParams.builder().perPage(5).page(1).build());

        verify(mockHttpClient).send(captor.capture(), any());
        String url = captor.getValue().uri().toString();
        assertTrue(url.contains("/users/octocat/gists"));
        assertTrue(url.contains("per_page=5"));
        assertTrue(url.contains("page=1"));
    }

    @Test
    void listUserGists_blankUsername_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> client.listUserGists("", null));
        assertThrows(IllegalArgumentException.class, () -> client.listUserGists(null, null));
    }

    // --- getGistRevision ---

    @Test
    void getGistRevision_usesCorrectEndpoint() throws Exception {
        stubResponse(200, GIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        Gist revision = client.getGistRevision(GIST_ID, "1a2b3c");

        verify(mockHttpClient).send(captor.capture(), any());
        assertEquals("GET", captor.getValue().method());
        assertTrue(captor.getValue().uri().toString().endsWith("/gists/" + GIST_ID + "/1a2b3c"));
        assertNotNull(revision);
        assertEquals(GIST_ID, revision.getId());
    }

    @Test
    void getGistRevision_blankSha_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> client.getGistRevision(GIST_ID, ""));
        assertThrows(IllegalArgumentException.class, () -> client.getGistRevision(GIST_ID, null));
    }

    // --- error handling ---

    @Test
    void apiError_throwsGistException() throws Exception {
        stubResponse(404, "{\"message\":\"Not Found\"}");

        GistException ex = assertThrows(GistException.class, () -> client.getGist("nonexistent"));
        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getResponseBody().contains("Not Found"));
    }

    @Test
    void apiError_401_throwsGistException() throws Exception {
        stubResponse(401, "{\"message\":\"Requires authentication\"}");

        GistException ex = assertThrows(GistException.class, () -> client.listGists(null));
        assertEquals(401, ex.getStatusCode());
    }

    @Test
    void ioError_throwsGistException() throws Exception {
        when(mockHttpClient.send(any(), any())).thenThrow(new IOException("connection refused"));

        assertThrows(GistException.class, () -> client.getGist(GIST_ID));
    }

    // --- builder ---

    @Test
    void builderWithoutToken_createsClientWithoutAuthHeader() throws Exception {
        HttpClient anonHttpClient = mock(HttpClient.class);
        GistClient anonClient = GistClientBuilder.builder()
                .httpClient(anonHttpClient)
                .build();

        stubResponseOn(anonHttpClient, 200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        anonClient.listPublicGists(null);

        verify(anonHttpClient).send(captor.capture(), any());
        assertFalse(captor.getValue().headers().firstValue("Authorization").isPresent());
    }

    @Test
    void builderWithCustomBaseUrl_usesCustomUrl() throws Exception {
        HttpClient customHttpClient = mock(HttpClient.class);
        GistClient customClient = GistClientBuilder.builder()
                .token("tok")
                .baseUrl("https://github.example.com/api/v3")
                .httpClient(customHttpClient)
                .build();

        stubResponseOn(customHttpClient, 200, GIST_LIST_JSON);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);

        customClient.listPublicGists(null);

        verify(customHttpClient).send(captor.capture(), any());
        assertTrue(captor.getValue().uri().toString().startsWith("https://github.example.com/api/v3"));
    }

    // --- ListGistsParams validation ---

    @Test
    void listGistsParams_invalidPerPage_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                ListGistsParams.builder().perPage(0).build());
        assertThrows(IllegalArgumentException.class, () ->
                ListGistsParams.builder().perPage(101).build());
    }

    @Test
    void listGistsParams_invalidPage_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                ListGistsParams.builder().page(0).build());
    }

    // --- CreateGistRequest validation ---

    @Test
    void createGistRequest_nullFiles_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                CreateGistRequest.builder(null).build());
    }

    @Test
    void createGistRequest_emptyFiles_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                CreateGistRequest.builder(Map.of()).build());
    }

    // --- GistCommentRequest validation ---

    @Test
    void gistCommentRequest_blankBody_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> GistCommentRequest.of(""));
        assertThrows(IllegalArgumentException.class, () -> GistCommentRequest.of(null));
    }

    // --- helpers ---

    @SuppressWarnings("unchecked")
    private void stubResponse(int status, String body) throws Exception {
        stubResponseOn(mockHttpClient, status, body);
    }

    @SuppressWarnings("unchecked")
    private void stubResponseOn(HttpClient httpClient, int status, String body) throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(status);
        when(mockResponse.body()).thenReturn(body);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
    }
}
