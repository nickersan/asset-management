package com.tn.assetmanagement.acceptance;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
  plugin = {"pretty", "html:target/site/cucumber"},
  features = {"classpath:features"},
  glue = {"com.tn.assetmanagement.acceptance.steps"},
  tags = {"not @ignore"}
)
public class Suite
{
}
