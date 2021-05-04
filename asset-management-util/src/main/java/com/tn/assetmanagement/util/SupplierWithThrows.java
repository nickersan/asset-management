package com.tn.assetmanagement.util;

@FunctionalInterface
public interface SupplierWithThrows<T, E extends Throwable>
{
  T get() throws E;
}
