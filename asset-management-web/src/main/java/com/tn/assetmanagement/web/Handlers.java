package com.tn.assetmanagement.web;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static com.tn.assetmanagement.util.Lambdas.wrapFunction;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.tn.assetmanagement.util.FunctionWithThrows;
import com.tn.assetmanagement.util.WrappedException;

public class Handlers
{
  private static final Logger LOGGER = LoggerFactory.getLogger(Handlers.class);

  public static final String PATH_VARIABLE_ID = "id";

  public static Function<Throwable, Mono<ServerResponse>> badRequest(String message)
  {
    return e ->
    {
      LOGGER.error(message, e);
      return ServerResponse.badRequest().build();
    };
  }

  public static Mono<ServerResponse> ok() {

    return ServerResponse.ok().build();
  }

  public static Mono<ServerResponse> okWithBody(Object body)
  {
    return ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(body), body.getClass());
  }

  public static <T> Mono<ServerResponse> okWithBody(Collection<T> body, Class<T> type)
  {
    return ServerResponse.ok().contentType(APPLICATION_JSON).body(Flux.just(body.toArray()), type);
  }

  public static Function<Throwable, Mono<ServerResponse>> internalServerError(String message)
  {
    return e ->
    {
      LOGGER.error(message, e instanceof WrappedException ? e.getCause() : e);
      return ServerResponse.status(INTERNAL_SERVER_ERROR).build();
    };
  }

  public static <T, R> Function<T, Mono<ServerResponse>> invoke(
    FunctionWithThrows<T, R, Exception> action,
    BiFunction<T, R, Mono<ServerResponse>> successResponseBuilder,
    Function<Throwable, Mono<ServerResponse>> errorResponseBuilder
  )
  {
    return t ->
    {
      try
      {
        LOGGER.debug("Invoking action with: {}", t);
        R result = action.apply(t);

        return successResponseBuilder.apply(t, result);
      }
      catch (Exception e)
      {
        return errorResponseBuilder.apply(e);
      }
    };
  }

  public static HandlerFunction<ServerResponse> noArg(Supplier<Mono<ServerResponse>> supplier)
  {
    return serverRequest -> supplier.get();
  }

  public static Optional<String> pathVariable(ServerRequest serverRequest, String variableName)
  {
    String id = serverRequest.pathVariables().get(variableName);
    return id != null ? Optional.of(id) : Optional.empty();
  }

  public static <T, E extends Exception> Optional<T> pathVariable(ServerRequest serverRequest, String variableName, FunctionWithThrows<String, T, E> conversionFunction) throws E
  {
    try
    {
      return pathVariable(serverRequest, variableName)
        .map(wrapFunction(conversionFunction));
    }
    catch (WrappedException e)
    {
      //noinspection unchecked
      throw (E)e.getCause();
    }
  }

  public static <T> Function<ClientResponse, Flux<T>> toFlux(Class<T> type, Function<ClientResponse, Exception> exceptionSupplier)
  {
    return clientResponse ->
    {
      if (clientResponse.statusCode().is4xxClientError()) return Flux.empty();
      else if (clientResponse.statusCode().isError()) return Flux.error(exceptionSupplier.apply(clientResponse));
      else return clientResponse.bodyToFlux(type);
    };
  }

  public static <T> Function<ClientResponse, Mono<T>> toMono(Class<T> type, Function<ClientResponse, Exception> exceptionSupplier)
  {
    return clientResponse ->
    {
      if (clientResponse.statusCode().isError()) return Mono.error(exceptionSupplier.apply(clientResponse));
      else return clientResponse.bodyToMono(type);
    };
  }

  public static Function<ClientResponse, Mono<Void>> toVoid(Function<ClientResponse, Exception> exceptionSupplier)
  {
    return clientResponse -> clientResponse.statusCode().isError() ? Mono.error(exceptionSupplier.apply(clientResponse)) : Mono.empty();
  }
}
