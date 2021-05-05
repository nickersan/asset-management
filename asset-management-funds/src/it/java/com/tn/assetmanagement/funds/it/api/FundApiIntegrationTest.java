package com.tn.assetmanagement.funds.it.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;

import com.tn.assetmanagement.funds.domain.Fund;
import com.tn.assetmanagement.funds.repository.FundRepository;
import com.tn.assetmanagement.funds.repository.RepositoryException;
import com.tn.assetmanagement.test.web.AbstractWebIntegrationTest;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan("com.tn.assetmanagement.funds")
class FundApiIntegrationTest extends AbstractWebIntegrationTest
{
  private static final String PATH_FUND = "fund";
  private static final String PATH_VERSION_1 = "1";

  @MockBean
  FundRepository fundRepository;

  @Test
  void testGet()
  {
    Fund fund1 = new Fund(1, "Fund 1", "F1");
    Fund fund2 = new Fund(2, "Fund 2", "F2");
    Fund fund3 = new Fund(3, "Fund 3", "F3");

    when(this.fundRepository.findAll()).thenReturn(List.of(fund1, fund2, fund3));

    assertGet(url(PATH_VERSION_1, PATH_FUND), HttpStatus.OK, fund1, fund2, fund3);
  }

  @Test
  void testGetWhenErrorOccurs()
  {
    when(this.fundRepository.findAll()).thenThrow(new RepositoryException("Testing"));

    assertGet(url(PATH_VERSION_1, PATH_FUND), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testGetWithId()
  {
    Fund fund = new Fund(1, "Fund 1", "F1");

    when(this.fundRepository.findForId(fund.getId())).thenReturn(Optional.of(fund));

    assertGet(url(PATH_VERSION_1, PATH_FUND, fund.getId()), HttpStatus.OK, fund);
  }

  @Test
  void testGetWithIdWhenErrorOccurs()
  {
    int fundId = 1;

    when(this.fundRepository.findForId(fundId)).thenThrow(new RepositoryException("Testing"));

    assertGet(url(PATH_VERSION_1, PATH_FUND, fundId), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testGetWithBadId()
  {
    assertGet(url(PATH_VERSION_1, PATH_FUND, "X"), HttpStatus.BAD_REQUEST);
  }

  @Test
  void testPost()
  {
    int fundId = 1;
    Fund fund = new Fund("Fund 1", "F1");

    when(this.fundRepository.save(fund)).thenReturn(fundId);

    assertPost(url(PATH_VERSION_1, PATH_FUND), fund, HttpStatus.OK, fund.withId(fundId));
  }

  @Test
  void testPostWhenErrorOccurs()
  {
    Fund fund = new Fund("Fund 1", "F1");

    when(this.fundRepository.save(fund)).thenThrow(new RepositoryException("Testing"));

    assertPost(url(PATH_VERSION_1, PATH_FUND), fund, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testPut()
  {
    int fundId = 1;
    Fund fund = new Fund("Fund 1", "F1");

    when(this.fundRepository.save(fund)).thenReturn(fundId);

    assertPut(url(PATH_VERSION_1, PATH_FUND), fund, HttpStatus.OK, fund.withId(fundId));
  }

  @Test
  void testPutWhenErrorOccurs()
  {
    Fund fund = new Fund("Fund 1", "F1");

    when(this.fundRepository.save(fund)).thenThrow(new RepositoryException("Testing"));

    assertPut(url(PATH_VERSION_1, PATH_FUND), fund, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testDelete()
  {
    int fundId = 10;

    assertDelete(url(PATH_VERSION_1, PATH_FUND, fundId), HttpStatus.OK);

    verify(this.fundRepository).delete(fundId);
  }

  @Test
  void testDeleteWithoutId()
  {
    assertDelete(url(PATH_VERSION_1, PATH_FUND), HttpStatus.NOT_FOUND);
  }

  @Test
  void testDeleteWhenErrorOccurs()
  {
    int fundId = 1;

    doThrow(new RepositoryException("Testing")).when(this.fundRepository).delete(fundId);

    assertDelete(url(PATH_VERSION_1, PATH_FUND, fundId), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void testDeleteWithBadId()
  {
    assertDelete(url(PATH_VERSION_1, PATH_FUND, "X"), HttpStatus.BAD_REQUEST);
  }
}
