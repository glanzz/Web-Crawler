package com.senku.crawler.structures;

import com.senku.crawler.utils.AppLogger;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.net.URI;


public class Page {
    public enum STATUS {
        PENDING, PROCESSING, COMPLETED
    }

    private String url;
    private URI urlObj = null;
    private Date visitedOn;
    private int rank; // Number of unexplored children
    private int totEdges; // Number of children
    private final List<Page> children = new ArrayList<>();
    private STATUS status;

    public Page(String url) {
        this.status = STATUS.PENDING;
        this.setUrl(url);
        this.totEdges = 0;
    }

    private void setUrl(String url) {
        /*Private method as URL is unique and should only be set when object is initialized*/
        this.url = url;
    }
    public URI getURI() throws URISyntaxException {
        if (this.urlObj == null)  this.urlObj = new URI(url); // Lazy loading
        return this.urlObj;
    }


    public String getUrlString() {
        return url;
    }
    public Date getVisitedOn() {
        return visitedOn;
    }
    public void setVisitedOn(Date visitedOn) {
        this.visitedOn = visitedOn;
    }
    public int getRank() {
        return this.children.size();
    }
    public STATUS getStatus() {
        return status;
    }
    public List<Page> getChildren() {
        return this.children;
    }

    public int getTotEdges() {
        return this.totEdges;
    }

    public void addChild(Page child) {
        this.totEdges++;
        this.children.add(child);
    }

    public void updateStatus(STATUS status) throws Exception {
        if (this.status == status) return;

        if (
                (status == STATUS.PROCESSING && this.status == STATUS.PENDING) ||
                        (status == STATUS.COMPLETED &&  this.status == STATUS.PROCESSING)
        ) {
            this.status = status;
            return;
        }

        AppLogger.getLogger().error("Invalid status update:", status, "from", this.status, ":",this.url);
        throw new Exception("Invalid Status");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Page) {
            Page p = (Page) obj;
            return this.url.equals(p.url);
        }
        return false;
    }


    @Override
    public String toString() {
        return "Page [url=" + url + ", visitedOn=" + visitedOn + ", rank=" + rank + ", status=" + status + "]";
    }

}
