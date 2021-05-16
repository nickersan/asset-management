package com.tn.assetmanagement.util;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public interface Collections
{
  Random RANDOM = new Random();

  static <T> Optional<T> randomElement(Collection<T> c)
  {
    return c.isEmpty() ? Optional.empty() : c.stream().skip(RANDOM.nextInt(c.size())).findFirst();
  }
}
