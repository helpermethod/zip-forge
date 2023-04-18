package com.github.helpermethod.zip_mold;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ZipMoldTest {

    @Nested
    class createZipFile {

        @MethodSource(
            "com.github.helpermethod.zip_mold.ZipMoldTest$createZipFile#zipfileEntries"
        )
        @ParameterizedTest
        void should_create_a_zip_file_with_the_correct_entries(
            Consumer<RootNode> block,
            List<String> entries,
            @TempDir Path tempDir
        ) throws IOException {
            var location = tempDir.resolve("test.zip");

            ZipMold.createZipFile(location, block);

            try (var zipFile = new ZipFile(location.toFile())) {
                assertThat(zipFile.stream())
                    .extracting("name")
                    .isEqualTo(entries);
            }
        }

        @MethodSource(
            "com.github.helpermethod.zip_mold.ZipMoldTest$createZipFile#zipFileContents"
        )
        @ParameterizedTest
        void shoud_create_a_zip_file_with_the_correct_contents(
            Consumer<RootNode> block,
            List<byte[]> fileContents,
            @TempDir Path tempDir
        ) throws IOException {
            var location = tempDir.resolve("test.zip");

            ZipMold.createZipFile(location, block);

            try (var zipFile = new ZipFile(location.toFile())) {
                var contents = zipFile
                    .stream()
                    .filter(not(ZipEntry::isDirectory))
                    .map(e -> {
                        try {
                            return zipFile.getInputStream(e).readAllBytes();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .toList();

                assertThat(contents).containsExactlyElementsOf(fileContents);
            }
        }

        static Stream<Arguments> zipfileEntries() {
            return Stream.of(
                arguments(
                    (Consumer<RootNode>) r -> r.file("a.txt", "a"),
                    List.of("a.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.file("a.txt", "a").file("b.txt", "b"),
                    List.of("a.txt", "b.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r -> r.directory("d", d -> {}),
                    List.of("d/")
                ),
                arguments(
                    (Consumer<RootNode>) r -> r.directory("d/", d -> {}),
                    List.of("d/")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory("d", d -> d.file("a.txt", "a")),
                    List.of("d/", "d/a.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory(
                            "d",
                            d -> d.file("a.txt", "a").file("b.txt", "b")
                        ),
                    List.of("d/", "d/a.txt", "d/b.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory(
                            "d",
                            d -> d.directory("e", e -> e.file("a.txt", "a"))
                        ),
                    List.of("d/", "d/e/", "d/e/a.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory(
                            "d",
                            d ->
                                d.directory(
                                    "e",
                                    e -> e.file("a.txt", "a").file("b.txt", "b")
                                )
                        ),
                    List.of("d/", "d/e/", "d/e/a.txt", "d/e/b.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r
                            .file("a.txt", "a")
                            .directory(
                                "d",
                                d ->
                                    d.directory(
                                        "e",
                                        e ->
                                            e
                                                .file("b.txt", "b")
                                                .file("c.txt", "c")
                                    )
                            ),
                    List.of("a.txt", "d/", "d/e/", "d/e/b.txt", "d/e/c.txt")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.file("a.txt", "a").directory("d", d -> {}),
                    List.of("a.txt", "d/")
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory("d", d -> {}).directory("e", e -> {}),
                    List.of("d/", "e/")
                )
            );
        }

        static Stream<Arguments> zipFileContents() {
            return Stream.of(
                arguments(
                    (Consumer<RootNode>) r -> r.file("a.txt", "a"),
                    List.of("a".getBytes(UTF_8))
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.file("a.txt", "a").file("b.txt", "b"),
                    List.of("a".getBytes(UTF_8), "b".getBytes(UTF_8))
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory("d", d -> d.file("a.txt", "a")),
                    List.of("a".getBytes(UTF_8))
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.directory(
                            "d",
                            d -> d.directory("e", e -> e.file("a", "a"))
                        ),
                    List.of("a".getBytes(UTF_8))
                ),
                arguments(
                    (Consumer<RootNode>) r ->
                        r.file("a.txt", "a".getBytes(UTF_8)),
                    List.of("a".getBytes(UTF_8))
                )
            );
        }
    }
}
