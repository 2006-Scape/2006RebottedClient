package org.rebotted.util;

public interface Filter<F> {
    boolean accept(F f);
}