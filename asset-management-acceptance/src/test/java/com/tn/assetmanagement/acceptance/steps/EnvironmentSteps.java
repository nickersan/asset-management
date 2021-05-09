package com.tn.assetmanagement.acceptance.steps;

//import brave.Span;
//import brave.Tracer;
//import io.cucumber.core.api.Scenario;
//import io.cucumber.java.After;
//import io.cucumber.java.Before;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tn.assetmanagement.acceptance.TestException;
import com.tn.assetmanagement.acceptance.TestMode;

public class EnvironmentSteps
{
  private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentSteps.class);

  private final TestMode testMode;
//  private final Tracer tracer;
//
//  private Span span;

  public EnvironmentSteps(/* Tracer tracer, */TestMode testMode)
  {
//    this.tracer = tracer;
    this.testMode = testMode;
  }

//  @Before
//  public void startTrace(Scenario scenario)
//  {
//    this.span = this.tracer.newTrace().name(scenario.getName()).start();
//  }
//
  @After
  public void finish()
  {
    LOGGER.info("Finishing");
//    this.span.finish();
    this.testMode.finish();
    LOGGER.info("Finished");
  }

  @Given("the test environment initialized")
  public void initialize() throws TestException
  {
    LOGGER.info("Initializing");
    this.testMode.initialize();
    LOGGER.info("Initialized");
  }
}
