package com.tn.assetmanagement.util;

@FunctionalInterface
public interface FunctionWithThrows<T, R, E extends Throwable>
{
  R apply(T t) throws E;
}
