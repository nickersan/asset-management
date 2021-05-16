package com.tn.assetmanagement.util;

@FunctionalInterface
public interface TriConsumerWithThrows<T, U, V, E extends Throwable>
{
  static <T, U, V, E extends Throwable> TriConsumerWithThrows<T, U, V, E> noop()
  {
    return (T t, U u, V v) -> {};
  }

  void accept(T t, U u, V v) throws E;
}
