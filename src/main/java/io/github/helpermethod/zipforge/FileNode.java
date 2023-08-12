package io.github.helpermethod.zipforge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

class FileNode implements Node {
    private final Path path;
    private final InputStream content;

    FileNode(Path path, InputStream content) {
        this.path = path;
        this.content = content;
    }

    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this);
    }

    Path path() {
        return path;
    }

    InputStream content() {
        return content;
    }
}
