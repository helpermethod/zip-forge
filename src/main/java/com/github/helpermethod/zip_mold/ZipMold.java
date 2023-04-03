package com.github.helpermethod.zip_mold;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipMold {

    private ZipMold() {}

    public static Path createZipFile(Path path, Consumer<RootNode> block)
        throws IOException {
        try (
            ZipOutputStream zipOutputStream = new ZipOutputStream(
                Files.newOutputStream(path)
            )
        ) {
            ZipFileVisitor zipFileVisitor = new ZipFileVisitor(zipOutputStream);

            RootNode rootNode = new RootNode();
            block.accept(rootNode);

            zipFileVisitor.visit(rootNode);
        }

        return path;
    }
}
