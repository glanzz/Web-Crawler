package com.senku.crawler.utils;

public class AppStats {
    private static int robotsFetchFailureCounts;
    private static int totalURLCrawled;
    private static double startTime = System.currentTimeMillis();
    public static int getRobotsFetchFailureCounts() {
        return robotsFetchFailureCounts;
    }
    public static void updateRobotsFetchFailureCounts() {
        robotsFetchFailureCounts ++;
    }

    public static int getTotalURLCrawled() {
        return totalURLCrawled;
    }
    public static void updateTotalURLCrawled() {
        totalURLCrawled++;
    }
    public static void printStats() {
        System.out.println("*********Stats*********");
        System.out.println("Total crawled:" + totalURLCrawled);
        System.out.println("Crawl time m/s:" + (System.currentTimeMillis() - startTime));
        System.out.println("***********************");
    }
}
