package com.senku.crawler.parser;

import com.senku.crawler.BaseTest;
import org.junit.jupiter.api.Test;


public class URLValidatorTest extends BaseTest {
    @Test
    public void testValidHTTPURL() {
        String validUrl = "http://sample.com";
        boolean result = URLValidator.isValid(validUrl);
        assert result;
    }
    @Test
    public void testValidHTTPSURL() {
        String validUrl = "https://sample.com";
        boolean result = URLValidator.isValid(validUrl);
        assert result;
    }
    @Test
    public void testRejectURLInvalidScheme() {
        String validUrl = "http://sample.com";
        validUrl = "ahjbsdhsa" + validUrl;
        boolean result = URLValidator.isValid(validUrl);
        assert !result;
    }
    @Test
    public void testRejectMalformedURL() {
        String invalidUrl = "http://sample.com//%//";
        boolean result = URLValidator.isValid(invalidUrl);
        assert !result;
        assert !URLValidator.isValid("http://.com?");
        assert !URLValidator.isValid("http://example");
        assert !URLValidator.isValid("http://");

    }
    @Test
    public void testRejectFileURL() {
        String validUrl = "http://sample.com/abc.pdf";
        assert !URLValidator.isValid(validUrl);
        validUrl = "http://sample.com/abc.png";
        assert !URLValidator.isValid(validUrl);
        validUrl = "https://sample.com/abc.js";
        assert !URLValidator.isValid(validUrl);
        validUrl = "http://sample.com/abc.json";
        assert !URLValidator.isValid(validUrl);
    }
}
