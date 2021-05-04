package com.tn.assetmanagement.funds.it.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

import com.tn.assetmanagement.funds.domain.Fund;
import com.tn.assetmanagement.funds.repository.FundRepository;
import com.tn.assetmanagement.test.repository.AbstractRepositoryIntegrationTest;

@ComponentScan("com.tn.assetmanagement.funds")
@TestPropertySource(
  properties = {
    "spring.datasource.username=FUNDS",
    "spring.datasource.password=P4$$word123"
  }
)
class FundRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest
{
  @Autowired
  FundRepository fundRepository;

  @Test
  void testCrud()
  {
    Fund fund1 = new Fund("Fund 1", "F1");
    fund1 = fund1.withId(this.fundRepository.save(fund1));

    Fund fund2 = new Fund("Fund 2", "F2");
    fund2 = fund2.withId(this.fundRepository.save(fund2));

    Fund fund3 = new Fund("Fund 3", "F3");
    fund3 = fund3.withId(this.fundRepository.save(fund3));

    Collection<Fund> funds = this.fundRepository.findAll();
    assertTrue(funds.containsAll(List.of(fund1, fund2, fund3)));

    fund1 = fund1.withName("Fund One");
    this.fundRepository.save(fund1);
    assertEquals(fund1, this.fundRepository.findForId(fund1.getId()).orElseThrow());

    funds.stream().map(Fund::getId).forEach(this.fundRepository::delete);
    assertTrue(this.fundRepository.findAll().isEmpty());
  }
}
