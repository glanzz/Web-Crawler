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
    private int rank;
    private List<Page> children = new ArrayList<>();
    private STATUS status;

    public Page(String url) {
        this.status = STATUS.PENDING;
        this.setUrl(url);
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
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public STATUS getStatus() {
        return status;
    }
    public List<Page> getChildren() {
        return this.children;
    }
    public void addChild(Page child) {
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
    public String toString() {
        return "Page [url=" + url + ", visitedOn=" + visitedOn + ", rank=" + rank + ", status=" + status + "]";
    }
}
