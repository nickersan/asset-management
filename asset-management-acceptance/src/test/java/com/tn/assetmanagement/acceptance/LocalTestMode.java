package com.tn.assetmanagement.acceptance;

import java.util.Collection;
import javax.json.JsonObject;

import org.springframework.web.reactive.function.client.WebClient;

public class LocalTestMode extends TestMode
{
  private static final String HOST = "localhost";
  private static final int PORT_FUND = 8080;

  public LocalTestMode(WebClient.Builder webClientBuilder, boolean apiDebug)
  {
    super(webClientBuilder, apiDebug);
  }

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
    // No initialization required - local model expects the services to be running and listening on 8080.
  }

  @Override
  public void close()
  {
    // Intentionally blank - required by super class.
  }
}
