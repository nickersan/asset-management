package com.tn.assetmanagement.funds.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static com.tn.assetmanagement.web.Handlers.PATH_VARIABLE_ID;
import static com.tn.assetmanagement.web.Handlers.badRequest;
import static com.tn.assetmanagement.web.Handlers.internalServerError;
import static com.tn.assetmanagement.web.Handlers.invoke;
import static com.tn.assetmanagement.web.Handlers.okWithBody;
import static com.tn.assetmanagement.web.Handlers.pathVariable;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import com.tn.assetmanagement.funds.domain.Fund;
import com.tn.assetmanagement.funds.repository.FundRepository;
import com.tn.assetmanagement.funds.repository.RepositoryException;
import com.tn.assetmanagement.web.Handlers;

public class FundHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger(FundHandler.class);

  private final FundRepository fundRepository;

  public FundHandler(FundRepository fundRepository)
  {
    this.fundRepository = fundRepository;
  }

  public Mono<ServerResponse> get(ServerRequest serverRequest)
  {
    try
    {
      Optional<Integer> id = pathVariable(serverRequest, PATH_VARIABLE_ID, Integer::parseInt);

      if (id.isPresent())
      {
        LOGGER.debug("GET: {}", id.get());
        return this.fundRepository.findForId(id.get())
          .map(Handlers::okWithBody)
          .orElseGet(ServerResponse.notFound()::build);
      }
      else
      {
        LOGGER.error("Fund ID missing");
        return ServerResponse.badRequest().build();
      }
    }
    catch (RepositoryException e)
    {
      LOGGER.error("Error invoking repository: " + e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,  e.getMessage(), e);
    }
    catch (NumberFormatException e)
    {
      LOGGER.error("Fund ID invalid: " + e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  e.getMessage(), e);
    }
  }

  public Mono<ServerResponse> getAll()
  {
    try
    {
      LOGGER.debug("GET: all");
      return okWithBody(this.fundRepository.findAll());
    }
    catch (RepositoryException e)
    {
      LOGGER.error("Error invoking repository: " + e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,  e.getMessage(), e);
    }
  }

  public Mono<ServerResponse> post(ServerRequest serverRequest)
  {
    return save(serverRequest);
  }

  public Mono<ServerResponse> put(ServerRequest serverRequest)
  {
    return save(serverRequest);
  }

  public Mono<ServerResponse> delete(ServerRequest serverRequest)
  {
    try
    {
      Optional<Integer> id = pathVariable(serverRequest, PATH_VARIABLE_ID, Integer::parseInt);

      if (id.isPresent())
      {
        LOGGER.debug("DELETE: {}", id.get());
        this.fundRepository.delete(id.get());
        return ServerResponse.ok().build();
      }
      else
      {
        LOGGER.error("Fund ID missing");
        return ServerResponse.badRequest().build();
      }
    }
    catch (NumberFormatException e)
    {
      LOGGER.error("Fund ID invalid: " + e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  e.getMessage(), e);
    }
    catch (Exception e)
    {
      LOGGER.error("Error invoking repository: " + e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,  e.getMessage(), e);
    }
  }

  private Mono<ServerResponse> save(ServerRequest serverRequest)
  {
    return serverRequest
      .bodyToMono(Fund.class)
      .doOnNext(fund -> LOGGER.debug("{}: {}", serverRequest.method(), fund))
      .flatMap(invoke(this.fundRepository::save, this::setFundId, internalServerError("Failed to save fund")))
      .onErrorResume(IllegalArgumentException.class, badRequest("Failed to save fund"))
      .onErrorResume(internalServerError("Failed to save fund"));
  }

  private Mono<ServerResponse> setFundId(Fund fund, Integer fundId)
  {
    return ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(fund.withId(fundId)), Fund.class);
  }
}
