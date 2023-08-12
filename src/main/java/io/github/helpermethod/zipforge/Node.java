package io.github.helpermethod.zipforge;

import java.io.IOException;

interface Node {
    void accept(Visitor visitor) throws IOException;
}
