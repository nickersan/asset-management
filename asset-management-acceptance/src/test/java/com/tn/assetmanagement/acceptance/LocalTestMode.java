package com.tn.assetmanagement.acceptance;

import static java.util.stream.Collectors.toList;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTestMode extends TestMode
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalTestMode.class);

  private static final String HOST = "localhost";
  private static final int PORT_FUND = 8080;

  @Override
  protected String getFundHost()
  {
    return HOST;
  }

  @Override
  protected int getFundPort()
  {
    return PORT_FUND;
  }

  @Override
  protected void initializeSystem()
  {
    //Deleting all the funds the exist in the system - should be safe locally.
    LOGGER.info("Clearing existing funds");
    deleteFunds(getFunds().stream().map(JsonObject.class::cast).collect(toList()));
  }

  @Override
  public void close()
  {
    // Intentionally blank - required by super class.
  }
}
