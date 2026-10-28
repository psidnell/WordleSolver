package com.psidnell.util;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Component {
    default void setContext(Context context) throws IOException, URISyntaxException, Exception {
    }

    default void init() throws Exception {
    }
}
