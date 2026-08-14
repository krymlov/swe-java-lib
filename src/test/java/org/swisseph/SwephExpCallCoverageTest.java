/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import swisseph.SwephExp;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Companion to {@link ApiCoverageTest}'s structural checks (every native reachable through
 * {@link ISwissEph}, every native overridden by {@code swisseph.SwissEph}): this instead
 * checks that every one of the 106 native methods is actually <em>called</em> somewhere
 * under {@code src/test/java} - the {@code SwephXxxTest} family ported from
 * {@code swe-java-api} exists specifically to give each one a call site through
 * {@link ISwissEph}, mirroring {@code swisseph.SwephApiCoverageTest} in the sibling project
 * (which does the same thing one layer down, straight against {@link SwephExp}).
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 */
class SwephExpCallCoverageTest {

    private static final Path TEST_SOURCE_ROOT = Paths.get("src", "test", "java");

    private static Set<String> declaredNativeMethodNames() {
        Set<String> names = new TreeSet<>();
        for (Method m : SwephExp.class.getDeclaredMethods()) {
            if (Modifier.isNative(m.getModifiers())) {
                names.add(m.getName());
            }
        }
        return names;
    }

    private static String allTestSourceExceptThisFile() throws IOException {
        List<Path> files;
        try (Stream<Path> walk = Files.walk(TEST_SOURCE_ROOT)) {
            files = walk.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.getFileName().toString().equals("SwephExpCallCoverageTest.java"))
                    .collect(Collectors.toList());
        }
        StringBuilder all = new StringBuilder();
        for (Path f : files) {
            all.append(new String(Files.readAllBytes(f))).append('\n');
        }
        return all.toString();
    }

    @Test
    void everyNativeMethodHasACallSiteSomewhereInTheTestSuite() throws IOException {
        Set<String> declared = declaredNativeMethodNames();
        assertTrue(declared.size() == 106, "expected 106 native methods, found " + declared.size());

        String source = allTestSourceExceptThisFile();
        List<String> uncalled = new ArrayList<>();
        for (String name : declared) {
            if (!source.contains(name + "(")) {
                uncalled.add(name);
            }
        }

        assertTrue(uncalled.isEmpty(), "native methods with no test call site: " + uncalled);
    }
}
