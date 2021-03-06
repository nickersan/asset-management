package com.tn.assetmanagement.acceptance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.tn.assetmanagement.acceptance.LocalTestMode;
import com.tn.assetmanagement.acceptance.TestMode;

@Configuration
@PropertySource("classpath:asset-management-test.properties")
@EnableAutoConfiguration
public class TestConfiguration
{
//  @Bean
//  @Profile("integrated")
//  TestMode integratedTestMode(
//    WebClient.Builder webClientBuilder,
//    @Value("${asset-management.acceptance.api.debug:false}") boolean apiDebug
//  )
//  {
//    return new IntegratedTestMode(webClientBuilder, webSocketClient, apiDebug, hazelcastDebug);
//  }

  @Bean
  @Profile("local")
  TestMode localTestMode()
  {
    return new LocalTestMode();
  }
}
