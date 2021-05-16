package com.tn.assetmanagement.util;

import org.junit.jupiter.api.Test;

class RunnableWithThrowsTest
{
  @Test
  void testNoop() throws Throwable
  {
    RunnableWithThrows.noop().run();
  }
}
