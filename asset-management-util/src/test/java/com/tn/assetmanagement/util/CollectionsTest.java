package com.tn.assetmanagement.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class CollectionsTest
{
  @Test
  void testRandomElement()
  {
    Collection<Integer> integers = IntStream.range(1, 10).boxed().collect(toList());
    assertTrue(integers.contains(Collections.randomElement(integers).orElse(null)));
  }

  @Test
  void testRandomElementEmpty()
  {
    assertTrue(Collections.randomElement(emptyList()).isEmpty());
  }

  @Test
  void testRandomElementSingle()
  {
    Integer i = 10;
    assertEquals(i, Collections.randomElement(singleton(i)).orElse(null));
  }
}
