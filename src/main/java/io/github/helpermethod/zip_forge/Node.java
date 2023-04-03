package io.github.helpermethod.zip_forge;

import java.io.IOException;

interface Node {
    void accept(Visitor visitor) throws IOException;
}
