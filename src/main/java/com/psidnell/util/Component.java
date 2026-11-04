package com.psidnell.util;

public interface Component {
    default void setContext(Context context) throws Exception {
    }

    default void init() throws Exception {
    }
}
