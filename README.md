# 🌋 ZIP Forge

[![CI](https://github.com/helpermethod/zip-forge/actions/workflows/ci.yml/badge.svg)](https://github.com/helpermethod/zip-forge/actions/workflows/ci.yml)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.helpermethod%3Azip-forge&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=io.github.helpermethod%3Azip-forge)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.helpermethod%3Azip-forge&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=io.github.helpermethod%3Azip-forge)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.helpermethod%3Azip-forge&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=io.github.helpermethod%3Azip-forge)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.helpermethod%3Azip-forge&metric=coverage)](https://sonarcloud.io/summary/new_code?id=io.github.helpermethod%3Azip-forge)

A tiny, formatter-friendly Java DSL for creating ZIP files.

# :sparkles: Features

## :pinching_hand: Tiny

The whole ZIP Forge API consists of only 3 methods.

## :clipboard: Formatter-friendly

Contrary to other DSLs, applying a code formatter like [palantir-java-format](https://github.com/palantir/palantir-java-format)
will not mess up ZIP Forge's indentation.

## :package: No external dependencies

ZIP Forge is based on Java's [ZIP File System Provider](https://docs.oracle.com/javase/8/docs/technotes/guides/io/fsp/zipfilesystemprovider.html) and requires no external dependencies.

# :hammer_and_wrench: Installation

## Maven

```xml
<dependency>
    <groupId>io.github.helpermethod</groupId>
    <artifactId>zip-forge</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Gradle

```groovy
implementation 'io.github.helpermethod:zip-forge:0.1.0'
```

## Gradle (Kotlin)

```kotlin
implementation("io.github.helpermethod:zip-forge:0.1.0")
```

# :gear: Usage

## Java

The following code snippet calls `createZipFile` to create the ZIP file at the given location.
It uses the `file` and `directory` methods to create files and directories within the context of the ZIP file.

Note that `file` and `directory` methods must never be used outside of `createZipFile`'s or `directory`'s context / lambda parameter.

```java
import java.nio.charset.StandardCharsets;

import static io.github.helpermethod.zip_forge.ZipForge.createZipFile;
import static io.github.helpermethod.zip_forge.ZipForge.file;
import static io.github.helpermethod.zip_forge.ZipForge.directory;
import static java.nio.charset.StandardCharsets.UTF_8;

class ZipForgeDemo {
    public static void main(String[] args) {
        // creates a ZIP file named demo.zip in the /home/helpermethod directory
        createZipFile(Paths.get("/home/helpermethod/demo.zip"), () -> {
            // the file content can be specified as a String...
            file("a.txt", "a");
            directory("d", () -> {
                // ... or a byte[]
                file("b.txt", "b".getBytes(UTF_8));
                file("c.txt", "c");
                // directories can be nested
                directory("e", () -> {
                    file("f.txt", "f");
                });
            });
        });
    }
}
```

The above code results in a ZIP file with the following structure.

```
Archive:  demo.zip
  Length      Date    Time    Name
---------  ---------- -----   ----
        0  07-11-2023 15:39   d/
        0  07-11-2023 15:39   d/e/
        1  07-11-2023 15:39   a.txt
        1  07-11-2023 15:39   d/b.txt
        1  07-11-2023 15:39   d/c.txt
        1  07-11-2023 15:39   d/e/f.txt
---------                     -------
        4                     6 files
```

## Kotlin

The same example as above written in Kotlin. Note that it uses the same API as the Java code snippet.

```kotlin
import io.github.helpermethod.zip_forge.ZipForge.createZipFile
import io.github.helpermethod.zip_forge.ZipForge.directory
import io.github.helpermethod.zip_forge.ZipForge.file
import kotlin.io.path.Path

createZipFile(Path("/home/helpermethod/demo.zip")) {
    file("a.txt", "a")
    directory("d") {
        file("b.txt", "b".toByteArray())
        file("c.txt", "c")
        directory("e") {
            file("f.txt", "f")
        }
    }
}
```
