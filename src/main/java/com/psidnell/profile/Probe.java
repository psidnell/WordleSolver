package com.psidnell.profile;

import java.io.Closeable;
import java.util.Objects;

public class Probe implements Closeable, Comparable<Probe> {
    private final String name;
    private long start;
    public long count = 0;
    private long duration = 0;

    Probe(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public Probe open() {
        if (Profiler.isEnabled()) {
            this.start = System.currentTimeMillis();
        }
        return this;
    }

    @Override
    public void close() {
        if (Profiler.isEnabled()) {
            count++;
            long end = System.currentTimeMillis();
            long currentDuration = end - start;
            duration += currentDuration;
            start = 0;
        }
    }

    @Override
    public int compareTo(Probe o) {
        int result = Long.compare(o.duration, duration);
        if (result != 0) {
            return result;
        }

        result = Long.compare(o.count, count);
        if (result != 0) {
            return result;
        }

        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Probe record)) return false;
        return duration == record.duration && count == record.count && Objects.equals(name, record.name);
    }

    public long getCount() {
        return duration;
    }

    public void reset() {
        count = 0;
        duration = 0;
    }
}
