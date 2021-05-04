package com.tn.assetmanagement.funds.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class Fund
{
  private final Integer id;
  private final String name;
  private final String ticker;

  public Fund(String name, String ticker)
  {
    this(null, name, ticker);
  }

  public Fund withId(int id)
  {
    return new Fund(id, this.name, this.ticker);
  }

  public Fund withName(String name)
  {
    return new Fund(this.id, name, this.ticker);
  }
}
