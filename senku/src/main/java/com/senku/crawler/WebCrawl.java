package com.senku.crawler;

import com.senku.crawler.parser.Parser;
import com.senku.crawler.parser.SafetyWall;
import com.senku.crawler.structures.Page;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SortbyRank implements Comparator<Page> {
    // Sorting in ascending order of rank
    public int compare(Page a, Page b)
    {
        return a.getRank() - b.getRank();
    }
}

public class WebCrawl {
    Parser parser;
    private static final int MAXIMUM_CONNECTIONS = 100;
    private static final int MAXIMUM_CONNECTIONS_PER_ROUTE = 20;
    private static final int CONNECTION_TIME_OUT = 5000;
    private PoolingHttpClientConnectionManager connectionPool;
    private CloseableHttpClient httpClient;
    private final ExecutorService executor;
    private static final int NUM_THREADS = 8;
    private static final int MAX_DEPTH = 5;
    private final Set<String> visited = new HashSet<>();
    private final PriorityQueue<Page> priorityQueue;

    private void initializeConnectionPool() {
        connectionPool = new PoolingHttpClientConnectionManager();
        connectionPool.setDefaultMaxPerRoute(MAXIMUM_CONNECTIONS_PER_ROUTE);
        connectionPool.setMaxTotal(MAXIMUM_CONNECTIONS);
    }

    private void initializeHttpClient() {
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

    public WebCrawl(Page startPage) {
        this.initializeHttpClient();
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
        this.priorityQueue = new PriorityQueue(20, new SortbyRank());
        this.priorityQueue.add(startPage);
        this.parser = new Parser(httpClient);
    }

    public void crawl() {
        Page page = priorityQueue.poll();
        while (page == null || visited.contains(page) || !SafetyWall.isSafe(page.getUrlString())) {
            System.out.println("Skipped: " + page.getUrlString());
            page = priorityQueue.poll();
        }
        crawl(page, 0);

    }

    public void crawl(Page page, int depth) {
        if (depth > MAX_DEPTH || visited.contains(page.getUrlString())) {
            return;
        }
        visited.add(page.getUrlString());

        CompletableFuture.supplyAsync(() -> page, executor)
                .thenAccept(checkedPage -> {
                    if (checkedPage != null) {
                        System.out.println("Crawled: " + checkedPage.getUrlString());
                        for (Page link : getChildren(checkedPage)) {
                            if (!visited.contains(link.getUrlString())) {
                                priorityQueue.add(link);
                                //crawl(link, depth + 1);
                            }
                        }
                    }
                });
        crawl();
    }


    private List<Page> getChildren(Page page) {
        try {
            this.parser.parsePage(page);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse page");
        }
        return page.getChildren();
    }

}
