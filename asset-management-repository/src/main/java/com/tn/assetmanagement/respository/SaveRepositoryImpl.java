package com.tn.assetmanagement.respository;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tn.assetmanagement.util.BiFunctionWithThrows;

public final class SaveRepositoryImpl<I, T> extends AbstractRepositoryImpl implements SaveRepository<I, T>
{
  private final String sqlSave;
  private final BiFunctionWithThrows<CallableStatement, T, I, SQLException> callableStatementCallback;

  public SaveRepositoryImpl(String singleName, JdbcTemplate jdbcTemplate, String sqlSave, BiFunctionWithThrows<CallableStatement, T, I, SQLException> callableStatementCallback)
  {
    super(singleName, jdbcTemplate);
    this.sqlSave = sqlSave;
    this.callableStatementCallback = callableStatementCallback;
  }

  @Override
  public I save(T entity) throws RepositoryException
  {
    try
    {
      return jdbcTemplate().execute(this.sqlSave, (CallableStatement callableStatement) -> this.callableStatementCallback.apply(callableStatement, entity));
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred saving " + entityName() + ": " + e, e);
    }
  }
}
