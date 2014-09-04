package com.wpetit.sonar.blame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ResourcesManager} class.
 * 
 * @author WPETIT
 * 
 */
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
        return authorsByLine.get(line);
    }

    /**
     * Fill a map containing the authors by line from the String api format.
     * 
     * @param authorsByLineData
     *            the initial api response
     * @return the map
     */
    private Map<Integer, String> parseAuthorsByLineData(final String authorsByLineData) {
        Map<Integer, String> authorsByLine = new ConcurrentHashMap<Integer, String>();

        String[] lines = authorsByLineData.split(ResourcesAPIConstants.LINE_SEPARATOR.getValue());
        for (String line : lines) {
            String[] lineData = line.split(ResourcesAPIConstants.LINE_CONTENT_SEPARATOR.getValue());
            authorsByLine.put(Integer.parseInt(lineData[0]), lineData[1]);
        }
        return authorsByLine;
    }

    /**
     * Return the last line commited for the given component.
     * 
     * @param componentId
     *            the component id
     * @return the line
     * @throws ParseException
     *             if the line data cannot be parsed
     */
    public int getLastLineCommited(final int componentId) throws ParseException {
        LOG.info("Retrieving last line commited of component : {}", componentId);
        Client client = ClientBuilder.newClient();
        WebTarget target = client
                .target(sonarBaseUrl)
                .path(ResourcesAPIConstants.ROOT_PATH.getValue())
                .queryParam(ResourcesAPIConstants.RESOURCE.getValue(), componentId)
                .queryParam(ResourcesAPIConstants.METRICS.getValue(),
                        ResourcesAPIConstants.LAST_COMMIT_DATETIMES_BY_LINE.getValue());

        JsonArray resources = target.request(MediaType.APPLICATION_JSON).get(JsonArray.class);

        JsonObject resource = resources.getJsonObject(0);

        JsonObject msr = resource.getJsonArray(ResourcesAPIConstants.MSR.getValue()).getJsonObject(
                0);

        return getLastCommitedLineByLineData(msr.getString(ResourcesAPIConstants.DATA.getValue()));
    }

    /**
     * Return the number of the last line commited for the given line data.
     * 
     * @param linesData
     *            the last_commit_datetimes_by_line data
     * @return the last line commited
     * @throws ParseException
     */
    private int getLastCommitedLineByLineData(final String linesData) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss-SSS",
                Locale.ENGLISH);

        String[] lines = linesData.split(ResourcesAPIConstants.LINE_SEPARATOR.getValue());
        int lastCommitedLine = 1;
        Date lastCommitedDate = null;
        for (String line : lines) {
            String[] lineData = line.split(ResourcesAPIConstants.LINE_CONTENT_SEPARATOR.getValue());
            Date lastCommitByLine = simpleDateFormat.parse(lineData[1]);
            if (lastCommitedDate == null || lastCommitByLine.after(lastCommitedDate)) {
                lastCommitedDate = lastCommitByLine;
                lastCommitedLine = Integer.parseInt(lineData[0]);
            }
        }
        return lastCommitedLine;
    }
}
