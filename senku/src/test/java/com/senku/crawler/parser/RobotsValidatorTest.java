package com.senku.crawler.parser;

import com.senku.crawler.BaseTest;
import com.senku.crawler.utils.AppStats;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class RobotsValidatorTest extends BaseTest {
    @Test
    public void testCaching() throws URISyntaxException {
        String Url = "https://google.com/maps";
        String robotsUrl = "https://google.com/robots.txt";
        RobotsValidator.RobotRules rules = new RobotsValidator.RobotRules(null);
        RobotsValidator.robots.put(robotsUrl, rules);
        assertEquals(rules, RobotsValidator.getRules(new URI(Url)));
    }
    @Test
    public void testFetchCount() throws URISyntaxException {
        String Url = "http://example";
        RobotsValidator.RobotRules rules = RobotsValidator.getRules(new URI(Url));
        assertTrue(rules.allowUrl(Url));
        assertEquals(AppStats.getRobotsFetchFailureCounts(), 1);
    }
}
