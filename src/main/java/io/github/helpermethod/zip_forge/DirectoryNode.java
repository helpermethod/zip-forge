package io.github.helpermethod.zip_forge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryNode implements Node {
    private final Path path;
    private final List<Node> children = new ArrayList<>();
    private final boolean root;

    DirectoryNode() {
        this.path = Paths.get("");
        this.root = true;
    }

    DirectoryNode(Path path) {
        this.path = path;
        this.root = false;
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

    boolean isRoot() {
        return root;
    }
}
