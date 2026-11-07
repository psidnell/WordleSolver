package com.psidnell.profile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Profiler {

    private static Map<String, Long> times = new TreeMap<>();
    private static Map<String, Long> counts = new TreeMap<>();

    public static void accumulate(Probe t) {
        long timeSoFar = times.containsKey(t.getName()) ? times.get(t.getName()) : 0;
        times.put(t.getName(), timeSoFar + t.getDuration());

        long countsSoFar = counts.containsKey(t.getName()) ? counts.get(t.getName()) : 0;
        counts.put(t.getName(), countsSoFar +1);
    }

    public static void reset() {
        times = new HashMap<>();
    }

    public static void report() {
        System.out.println("Profiling:");
        Collection<String> keys = times.keySet();
        keys.forEach(name -> {
            System.out.println(" - " + name + " count:" + counts.get(name) + " time:" + times.get(name) + "ms");
        });
    }
}
