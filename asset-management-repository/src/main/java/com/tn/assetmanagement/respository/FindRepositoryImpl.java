package com.tn.assetmanagement.respository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tn.assetmanagement.util.BiConsumerWithThrows;
import com.tn.assetmanagement.util.FunctionWithThrows;

public final class FindRepositoryImpl<I, T> extends AbstractRepositoryImpl implements FindRepository<I, T>
{
  static final String SQL_WHERE = " WHERE ";

  private final String pluralEntityName;
  private final String sqlSelect;
  private final String idPredicate;
  private final BiConsumerWithThrows<PreparedStatement, I, SQLException> idSetter;
  private final FunctionWithThrows<ResultSet, T, SQLException> entityFactory;

  public FindRepositoryImpl(
    String singleEntityName,
    String pluralEntityName,
    JdbcTemplate jdbcTemplate,
    String sqlSelect,
    String idPredicate,
    BiConsumerWithThrows<PreparedStatement, I, SQLException> idSetter,
    FunctionWithThrows<ResultSet, T, SQLException> entityFactory
  )
  {
    super(singleEntityName, jdbcTemplate);
    this.pluralEntityName = pluralEntityName;
    this.sqlSelect = sqlSelect;
    this.idPredicate = idPredicate;
    this.idSetter = idSetter;
    this.entityFactory = entityFactory;
  }

  @Override
  public Collection<T> findAll() throws RepositoryException
  {
    try
    {
      return jdbcTemplate().query(
        connection -> connection.prepareStatement(this.sqlSelect),
        this::entities
      );
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred finding " + this.pluralEntityName, e);
    }
  }

  @Override
  public Optional<T> findForId(I id) throws RepositoryException
  {
    try
    {
      return jdbcTemplate().query(
        connection -> connection.prepareStatement(this.sqlSelect + SQL_WHERE + this.idPredicate),
        preparedStatement -> this.idSetter.accept(preparedStatement, id),
        resultSet -> Optional.ofNullable(resultSet.next() ? this.entityFactory.apply(resultSet) : null)
      );
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred finding " + entityName() + " for id: " + id, e);
    }
  }

  private Collection<T> entities(ResultSet resultSet) throws SQLException
  {
    List<T> entities = new ArrayList<>();
    while (resultSet.next()) entities.add(this.entityFactory.apply(resultSet));

    return entities;
  }
}
