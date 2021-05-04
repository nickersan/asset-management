package com.tn.assetmanagement.web;

import static java.lang.String.format;

import org.springframework.web.reactive.function.client.ClientResponse;

public class HttpException extends Exception
{
  private static final String TEMPLATE_MESSAGE = "%s: %d";

  private final ClientResponse clientResponse;

  public HttpException(String message, ClientResponse clientResponse)
  {
    super(format(TEMPLATE_MESSAGE, message, clientResponse.statusCode().value()));
    this.clientResponse = clientResponse;
  }

  public ClientResponse getClientResponse()
  {
    return this.clientResponse;
  }
}
