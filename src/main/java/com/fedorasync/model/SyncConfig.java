package com.fedorasync.model;

/**
 * Configuration model for the Fedora Sync application.
 */
public class SyncConfig {
    private String fedoraUrl;
    private String username;
    private String password;
    private String apiVersion;
    private String userIdsFile;
    private String loggingLevel;

    public SyncConfig(String fedoraUrl, String username, String password,
                      String apiVersion, String userIdsFile, String loggingLevel) {
        this.fedoraUrl = fedoraUrl;
        this.username = username;
        this.password = password;
        this.apiVersion = apiVersion;
        this.userIdsFile = userIdsFile;
        this.loggingLevel = loggingLevel;
    }

    public String getFedoraUrl() { return fedoraUrl; }
    public void setFedoraUrl(String fedoraUrl) { this.fedoraUrl = fedoraUrl; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    public String getUserIdsFile() { return userIdsFile; }
    public void setUserIdsFile(String userIdsFile) { this.userIdsFile = userIdsFile; }
    public String getLoggingLevel() { return loggingLevel; }
    public void setLoggingLevel(String loggingLevel) { this.loggingLevel = loggingLevel; }

    @Override
    public String toString() {
        return "SyncConfig{" +
                "fedoraUrl='" + fedoraUrl + '\'' +
                ", username='" + username + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                ", userIdsFile='" + userIdsFile + '\'' +
                ", loggingLevel='" + loggingLevel + '\'' +
                '}';
    }
}
