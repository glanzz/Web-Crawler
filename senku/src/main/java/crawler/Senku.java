package crawler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

import java.util.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;



public class Senku {
    private static final int MAXIMUM_CONNECTIONS = 100;
    private static final int MAXIMUM_CONNECTIONS_PER_ROUTE = 20;
    private static final int CONNECTION_TIME_OUT = 5000;
    private final PoolingHttpClientConnectionManager connectionPool;
    private final CloseableHttpClient httpClient;

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
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionPool)
                .setDefaultRequestConfig(httpClientConfig)
                .build();
    }

    public Senku() {
        initializeHttpClient();
    }


    public static void main(String[] args) {
        Senku senku = new Senku();

    }


}
