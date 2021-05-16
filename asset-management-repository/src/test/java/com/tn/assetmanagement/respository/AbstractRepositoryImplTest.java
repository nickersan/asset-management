package com.tn.assetmanagement.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class AbstractRepositoryImplTest
{
  @Test
  void testEntityName()
  {
    String entityName = "test";
    assertEquals(entityName, new AbstractRepositoryImpl(entityName, mock(JdbcTemplate.class)){}.entityName());
  }
}
