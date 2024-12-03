package com.senku.crawler;

import com.senku.crawler.db.Neo4jRepository;
import com.senku.crawler.parser.Parser;
import com.senku.crawler.parser.SafetyWall;
import com.senku.crawler.structures.Page;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class SortbyRank implements Comparator<Page> {
    // Sorting in ascending order of rank
    public int compare(Page a, Page b)
    {
        return b.getRank() - a.getRank();
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
    private int testDepth;
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
        this.testDepth = 0;
    }

    public void crawl() {
        for(Object p:priorityQueue.toArray()){
            Page curr = (Page)p;
            System.out.println(curr.getUrlString() + ": " + curr.getRank());
        }

        Page page = priorityQueue.poll();
        System.out.println(page);
        while (page == null || !SafetyWall.isSafe(page.getUrlString())) { //|| visited.contains(page)
            if (page == null) {
                System.out.println("Queue is empty");
                return;
            }
            System.out.println("Skipped: " + page.getUrlString());
            page = priorityQueue.poll();
        }
        testDepth++;
        crawl(page, testDepth);

    }

    public void crawl(Page page, int depth) {
        if (depth > MAX_DEPTH) {
            return;
        }

        CompletableFuture.supplyAsync(() -> page, executor)
                .thenAccept(checkedPage -> {
                    if (checkedPage != null) {
                        System.out.println("Crawled: " + checkedPage.getUrlString());
                        List<Page> children = getChildren(checkedPage);
                        for (Page link : children) {//getChildren(checkedPage)) {
                            if (!link.wasKnown()) {
                                priorityQueue.add(link);
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
        try {
            Neo4jRepository.addPage(page);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to add to Neo4j");
        }
        return page.getChildren();
    }

}
