package com.tn.assetmanagement.funds.it.api;

import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ReactiveTypeDescriptor;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import com.tn.assetmanagement.funds.domain.Fund;
import com.tn.assetmanagement.funds.repository.FundRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ComponentScan("com.tn.assetmanagement.funds")
class FundApiIntegrationTest
{
  private static final String HOST = "localhost";
  private static final String PATH_FUND = "fund";
  private static final String PATH_VERSION_1 = "1";
  private static final String SCHEME = "http";

  @MockBean
  FundRepository fundRepository;

  @LocalServerPort
  int port;

  @Autowired
  WebTestClient webTestClient;

  @Test
  void testGet()
  {
    Fund fund1 = new Fund(1, "Fund 1", "F1");
    Fund fund2 = new Fund(2, "Fund 2", "F2");
    Fund fund3 = new Fund(3, "Fund 3", "F3");

    when(this.fundRepository.findAll()).thenReturn(List.of(fund1, fund2, fund3));

    assertGet(url(PATH_VERSION_1, PATH_FUND), fund1, fund2, fund3);
  }

  @Test
  void testGetWithId()
  {
    Fund fund = new Fund(1, "Fund 1", "F1");

    when(this.fundRepository.findForId(fund.getId())).thenReturn(Optional.of(fund));

    assertGet(url(PATH_VERSION_1, PATH_FUND, Integer.toString(fund.getId())), fund);
  }

  @Test
  void testPost()
  {
    int fundId = 1;
    Fund fund = new Fund("Fund 1", "F1");

    when(this.fundRepository.save(fund)).thenReturn(fundId);

    assertPost(url(PATH_VERSION_1, PATH_FUND), fund, fund.withId(fundId));
  }

  private Function<UriBuilder, URI> url(String... elements)
  {
    return uriBuilder -> uriBuilder.scheme(SCHEME).host(HOST).port(this.port).pathSegment(elements).build();
  }

  private void assertGet(Function<UriBuilder, URI> uriFunction, Fund... expected)
  {
    this.webTestClient.get().uri(uriFunction)
      .exchange()
      .expectStatus().isOk()
      .expectBodyList(Fund.class)
      .contains(expected);
  }

  private void assertPost(Function<UriBuilder, URI> uriFunction, Fund body, Fund expected)
  {
    this.webTestClient.post().uri(uriFunction)
      .body(Mono.just(body), Fund.class)
      .exchange()
      .expectStatus().isOk()
      .expectBodyList(Fund.class)
      .contains(expected);
  }
}
