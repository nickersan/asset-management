package com.tn.assetmanagement.util;

import org.junit.jupiter.api.Test;

class ConsumerWithThrowsTest
{
  @Test
  void testNoop() throws Throwable
  {
    ConsumerWithThrows.noop().accept(new Object());
  }
}
