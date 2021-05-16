package com.tn.assetmanagement.respository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tn.assetmanagement.util.BiFunctionWithThrows;

class SaveRepositoryImplTest
{
  private static final String SQL_SAVE = "{CALL ? = package.save(?, ?)}";
  private static final int INDEX_CALLBACK = 1;

  @Test
  void testSave() throws Exception
  {
    Object entity = new Object();
    Integer id = 10;

    CallableStatement callableStatement = mock(CallableStatement.class);

    @SuppressWarnings("unchecked")
    BiFunctionWithThrows<CallableStatement, Object, Integer, SQLException> callableStatementCallback = mock(BiFunctionWithThrows.class);
    when(callableStatementCallback.apply(callableStatement, entity)).thenReturn(id);

    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.execute(eq(SQL_SAVE), isA(CallableStatementCallback.class))).thenAnswer(
      invocation -> invocation.<CallableStatementCallback<Void>>getArgument(INDEX_CALLBACK).doInCallableStatement(callableStatement)
    );

    assertEquals(id, saveRepository(jdbcTemplate, callableStatementCallback).save(entity));
  }

  @Test
  void testSaveWithException()
  {
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    //noinspection unchecked
    when(jdbcTemplate.execute(eq(SQL_SAVE), isA(CallableStatementCallback.class))).thenThrow(new DataAccessException("testing"){});

    //noinspection unchecked
    assertThrows(RepositoryException.class, () -> saveRepository(jdbcTemplate, mock(BiFunctionWithThrows.class)).save(10));
  }

  private static SaveRepository<Integer, Object> saveRepository(
    JdbcTemplate jdbcTemplate,
    BiFunctionWithThrows<CallableStatement, Object, Integer, SQLException> callableStatementCallback
  )
  {
    return new SaveRepositoryImpl<>("test", jdbcTemplate, SQL_SAVE, callableStatementCallback);
  }
}
