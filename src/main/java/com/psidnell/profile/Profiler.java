package com.psidnell.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Profiler {
    private static Map<String, Record> records = new HashMap<>();

    public static void accumulate(Probe p) {
        Record r = records.computeIfAbsent(p.getName(), k -> new Record(p.getName()));
        r.duration += p.getDuration();
        r.count++;
    }

    public static void reset() {
        records = new HashMap<>();
    }

    public static void report() {
        System.out.println("Profiling:");
        Set<Record> records = new TreeSet<>(Profiler.records.values());
        records.forEach(r -> {
            System.out.println(" - " + r.name + " count:" + r.count + " time:" + r.duration + "ms");
        });
    }

    private static class Record implements Comparable<Record> {
        private String name;
        private long duration = 0;
        private long count = 0;

        public Record(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(Record o) {
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
            if (!(o instanceof Record record)) return false;
            return duration == record.duration && count == record.count && Objects.equals(name, record.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, duration, count);
        }
    }
}
