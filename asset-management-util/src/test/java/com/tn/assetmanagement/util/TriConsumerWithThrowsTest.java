package com.tn.assetmanagement.util;

import org.junit.jupiter.api.Test;

class TriConsumerWithThrowsTest
{
  @Test
  void testNoop() throws Throwable
  {
    TriConsumerWithThrows.noop().accept(new Object(), new Object(), new Object());
  }
}
