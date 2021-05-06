package com.tn.assetmanagement.test.web;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractWebIntegrationTest
{
  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_SCHEME = "http";

  @LocalServerPort
  int port;

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  WebTestClient webTestClient;

  private final String scheme;
  private final String host;

  public AbstractWebIntegrationTest()
  {
    this(DEFAULT_SCHEME, DEFAULT_HOST);
  }

  public AbstractWebIntegrationTest(String scheme, String host)
  {
    this.scheme = scheme;
    this.host = host;
  }

  protected void assertDelete(Function<UriBuilder, URI> uriFunction, HttpStatus expectedStatus)
  {
    this.webTestClient.delete().uri(uriFunction).exchange().expectStatus().isEqualTo(expectedStatus);
  }

  @SuppressWarnings("unchecked")
  protected <T> void assertGet(Function<UriBuilder, URI> uriFunction, HttpStatus expectedStatus, T... expected)
  {
    WebTestClient.ResponseSpec response = this.webTestClient.get().uri(uriFunction).exchange().expectStatus().isEqualTo(expectedStatus);
    if (expected.length > 0) response.expectBodyList(ParameterizedTypeReference.forType(expected.getClass().getComponentType())).contains(expected);
  }

  @SuppressWarnings("unchecked")
  protected <T> void assertPost(Function<UriBuilder, URI> uriFunction, Object body, HttpStatus expectedStatus, T... expected)
  {
    this.webTestClient.post().uri(uriFunction)
      .body(Mono.just(body), body.getClass())
      .exchange()
      .expectStatus().isEqualTo(expectedStatus)
      .expectBodyList(ParameterizedTypeReference.forType(expected.getClass().getComponentType()))
      .contains(expected);
  }

  @SuppressWarnings("unchecked")
  protected <T> void assertPut(Function<UriBuilder, URI> uriFunction, Object body, HttpStatus expectedStatus, T... expected)
  {
    this.webTestClient.put().uri(uriFunction)
      .body(Mono.just(body), body.getClass())
      .exchange()
      .expectStatus().isEqualTo(expectedStatus)
      .expectBodyList(ParameterizedTypeReference.forType(expected.getClass().getComponentType()))
      .contains(expected);
  }

  protected Function<UriBuilder, URI> url(Object... elements)
  {
    return uriBuilder -> uriBuilder
      .scheme(this.scheme)
      .host(this.host)
      .port(this.port)
      .pathSegment(Stream.of(elements).filter(Objects::nonNull).map(Object::toString).toArray(String[]::new))
      .build();
  }
}
