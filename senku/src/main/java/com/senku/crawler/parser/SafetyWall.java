package com.senku.crawler.parser;
import java.net.*;



public class SafetyWall {
    public static boolean isSafe(String url) {
        if (URLValidator.isValid(url)) {
            URI uri = URI.create(url);
            RobotsValidator.RobotRules rules = RobotsValidator.getRules(uri);
            return rules.allowUrl(url);
        }
        return false;
    }

    public static void main(String[] args) throws URISyntaxException {
        // Test code; Do not run on prod
        boolean result = SafetyWall.isSafe("https://google.com");
        RobotsValidator.robots.forEach((k,v) -> System.out.println(k + ": " + v));
        System.out.println(result);
    }
}
