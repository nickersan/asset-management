package com.tn.assetmanagement.acceptance.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
  plugin = {"pretty", "html:target/site/cucumber"},
  features = {"classpath:features/fund.feature"},
  glue = {"com.tn.assetmanagement.acceptance.steps"},
  tags = {"not @ignore"},
  name = "CRUD"
)
public class FundCRUDSystemTest
{
}
