package com.tn.assetmanagement.acceptance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpConfiguration
{
  @Bean
  WebClient.Builder webClientBuilder()
  {
    return WebClient.builder();
  }
}
