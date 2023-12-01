package specifikacija;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss");

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType,
                                 JsonSerializationContext context) {

        return new JsonPrimitive(formatter.format(localDateTime));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT,
                                     JsonDeserializationContext context) throws JsonParseException {

        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
