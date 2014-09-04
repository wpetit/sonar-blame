package com.wpetit.sonar.blame;

/**
 * The {@link ResourcesAPIConstants} class.
 * 
 * @author WPETIT
 * 
 */
public enum ResourcesAPIConstants {
    ROOT_PATH("api/resources"),
    RESOURCE("resource"),
    MSR("msr"),
    DATA("data"),
    METRICS("metrics"),
    AUTHORS_BY_LINE("authors_by_line"),
    LAST_COMMIT_DATETIMES_BY_LINE("last_commit_datetimes_by_line"),
    LINE_CONTENT_SEPARATOR("="),
    LINE_SEPARATOR(";");

    /** The value. */
    private String value;

    /**
     * Constructor.
     * 
     * @param value
     *            the value
     */
    private ResourcesAPIConstants(final String value) {
        this.value = value;
    }

    /**
     * Return the ResourcesAPIConstants value.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
