package com.tn.assetmanagement.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Lambdas
{
  static Throwable unwrapException(Throwable e)
  {
    return e instanceof WrappedException ? e.getCause() : e;
  }

  static <T, U> BiConsumer<T, U> wrapBiConsumer(BiConsumerWithThrows<T, U, ?> biConsumerWithThrows)
  {
    return (t, u) ->
    {
      try
      {
        biConsumerWithThrows.accept(t, u);
      }
      catch (Throwable e)
      {
        throw new WrappedException(e);
      }
    };
  }

  static <T, U, R> BiFunction<T, U, R> wrapBiFunction(BiFunctionWithThrows<T, U, R, ?> biFunctionWithThrows)
  {
    return (t, u) ->
    {
      try
      {
        return biFunctionWithThrows.apply(t, u);
      }
      catch (Throwable e)
      {
        throw new WrappedException(e);
      }
    };
  }

  static <T> Consumer<T> wrapConsumer(ConsumerWithThrows<T, ?> consumerWithThrows)
  {
    return t ->
    {
      try
      {
        consumerWithThrows.accept(t);
      }
      catch (Throwable e)
      {
        throw new WrappedException(e);
      }
    };
  }

  static <T, R> Function<T, R> wrapFunction(FunctionWithThrows<T, R, ?> functionWithThrows)
  {
    return t ->
    {
      try
      {
        return functionWithThrows.apply(t);
      }
      catch (Throwable e)
      {
        throw new WrappedException(e);
      }
    };
  }

  static <T> Supplier<T> wrapSupplier(SupplierWithThrows<T, ?> supplierWithThrows)
  {
    return () ->
    {
      try
      {
        return supplierWithThrows.get();
      }
      catch (Throwable e)
      {
        throw new WrappedException(e);
      }
    };
  }
}
