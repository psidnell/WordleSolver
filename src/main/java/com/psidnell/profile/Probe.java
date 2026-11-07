package com.psidnell.profile;

import java.io.Closeable;

public class Probe implements Closeable {
    private final String name;
    private final long start;
    private long duration;

    public Probe(Class clazz, String method) {
        this.name = clazz.getName() + ':' + method;
        this.start = System.currentTimeMillis();
    }

    public Probe(Class clazz, String method, String name) {
        this.name = clazz.getName() + ':' + method + ':' + name;
        this.start = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public void close() {
        long end = System.currentTimeMillis();
        duration = end - start;
        Profiler.accumulate(this);
    }
}
