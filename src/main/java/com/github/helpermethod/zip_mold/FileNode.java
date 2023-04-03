package com.github.helpermethod.zip_mold;

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

    String name() {
        return path;
    }

    byte[] content() {
        return content;
    }
}
