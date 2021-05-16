package com.tn.assetmanagement.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.tn.assetmanagement.respository.FindRepositoryImpl.SQL_WHERE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.tn.assetmanagement.util.BiConsumerWithThrows;
import com.tn.assetmanagement.util.FunctionWithThrows;

class FindRepositoryImplTest
{
  private static final String ID_PREDICATE = "id = ?";
  @SuppressWarnings("SqlResolve")
  private static final String SQL_SELECT = "SELECT * FROM test";

  @Test
  void testFindAll() throws Exception
  {
    Object entity1 = new Object();
    Object entity2 = new Object();
    Object entity3 = new Object();

    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.next()).thenReturn(true, true, true, false);

    Connection connection = mock(Connection.class);
    when(connection.prepareStatement(SQL_SELECT)).thenReturn(mock(PreparedStatement.class));

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.query(isA(PreparedStatementCreator.class), isA(ResultSetExtractor.class))).thenAnswer(
      invocation ->
      {
        assertNotNull(invocation.<PreparedStatementCreator>getArgument(0).createPreparedStatement(connection));
        return invocation.<ResultSetExtractor<Collection<Object>>>getArgument(1).extractData(resultSet);
      }
    );

    @SuppressWarnings("unchecked")
    FunctionWithThrows<ResultSet, Object, SQLException> entityFactory = mock(FunctionWithThrows.class);
    when(entityFactory.apply(resultSet)).thenReturn(entity1, entity2, entity3);

    //noinspection unchecked
    assertEquals(List.of(entity1, entity2, entity3), findRepository(jdbcTemplate, mock(BiConsumerWithThrows.class), entityFactory).findAll());

    verify(connection).prepareStatement(SQL_SELECT);
  }

  @Test
  void testFindAllWithNoData() throws Exception
  {
    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.next()).thenReturn(false);

    Connection connection = mock(Connection.class);
    when(connection.prepareStatement(SQL_SELECT)).thenReturn(mock(PreparedStatement.class));

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.query(isA(PreparedStatementCreator.class), isA(ResultSetExtractor.class))).thenAnswer(
      invocation ->
      {
        assertNotNull(invocation.<PreparedStatementCreator>getArgument(0).createPreparedStatement(connection));
        return invocation.<ResultSetExtractor<Collection<Object>>>getArgument(1).extractData(resultSet);
      }
    );

    @SuppressWarnings("unchecked")
    FunctionWithThrows<ResultSet, Object, SQLException> entityFactory = mock(FunctionWithThrows.class);

    //noinspection unchecked
    assertTrue(findRepository(jdbcTemplate, mock(BiConsumerWithThrows.class), entityFactory).findAll().isEmpty());

    verify(connection).prepareStatement(SQL_SELECT);
  }

  @Test
  void testFindAllWithException()
  {
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.query(isA(PreparedStatementCreator.class), isA(ResultSetExtractor.class))).thenThrow(new DataAccessException("Testing"){});

    //noinspection unchecked
    assertThrows(RepositoryException.class, () -> findRepository(jdbcTemplate, mock(BiConsumerWithThrows.class), mock(FunctionWithThrows.class)).findAll());
  }

  @Test
  void testFindForId() throws Exception
  {
    int id = 10;
    Object entity = new Object();

    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.next()).thenReturn(true, false);

    PreparedStatement preparedStatement = mock(PreparedStatement.class);

    Connection connection = mock(Connection.class);
    when(connection.prepareStatement(SQL_SELECT + SQL_WHERE + ID_PREDICATE)).thenReturn(preparedStatement);

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.query(isA(PreparedStatementCreator.class), isA(PreparedStatementSetter.class), isA(ResultSetExtractor.class))).thenAnswer(
      invocation ->
      {
        invocation.<PreparedStatementSetter>getArgument(1).setValues(invocation.<PreparedStatementCreator>getArgument(0).createPreparedStatement(connection));
        return invocation.<ResultSetExtractor<Collection<Object>>>getArgument(2).extractData(resultSet);
      }
    );

    @SuppressWarnings("unchecked")
    BiConsumerWithThrows<PreparedStatement, Integer, SQLException> idSetter = mock(BiConsumerWithThrows.class);

    @SuppressWarnings("unchecked")
    FunctionWithThrows<ResultSet, Object, SQLException> entityFactory = mock(FunctionWithThrows.class);
    when(entityFactory.apply(resultSet)).thenReturn(entity);

    assertEquals(entity, findRepository(jdbcTemplate, idSetter, entityFactory).findForId(id).orElseThrow());

    verify(connection).prepareStatement(SQL_SELECT + SQL_WHERE + ID_PREDICATE);
    verify(idSetter).accept(isA(PreparedStatement.class), eq(id));
  }

  @Test
  void testFindForIdWithNoData() throws Exception
  {
    int id = 10;

    ResultSet resultSet = mock(ResultSet.class);
    when(resultSet.next()).thenReturn(false);

    PreparedStatement preparedStatement = mock(PreparedStatement.class);

    Connection connection = mock(Connection.class);
    when(connection.prepareStatement(SQL_SELECT + SQL_WHERE + ID_PREDICATE)).thenReturn(preparedStatement);

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.query(isA(PreparedStatementCreator.class), isA(PreparedStatementSetter.class), isA(ResultSetExtractor.class))).thenAnswer(
      invocation ->
      {
        invocation.<PreparedStatementSetter>getArgument(1).setValues(invocation.<PreparedStatementCreator>getArgument(0).createPreparedStatement(connection));
        return invocation.<ResultSetExtractor<Collection<Object>>>getArgument(2).extractData(resultSet);
      }
    );

    @SuppressWarnings("unchecked")
    BiConsumerWithThrows<PreparedStatement, Integer, SQLException> idSetter = mock(BiConsumerWithThrows.class);

    @SuppressWarnings("unchecked")
    FunctionWithThrows<ResultSet, Object, SQLException> entityFactory = mock(FunctionWithThrows.class);

    assertTrue(findRepository(jdbcTemplate, idSetter, entityFactory).findForId(id).isEmpty());

    verify(connection).prepareStatement(SQL_SELECT + SQL_WHERE + ID_PREDICATE);
    verify(idSetter).accept(isA(PreparedStatement.class), eq(id));
  }

  @Test
  void testFindForIdWithException()
  {
     JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.query(isA(PreparedStatementCreator.class), isA(PreparedStatementSetter.class), isA(ResultSetExtractor.class))).thenThrow(
      new DataAccessException("Testing"){}
    );

    //noinspection unchecked
    assertThrows(RepositoryException.class, () -> findRepository(jdbcTemplate, mock(BiConsumerWithThrows.class), mock(FunctionWithThrows.class)).findForId(10));
  }

  private static FindRepositoryImpl<Integer, Object> findRepository(
    JdbcTemplate jdbcTemplate,
    BiConsumerWithThrows<PreparedStatement, Integer, SQLException> idSetter,
    FunctionWithThrows<ResultSet, Object, SQLException> entityFactory
  )
  {
    return new FindRepositoryImpl<>(
      "test",
      "tests",
      jdbcTemplate,
      SQL_SELECT,
      ID_PREDICATE,
      idSetter,
      entityFactory
    );
  }
}
