package com.senku.crawler;

import com.senku.crawler.structures.Page;
import com.senku.crawler.utils.AppStats;

public class Main {
    public static void main(String[] args) {
        Page start = new Page("https://www.northeastern.edu");
        WebCrawl crawler = new WebCrawl(start);
        crawler.crawl();

        /* For Benchmarking */
        //Runtime.getRuntime().addShutdownHook(new Thread(AppStats::printStats));
    }
}
