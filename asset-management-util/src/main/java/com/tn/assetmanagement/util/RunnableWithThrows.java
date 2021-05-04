package com.tn.assetmanagement.util;

@FunctionalInterface
public interface RunnableWithThrows<E extends Throwable>
{
  static <E extends Throwable> RunnableWithThrows<E> noop()
  {
    return () -> {};
  }

  void run() throws E;
}
