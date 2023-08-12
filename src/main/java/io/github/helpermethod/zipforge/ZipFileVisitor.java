package io.github.helpermethod.zipforge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

class ZipFileVisitor implements Visitor {

    private final FileSystem zipFileSystem;

    ZipFileVisitor(FileSystem zipFileSystem) {
        this.zipFileSystem = zipFileSystem;
    }

    @Override
    public void visit(FileNode file) throws IOException {
        try (InputStream content = file.content()) {
            Files.copy(content, zipFileSystem.getPath(file.path().toString()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public void visit(DirectoryNode directory) throws IOException {
        Files.createDirectories(zipFileSystem.getPath(directory.path().toString()));

        for (Node node : directory.children()) {
            node.accept(this);
        }
    }
}
