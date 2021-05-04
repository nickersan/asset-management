package com.tn.assetmanagement.test.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;

public class OracleWaitStrategy implements WaitStrategy
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OracleWaitStrategy.class);

  private static final String ORACLE_CONNECTION_URL = "jdbc:oracle:thin:@localhost:%d:XE";
  private static final int ORACLE_PORT = 1521;
  private static final String ORACLE_SYSTEM_USER = "system";
  private static final String ORACLE_TEST_QUERY = "SELECT 1 FROM DUAL";

  private final String systemPassword;

  private Duration startupTimeout;

  public OracleWaitStrategy(String systemPassword)
  {
    this.systemPassword = systemPassword;
    this.startupTimeout = Duration.ofMinutes(5);
  }

  @Override
  @SuppressWarnings("BusyWait")
  public void waitUntilReady(WaitStrategyTarget waitStrategyTarget)
  {
    String connectionUrl = connectionUrl(waitStrategyTarget);

    LOGGER.info("Waiting for database connection to become available at {} using query '{}'", connectionUrl, ORACLE_TEST_QUERY);

    long start = System.currentTimeMillis();

    try
    {
      while (System.currentTimeMillis() < start + this.startupTimeout.toMillis())
      {
        try
        {
          if (!waitStrategyTarget.isRunning())
          {
            Thread.sleep(100L);
            continue;
          }

          try (Connection connection = connection(connectionUrl))
          {
            if (connection.createStatement().execute(ORACLE_TEST_QUERY))
            {
              LOGGER.info("Container is started (JDBC URL: {})", connectionUrl);
              break;
            }
          }
        }
        catch (Exception e)
        {
          LOGGER.debug("Failure when trying test query", e);
          Thread.sleep(100L);
        }
      }
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
      throw new ContainerLaunchException("Container startup wait was interrupted", e);
    }
  }

  @Override
  public WaitStrategy withStartupTimeout(Duration startupTimeout)
  {
    this.startupTimeout = startupTimeout;
    return this;
  }

  public Connection connection(String connectionString) throws SQLException
  {
    return DriverManager.getConnection(connectionString, ORACLE_SYSTEM_USER, this.systemPassword);
  }

  private String connectionUrl(WaitStrategyTarget waitStrategyTarget)
  {
    return String.format(ORACLE_CONNECTION_URL, waitStrategyTarget.getMappedPort(ORACLE_PORT));
  }
}
