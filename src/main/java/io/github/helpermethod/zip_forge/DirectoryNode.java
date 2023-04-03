package io.github.helpermethod.zip_forge;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryNode implements Node {
    private final String path;
    private final List<Node> children = new ArrayList<>();

    DirectoryNode(String path) {
        this.path = path;
    }

    public void file(String path, String content) {
        file(path, content.getBytes(UTF_8));
    }

    public void file(String path, byte[] content) {
        children.add(new FileNode(path, content));
    }

    public void directory(DirectoryNode directory) {
        children.add(directory);
    }

    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this);
    }

    public String path() {
        return path;
    }

    List<Node> children() {
        return Collections.unmodifiableList(children);
    }
}
