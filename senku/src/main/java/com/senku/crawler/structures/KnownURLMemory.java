package com.senku.crawler.structures;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

public class KnownURLMemory {
    private static final int EXPECTED_INSERTIONS = 8000;
    private static final double FALSE_PROBABLITY_THRESHOLD = 0.09;

    private BloomFilter<String> knownUrlSet;

    public KnownURLMemory() {
        Funnel<String> urlFunnel = new Funnel<String>() {
            @Override
            public void funnel(String URL, PrimitiveSink sink) {
                sink.putString(URL, Charsets.UTF_8);
            }
        };

        knownUrlSet = BloomFilter.create(urlFunnel, EXPECTED_INSERTIONS, FALSE_PROBABLITY_THRESHOLD);
        load();
    }

    void load() {
        // Load from the database on initial run to avoid visiting same URLs
    }

    public void addAsKnown(String url) {
        knownUrlSet.put(url);
    }

    public boolean isKnown(String url) {
        //TODO: Switch to check from the neo4j if less than 2 URLs (Critical)
        return knownUrlSet.mightContain(url);
    }

}


