package com.senku.crawler.structures;
import com.senku.crawler.BaseTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PageTest extends BaseTest {
    static final String URL = "https://sampletest.url";
    @Test
    void defaultPageProperties() {
        Page page = new Page(URL);
        assertEquals(page.getStatus(), Page.STATUS.PENDING);
        assertEquals(page.getUrlString(), URL);
        assertNull(page.getVisitedOn());
    }

    @Test
    void testStatusChange() throws Exception {
        Page page = new Page(URL);

        // Page should not allow updation to completed
        assertThrows(Exception.class, () -> {
            page.updateStatus(Page.STATUS.COMPLETED);
        });

        // Page should not throw exception or change when passed the same status
        Page.STATUS status = page.getStatus();
        page.updateStatus(status);
        assertEquals(page.getStatus(), status);

        page.updateStatus(Page.STATUS.PROCESSING);

        // Page should not allow changing status back
        assertThrows(Exception.class, () -> {
            page.updateStatus(Page.STATUS.PENDING);
        });

        // Page should change the status to completed
        Page.STATUS completedStatus = Page.STATUS.COMPLETED;
        page.updateStatus(completedStatus);
        assertEquals(page.getStatus(), completedStatus);
    }
}
