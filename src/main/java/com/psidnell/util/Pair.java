package com.psidnell.util;

public record Pair<L, R>(L left, R right) {
    @Override

    public String toString() {
        StringBuilder b = new StringBuilder();
        return b.append('{').append(left).append(',').append(right).append('}').toString();
    }
}
