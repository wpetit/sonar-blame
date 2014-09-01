package com.wpetit.sonar.blame;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcesManager {

    /** The LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesManager.class);

    /** The sonarBaseUrl. */
    private final String sonarBaseUrl;

    /**
     * Constructor.
     * 
     * @param sonarBaseUrl
     *            the sonar base url
     */
    public ResourcesManager(final String sonarBaseUrl) {
        this.sonarBaseUrl = sonarBaseUrl;
    }

    /**
     * Return the author of the line and component given.
     * 
     * @param componentId
     *            the component
     * @param line
     *            the line
     * @return the author
     */
    public String getLineAuthor(final int componentId, final int line) {
        LOG.info("Retrieving author for line {} of component : {}", line, componentId);
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target(sonarBaseUrl)
                .path(ResourcesAPIConstants.ROOT_PATH.getValue())
                .queryParam(ResourcesAPIConstants.RESOURCE.getValue(), componentId)
                .queryParam(ResourcesAPIConstants.METRICS.getValue(),
                        ResourcesAPIConstants.AUTHORS_BY_LINE.getValue());
        JsonArray resources = target.request(MediaType.APPLICATION_JSON).get(JsonArray.class);

        JsonObject resource = resources.getJsonObject(0);

        JsonObject msr = resource.getJsonArray(ResourcesAPIConstants.MSR.getValue()).getJsonObject(
                0);
        Map<Integer, String> authorsByLine = parseAuthorsByLineData(msr
                .getString(ResourcesAPIConstants.DATA.getValue()));
        String author = authorsByLine.get(line);
        return author;
    }

    /**
     * Fill a map containing the authors by line from the String api format.
     * 
     * @param authorsByLineData
     *            the initial api response
     * @return the map
     */
    private Map<Integer, String> parseAuthorsByLineData(final String authorsByLineData) {
        Map<Integer, String> authorsByLine = new HashMap<Integer, String>();

        String[] lines = authorsByLineData.split(ResourcesAPIConstants.LINE_SEPARATOR.getValue());
        for (String line : lines) {
            String[] lineData = line.split(ResourcesAPIConstants.LINE_BY_AUTHOR_SEPARATOR
                    .getValue());
            authorsByLine.put(Integer.parseInt(lineData[0]), lineData[1]);
        }
        return authorsByLine;
    }
}
