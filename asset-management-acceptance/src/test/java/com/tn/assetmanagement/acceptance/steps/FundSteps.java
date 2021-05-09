package com.tn.assetmanagement.acceptance.steps;

import static java.util.stream.Collectors.toList;

import static com.tn.assetmanagement.acceptance.Fields.FIELD_ID;
import static com.tn.assetmanagement.acceptance.Fields.FIELD_NAME;
import static com.tn.assetmanagement.acceptance.Fields.FIELD_TICKER;
import static com.tn.assetmanagement.util.Collections.randomElement;
import static com.tn.assetmanagement.util.JsonObjects.setString;

import java.util.stream.IntStream;
import javax.json.Json;
import javax.json.JsonObject;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import com.tn.assetmanagement.acceptance.TestException;
import com.tn.assetmanagement.acceptance.TestMode;

public class FundSteps
{
  private static final int INDEX_NAME = 0;
  private static final int INDEX_TICKER = 1;

  private final TestMode testMode;

  public FundSteps(TestMode testMode)
  {
    this.testMode = testMode;
  }

  @Then("all funds can be retrieved")
  public void assertGetAllFunds()
  {
    this.testMode.assertFunds(this.testMode.getCachedFunds(), this.testMode.getFunds());
  }

  @Then("a specific fund can be retrieved")
  public void assertGetFund()
  {
    JsonObject fund = randomElement(this.testMode.getCachedFunds()).orElseThrow(() -> new TestException("No funds cached"));
    this.testMode.assertFund(fund, this.testMode.getFund(fund.getInt(FIELD_ID)));
  }

  @Then("a specific fund can be updated")
  public void assertUpdateFund()
  {
    JsonObject fund = randomElement(this.testMode.getCachedFunds()).orElseThrow(() -> new TestException("No funds cached"));
    JsonObject fundUpdated = setString(fund, FIELD_NAME, fund.getString(FIELD_NAME) + " - Updated");

    this.testMode.assertFund(fundUpdated, this.testMode.updateFund(fundUpdated));
    this.testMode.assertFund(fundUpdated, this.testMode.getFund(fundUpdated.getInt(FIELD_ID)));
  }

  @Then("a specific fund can be deleted")
  public void assertDeleteFund()
  {
    JsonObject fund = randomElement(this.testMode.getCachedFunds()).orElseThrow(() -> new TestException("No funds cached"));
    this.testMode.deleteFund(fund);
    this.testMode.assertFundDeleted(fund);
  }

  @When("the following funds are created")
  public void createFunds(DataTable data)
  {
    this.testMode.createFunds(
      IntStream.range(0, data.height())
        .mapToObj(row -> fund(data.cell(row, INDEX_NAME), data.cell(row, INDEX_TICKER)))
        .collect(toList())
    );
  }

  private JsonObject fund(String name, String ticker)
  {
    return Json.createObjectBuilder()
      .add(FIELD_NAME, name)
      .add(FIELD_TICKER, ticker)
      .build();
  }
}
