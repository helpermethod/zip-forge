package com.github.helpermethod.zip_mold;

import java.io.IOException;

interface Node {
    void accept(Visitor visitor) throws IOException;
}
