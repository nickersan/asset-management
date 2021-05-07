package com.tn.assetmanagement.acceptance;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static com.tn.assetmanagement.acceptance.Fields.FIELD_NAME;
import static com.tn.assetmanagement.acceptance.Fields.FIELD_TICKER;

import java.io.StringReader;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class TestMode implements AutoCloseable
{
  protected static final String PATH_FUND = "/1/fund";
  protected static final String TEMPLATE_HOST = "http://%s:%d";

  private static final Duration TIMEOUT = Duration.ofSeconds(5);
  private static final Duration TIMEOUT_DEBUG = Duration.ofMinutes(5);

  private final boolean apiDebug;
  private final WebClient.Builder webClientBuilder;

  private Map<String, JsonObject> funds;

  public TestMode(WebClient.Builder webClientBuilder, boolean apiDebug)
  {
    this.webClientBuilder = webClientBuilder;
    this.apiDebug = apiDebug;
  }

  public final void initialize() throws TestException
  {
    initializeSystem();
    this.funds = new HashMap<>();
  }

  public void assertFund(JsonObject expectedFund, JsonObject actualFund)
  {
    assertNotNull(expectedFund);
    assertNotNull(actualFund);
    assertEquals(expectedFund.getString(FIELD_TICKER), actualFund.getString(FIELD_TICKER));
    assertEquals(expectedFund.getString(FIELD_NAME), actualFund.getString(FIELD_NAME));
  }

  public void assertFunds(Collection<JsonObject> expectedFunds, JsonArray actualFunds)
  {
    actualFunds.stream()
      .map(JsonObject.class::cast)
      .forEach(
        actualFund -> assertFund(
          expectedFunds.stream()
            .filter(expectedFund -> expectedFund.getString(FIELD_TICKER).equals(actualFund.getString(FIELD_TICKER)))
            .findFirst()
            .orElse(null),
          actualFund
        )
      );
  }

  public Collection<JsonObject> getCreatedFunds()
  {
    return this.funds.values();
  }

  public JsonArray getFunds()
  {
    return getArray(format(TEMPLATE_HOST, getFundHost(), getFundPort()) + PATH_FUND);
  }

  public void createFunds(Collection<JsonObject> funds)
  {
    post(format(TEMPLATE_HOST, getFundHost(), getFundPort()) + PATH_FUND, funds).forEach(fund -> this.funds.put(fund.getString(FIELD_TICKER), fund));
  }

  protected boolean isApiDebug()
  {
    return this.apiDebug;
  }

  protected abstract String getFundHost();

  protected abstract int getFundPort();

  protected abstract void initializeSystem() throws TestException;

  private Duration getTimeout()
  {
    return isApiDebug() ? TIMEOUT_DEBUG : TIMEOUT;
  }

  private JsonArray getArray(String baseUrl)
  {
    WebClient webClient = this.webClientBuilder.baseUrl(baseUrl).build();

    return webClient.get()
      .exchangeToMono(Mono::just)
      .map(this::bodyToJsonArray)
      .blockOptional(getTimeout())
      .orElseThrow(() -> new TestException("Error reading response body"));
  }

  private Collection<JsonObject> post(String baseUrl, Collection<JsonObject> bodies)
  {
    WebClient webClient = this.webClientBuilder.baseUrl(baseUrl).build();

    return bodies.stream()
      .map(body -> webClient.post().contentType(APPLICATION_JSON).body(Mono.just(body.toString()), String.class).exchangeToMono(Mono::just))
      .map(mono -> mono.blockOptional(getTimeout()).orElseThrow(() -> new TestException("Error sending request")))
      .map(this::bodyToJsonObject)
      .collect(toList());
  }

  private JsonArray bodyToJsonArray(ClientResponse clientResponse) throws TestException
  {
    if (clientResponse.statusCode().isError()) throw new TestException("Error sending request: " + clientResponse.statusCode().value());

    return clientResponse.bodyToMono(String.class)
      .blockOptional(getTimeout())
      .map(this::toJsonArray)
      .orElseThrow(() -> new TestException("Error reading response body"));
  }

  private JsonObject bodyToJsonObject(ClientResponse clientResponse) throws TestException
  {
    if (clientResponse.statusCode().isError()) throw new TestException("Error sending request: " + clientResponse.statusCode().value());
    //if (clientResponse.headers().contentType().orElseGet(null)

    return clientResponse.bodyToMono(String.class)
      .blockOptional(getTimeout())
      .map(this::toJsonObject)
      .orElseThrow(() -> new TestException("Error reading response body"));
  }

  private JsonArray toJsonArray(String s)
  {
    return Json.createReader(new StringReader(s)).readArray();
  }

  private JsonObject toJsonObject(String s)
  {
    return Json.createReader(new StringReader(s)).readObject();
  }
}
