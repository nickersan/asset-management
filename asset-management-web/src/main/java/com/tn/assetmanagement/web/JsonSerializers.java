package com.tn.assetmanagement.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializers
{
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static Supplier<IllegalArgumentException> missingField(String fieldName)
  {
    return () -> new IllegalArgumentException("Missing field: " + fieldName);
  }

  public static OptionalInt readInt(JsonNode node, String name)
  {
    JsonNode value = node.get(name);
    return value != null && value.isInt() ? OptionalInt.of(value.intValue()) : OptionalInt.empty();
  }

  public static void writeInt(JsonGenerator jsonGenerator, String name, int i) throws IOException
  {
    jsonGenerator.writeNumberField(name, i);
  }

  public static OptionalLong readLong(JsonNode node, String name)
  {
    JsonNode value = node.get(name);
    return value != null && (value.isInt() || value.isLong()) ? OptionalLong.of(value.longValue()) : OptionalLong.empty();
  }

  public static void writeLong(JsonGenerator jsonGenerator, String name, long i) throws IOException
  {
    jsonGenerator.writeNumberField(name, i);
  }

  public static <T> Optional<T> readObject(JsonNode node, String name, Class<T> type) throws IOException
  {
    JsonNode value = node.get(name);
    return value != null ? Optional.of(OBJECT_MAPPER.readValue(value.toString(), type)) : Optional.empty();
  }

  public static void writeObject(JsonGenerator jsonGenerator, String name, Object object) throws IOException
  {
    jsonGenerator.writeObjectField(name, object);
  }

  public static Optional<String> readString(JsonNode node, String name)
  {
    JsonNode value = node.get(name);
    return value != null && value.isTextual() ? Optional.of(value.textValue()) : Optional.empty();
  }

  public static void writeString(JsonGenerator jsonGenerator, String name, String s) throws IOException
  {
    jsonGenerator.writeStringField(name, s);
  }

  public static Optional<Object> readValue(JsonNode node, String name)
  {
    JsonNode value = node.get(name);

    //TODO: review this
    if (value == null) return Optional.empty();
    else if (value.isTextual()) return Optional.of(value.textValue());
    else if (value.isBoolean()) return Optional.of(value.booleanValue());
    else if (value.isNumber()) return Optional.of(value.decimalValue());
    else throw new IllegalStateException(value.getNodeType() + " not allowed");
  }

  public static void writeValue(JsonGenerator jsonGenerator, String name, Object value) throws IOException
  {
    //TODO: review this
    if (value instanceof String) jsonGenerator.writeStringField(name, (String)value);
    else if (value instanceof Boolean) jsonGenerator.writeBooleanField(name, (Boolean)value);
    else if (value instanceof BigDecimal) jsonGenerator.writeNumberField(name, (BigDecimal)value);
    else if (value != null) throw new IllegalStateException(value.getClass() + " not allowed");
  }
}
