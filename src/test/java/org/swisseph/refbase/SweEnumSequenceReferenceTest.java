/*
 * Copyright (C) By the Author
 * Author    Yura Krymlov
 * Created   2026-08
 */
package org.swisseph.refbase;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.swisseph.api.ISweEnumSequence;
import org.swisseph.app.SweAyanamsa;
import org.swisseph.app.SweHouseSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A reference base for every {@link ISweEnumSequence} this project declares - {@link SweAyanamsa}
 * (48 constants) and {@link SweHouseSystem} (15) - walked with {@code first()}, {@code last()},
 * {@code following()}, {@code previous()} and {@code follow(k)} across the whole cycle in both
 * directions, and recorded as {@code name | code | fid | uid | ordinal} in
 * {@code src/test/resources/org/swisseph/refbase/sequence/}.
 *
 * <h2>Why here and not only downstream</h2>
 * {@code follow()} is a default method <b>declared in this project</b> and inherited by roughly
 * 250 sequences in the Jyotisha layer. It has been wrong before: until 2026-08-14 it jumped to
 * {@code last()} on any negative step and reduced positive ones by {@code last()} rather than by
 * the element count, returning a <b>plausible neighbour</b> rather than throwing. The two enums
 * here were the only implementors inside this project and were never covered, which is part of
 * why it went unnoticed. Fixing that in the downstream project alone would leave the method's own
 * home untested.
 * <p>
 * There is no {@code length} column: {@code length()} belongs to {@code IKundaliSegment} in
 * {@code swe-jyotisa-api}, and nothing at this layer has a degree span.
 *
 * <h2>Golden masters</h2>
 * The files are read and compared, never rewritten by a passing run. On a mismatch the actual
 * output is written to the OS temp directory under the same relative path, so an intended change
 * is a diff and a copy. {@code -Drefbase.generate=true} writes the base in one pass - still to
 * the temp directory, so installing it stays a deliberate step.
 *
 * @author Yura Krymlov
 * @version 1.0, 2026-08
 * @see ISweEnumSequence#follow(int)
 */
class SweEnumSequenceReferenceTest {

    private static final String RESOURCES = "org/swisseph/refbase/sequence/";
    private static final String GENERATE = "refbase.generate";

    // ------------------------------------------------------------------ discovery

    /**
     * Every enum under {@code org.swisseph} that is an {@link ISweEnumSequence}, discovered from
     * the compiled classes rather than listed, so one added later is covered without anyone
     * remembering - it shows up as a missing reference file, which fails.
     * <p>
     * Enum constants with a body compile to synthetic subclasses ({@code SweHouseSystem$1}) for
     * which {@link Class#isEnum()} is false, so they filter themselves out.
     */
    private static List<Class<?>> sequences() {
        final File root = codeSource();
        final List<Class<?>> found = new ArrayList<>();

        collect(new File(root, "org/swisseph"), root, found);
        found.sort(Comparator.comparing(Class::getName));
        return found;
    }

    private static File codeSource() {
        try {
            return new File(ISweEnumSequence.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("cannot locate the compiled classes", e);
        }
    }

    private static void collect(final File dir, final File root, final List<Class<?>> found) {
        final File[] entries = dir.listFiles();
        if (null == entries) return;

        for (File entry : entries) {
            if (entry.isDirectory()) {
                collect(entry, root, found);
                continue;
            }
            if (!entry.getName().endsWith(".class")) continue;

            final String path = root.toURI().relativize(entry.toURI()).getPath();
            final String name = path.substring(0, path.length() - ".class".length())
                    .replace('/', '.');

            final Class<?> type;
            try {
                type = Class.forName(name, false,
                        SweEnumSequenceReferenceTest.class.getClassLoader());
            } catch (Throwable notLoadable) {
                continue;
            }

            if (type.isEnum() && ISweEnumSequence.class.isAssignableFrom(type)) found.add(type);
        }
    }

    // ------------------------------------------------------------------ walking

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<String> walk(final Class<?> type) {
        final ISweEnumSequence[] constants = (ISweEnumSequence[]) type.getEnumConstants();

        final List<String> lines = new ArrayList<>();
        lines.add("== " + type.getName() + " | " + constants.length + " constants");
        lines.add("-- step | name | code | fid | uid | ordinal");

        final ISweEnumSequence first = constants[0].first();
        final ISweEnumSequence last = constants[0].last();
        final int count = last.ordinal() - first.ordinal() + 1;

        lines.add(render("first", first));
        lines.add(render("last", last));

        ISweEnumSequence cursor = first;
        for (int i = 0; i < count; i++) {
            lines.add(render("following[" + i + "]", cursor));
            cursor = cursor.following();
        }

        cursor = last;
        for (int i = 0; i < count; i++) {
            lines.add(render("previous[" + i + "]", cursor));
            cursor = cursor.previous();
        }

        for (int k = 0; k < count; k++) lines.add(render("follow(+" + k + ")", first.follow(k)));
        for (int k = 0; k < count; k++) lines.add(render("follow(-" + k + ")", first.follow(-k)));

        return lines;
    }

    private static String render(final String step, final ISweEnumSequence<?> element) {
        return step + " | " + element.name() + " | " + element.code()
                + " | " + element.fid() + " | " + element.uid() + " | " + element.ordinal();
    }

    // ------------------------------------------------------------------ the base

    @TestFactory
    Stream<DynamicTest> everySequenceMatchesItsReference() {
        return sequences().stream().map(type -> DynamicTest.dynamicTest(
                type.getSimpleName(),
                () -> assertMatches(type.getSimpleName().toLowerCase(), walk(type))));
    }

    // ------------------------------------------------------------------ invariants

    /** a full lap returns to the start, by all three routes */
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void everySequenceReturnsToItsStartAfterAFullLap() {
        for (Class<?> type : sequences()) {
            final ISweEnumSequence[] constants = (ISweEnumSequence[]) type.getEnumConstants();

            final ISweEnumSequence first = constants[0].first();
            final int count = constants[0].last().ordinal() - first.ordinal() + 1;

            assertSame(first, first.follow(count), type.getName() + ": follow(+count)");
            assertSame(first, first.follow(-count), type.getName() + ": follow(-count)");

            ISweEnumSequence cursor = first;
            for (int i = 0; i < count; i++) cursor = cursor.following();
            assertSame(first, cursor, type.getName() + ": count x following()");
        }
    }

    /**
     * Navigation never leaves {@code [first .. last]} whatever the step, which is what keeps the
     * sentinels out of a cycle. Both projects put one at ordinal 0 ({@code SweHouseSystem.NIL});
     * {@link SweAyanamsa} additionally puts one at the <b>end</b> - {@code AY_NONE}, ordinal 48 of
     * 49, the tropical "no ayanamsa" member, excluded because {@code last()} is {@code AY_USER}.
     * A sentinel past {@code last()} is easy to miss when reading the enum, so the reference file
     * recording that no step ever reaches it is the useful half of this check.
     */
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void navigationStaysBetweenFirstAndLast() {
        for (Class<?> type : sequences()) {
            final ISweEnumSequence[] constants = (ISweEnumSequence[]) type.getEnumConstants();

            final ISweEnumSequence first = constants[0].first();
            final ISweEnumSequence last = constants[0].last();

            for (int step = -2 * constants.length; step <= 2 * constants.length; step++) {
                final int ordinal = first.follow(step).ordinal();

                assertTrue(ordinal >= first.ordinal() && ordinal <= last.ordinal(),
                        type.getName() + ": follow(" + step + ") left the range at " + ordinal);
            }
        }
    }

    /** {@code following()} and {@code previous()} are each other's inverse */
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void followingAndPreviousAreInverses() {
        for (Class<?> type : sequences()) {
            for (ISweEnumSequence element : (ISweEnumSequence[]) type.getEnumConstants()) {
                if (element.ordinal() < element.first().ordinal()
                        || element.ordinal() > element.last().ordinal()) continue;

                assertSame(element, element.following().previous(),
                        type.getName() + '.' + element.name());
                assertSame(element, element.previous().following(),
                        type.getName() + '.' + element.name());
            }
        }
    }

    /**
     * The scan has to find things - a silent empty scan would make every test above pass while
     * checking nothing, the one failure mode a reflective suite invites.
     */
    @Test
    void theScanFindsBothSequences() {
        final List<Class<?>> found = sequences();

        assertTrue(found.contains(SweAyanamsa.class), "SweAyanamsa must be discovered");
        assertTrue(found.contains(SweHouseSystem.class), "SweHouseSystem must be discovered");
    }

    // ------------------------------------------------------------------ golden-master plumbing

    private static void assertMatches(final String name, final List<String> actual)
            throws IOException {

        final String resource = RESOURCES + name + ".txt";
        final String text = String.join("\n", actual);

        final Path temp = Paths.get(System.getProperty("java.io.tmpdir")).resolve(resource);
        Files.createDirectories(temp.getParent());
        Files.write(temp, text.getBytes(UTF_8));

        if (Boolean.getBoolean(GENERATE)) return;

        try (InputStream in = SweEnumSequenceReferenceTest.class
                .getClassLoader().getResourceAsStream(resource)) {

            assertNotNull(in, "missing reference file: " + resource
                    + " - copy the one just written to " + temp);

            final String expected = new String(readAll(in), UTF_8).replace("\r\n", "\n").trim();
            assertEquals(expected, text, name + " differs from its reference; the actual output"
                    + " was written to " + temp);
        }
    }

    private static byte[] readAll(final InputStream in) throws IOException {
        final java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        final byte[] buffer = new byte[8192];

        for (int read; (read = in.read(buffer)) > 0; ) out.write(buffer, 0, read);
        return out.toByteArray();
    }
}
