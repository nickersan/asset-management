package com.tn.assetmanagement.util;

import org.junit.jupiter.api.Test;

class BiConsumerWithThrowsTest
{
  @Test
  void testNoop() throws Throwable
  {
    BiConsumerWithThrows.noop().accept(new Object(), new Object());
  }
}
