package com.tn.assetmanagement.util;

import javax.json.Json;
import javax.json.JsonObject;

public interface JsonObjects
{
  static JsonObject setString(JsonObject object, String name, String value)
  {
    return Json.createObjectBuilder(object).remove(name).add(name, value).build();
  }
}
