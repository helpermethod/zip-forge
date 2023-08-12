package io.github.helpermethod.zipforge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryNode implements Node {
    private final Path path;
    private final List<Node> children = new ArrayList<>();

    DirectoryNode(Path path) {
        this.path = path;
    }

    public void file(Path path, InputStream content) {
        children.add(new FileNode(path, content));
    }

    public void directory(DirectoryNode directory) {
        children.add(directory);
    }

    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this);
    }

    Path path() {
        return path;
    }

    List<Node> children() {
        return Collections.unmodifiableList(children);
    }
}
