package com.tn.assetmanagement.web;

import static java.util.Collections.emptyMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import static com.tn.assetmanagement.web.Handlers.badRequest;
import static com.tn.assetmanagement.web.Handlers.internalServerError;
import static com.tn.assetmanagement.web.Handlers.invoke;
import static com.tn.assetmanagement.web.Handlers.noArg;
import static com.tn.assetmanagement.web.Handlers.ok;
import static com.tn.assetmanagement.web.Handlers.okWithBody;
import static com.tn.assetmanagement.web.Handlers.pathVariable;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.tn.assetmanagement.util.FunctionWithThrows;

class HandlersTest
{
  @Test
  void testBadRequest()
  {
    StepVerifier.create(badRequest("Testing").apply(new Exception("Error")))
      .expectNextMatches(status(HttpStatus.BAD_REQUEST))
      .verifyComplete();
  }

  @Test
  void testOk()
  {
    StepVerifier.create(ok())
      .expectNextMatches(status(OK))
      .verifyComplete();
  }

  @Test
  void testOkWithBody()
  {
    String body = "{'test':'ing'}";
    StepVerifier.create(okWithBody(body))
      .expectNextMatches(status(OK).and(body(body)))
      .verifyComplete();
  }

  @Test
  void testOkWithBodyCollection()
  {
    Collection<String> body = List.of("Testing", "One", "Two");
    StepVerifier.create(okWithBody(body, String.class))
      .expectNextMatches(status(OK).and(body(body)))
      .verifyComplete();
  }

  @Test
  void testInternalServerError()
  {
    StepVerifier.create(internalServerError("Unit Test").apply(new Exception("Testing")))
      .expectNextMatches(status(INTERNAL_SERVER_ERROR))
      .verifyComplete();
  }

  @Test
  void testInvoke() throws Exception
  {
    String subject = "Test";
    int result = 10;

    @SuppressWarnings("unchecked")
    FunctionWithThrows<String, Integer, Exception> action = mock(FunctionWithThrows.class);
    when(action.apply(subject)).thenReturn(result);

    @SuppressWarnings("unchecked")
    BiFunction<String, Integer, Mono<ServerResponse>> successResponseBuilder = mock(BiFunction.class);
    when(successResponseBuilder.apply(subject, result)).thenReturn(ServerResponse.ok().build());

    @SuppressWarnings("unchecked")
    Function<Throwable, Mono<ServerResponse>> errorResponseBuilder = mock(Function.class);

    StepVerifier.create(invoke(action, successResponseBuilder, errorResponseBuilder).apply(subject))
      .expectNextMatches(status(OK))
      .verifyComplete();
  }

  @Test
  void testInvokeWithError() throws Exception
  {
    String subject = "Test";
    Exception e = new Exception("Testing");

    @SuppressWarnings("unchecked")
    FunctionWithThrows<String, Integer, Exception> action = mock(FunctionWithThrows.class);
    when(action.apply(subject)).thenThrow(e);

    @SuppressWarnings("unchecked")
    BiFunction<String, Integer, Mono<ServerResponse>> successResponseBuilder = mock(BiFunction.class);

    @SuppressWarnings("unchecked")
    Function<Throwable, Mono<ServerResponse>> errorResponseBuilder = mock(Function.class);
    when(errorResponseBuilder.apply(e)).thenReturn(ServerResponse.status(INTERNAL_SERVER_ERROR).build());

    StepVerifier.create(invoke(action, successResponseBuilder, errorResponseBuilder).apply(subject))
      .expectNextMatches(status(INTERNAL_SERVER_ERROR))
      .verifyComplete();
  }

  @Test
  void testNoArg()
  {
    Mono<ServerResponse> responseMono = ServerResponse.ok().build();

    ServerRequest request = mock(ServerRequest.class);
    when(request.method()).thenReturn(HttpMethod.GET);

    assertEquals(responseMono, noArg(() -> responseMono).handle(request));
  }

  @Test
  void testPathVariable()
  {
    String name = "test";
    String value = "value";

    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariables()).thenReturn(Map.of(name, value));

    assertEquals(value, pathVariable(request, name).orElseThrow());
  }

  @Test
  void testPathVariableWithoutValue()
  {
    String name = "test";

    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariables()).thenReturn(emptyMap());

    assertTrue(pathVariable(request, name).isEmpty());
  }

  @Test
  void testPathVariableWithConversion()
  {
    String name = "test";
    String value = "10";

    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariables()).thenReturn(Map.of(name, value));

    assertEquals(Integer.valueOf(value), pathVariable(request, name, Integer::parseInt).orElseThrow());
  }

  @Test
  void testPathVariableWithoutValueAndWithConversion()
  {
    String name = "test";

    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariables()).thenReturn(emptyMap());

    assertTrue(pathVariable(request, name, Integer::parseInt).isEmpty());
  }

  @Test
  void testPathVariableWithConversionError()
  {
    String name = "test";
    String value = "X";

    ServerRequest request = mock(ServerRequest.class);
    when(request.pathVariables()).thenReturn(Map.of(name, value));

    assertThrows(NumberFormatException.class, () -> pathVariable(request, name, Integer::parseInt).orElseThrow());
  }

  private static Predicate<ServerResponse> status(HttpStatus expectedStatus)
  {
    return serverResponse -> expectedStatus.equals(serverResponse.statusCode());
  }

  private static <T> Predicate<ServerResponse> body(T expectedBody)
  {
    //noinspection unchecked
    return serverResponse -> expectedBody.equals(((EntityResponse<Mono<T>>)serverResponse).entity().block(Duration.ofMillis(100)));
  }

  private static <T> Predicate<ServerResponse> body(Collection<T> expectedBody)
  {
    //noinspection unchecked
    return serverResponse -> Boolean.TRUE.equals(((EntityResponse<Flux<T>>)serverResponse).entity().all(expectedBody::contains).block(Duration.ofMillis(100)));
  }
}
