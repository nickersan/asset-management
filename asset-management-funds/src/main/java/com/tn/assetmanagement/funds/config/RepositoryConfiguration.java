package com.tn.assetmanagement.funds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tn.assetmanagement.funds.repository.FundRepository;
import com.tn.assetmanagement.funds.repository.FundRepositoryImpl;

@Configuration
class RepositoryConfiguration
{
  @Bean
  FundRepository fundRepository(JdbcTemplate jdbcTemplate)
  {
    return new FundRepositoryImpl(jdbcTemplate);
  }
}

