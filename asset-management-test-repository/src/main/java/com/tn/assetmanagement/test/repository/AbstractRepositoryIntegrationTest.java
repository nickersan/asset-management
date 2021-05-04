package com.tn.assetmanagement.test.repository;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(
  properties = {
    "spring.datasource.url=jdbc:oracle:thin:@localhost:${oracle.port}:XE",
    "spring.datasource.username=FUNDS",
    "spring.datasource.password=P4$$word123"
  }
)
@Testcontainers
public class AbstractRepositoryIntegrationTest
{
  static final String PASSWORD_ORACLE_SYSTEM = "P4$$word123";

  private static final String CHARACTER_SET_ORACLE = "AL32UTF8";
  private static final String DIRECTORY_PROJECT_ROOT = ".";
  private static final String DIRECTORY_CONTAINER_ORACLE_DATA = "/opt/oracle/oradata";
  private static final String DIRECTORY_CONTAINER_ORACLE_SETUP = "/opt/oracle/scripts/setup";
  private static final String DIRECTORY_HOST_ORACLE_DATA = "target/oradata";
  private static final String DIRECTORY_HOST_ORACLE_SETUP = "src/it/resources/oracle/setup";
  private static final DockerImageName DOCKER_IMAGE_ORACLE = DockerImageName.parse("oracle/database").withTag("18.4.0-xe");
  private static final String EV_ORACLE_PASSWORD = "ORACLE_PWD";
  private static final String EV_ORACLE_CHARACTER_SET = "ORACLE_CHARACTERSET";
  private static final String LOGGER_ORACLE = "oracle";
  private static final int PORT_ORACLE_LISTENER = 1521;
  private static final Duration ORACLE_STARTUP_TIMEOUT = Duration.ofMinutes(30);

  @DynamicPropertySource
  @SuppressWarnings("unused")
  static void dataSourceProperties(DynamicPropertyRegistry registry)
  {
    registry.add("oracle.port", () -> ORACLE_XE.getMappedPort(PORT_ORACLE_LISTENER));
  }

  @Container
  private static final GenericContainer<?> ORACLE_XE = new GenericContainer<>(DOCKER_IMAGE_ORACLE)
    .withExposedPorts(PORT_ORACLE_LISTENER)
    .withFileSystemBind(directory(DIRECTORY_HOST_ORACLE_DATA), DIRECTORY_CONTAINER_ORACLE_DATA)
    .withFileSystemBind(directory(DIRECTORY_HOST_ORACLE_SETUP), DIRECTORY_CONTAINER_ORACLE_SETUP)
    .withEnv(EV_ORACLE_PASSWORD, PASSWORD_ORACLE_SYSTEM)
    .withEnv(EV_ORACLE_CHARACTER_SET, CHARACTER_SET_ORACLE)
    .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(LOGGER_ORACLE)))
    .waitingFor(new OracleWaitStrategy(PASSWORD_ORACLE_SYSTEM).withStartupTimeout(ORACLE_STARTUP_TIMEOUT));

  private static String directory(String path)
  {
    try
    {
      File oracleDataDirectory = new File(DIRECTORY_PROJECT_ROOT, path);

      if (!oracleDataDirectory.exists())
      {
        if (!oracleDataDirectory.mkdir()) throw new RuntimeException("Failed to create Oracle data-directory");
      }

      return oracleDataDirectory.getCanonicalPath();
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed to get Oracle data-directory", e);
    }
  }
}

