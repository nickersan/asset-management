package com.tn.assetmanagement.acceptance;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static com.tn.assetmanagement.acceptance.Fields.FIELD_ID;
import static com.tn.assetmanagement.acceptance.Fields.FIELD_NAME;
import static com.tn.assetmanagement.acceptance.Fields.FIELD_TICKER;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public abstract class TestMode implements AutoCloseable
{
  protected static final String PATH_FUND = "/1/fund";
  @SuppressWarnings("HttpUrlsUsage")
  protected static final String TEMPLATE_HOST = "http://%s:%d";

  private static final String PATH_SEPARATOR = "/";

  private Map<String, JsonObject> funds;

  public final void initialize() throws TestException
  {
    initializeSystem();
    this.funds = new HashMap<>();
  }

  public void assertFund(JsonObject expectedFund, JsonObject actualFund)
  {
    assertNotNull(expectedFund);
    assertNotNull(actualFund);
    assertEquals(expectedFund.getInt(FIELD_ID), actualFund.getInt(FIELD_ID));
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

  public void assertFundDeleted(JsonObject fund)
  {
    webClient(fundUrl(fund.getInt(FIELD_ID))).get()
      .header(ACCEPT, APPLICATION_JSON.getType())
      .exchange()
      .expectStatus().is4xxClientError();
  }
  
  public Collection<JsonObject> getCachedFunds()
  {
    return this.funds.values();
  }

  public JsonObject getFund(int fundId)
  {
    return get(fundUrl(fundId), this::toJsonObject);
  }

  public JsonArray getFunds()
  {
    return get(fundUrl(), this::toJsonArray);
  }

  public void createFunds(Collection<JsonObject> funds)
  {
    post(fundUrl(), funds).forEach(fund -> this.funds.put(fund.getString(FIELD_TICKER), fund));
  }

  public void deleteFund(JsonObject fund)
  {
    delete(fundUrl(), fund.getInt(FIELD_ID));
  }

  public void deleteFunds(Collection<JsonObject> funds)
  {
    funds.forEach(this::deleteFund);
  }
  
  public JsonObject updateFund(JsonObject fund)
  {
    JsonObject updated = post(fundUrl(), fund);
    this.funds.put(updated.getString(FIELD_TICKER), updated);

    return updated;
  }
  
  public void finish()
  {
    deleteFunds(getCachedFunds());
  }

  protected abstract String getFundHost();

  protected abstract int getFundPort();

  protected abstract void initializeSystem() throws TestException;
  
  private String fundUrl()
  {
    return format(TEMPLATE_HOST, getFundHost(), getFundPort()) + PATH_FUND;
  }

  private String fundUrl(int id)
  {
    return fundUrl() + toPathElement(id);
  }
  
  private <T> T get(String baseUrl, Function<String, T> mapping)
  {
    return mapping.apply(
      webClient(baseUrl).get()
        .header(ACCEPT, APPLICATION_JSON.getType())
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody()
    );
  }

  private void delete(String baseUrl, Collection<?> ids)
  {
    ids.forEach(id -> this.delete(baseUrl, id));
  }

  private void delete(String baseUrl, Object id)
  {
    webClient(baseUrl + toPathElement(id)).delete()
      .exchange()
      .expectStatus().is2xxSuccessful();
  }

  private Collection<JsonObject> post(String baseUrl, Collection<JsonObject> bodies)
  {
    return bodies.stream().map(body -> post(baseUrl, body)).collect(toList());
  }

  private JsonObject post(String baseUrl, JsonObject body)
  {
    return toJsonObject(
      webClient(baseUrl)
        .post()
        .contentType(APPLICATION_JSON)
        .body(Mono.just(body.toString()), String.class)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(String.class)
        .returnResult()
        .getResponseBody()
    );
  }

  private JsonArray toJsonArray(String s)
  {
    return Json.createReader(new StringReader(s)).readArray();
  }

  private JsonObject toJsonObject(String s)
  {
    return Json.createReader(new StringReader(s)).readObject();
  }

  private String toPathElement(Object obj)
  {
    return PATH_SEPARATOR + obj;
  }

  private WebTestClient webClient(String baseUrl)
  {
    return WebTestClient.bindToServer().baseUrl(baseUrl).build();
  }  
}
