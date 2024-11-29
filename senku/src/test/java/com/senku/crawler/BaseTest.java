package com.senku.crawler;


import com.senku.crawler.utils.AppLogger;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;

import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.mockStatic;
// import org.mockito.MockedStatic;
// import org.apache.logging.log4j.LogManager;

public class BaseTest {

    @BeforeAll
    public static void  setup() {
        Logger mockLogger = mock(Logger.class);
        //MockedStatic<AppLogger> mockedAppLogger = mockStatic(AppLogger.class);
        AppLogger.LOGGER = mockLogger;
        //mockedAppLogger.when(AppLogger::getLogger).thenReturn(mockLogger);
    }
}

