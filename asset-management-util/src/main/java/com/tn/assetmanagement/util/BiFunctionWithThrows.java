package com.tn.assetmanagement.util;

@FunctionalInterface
public interface BiFunctionWithThrows<T, U, R, E extends Throwable>
{
  R apply(T t, U u) throws E;
}
