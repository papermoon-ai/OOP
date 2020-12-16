package data.sources;

import com.google.gson.*;
import data.Entry;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class InterfaceAdapter implements JsonSerializer<Entry>, JsonDeserializer<Entry> {
    private static final String CLASSNAME = "CLASSNAME";
    private static final String HEADLINE = "HEADLINE";
    private static final String DATE = "DATE";
    private static final String CONTENT = "CONTENT";

    @Override
    public JsonElement serialize(Entry o, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(HEADLINE, o.getHeadline());
        jsonObject.addProperty(DATE, o.getDateOfChange().toString());
        jsonObject.addProperty(CLASSNAME, o.getType().toString());
        jsonObject.addProperty(CONTENT, o.getContent().toString());

        jsonObject.add(HEADLINE, jsonSerializationContext.serialize(o.getHeadline()));
        jsonObject.add(DATE, jsonSerializationContext.serialize(o.getDateOfChange()));
        jsonObject.add(CLASSNAME, jsonSerializationContext.serialize(o.getType().getName()));
        jsonObject.add(CONTENT, jsonSerializationContext.serialize(o.getContent()));

        return jsonObject;
    }

    @Override
    public Entry deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
        String className = prim.getAsString();
        Class clazz = getObjectClass(className);

        Entry entry = new Entry(clazz);
        entry.setHeadline(context.deserialize(jsonObject.get(HEADLINE), String.class));
        entry.setDateOfChange(context.deserialize(jsonObject.get(DATE), LocalDateTime.class));
        entry.setContent(context.deserialize(jsonObject.get(CONTENT), entry.getType()));

        return entry;
    }

    public Class getObjectClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage());
        }
    }
}
