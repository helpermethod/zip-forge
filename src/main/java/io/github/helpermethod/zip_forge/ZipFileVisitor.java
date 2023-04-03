package io.github.helpermethod.zip_forge;

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
        zipOutputStream.putNextEntry(new ZipEntry(file.path()));
        zipOutputStream.write(file.content());
    }

    @Override
    public void visit(DirectoryNode directory) throws IOException {
        if (!isRootNode(directory)) {
            zipOutputStream.putNextEntry(new ZipEntry(directory.path()));
        }

        for (Node node : directory.children()) {
            node.accept(this);
        }
    }

    private static boolean isRootNode(DirectoryNode directory) {
        return directory.path().isEmpty();
    }
}
