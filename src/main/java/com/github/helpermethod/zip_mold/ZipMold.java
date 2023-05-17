package com.github.helpermethod.zip_mold;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.zip.ZipOutputStream;

public class ZipMold {
    static final ThreadLocal<Deque<DirectoryNode>> nodeDeque = ThreadLocal.withInitial(ArrayDeque::new);

    private ZipMold() {}

    public static Path createZipFile(Path path, NodeGroup nodeGroup) throws IOException {
        DirectoryNode rootNode = new DirectoryNode("");

        nodeDeque.get().addLast(rootNode);

        nodeGroup.addNodes();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(path))) {
            new ZipFileVisitor(zipOutputStream).visit(nodeDeque.get().removeLast());
        }

        return path;
    }

    public static void file(String name, String content) {
        nodeDeque.get().getLast().file(path(name), content);
    }

    public static void file(String name, byte[] content) {
        nodeDeque.get().getLast().file(path(name), content);
    }

    public static void directory(String name, NodeGroup nodeGroup) {
        DirectoryNode directoryNode = new DirectoryNode(path(name.replaceFirst("([^/])$", "$1/")));
        nodeDeque.get().getLast().directory(directoryNode);

        nodeDeque.get().addLast(directoryNode);
        nodeGroup.addNodes();
        nodeDeque.get().removeLast();
    }

    private static String path(String name) {
        return nodeDeque.get().getLast().path() + name;
    }
}
