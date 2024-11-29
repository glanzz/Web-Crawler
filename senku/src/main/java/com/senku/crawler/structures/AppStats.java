package com.senku.crawler.structures;

public class AppStats {
    private static int robotsFetchFailureCounts;
    public static int getRobotsFetchFailureCounts() {
        return robotsFetchFailureCounts;
    }
    public static void updateRobotsFetchFailureCounts() {
        robotsFetchFailureCounts ++;
    }
}
