package com.github.helpermethod.zip_mold;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ZipFileVisitor implements Visitor {

    private final ZipOutputStream zipOutputStream;

    ZipFileVisitor(ZipOutputStream zipOutputStream) {
        this.zipOutputStream = zipOutputStream;
    }

    @Override
    public void visit(FileNode file) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(file.name()));
        zipOutputStream.write(file.content());
    }

    @Override
    public void visit(DirectoryNode directory) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(directory.name()));

        for (Node node : directory.children()) {
            node.accept(this);
        }
    }

    @Override
    public void visit(RootNode rootNode) throws IOException {
        for (Node node : rootNode.children()) {
            node.accept(this);
        }
    }

    private static boolean isRoot(DirectoryNode directory) {
        return directory.name().isEmpty();
    }
}
