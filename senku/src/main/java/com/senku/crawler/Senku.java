package com.senku.crawler;
import com.senku.crawler.parser.Parser;
import com.senku.crawler.structures.Page;
import com.senku.crawler.utils.AppLogger;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;



public class Senku {
    private static final int MAXIMUM_CONNECTIONS = 100;
    private static final int MAXIMUM_CONNECTIONS_PER_ROUTE = 20;
    private static final int CONNECTION_TIME_OUT = 5000;
    private PoolingHttpClientConnectionManager connectionPool;
    private CloseableHttpClient httpClient;

    private void initializeConnectionPool() {
        connectionPool = new PoolingHttpClientConnectionManager();
        connectionPool.setDefaultMaxPerRoute(MAXIMUM_CONNECTIONS_PER_ROUTE);
        connectionPool.setMaxTotal(MAXIMUM_CONNECTIONS);
    }

    private void initializeHttpClient() {
        /*
         * Initalizes the HTTP client with pooling to reduce latency
         */
        
        // Configure the connection pool manager for the client
        initializeConnectionPool();

        RequestConfig httpClientConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIME_OUT)
                .setSocketTimeout(CONNECTION_TIME_OUT)
                .build();
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionPool)
                .setDefaultRequestConfig(httpClientConfig)
                .build();
    }

    public Senku() {
        initializeHttpClient();
    }

    public void start() {
        AppLogger.getLogger().info("Starting to parse...");
        Parser parser = new Parser(httpClient);
        Page page = new Page("https://northeastern.edu");

        System.out.println(page);
        try {
            parser.parsePage(page);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse page");
        }

        System.out.println(page);
        page.getChildren().forEach(child -> System.out.println(child.getUrlString()));
        AppLogger.getLogger().info("Parsing complete.");
    }


    public static void main(String[] args) {
        Senku senku = new Senku();
        senku.start();
    }


}
