package com.fedorasync.client;

import com.fedorasync.model.SyncConfig;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST client for communicating with Fedora Repository.
 * Handles HTTP requests with authentication.
 * Compatible with Java 1.8
 */
public class FedoraRestClient {

    private static final Logger logger = LoggerFactory.getLogger(FedoraRestClient.class);
    private static final String DEFAULT_ACCEPT_HEADER = "application/ld+json";

    private final HttpClient httpClient;
    private final String baseUrl;
    private final HttpClientContext context;

    public FedoraRestClient(SyncConfig config) {
        this.baseUrl = config.getFedoraUrl();

        // Set up basic authentication
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(config.getUsername(), config.getPassword())
        );

        // Set up auth cache
        AuthCache authCache = new BasicAuthCache();
        authCache.put(getHttpHost(config.getFedoraUrl()), new BasicScheme());

        // Create context with auth configuration
        this.context = HttpClientContext.create();
        this.context.setCredentialsProvider(credentialsProvider);
        this.context.setAuthCache(authCache);

        // Create HTTP client
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Performs a GET request to the Fedora repository with JSON-LD accept header.
     *
     * @param endpoint The API endpoint (e.g., "/?offset=0&limit=100")
     * @return The response body as a string
     * @throws IOException If an error occurs during the request
     */
    public String get(String endpoint) throws IOException {
        return get(endpoint, DEFAULT_ACCEPT_HEADER);
    }

    /**
     * Performs a GET request to the Fedora repository with custom accept header.
     *
     * @param endpoint The API endpoint (e.g., "/?offset=0&limit=100")
     * @param acceptHeader The Accept header value (e.g., "application/ld+json")
     * @return The response body as a string
     * @throws IOException If an error occurs during the request
     */
    public String get(String endpoint, String acceptHeader) throws IOException {
        String url = baseUrl + endpoint;
        logger.debug("Performing GET request to: {}", url);

        HttpGet httpGet = new HttpGet(url);
        // Set Accept header to request JSON-LD format from Fedora
        httpGet.setHeader("Accept", acceptHeader);
        logger.debug("Set Accept header to: {}", acceptHeader);
        
        try {
            HttpResponse response = httpClient.execute(httpGet, context);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                String result = EntityUtils.toString(response.getEntity());
                logger.debug("GET request successful, response size: {} bytes, content-type: {}", 
                    result.length(),
                    response.getFirstHeader("Content-Type") != null ? response.getFirstHeader("Content-Type").getValue() : "unknown");
                return result;
            } else {
                logger.error("GET request failed with status code: {}", statusCode);
                throw new IOException("HTTP " + statusCode + ": " + response.getStatusLine().getReasonPhrase());
            }
        } finally {
            httpGet.releaseConnection();
        }
    }

    /**
     * Performs a DELETE request to the Fedora repository.
     *
     * @param endpoint The API endpoint (e.g., "/objectId")
     * @return True if the deletion was successful, false otherwise
     */
    public boolean delete(String endpoint) {
        String url = baseUrl + endpoint;
        logger.debug("Performing DELETE request to: {}", url);

        HttpDelete httpDelete = new HttpDelete(url);
        try {
            HttpResponse response = httpClient.execute(httpDelete, context);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                logger.debug("DELETE request successful");
                return true;
            } else {
                logger.warn("DELETE request failed with status code: {}", statusCode);
                return false;
            }
        } catch (IOException e) {
            logger.error("Error during DELETE request", e);
            return false;
        } finally {
            httpDelete.releaseConnection();
        }
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    public void close() {
        try {
            httpClient.getConnectionManager().shutdown();
            logger.info("HTTP client closed");
        } catch (Exception e) {
            logger.error("Error closing HTTP client", e);
        }
    }

    /**
     * Helper method to extract host information from URL.
     *
     * @param url The URL string
     * @return The HttpHost object
     */
    private org.apache.http.HttpHost getHttpHost(String url) {
        try {
            URI uri = new URI(url);
            return new org.apache.http.HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        } catch (URISyntaxException e) {
            logger.error("Error parsing URL: {}", url, e);
            return new org.apache.http.HttpHost("localhost", 8080, "http");
        }
    }
}
