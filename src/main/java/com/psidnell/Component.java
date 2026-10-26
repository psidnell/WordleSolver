package com.psidnell;

public interface Component {
    default void setContext(Context context) {
    }

    default void init() {
    }
}
