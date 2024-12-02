package com.senku.crawler.db;
import com.senku.crawler.structures.Page;
import com.senku.crawler.utils.AppLogger;
import org.neo4j.driver.*;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Map;

public class Neo4jRepository {
    public static final String URI;
    public static final String USERNAME;
    public static final String PASSWORD;

    static {
        Properties properties = new Properties();
        try (InputStream input = Neo4jRepository.class.getClassLoader().getResourceAsStream("neo4j.properties")) {
            if (input == null) {
                throw new RuntimeException("Configuration file 'neo4j.properties' not found in resources.");
            }
            properties.load(input);
        } catch (Exception e) {
            AppLogger.getLogger().error(e.getMessage());
            throw new RuntimeException("Failed to load configuration file.", e);

        }

        // Initialize static properties
        URI = properties.getProperty("neo4j.uri");
        USERNAME = properties.getProperty("neo4j.username");
        PASSWORD = properties.getProperty("neo4j.password");
    }

    private static final Driver driver = GraphDatabase.driver(URI, AuthTokens.basic(USERNAME, PASSWORD));


    public static void addPage(Page page) throws URISyntaxException {
        String query = """
            MERGE (main:Page {url: $mainUrl})
            ON CREATE SET main.host = $mainHost, main.visited_at = $mainVisited, main.has_robots = $mainHasRobots, main.modified_at = $mainModifiedAt
            ON MATCH SET main.host = COALESCE(main.host, $mainHost), main.visited_at = COALESCE(main.visited_at, $mainVisited), main.has_robots = COALESCE(main.has_robots, $mainHasRobots), main.modified_at = COALESCE(main.modified_at, $mainModifiedAt)
            WITH main
            UNWIND $children AS child
            MERGE (sub:Page {url: child.url})
            ON CREATE SET sub.host = child.host, sub.has_robots = child.has_robots
            MERGE (main)-[rel:REFERS]->(sub)
            ON CREATE SET rel.times = child.times;
            """;

//        System.out.println("NEO::::********************************************************");
//        System.out.println(page.getUrlString());
//        System.out.println("mainHost"+page.getURI().getHost());
//        System.out.println("mainHasRobots"+page.getHasRobots());
//                        System.out.println("mainVisited"+""+page.getVisitedOn());
//                        System.out.println("mainModifiedAt"+page.getModifiedAt());
//                page.getChildren().stream().forEach(child -> {
//                            System.out.println("Child Data:");
//                            System.out.println("url" + child.getUrlString());
//                    try {
//                        System.out.println("host" + child.getURI().getHost());
//                    } catch (URISyntaxException e) {
//                        System.out.println("host is not a valid URI for this child");
//                    }
//                    System.out.println("has_robots" + child.getHasRobots());
//                            System.out.println("times" + child.getTotalRefers());
//                        });
//        System.out.println("********************************************************");

        // Prepare parameters
        Map<String, Object> parameters = Map.of(
                "mainUrl", page.getUrlString(),
                "mainHost", page.getURI().getHost(),
                "mainHasRobots", page.getHasRobots(),
                "mainVisited", ""+page.getVisitedOn(),
                "mainModifiedAt", page.getModifiedAt(),
                "children", page.getChildren().stream().map(child -> {
                    try {
                        return Map.of(
                                "url", child.getUrlString(),
                                "host", child.getURI().getHost() == null ? page.getURI().getHost(): child.getURI().getHost(),
                                "has_robots", child.getHasRobots(),
                                "times", child.getTotalRefers()
                        );
                    } catch (URISyntaxException e) {
                        // Ignore never happens
                        AppLogger.getLogger().error(e);
                    }
                    return Map.of();
                }).toList()
        );

        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            session.executeWrite(tx -> tx.run(query, parameters).consume());
        }
    }






    // Close the driver (to be called during application shutdown)
    public void close() {
        driver.close();
    }

}