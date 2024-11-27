package com.senku.crawler.utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppLogger {
    private static final String NAME = "Senku";
    private static final Logger LOGGER = LogManager.getLogger(NAME);

    // API for strict decoupling
    public static Logger getLogger() {
        return LOGGER;
    }
}
