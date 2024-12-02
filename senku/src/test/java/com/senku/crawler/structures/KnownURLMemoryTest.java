package com.senku.crawler.structures;

import org.junit.jupiter.api.Test;


public class KnownURLMemoryTest {
    final String testURLS[] = new String[] {
            "https://test1.com",
            "https://test2.us",
            "https://test3.in",
            "https://test4.co"
    };
    public KnownURLMemory getLoadedMemory() {
        KnownURLMemory memory = new KnownURLMemory();
        for(String testURl: testURLS) {
            memory.addAsKnown(testURl);
        }
        return memory;
    }

    @Test
    public void testIsVisited() {
        KnownURLMemory memory = getLoadedMemory();

        for (String url : testURLS) {
            assert memory.isKnown(url);
        }
    }

    @Test
    public void testNotVisited() {
        KnownURLMemory memory = getLoadedMemory();
        assert !memory.isKnown("abcdef");
    }

}
