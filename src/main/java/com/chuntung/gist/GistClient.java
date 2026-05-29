package com.chuntung.gist;

import com.chuntung.gist.exception.GistException;
import com.chuntung.gist.model.CreateGistRequest;
import com.chuntung.gist.model.Gist;
import com.chuntung.gist.model.GistComment;
import com.chuntung.gist.model.GistCommentRequest;
import com.chuntung.gist.model.GistCommit;
import com.chuntung.gist.model.ListGistsParams;
import com.chuntung.gist.model.UpdateGistRequest;

import java.util.List;

/**
 * Client for the GitHub Gists REST API.
 *
 * <pre>{@code
 * GistClient client = GistClientBuilder.builder()
 *     .token("ghp_your_token")
 *     .build();
 * }</pre>
 */
public interface GistClient {

    /**
     * Lists gists for the authenticated user.
     * Requires authentication.
     *
     * @param params optional query parameters (may be {@code null})
     * @return list of gists
     * @throws GistException on API error
     */
    List<Gist> listGists(ListGistsParams params);

    /**
     * Creates a new gist.
     * Requires authentication.
     *
     * @param request gist creation request (files required)
     * @return the created gist
     * @throws GistException on API error
     */
    Gist createGist(CreateGistRequest request);

    /**
     * Lists public gists sorted by most recently updated.
     *
     * @param params optional query parameters (may be {@code null})
     * @return list of public gists
     * @throws GistException on API error
     */
    List<Gist> listPublicGists(ListGistsParams params);

    /**
     * Lists gists starred by the authenticated user.
     * Requires authentication.
     *
     * @param params optional query parameters (may be {@code null})
     * @return list of starred gists
     * @throws GistException on API error
     */
    List<Gist> listStarredGists(ListGistsParams params);

    /**
     * Gets a single gist by ID.
     *
     * @param gistId the gist identifier
     * @return the gist
     * @throws GistException on API error
     */
    Gist getGist(String gistId);

    /**
     * Updates a gist.
     * Requires authentication and ownership of the gist.
     *
     * @param gistId  the gist identifier
     * @param request update request (description and/or files)
     * @return the updated gist
     * @throws GistException on API error
     */
    Gist updateGist(String gistId, UpdateGistRequest request);

    /**
     * Deletes a gist.
     * Requires authentication and ownership of the gist.
     *
     * @param gistId the gist identifier
     * @throws GistException on API error
     */
    void deleteGist(String gistId);

    /**
     * Lists commits for a gist.
     *
     * @param gistId the gist identifier
     * @return list of gist commits
     * @throws GistException on API error
     */
    List<GistCommit> listGistCommits(String gistId);

    /**
     * Lists forks of a gist.
     *
     * @param gistId the gist identifier
     * @return list of forked gists
     * @throws GistException on API error
     */
    List<Gist> listGistForks(String gistId);

    /**
     * Forks a gist.
     * Requires authentication.
     *
     * @param gistId the gist identifier
     * @return the forked gist
     * @throws GistException on API error
     */
    Gist forkGist(String gistId);

    /**
     * Checks whether a gist is starred by the authenticated user.
     * Requires authentication.
     *
     * @param gistId the gist identifier
     * @return {@code true} if starred, {@code false} if not starred
     * @throws GistException on API error (other than 404)
     */
    boolean isGistStarred(String gistId);

    /**
     * Stars a gist.
     * Requires authentication.
     *
     * @param gistId the gist identifier
     * @throws GistException on API error
     */
    void starGist(String gistId);

    /**
     * Unstars a gist.
     * Requires authentication.
     *
     * @param gistId the gist identifier
     * @throws GistException on API error
     */
    void unstarGist(String gistId);

    /**
     * Lists comments on a gist.
     *
     * @param gistId the gist identifier
     * @return list of comments
     * @throws GistException on API error
     */
    List<GistComment> listGistComments(String gistId);

    /**
     * Creates a comment on a gist.
     * Requires authentication.
     *
     * @param gistId  the gist identifier
     * @param request the comment request (body required)
     * @return the created comment
     * @throws GistException on API error
     */
    GistComment createGistComment(String gistId, GistCommentRequest request);

    /**
     * Gets a single comment on a gist.
     *
     * @param gistId    the gist identifier
     * @param commentId the comment identifier
     * @return the comment
     * @throws GistException on API error
     */
    GistComment getGistComment(String gistId, long commentId);

    /**
     * Updates a comment on a gist.
     * Requires authentication.
     *
     * @param gistId    the gist identifier
     * @param commentId the comment identifier
     * @param request   the update request
     * @return the updated comment
     * @throws GistException on API error
     */
    GistComment updateGistComment(String gistId, long commentId, GistCommentRequest request);

    /**
     * Deletes a comment on a gist.
     * Requires authentication.
     *
     * @param gistId    the gist identifier
     * @param commentId the comment identifier
     * @throws GistException on API error
     */
    void deleteGistComment(String gistId, long commentId);

    /**
     * Lists public gists for the given user.
     *
     * @param username the GitHub username
     * @param params   optional query parameters (may be {@code null})
     * @return list of public gists for the user
     * @throws GistException on API error
     */
    List<Gist> listUserGists(String username, ListGistsParams params);

    /**
     * Gets a specific revision of a gist.
     *
     * @param gistId the gist identifier
     * @param sha    the SHA of the revision
     * @return the gist at that revision
     * @throws GistException on API error
     */
    Gist getGistRevision(String gistId, String sha);
}
