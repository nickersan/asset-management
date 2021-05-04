package com.tn.assetmanagement.funds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tn.assetmanagement.funds.handler.FundHandler;
import com.tn.assetmanagement.funds.repository.FundRepository;

@Configuration
class HandlerConfiguration
{
  @Bean
  FundHandler fundHandler(FundRepository fundRepository)
  {
    return new FundHandler(fundRepository);
  }
}
