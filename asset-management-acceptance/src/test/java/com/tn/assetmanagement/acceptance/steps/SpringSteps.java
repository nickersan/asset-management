package com.tn.assetmanagement.acceptance.steps;

import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import com.tn.assetmanagement.acceptance.config.SpringConfiguration;
import com.tn.assetmanagement.acceptance.config.TestConfiguration;

@ContextConfiguration(classes = {TestConfiguration.class, SpringConfiguration.class})
public class SpringSteps
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SpringSteps.class);

  @Before
  public void initializeContext()
  {
    // This class makes Cucumber load the context before any tests are run.

    LOGGER.info("Starting Spring initialization");
  }
}
