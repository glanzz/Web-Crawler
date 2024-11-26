package crawler.structures;

import java.util.Date;

public class Page {
    private String url;
    private Date visitedOn;
    private int rank;
    public Page(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
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

    @Override
    public String toString() {
        return "Page [url=" + url + ", visitedOn=" + visitedOn + ", rank=" + rank + "]";
    }
}
