package com.tn.assetmanagement.util;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class Collections
{
  private static final Random RANDOM = new Random();

  public static <T> Optional<T> randomElement(Collection<T> c)
  {
    return c.isEmpty() ? Optional.empty() : c.stream().skip(RANDOM.nextInt(c.size())).findFirst();
  }
}
