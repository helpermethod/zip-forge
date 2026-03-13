package io.github.helpermethod.zipforge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public record FileNode(Path path, InputStream content) implements Node {
    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this);
    }
}
