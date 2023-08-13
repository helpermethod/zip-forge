package io.github.helpermethod.zipforge;

import java.io.IOException;

interface Visitor {
    void visit(FileNode file) throws IOException;

    void visit(DirectoryNode directory) throws IOException;
}
