package com.tn.assetmanagement.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.tn.assetmanagement.web.Handlers.PATH_VARIABLE_ID;
import static com.tn.assetmanagement.web.Paths.idPath;
import static com.tn.assetmanagement.web.Paths.variablePath;

import org.junit.jupiter.api.Test;

class PathsTest
{
  @Test
  void testIdPath()
  {
    assertEquals("/{id}", idPath());
  }

  @Test
  void testVariablePath()
  {
    assertEquals("/{test}", variablePath("test"));
  }
}
