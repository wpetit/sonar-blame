package com.wpetit.sonar.blame;

public class SonarBlameLauncher {
    /**
     * Launch the sonar blame application
     * 
     * @param args
     */
    public static void main(final String[] args) {
        IssuesManager i = new IssuesManager(args[0], args[1]);
        i.getIssuesCreatedAfter(args[2]);
    }
}
