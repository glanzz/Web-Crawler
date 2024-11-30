package com.senku.crawler.parser;

import com.senku.crawler.utils.AppStats;
import com.senku.crawler.utils.AppConstants;
import com.senku.crawler.utils.AppLogger;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RobotsValidator {
    public static class RobotRules {
        BaseRobotRules rules;
        private final boolean exists;// False when rules is null
        public RobotRules(BaseRobotRules rules) {
            this.rules = rules;
            this.exists = rules != null;
        }
        public boolean allowUrl(String url) {
            return !exists || rules.isAllowed(url); // If rules are empty
        }
    }

    static Map<String, RobotRules> robots = new ConcurrentHashMap<>();

    private static String getRobotsUrl(URI url) {
        return "https://" + url.getHost() + (url.getPort() != -1 ? (":" + url.getPort()): "") + "/robots.txt";
    }

    public static RobotRules getRules(URI anyUrl) {
        /**
         * Get Robots rules of any URL to crawl
         */
        String robotsUrl = getRobotsUrl(anyUrl);
        if (!robots.containsKey(robotsUrl)) {
            fetchRobotRules(robotsUrl);
        }
        return robots.get(robotsUrl);
    }

    private static void fetchRobotRules(String robotsUrl) {
        /**
         * Method to fetch and add rules to the cache if not exists
         */
        BaseRobotRules rules;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(robotsUrl).openConnection();
            connection.setRequestProperty("User-Agent", AppConstants.APP_USER_AGENT );
            byte[] robotsTxtContent = connection.getInputStream().readAllBytes();
            SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
            rules = parser.parseContent(robotsUrl, robotsTxtContent, "text/plain", AppConstants.APP_USER_AGENT);

        } catch (Exception e) {
            AppLogger.getLogger().error("Failed to fetch robots file", e);
            AppStats.updateRobotsFetchFailureCounts();
            rules = null;
        }

        RobotRules robotRules = new RobotRules(rules);
        robots.putIfAbsent(robotsUrl, robotRules);
    }
    

}
