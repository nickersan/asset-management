package com.tn.assetmanagement.funds.repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tn.assetmanagement.funds.domain.Fund;

public class FundRepositoryImpl implements FundRepository
{
  private static final int INDEX_FUND_ID = 1;
  private static final int INDEX_NAME = 2;
  private static final int INDEX_TICKER = 3;
  private static final String PREDICATE_FUND_ID = " WHERE fund_id = ?";
  private static final String SQL_SELECT = "SELECT fund_id, name, ticker FROM v_funds";
  private static final String SQL_SAVE = "{? = call pkg_funds.save(?, ?, ?)}";
  private static final String SQL_DELETE = "{CALL pkg_funds.delete(?)}";

  private final JdbcTemplate jdbcTemplate;

  public FundRepositoryImpl(JdbcTemplate jdbcTemplate)
  {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Collection<Fund> findAll() throws RepositoryException
  {
    try
    {
      return this.jdbcTemplate.query(
        connection -> connection.prepareStatement(SQL_SELECT),
        this::funds
      );
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred finding funds", e);
    }
  }

  @Override
  public Optional<Fund> findForId(int id) throws RepositoryException
  {
    try
    {
      return this.jdbcTemplate.query(
        connection -> connection.prepareStatement(SQL_SELECT + PREDICATE_FUND_ID),
        preparedStatement -> preparedStatement.setInt(INDEX_FUND_ID, id),
        resultSet -> Optional.ofNullable(resultSet.next() ? fund(resultSet) : null)
      );
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred finding fund for id: " + id, e);
    }
  }

  @Override
  public Integer save(Fund fund) throws RepositoryException
  {
    try
    {
      return this.jdbcTemplate.execute(
        SQL_SAVE,
        (CallableStatement callableStatement) ->
        {
          callableStatement.registerOutParameter(INDEX_FUND_ID, Types.INTEGER);
          if (fund.getId() != null) callableStatement.setInt(offsetForReturnParam(INDEX_FUND_ID), fund.getId());
          else callableStatement.setNull(offsetForReturnParam(INDEX_FUND_ID), Types.INTEGER);
          callableStatement.setString(offsetForReturnParam(INDEX_NAME), fund.getName());
          callableStatement.setString(offsetForReturnParam(INDEX_TICKER), fund.getTicker());

          callableStatement.execute();

          return callableStatement.getInt(INDEX_FUND_ID);
        }
      );
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred saving fund: " + fund, e);
    }
  }

  public void delete(int id) throws RepositoryException
  {
    try
    {
      this.jdbcTemplate.execute(
        SQL_DELETE,
        (CallableStatement callableStatement) ->
        {
          callableStatement.setInt(INDEX_FUND_ID, id);
          callableStatement.execute();
          return null;
        }
      );
    }
    catch (DataAccessException e)
    {
      throw new RepositoryException("An error occurred deleting fund id: " + id, e);
    }
  }

  private Fund fund(ResultSet resultSet) throws SQLException
  {
    return new Fund(
      resultSet.getInt(INDEX_FUND_ID),
      resultSet.getString(INDEX_NAME),
      resultSet.getString(INDEX_TICKER)
    );
  }

  private Collection<Fund> funds(ResultSet resultSet) throws SQLException
  {
    List<Fund> funds = new ArrayList<>();
    while (resultSet.next()) funds.add(fund(resultSet));

    return funds;
  }

  private int offsetForReturnParam(int index)
  {
    return index + 1;
  }
}
