package com.wpetit.sonar.blame;


/**
 * The {@link SonarBlameLauncher} class.
 * 
 * @author WPETIT
 * 
 */
public final class SonarBlameLauncher {

    /**
     * Private Constructor cause it's an utility class.
     */
    private SonarBlameLauncher() {
        // does nothing
    }

    /**
     * Launch the sonar blame application.
     * 
     * @param args
     *            args[0] : sonar base URL, args[1] : optional component to search issue on, args[2]
     *            the creation date of issues to search
     */
    public static void main(final String... args) {
        IssuesManager i = new IssuesManager(args[0], args[1]);
        i.getIssuesCreatedAfter(args[2]);
    }

}
