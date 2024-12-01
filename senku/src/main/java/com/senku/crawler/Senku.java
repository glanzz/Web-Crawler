package com.senku.crawler;
import com.senku.crawler.parser.Parser;
import com.senku.crawler.structures.Page;
import com.senku.crawler.utils.AppLogger;
import com.senku.crawler.structures.AppStats;
//import org.apache.commons.collections.PriorityQueue;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.Queue;
import java.util.LinkedList;


class SortbyRank implements Comparator<Page> {
    // Sorting in ascending order of rank
    public int compare(Page a, Page b)
    {
        return a.getRank() - b.getRank();
    }
}


public class Senku {
    private static final int MAXIMUM_CONNECTIONS = 100;
    private static final int MAXIMUM_CONNECTIONS_PER_ROUTE = 20;
    private static final int CONNECTION_TIME_OUT = 5000;
    private PoolingHttpClientConnectionManager connectionPool;
    private CloseableHttpClient httpClient;
    private static final int NUM_THREADS = 8;
    private static ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
    Parser parser;
    PriorityQueue<Page> priorityQueue;

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
        priorityQueue = new PriorityQueue(20, new SortbyRank());
    }


    // Method to initiate crawling in parallel
    private CompletableFuture<Void> crawlInParallel() {
        CompletableFuture<Void> allCrawlTasks = CompletableFuture.allOf();

        // Submit crawl tasks to executor as long as there are URLs in the queue
        for(int i = 0; i < 6; i++) {
            CompletableFuture<Void> crawlTask = CompletableFuture.runAsync(() -> {
                for(int j = 0; j < 200; j++) {
                    System.out.println("\n\n\n\n\nWokeup... " + Thread.currentThread().getName());
                    while(!priorityQueue.isEmpty()) {
                        Page page = priorityQueue.poll();
                        crawl(page);
                        priorityQueue.add(page);
                    }
                    /*try {
                        Thread.sleep(600);
                        System.out.println("\n\n\n\n\nSLEEPING... " + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        System.out.println("Sleep interrupted for " + Thread.currentThread().getName());
                    }*/
                }
            }, executor);

            // Combine the current task into the overall CompletableFuture
            allCrawlTasks = allCrawlTasks.thenCombine(crawlTask, (a, b) -> null);
        }

        return allCrawlTasks;
    }

    // Simulate the crawling of a URL
    private void crawl(Page page) {
        System.out.println("Crawling: " + page.getUrlString() + " | Thread: " + Thread.currentThread().getName());
        System.out.println(page);
        try {
            this.parser.parsePage(page);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse page");
        }

        System.out.println(page);
        page.getChildren().forEach(child -> {
            System.out.println(child.getUrlString());
            priorityQueue.add(child);
            //AppStats.updateTotalURLCrawled();
        });

        AppLogger.getLogger().info("Parsing complete.");
        System.out.println("Finished crawling: " + page.getUrlString());
    }

    // Method to shut down the executor service
    private static void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        //AppStats.printStats();
    }
    public void start() {
        // Test code remove later
        Thread mainThread = Thread.currentThread();
        //Runtime.getRuntime().addShutdownHook(new Thread(AppStats::printStats));
        try {
            AppLogger.getLogger().info("Starting to parse...");
            this.parser = new Parser(httpClient);
            priorityQueue.add(new Page("https://northeastern.edu"));

            // Start the crawling tasks
            CompletableFuture<Void> crawlTasks = crawlInParallel();//urlQueue);

            // Wait for all tasks to complete
            crawlTasks.join();

            // Shutdown the executor service
            shutdownExecutor();
        } catch (Exception e) {
            System.out.println("Failed to parse page");
        } finally {
            //AppStats.printStats();
        }
    }


    public static void main(String[] args) throws UnknownHostException {
        Senku senku = new Senku();
        senku.start();
//        String[] URLS = new String[]{
//                "https://www.instagram.com/northeastern",
//                "https://instagram.com/northeastern/",
//                "https://library.northeastern.edu",
//                "https://www.northeastern.edu/emergency-information",
//                "https://northeastern.edu/emergency-information"};
//
//        for(String URL : URLS) {
//            String hostname = URI.create(URL).getHost();
//
//            InetAddress addresses = InetAddress.getByName(hostname);
//            System.out.println(URL);
//            System.out.println(addresses);
//        }
    }


}
