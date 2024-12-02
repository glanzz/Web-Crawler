package com.senku.crawler.parser;


import com.senku.crawler.structures.KnownURLMemory;
import com.senku.crawler.utils.AppLogger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import com.senku.crawler.structures.Page;

public class Parser {
    CloseableHttpClient httpClient;
    ReentrantLock lookupLock;
    KnownURLMemory memory;
    public Parser(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.lookupLock = new ReentrantLock();
        memory = new KnownURLMemory();
    }

    private static String standardUrl(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        url = url.toLowerCase();
        return url;
    }

    String fetchContents(URI url) throws IOException {
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new IOException("Failed to fetch contents: " + url + ", Status: " + response.getStatusLine());
            }
        }
    }

    public Map<String, Integer> extractLinks(String htmlContent, String baseUrl) {
        Source source = new Source(htmlContent);
        List<StartTag> anchorTags = source.getAllStartTags("a");
        HashMap<String, Integer> links = new HashMap<>();

        for (StartTag tag : anchorTags) {
            String href = tag.getAttributeValue("href");
            if (href != null) {
                try {
                    String fullUrl;
                    if (href.startsWith("/")) {
                        URI baseURI = new URI(baseUrl);
                        URI relativeURI = new URI(href);
                        fullUrl = baseURI.resolve(relativeURI).toString();
                    } else {
                        try {
                            fullUrl = new URI(href).normalize().toURL().toString();
                        }  catch (Exception e) {
                            AppLogger.getLogger().info("Invalid URL found: Base URL", baseUrl, ", HREF:", href);
                            continue;
                        }
                    }
                    // Track number of links to the new URL
                    String newUrl = standardUrl(fullUrl);
                    if(links.containsKey(newUrl)) {
                        links.put(newUrl, links.get(newUrl) + 1);
                    } else {
                        links.put(newUrl, 1);
                    }
                } catch (Exception e) {
                    AppLogger.getLogger().info("Failed to extract links from " + href);
                }
            }
        }
        return links;
    }

    private void populateUnseenLinks(Map<String, Integer> links, Page page) {
            lookupLock.lock();
                System.out.println(links.size());
                try {
                    links.forEach((link, count) -> {
                        Page child = new Page(link);
                        if (!memory.isKnown(link)) {
                            memory.addAsKnown(link);
                        } else {
                            child.setWasKnown(true);
                        }
                        child.setTotalRefers(count);
                        page.addChild(child);
                    });
                }
                catch (Exception e) {
                    AppLogger.getLogger().error("Interrupted", e);
                }
                finally {
                    lookupLock.unlock();
                }
    }

    public void parsePage(Page page) throws Exception {
        /**
         * Check robots.txt file and cache it
         * If allowed parse the content from the connection pool
         */
        page.updateStatus(Page.STATUS.PROCESSING);

        URI url = page.getURI();
        String pageContent = this.fetchContents(url);
        Map<String, Integer> links = this.extractLinks(pageContent, page.getUrlString());
        populateUnseenLinks(links, page);
        page.setVisitedOn(new Date());
        page.updateStatus(Page.STATUS.COMPLETED);
    }
}
