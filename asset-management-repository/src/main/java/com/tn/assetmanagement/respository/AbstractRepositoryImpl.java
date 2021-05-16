package com.tn.assetmanagement.respository;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractRepositoryImpl
{
  private final JdbcTemplate jdbcTemplate;
  private final String entityName;

  public AbstractRepositoryImpl(String entityName, JdbcTemplate jdbcTemplate)
  {
    this.entityName = entityName;
    this.jdbcTemplate = jdbcTemplate;
  }

  protected JdbcTemplate jdbcTemplate()
  {
    return jdbcTemplate;
  }

  protected String entityName()
  {
    return this.entityName;
  }
}
