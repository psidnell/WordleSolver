package com.psidnell.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Profiler {
    private static Map<String, Probe> probes = new HashMap<>();

    private static boolean enabled = false;

    public static void enable(boolean enabled) {
        Profiler.enabled = enabled;
        reset();
    }

    static boolean isEnabled() {
        return enabled;
    }

    public static Probe getProbe(Class clazz, String method, String name) {
        String pName = clazz.getName() + ':' + method + ':' + name;
        return probes.computeIfAbsent(pName, k -> new Probe(pName));
    }

    public static Probe getProbe(Class clazz, String method) {
        String pName = clazz.getName() + ':' + method;
        return probes.computeIfAbsent(pName, k -> new Probe(pName));
    }

    public static void reset() {
        probes.values().forEach(Probe::reset);
    }

    public static void report() {
        if (!enabled) {
            System.out.println("Profiling disabled");
            return;
        }
        System.out.println("Profiling:");
        Set<Probe> probes = new TreeSet<>(Profiler.probes.values());
        probes.forEach(p -> {
            System.out.println(" - " + p.getName() + " count:" + p.getCount() + " time:" + p.getDuration() + "ms");
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
