package com.tn.assetmanagement.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.tn.assetmanagement.util.JsonObjects.setString;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.jupiter.api.Test;

class JsonObjectsTest
{
  @Test
  void testSetString()
  {
    String name = "Test";
    String oldValue = "Value";
    String newValue = "Value Updated";

    JsonObject object = Json.createObjectBuilder().build();

    assertEquals(oldValue, setString(object, name, oldValue).getString(name));
    assertEquals(newValue, setString(object, name, newValue).getString(name));
  }
}
