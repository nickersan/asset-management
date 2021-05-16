package com.tn.assetmanagement.respository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tn.assetmanagement.util.BiConsumerWithThrows;

class DeleteRepositoryImplTest
{
  private static final String SQL_DELETE = "{CALL package.delete(?)}";

  @Test
  void testDelete() throws Exception
  {
    Integer id = 10;

    CallableStatement callableStatement = mock(CallableStatement.class);

    @SuppressWarnings("unchecked")
    BiConsumerWithThrows<CallableStatement, Integer, SQLException> idSetter = mock(BiConsumerWithThrows.class);

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.execute(eq(SQL_DELETE), isA(CallableStatementCallback.class))).thenAnswer(
      invocation -> invocation.<CallableStatementCallback<Void>>getArgument(1).doInCallableStatement(callableStatement)
    );

    deleteRepository(jdbcTemplate, idSetter).delete(id);

    verify(idSetter).accept(callableStatement, id);
  }

  @Test
  void testDeleteWithException()
  {
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.execute(eq(SQL_DELETE), isA(CallableStatementCallback.class))).thenThrow(new DataAccessException("testing"){});

    //noinspection unchecked
    assertThrows(RepositoryException.class, () -> deleteRepository(jdbcTemplate, mock(BiConsumerWithThrows.class)).delete(10));
  }

  private static DeleteRepository<Integer> deleteRepository(JdbcTemplate jdbcTemplate, BiConsumerWithThrows<CallableStatement, Integer, SQLException> idSetter)
  {
    return new DeleteRepositoryImpl<>("test", jdbcTemplate, SQL_DELETE, idSetter);
  }
}
