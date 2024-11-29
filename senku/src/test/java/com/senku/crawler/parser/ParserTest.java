package com.senku.crawler.parser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.senku.crawler.structures.Page;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import com.senku.crawler.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class ParserTest extends  BaseTest {
    public static final String TEST_URL = "http://testsample.site";
    public static final String[] CONTENT_URLS = new String[]{"http://testinsidesample.site"};
    public static final String HTML_CONTENTS = "<html><body><a href='" + CONTENT_URLS[0] + "'>New</a></body></html>";

    public Parser getMockedParser() {
        CloseableHttpClient httpClient = Mockito.mock(CloseableHttpClient.class);
        Parser parser = new Parser(httpClient);
        return Mockito.spy(parser);
    }

    public Parser getMockedParserWithHTML() throws IOException {
        Parser parser = getMockedParser();
        doReturn(HTML_CONTENTS).when(parser).fetchContents(any(URI.class));
        return parser;
    }
    @Test
    public void testParseShouldUpdateStatus() throws Exception {
        Page page = new Page(TEST_URL);
        Parser parser = getMockedParserWithHTML();
        parser.parsePage(page);
        assertEquals(page.getStatus(), Page.STATUS.COMPLETED);
    }
    @Test
    public void testParseShouldSetVisited() throws Exception {
        Page page = new Page(TEST_URL);
        Parser parser = getMockedParserWithHTML();
        parser.parsePage(page);
        assertNotNull(page.getVisitedOn());
    }

    @Test
    public void testParseLinks() throws Exception {
        Page page = new Page(TEST_URL);
        Parser parser = getMockedParserWithHTML();
        parser.parsePage(page);
        assertEquals(page.getChildren().size(), CONTENT_URLS.length);
        page.getChildren().forEach(child -> assertTrue(Arrays.asList(CONTENT_URLS).contains(child.getUrlString())));
    }
}
