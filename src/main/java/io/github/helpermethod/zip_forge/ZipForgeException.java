package io.github.helpermethod.zip_forge;

import java.io.IOException;

public class ZipForgeException extends RuntimeException {
    public ZipForgeException(IOException e) {
        super(e);
    }
}
