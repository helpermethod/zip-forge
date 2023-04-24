package com.github.helpermethod.zip_mold;

import java.io.IOException;

interface Visitor {
    void visit(FileNode file) throws IOException;

    void visit(DirectoryNode directory) throws IOException;

    void visit(RootNode rootNode) throws IOException;
}
