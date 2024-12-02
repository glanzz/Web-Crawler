package com.senku.crawler.parser;

import com.senku.crawler.utils.AppLogger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

public class URLValidator {
    private static boolean isHyperTextUrl(String url) {
        String lowercaseUrl = url.toLowerCase();
        return lowercaseUrl.startsWith("http://") || lowercaseUrl.startsWith("https://");
    }
    private static boolean isFile(String url) {
        String validFileExtensions = ".*\\.(png|jpg|jpeg|gif|pdf|txt|doc|docx|css|js|mp4|json|xml|zip|tar\\.gz)$";
        Pattern pattern = Pattern.compile(validFileExtensions, Pattern.CASE_INSENSITIVE);

        return pattern.matcher(url).matches();
    }
    private static boolean isValidUrl(String url) {
        try {
            URI.create(url).toURL();
            String[] schemes = {"http","https"};
            org.apache.commons.validator.routines.UrlValidator urlValidator = new org.apache.commons.validator.routines.UrlValidator(schemes);
            return urlValidator.isValid(url);
        } catch (Exception e) {
            AppLogger.getLogger().error("Malformed URL", e);
            return false;
        }
    }

    public static boolean isValid(String url) {
        /**
         * Checks for valid URL allows only:
         * i) HTTP/HTTPS ii) Non files URLs
         *
         * Note: Add more validators as private functions above
         */
        return isValidUrl(url) && isHyperTextUrl(url) && !isFile(url);
    }


}
