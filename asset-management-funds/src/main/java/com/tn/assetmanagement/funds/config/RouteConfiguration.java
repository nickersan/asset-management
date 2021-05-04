package com.tn.assetmanagement.funds.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.method;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import static com.tn.assetmanagement.web.Handlers.noArg;
import static com.tn.assetmanagement.web.Paths.idPath;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.tn.assetmanagement.funds.handler.FundHandler;

@Configuration
class RouteConfiguration
{
  private static final String PATH_VERSION_1 = "/1";
  private static final String PATH_FUND = "/fund";

  @Bean
  RouterFunction<ServerResponse> routerFunction(FundHandler fundHandler)
  {
    return nest(
      path(PATH_VERSION_1),
      nest(
        path(PATH_FUND),
        route(method(GET).and(path(idPath())), fundHandler::get)
          .andRoute(method(GET), noArg(fundHandler::getAll))
          .andRoute(method(POST), fundHandler::post)
          .andRoute(method(PUT).and(path(idPath())), fundHandler::put)
          .andRoute(method(DELETE).and(path(idPath())), fundHandler::delete)
      )
    );
  }
}
