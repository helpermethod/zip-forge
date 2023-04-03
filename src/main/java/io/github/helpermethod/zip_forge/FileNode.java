package io.github.helpermethod.zip_forge;

import java.io.IOException;

class FileNode implements Node {
    private final String path;
    private final byte[] content;

    FileNode(String path, byte[] content) {
        this.path = path;
        this.content = content;
    }

    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this);
    }

    public String path() {
        return path;
    }

    byte[] content() {
        return content;
    }
}
