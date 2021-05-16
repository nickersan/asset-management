package com.tn.assetmanagement.respository;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tn.assetmanagement.util.BiConsumerWithThrows;

public final class DeleteRepositoryImpl<I> extends AbstractRepositoryImpl implements DeleteRepository<I>
{
  private final String sqlDelete;
  private final BiConsumerWithThrows<CallableStatement, I, SQLException> idSetter;

  public DeleteRepositoryImpl(String entityName, JdbcTemplate jdbcTemplate, String sqlDelete, BiConsumerWithThrows<CallableStatement, I, SQLException> idSetter)
  {
    super(entityName, jdbcTemplate);
    this.sqlDelete = sqlDelete;
    this.idSetter = idSetter;
  }

  @Override
  public void delete(I id) throws RepositoryException
  {
    try
    {
      jdbcTemplate().execute(
        this.sqlDelete,
        (CallableStatement callableStatement) ->
        {
          this.idSetter.accept(callableStatement, id);
          callableStatement.execute();
          return null;
        }
      );
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred deleting " + entityName() + " id: " + id, e);
    }
  }
}
