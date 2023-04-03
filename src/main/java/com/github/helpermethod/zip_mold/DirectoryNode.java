package com.github.helpermethod.zip_mold;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class DirectoryNode implements Node {

    private final String path;
    private final List<Node> children = new ArrayList<>();

    DirectoryNode(String path) {
        this.path = path;
    }

    public DirectoryNode file(String name, String content) {
        return file(name, content.getBytes(UTF_8));
    }

    public DirectoryNode file(String name, byte[] content) {
        children.add(new FileNode(this.path + name, content));

        return this;
    }

    public DirectoryNode directory(String name, Consumer<DirectoryNode> block) {
        String path = this.path + (name.endsWith("/") ? name : name + "/");
        DirectoryNode zipParentNode = new DirectoryNode(path);
        block.accept(zipParentNode);

        children.add(zipParentNode);

        return this;
    }

    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this);
    }

    String name() {
        return path;
    }

    List<Node> children() {
        return Collections.unmodifiableList(children);
    }
}
