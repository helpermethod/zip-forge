package io.github.helpermethod.zipforge;

import static io.github.helpermethod.zipforge.ZipForge.createZipFile;
import static io.github.helpermethod.zipforge.ZipForge.directory;
import static io.github.helpermethod.zipforge.ZipForge.file;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ZipForgeTest {

    @Nested
    class CreateZipFile {

        @ArgumentsSource(ZipFileEntries.class)
        @ParameterizedTest
        void should_create_a_zip_file_with_correct_entries(
                NodeGroup nodeGroup, List<String> entries, @TempDir Path tempDir) throws IOException {
            var location = tempDir.resolve("test.zip");

            createZipFile(location, nodeGroup);

            try (var zipFile = new ZipFile(location.toFile())) {
                assertThat(zipFile.stream()).extracting("name").isEqualTo(entries);
            }
        }

        @ArgumentsSource(ZipFileContents.class)
        @ParameterizedTest
        void should_create_a_zip_file_with_correct_contents(
                NodeGroup nodeGroup, List<byte[]> fileContents, @TempDir Path tempDir) throws IOException {
            var location = tempDir.resolve("test.zip");

            createZipFile(location, nodeGroup);

            try (var zipFile = new ZipFile(location.toFile())) {
                var contents = zipFile.stream()
                        .filter(not(ZipEntry::isDirectory))
                        .map(e -> {
                            try {
                                return zipFile.getInputStream(e).readAllBytes();
                            } catch (IOException ex) {
                                throw new AssertionError(ex);
                            }
                        })
                        .toList();

                assertThat(contents).containsExactlyElementsOf(fileContents);
            }
        }

        static class ZipFileEntries implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                return Stream.of(
                        arguments(
                                (NodeGroup) () -> {
                                    file("a.txt", "a");
                                },
                                List.of("a.txt")),
                        arguments(
                                (NodeGroup) () -> {
                                    file("a.txt", "a");
                                    file("b.txt", "b");
                                },
                                List.of("a.txt", "b.txt")),
                        arguments(
                                (NodeGroup) () -> {
                                    directory("d", () -> {});
                                },
                                List.of("d/")),
                        arguments((NodeGroup) () -> directory("d/", () -> {}), List.of("d/")),
                        arguments(
                                (NodeGroup) () -> directory("d", () -> {
                                    file("a.txt", "a");
                                }),
                                List.of("d/", "d/a.txt")),
                        arguments(
                                (NodeGroup) () -> directory("d", () -> {
                                    file("a.txt", "a");
                                    file("b.txt", "b");
                                }),
                                List.of("d/", "d/a.txt", "d/b.txt")),
                        arguments(
                                (NodeGroup) () -> directory("d", () -> {
                                    directory("e", () -> {
                                        file("a.txt", "a");
                                    });
                                }),
                                List.of("d/", "d/e/", "d/e/a.txt")),
                        arguments(
                                (NodeGroup) () -> directory("d", () -> {
                                    directory("e", () -> {
                                        file("a.txt", "a");
                                        file("b.txt", "b");
                                    });
                                }),
                                List.of("d/", "d/e/", "d/e/a.txt", "d/e/b.txt")),
                        arguments(
                                (NodeGroup) () -> {
                                    file("a.txt", "a");
                                    directory("d", () -> {
                                        directory("e", () -> {
                                            file("b.txt", "b");
                                            file("c.txt", "c");
                                        });
                                    });
                                },
                                List.of("a.txt", "d/", "d/e/", "d/e/b.txt", "d/e/c.txt")),
                        arguments(
                                (NodeGroup) () -> {
                                    file("a.txt", "a");
                                    directory("d", () -> {});
                                },
                                List.of("a.txt", "d/")),
                        arguments(
                                (NodeGroup) () -> {
                                    directory("d", () -> {});
                                    directory("e", () -> {});
                                },
                                List.of("d/", "e/")),
                        arguments((NodeGroup) () -> {}, List.of()));
            }
        }

        static class ZipFileContents implements ArgumentsProvider {
            @Override
            public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
                return Stream.of(
                        arguments((NodeGroup) () -> file("a.txt", "a"), List.of("a".getBytes(UTF_8))),
                        arguments(
                                (NodeGroup) () -> {
                                    file("a.txt", "a");
                                    file("b.txt", "b");
                                },
                                List.of("a".getBytes(UTF_8), "b".getBytes(UTF_8))),
                        arguments(
                                (NodeGroup) () -> {
                                    directory("d", () -> {
                                        file("a.txt", "a");
                                    });
                                },
                                List.of("a".getBytes(UTF_8))),
                        arguments(
                                (NodeGroup) () -> {
                                    directory("d", () -> {
                                        directory("e", () -> {
                                            file("a", "a");
                                        });
                                    });
                                },
                                List.of("a".getBytes(UTF_8))),
                        arguments(
                                (NodeGroup) () -> {
                                    file("a.txt", "a".getBytes(UTF_8));
                                },
                                List.of("a".getBytes(UTF_8))),
                        arguments((NodeGroup) () -> {}, List.of()));
            }
        }
    }

    @Nested
    class File {
        @Test
        void should_support_large_files(@TempDir Path tempDir) throws IOException {
            var largeFile = tempDir.resolve("large-file.bin");

            try (var twoGigaByteFile = Files.newByteChannel(largeFile, CREATE_NEW, WRITE)) {
                long twoGigaBytesInBytes = 2L * 1024L * 1024L * 1024L;

                twoGigaByteFile.position(twoGigaBytesInBytes - 1);
                twoGigaByteFile.write(ByteBuffer.wrap(new byte[] {0}));
            }

            assertThat(createZipFile(tempDir.resolve("test.zip"), () -> file("a.bin", largeFile)))
                    .exists();
        }

        @Test
        void should_throw_an_exception_if_the_path_does_not_exist(@TempDir Path tempDir) throws IOException {
            assertThatThrownBy(() -> createZipFile(tempDir.resolve("test.zip"), () -> file("a.bin", Path.of("a.bin"))))
                    .isInstanceOf(ZipForgeException.class)
                    .hasMessage("java.nio.file.NoSuchFileException: a.bin");
        }
    }
}
