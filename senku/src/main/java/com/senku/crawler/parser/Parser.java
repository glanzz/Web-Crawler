package com.senku.crawler.parser;

import com.senku.crawler.utils.AppLogger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import com.senku.crawler.structures.Page;

public class Parser {
    CloseableHttpClient httpClient;
    public Parser(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
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

    public List<String> extractLinks(String htmlContent, String baseUrl) {
        Source source = new Source(htmlContent);
        List<StartTag> anchorTags = source.getAllStartTags("a");
        List<String> links = new ArrayList<>();

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
                            fullUrl = new URI(href).toURL().toString();
                        }  catch (MalformedURLException e) {
                            AppLogger.getLogger().info("Invalid URL found: Base URL", baseUrl, ", HREF:", href);
                            continue;
                        }
                    }
                    links.add(fullUrl);
                } catch (Exception e) {
                    AppLogger.getLogger().info("Failed to extract links from " + href);
                }
            }
        }
        return links;
    }

    public void parsePage(Page page) throws Exception {
        /*
         Check robots.txt file and cache it
         If allowed parse the content from the connection pool
         */
        page.updateStatus(Page.STATUS.PROCESSING);

        URI url = page.getURI();
        String pageContent = this.fetchContents(url);
        List<String> links = this.extractLinks(pageContent, page.getUrlString());
        links.forEach(link -> {
            Page child = new Page(link);
            page.addChild(child);
        });

        page.setVisitedOn(new Date());
        page.updateStatus(Page.STATUS.COMPLETED);
    }
}
