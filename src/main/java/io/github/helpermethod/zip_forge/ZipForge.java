package io.github.helpermethod.zip_forge;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ZipForge {
    private static final ThreadLocal<Deque<DirectoryNode>> nodeDeque = ThreadLocal.withInitial(ArrayDeque::new);

    private ZipForge() {}

    public static Path createZipFile(Path path, NodeGroup nodeGroup) throws IOException {
        DirectoryNode rootNode = new DirectoryNode(Paths.get(""));

        try {
            nodeDeque.get().addLast(rootNode);
            nodeGroup.addNodes();
        } finally {
            nodeDeque.remove();
        }

        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        try (FileSystem zipFileSystem = FileSystems.newFileSystem(URI.create("jar:" + path.toUri()), env)) {
            new ZipFileVisitor(zipFileSystem).visit(rootNode);
        }

        return path;
    }

    public static void file(String name, String content) {
        file(name, content.getBytes(UTF_8));
    }

    public static void file(String name, byte[] content) {
        nodeDeque.get().getLast().file(path(name), new ByteArrayInputStream(content));
    }

    public static void file(String name, Path content) {
        try {
            nodeDeque.get().getLast().file(path(name), Files.newInputStream(content));
        } catch (IOException e) {
            throw new ZipForgeException(e);
        }
    }

    public static void directory(String name, NodeGroup nodeGroup) {
        DirectoryNode directoryNode = new DirectoryNode(path(name));
        nodeDeque.get().getLast().directory(directoryNode);

        nodeDeque.get().addLast(directoryNode);
        nodeGroup.addNodes();
        nodeDeque.get().removeLast();
    }

    private static Path path(String name) {
        return nodeDeque.get().getLast().path().resolve(name);
    }
}
