package com.tn.assetmanagement.web;

import static java.lang.String.format;

import static com.tn.assetmanagement.web.Handlers.PATH_VARIABLE_ID;

public class Paths
{
  private static final String FORMAT_PATH_VARIABLE = "/{%s}";

  public static String idPath()
  {
    return variablePath(PATH_VARIABLE_ID);
  }

  public static String variablePath(String variableName)
  {
    return format(FORMAT_PATH_VARIABLE, variableName);
  }
}
