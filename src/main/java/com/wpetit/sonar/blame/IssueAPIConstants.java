package com.wpetit.sonar.blame;

/**
 * The {@link IssueAPIConstants} class.
 * 
 * @author WPETIT
 * 
 */
public enum IssueAPIConstants {

    SEARCH_PATH("/api/issues/search"),
    ASSIGN_PATH("api/issues/assign"),
    ISSUES("issues"),
    ISSUE("issue"),
    KEY("key"),
    ASSIGNEE("assignee"),
    COMPONENT_ID("componentId"),
    LINE("line"),
    COMPONENT_ROOTS("componentRoots"),
    STATUSES("statuses"),
    CREATED_AFTER("createdAfter");

    /** The value. */
    private String value;

    /**
     * Constructor.
     * 
     * @param value
     *            the value
     */
    private IssueAPIConstants(final String value) {
        this.value = value;
    }

    /**
     * Return the IssueAPIConstants value.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
