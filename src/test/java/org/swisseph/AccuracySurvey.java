/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-11
 */
package org.swisseph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Measures, without a tolerance, how far the pure-Java engine is from the native one, and writes
 * the result to {@code target/accuracy/<name>.tsv}: one row per key with the number of compared
 * values, the largest absolute difference and where it was found, and the number of calls whose
 * return value or error text differed.
 * <p>
 * The survey classes run their calls in a thread of their own: the native state (ephemeris path,
 * open files, topocentre) is thread-local in C, so a fresh thread starts from a clean state and
 * leaves the thread-local engines of {@link AbstractTest} alone.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-11
 */
final class AccuracySurvey {

    /** {@code $SWISSEPH_HOME/ephe}: the whole -13000 .. +17000 set of the reference library, or null */
    static final String REF_EPHE = refEphe();

    static final class Row {
        long n, retDiff, serrDiff;
        double max;
        String where = "";
    }

    private final String name;
    private final Map<String, Row> rows = new TreeMap<>();

    AccuracySurvey(String name) {
        this.name = name;
    }

    private static String refEphe() {
        final String home = System.getenv("SWISSEPH_HOME");
        if (null == home) return null;
        final File ephe = new File(home, "ephe");
        return new File(ephe, "sepl_18.se1").isFile() ? ephe.getAbsolutePath() : null;
    }

    Row row(String key) {
        return rows.computeIfAbsent(key, k -> new Row());
    }

    /** one compared value */
    void diff(String key, double d, Supplier<String> where) {
        final Row r = row(key);
        r.n++;
        if (Double.isNaN(d)) d = Double.POSITIVE_INFINITY;
        if (d > r.max) {
            r.max = d;
            r.where = where.get();
        }
    }

    /** a difference of two longitudes, 0 .. 180 */
    static double angle(double a, double b) {
        double d = Math.abs(a - b) % 360;
        return d > 180 ? 360 - d : d;
    }

    /** one call's return value and error text */
    boolean call(String key, int rc, int rj, CharSequence ec, CharSequence ej, Supplier<String> where) {
        final Row r = row(key);
        final boolean sameRet = rc == rj, sameSerr = String.valueOf(ec).equals(String.valueOf(ej));
        if (!sameRet) {
            if (0 == r.retDiff) r.where = "ret " + rc + "/" + rj + " " + where.get() + " '" + ec + "' / '" + ej + "'";
            r.retDiff++;
        }
        if (!sameSerr) r.serrDiff++;
        return sameRet;
    }

    double max(String key) {
        final Row r = rows.get(key);
        return null == r ? 0 : r.max;
    }

    /** the largest difference over every key that starts with the prefix and ends with the suffix */
    double max(String prefix, String suffix) {
        double m = 0;
        for (Map.Entry<String, Row> e : rows.entrySet())
            if (e.getKey().startsWith(prefix) && e.getKey().endsWith(suffix)) m = Math.max(m, e.getValue().max);
        return m;
    }

    void forEachRow(java.util.function.BiConsumer<String, Row> action) {
        rows.forEach(action);
    }

    long count() {
        long n = 0;
        for (Row r : rows.values()) n += r.n;
        return n;
    }

    long retDiffs() {
        long n = 0;
        for (Row r : rows.values()) n += r.retDiff;
        return n;
    }

    String retDiffList() {
        final StringBuilder sb = new StringBuilder();
        rows.forEach((k, r) -> {
            if (r.retDiff > 0) sb.append(k).append(": ").append(r.retDiff).append(", first ").append(r.where).append('\n');
        });
        return sb.toString();
    }

    String serrDiffList() {
        final StringBuilder sb = new StringBuilder();
        rows.forEach((k, r) -> {
            if (r.serrDiff > 0) sb.append(k).append(": ").append(r.serrDiff).append('\n');
        });
        return sb.toString();
    }

    /** the keys whose largest difference is above the bound */
    String above(String prefix, String suffix, double bound) {
        final StringBuilder sb = new StringBuilder();
        rows.forEach((k, r) -> {
            if (k.startsWith(prefix) && k.endsWith(suffix) && !(r.max <= bound))
                sb.append(k).append(" max ").append(r.max).append(" at ").append(r.where).append('\n');
        });
        return sb.toString();
    }

    void write() {
        final File dir = new File("target", "accuracy");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        try (PrintWriter w = new PrintWriter(new File(dir, name + ".tsv"), StandardCharsets.UTF_8.name())) {
            w.println("key\tn\tmax\tretDiff\tserrDiff\twhere");
            rows.forEach((k, r) -> w.println(k + '\t' + r.n + '\t' + r.max + '\t' + r.retDiff + '\t' + r.serrDiff + '\t' + r.where));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** runs the work in a new thread, so that the native thread-local state is a fresh one */
    static <T> T inFreshThread(Callable<T> work) {
        final AtomicReference<T> result = new AtomicReference<>();
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final Thread t = new Thread(() -> {
            try {
                result.set(work.call());
            } catch (Throwable e) {
                failure.set(e);
            }
        }, "accuracy-survey");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
        final Throwable e = failure.get();
        if (e instanceof RuntimeException) throw (RuntimeException) e;
        if (e instanceof Error) throw (Error) e;
        if (null != e) throw new IllegalStateException(e);
        return result.get();
    }

    /** deterministic dates: n points from a to b, each moved by a fraction of a day */
    static double[] dates(double a, double b, int n) {
        final double[] d = new double[n];
        long s = 0x9E3779B97F4A7C15L;
        for (int i = 0; i < n; i++) {
            s ^= s << 13;
            s ^= s >>> 7;
            s ^= s << 17;
            final double frac = (s >>> 11) * 0x1.0p-53;
            d[i] = a + (b - a) * i / (n - 1) + (i == 0 || i == n - 1 ? 0 : frac);
        }
        return d;
    }
}
