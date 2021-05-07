package com.tn.assetmanagement.funds.domain;

import static com.tn.assetmanagement.web.JsonSerializers.missingField;
import static com.tn.assetmanagement.web.JsonSerializers.readInt;
import static com.tn.assetmanagement.web.JsonSerializers.readString;
import static com.tn.assetmanagement.web.JsonSerializers.writeInt;
import static com.tn.assetmanagement.web.JsonSerializers.writeString;

import java.io.IOException;
import java.util.OptionalInt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.relational.core.sql.In;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
@JsonSerialize(using = Fund.Serializer.class)
@JsonDeserialize(using = Fund.Deserializer.class)
public class Fund
{
  protected static final String ATTRIBUTE_ID = "id";
  protected static final String ATTRIBUTE_NAME = "name";
  protected static final String ATTRIBUTE_TICKER = "ticker";

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

  public static class Deserializer extends JsonDeserializer<Fund>
  {
    @Override
    public Fund deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException
    {
      JsonNode node = jsonParser.getCodec().readTree(jsonParser);

      OptionalInt id = readInt(node, ATTRIBUTE_ID);

      return new Fund(
        id.isPresent() ? id.getAsInt() : null,
        readString(node, ATTRIBUTE_NAME).orElseThrow(missingField(ATTRIBUTE_NAME)),
        readString(node, ATTRIBUTE_TICKER).orElseThrow(missingField(ATTRIBUTE_TICKER))
      );
    }
  }

  public static class Serializer extends JsonSerializer<Fund>
  {
    @Override
    public void serialize(Fund workspace, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException
    {
      jsonGenerator.writeStartObject();

      if (workspace.getId() != null) writeInt(jsonGenerator, ATTRIBUTE_ID, workspace.getId());
      writeString(jsonGenerator, ATTRIBUTE_NAME, workspace.getName());
      writeString(jsonGenerator, ATTRIBUTE_TICKER, workspace.getTicker());
      jsonGenerator.writeEndObject();
    }
  }
}
