package io.github.helpermethod.zipforge;

import java.io.IOException;

public class ZipForgeException extends RuntimeException {
    public ZipForgeException(IOException e) {
        super(e);
    }
}
