package com.chuntung.gist;

import com.chuntung.gist.exception.GistException;
import com.chuntung.gist.model.CreateGistRequest;
import com.chuntung.gist.model.Gist;
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
}
