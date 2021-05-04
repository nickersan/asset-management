package com.tn.assetmanagement.util;

@FunctionalInterface
public interface BiConsumerWithThrows<T, U, E extends Throwable>
{
  static <T, U, E extends Throwable> BiConsumerWithThrows<T, U, E> noop()
  {
    return (T t, U u) -> {};
  }

  void accept(T t, U u) throws E;
}
