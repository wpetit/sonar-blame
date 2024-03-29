package com.wpetit.sonar.blame;

import java.text.ParseException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link IssuesManager} class.
 * 
 * @author WPETIT
 * 
 */
public class IssuesManager {

    /** The LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(IssuesManager.class);

    /** The sonarBaseUrl. */
    private final String sonarBaseUrl;

    /** The component. */
    private final String component;

    /** The resourcesManager. */
    private final ResourcesManager resourcesManager;

    /**
     * Constructor.
     * 
     * @param sonarBaseUrl
     *            the sonar base url
     * @param component
     *            the component
     */
    public IssuesManager(final String sonarBaseUrl, final String component) {
        this.sonarBaseUrl = sonarBaseUrl;
        this.component = component;
        resourcesManager = new ResourcesManager(sonarBaseUrl);
    }

    /**
     * Assign issues with the creation date limit.
     * 
     * @param date
     *            the creation date limit
     */
    public void getIssuesCreatedAfter(final String date) {

        Client client = ClientBuilder.newClient();
        WebTarget target;
        if (StringUtils.isNotEmpty(component)) {
            target = client.target(sonarBaseUrl).path(IssueAPIConstants.SEARCH_PATH.getValue())
                    .queryParam(IssueAPIConstants.CREATED_AFTER.getValue(), date)
                    .queryParam(IssueAPIConstants.STATUSES.getValue(), "OPEN,REOPENED,CONFIRMED")
                    .queryParam(IssueAPIConstants.COMPONENT_ROOTS.getValue(), component);

        } else {
            target = client.target(sonarBaseUrl).path(IssueAPIConstants.SEARCH_PATH.getValue())
                    .queryParam(IssueAPIConstants.CREATED_AFTER.getValue(), date)
                    .queryParam(IssueAPIConstants.STATUSES.getValue(), "OPEN,REOPENED,CONFIRMED");
        }

        JsonObject j = target.request(MediaType.APPLICATION_JSON).get(JsonObject.class);

        JsonArray issues = j.getJsonArray(IssueAPIConstants.ISSUES.getValue());

        for (int i = 0; i < issues.size(); i++) {
            JsonObject issue = issues.getJsonObject(i);
            treatIssue(issue);
        }
    }

    /**
     * Treat the given issue.
     * 
     * @param issue
     *            the issue
     * @throws ParseException
     *             if the last commited date cannot be parsed
     */
    private void treatIssue(final JsonObject issue) {
        String issueKey = issue.getString(IssueAPIConstants.KEY.getValue());
        String assignee = issue.getString(IssueAPIConstants.ASSIGNEE.getValue(), null);
        LOG.info("Treating issue : {} - {} - with assignee {} ", issueKey,
                issue.getString("message"), assignee);
        try {
            if (assignee == null) {
                int componentId = issue.getInt(IssueAPIConstants.COMPONENT_ID.getValue());
                int line = 0;
                if (issue.containsKey(IssueAPIConstants.LINE.getValue())) {
                    line = issue.getInt(IssueAPIConstants.LINE.getValue());
                } else {

                    line = resourcesManager.getLastLineCommited(componentId);

                }
                String author = resourcesManager.getLineAuthor(componentId, line);
                assignIssue(issueKey, author);

            }
        } catch (ParseException e) {
            LOG.error("Cannot retrieve the last line commited for issue {}", issueKey, e);
        }

    }

    /**
     * Assign the given issue to the given author.
     * 
     * @param issueKey
     *            the issue
     * @param author
     *            the author
     */
    private void assignIssue(final String issueKey, final String author) {
        LOG.info("Assigning issue {} to {} ", issueKey, author);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(sonarBaseUrl)
                .path(IssueAPIConstants.ASSIGN_PATH.getValue())
                .queryParam(IssueAPIConstants.ISSUE.getValue(), issueKey)
                .queryParam(IssueAPIConstants.ASSIGNEE.getValue(), author);

        Response response = target.request().post(null);
        if (Response.Status.OK.getStatusCode() != response.getStatus()) {
            LOG.error("Cannot assign issue {} to {} because : {}", issueKey, author,
                    response.readEntity(String.class));
        }

    }

}
