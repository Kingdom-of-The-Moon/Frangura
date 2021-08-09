package org.moon.frangura.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import org.moon.frangura.FranguraMod;
import org.moon.frangura.model.ModelElement;
import org.moon.frangura.model.ModelGroup;
import org.moon.frangura.model.ModelPart;
import org.moon.frangura.model.ParentType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

public class BlockbenchModelAsset extends FranguraAsset {


    public Map<String, ModelPart> partLookup = new HashMap<>();
    public Map<String, ModelGroup> groupLookup = new HashMap<>();
    public ArrayList<ModelElement> modelElements = new ArrayList<>();

    public String name;
    public String geometryName;
    public Vec2f textureSize;

    public BlockbenchModelAsset(Path assetPath) {
        super(assetPath);

        Optional<InputStream> fileStream = getInputStream();
        if (fileStream.isPresent()) {
            FranguraMod.LOGGER.info("Loading model~");
            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(fileStream.get()));
            JsonObject modelObject = new JsonParser().parse(jsonReader).getAsJsonObject();

            // Load the model's name
            name = modelObject.get("name").getAsString();
            geometryName = modelObject.get("geometry_name").getAsString();

            // Load the texture's resolution
            JsonObject resolution = modelObject.get("resolution").getAsJsonObject();
            int textureWidth = resolution.get("width").getAsInt();
            int textureHeight = resolution.get("height").getAsInt();
            textureSize = new Vec2f(textureWidth, textureHeight);

            // If the model has the elements (parts) array
            if (modelObject.has("elements")) {
                JsonArray elementArray = modelObject.getAsJsonArray("elements");
                for (JsonElement entry : elementArray) {
                    JsonObject elementObject = entry.getAsJsonObject();
                    ModelPart newPart = parseModelPart(elementObject);
                    partLookup.put(newPart.uuid, newPart);
                }
            }

            // If the model has the outliner (groups) array
            if (modelObject.has("outliner")) {
                JsonArray outliner = modelObject.getAsJsonArray("outliner");
                modelElements.addAll(parseOutlinerArray(outliner));
            }

            // If the model has overrides (extra data)
            if (modelObject.has("overrides")) {
                JsonObject overrides = modelObject.getAsJsonObject("overrides");

                // If there is custom parent data in the overrides
                if (overrides.has("fran")) {
                    JsonObject groupParentMap = overrides.getAsJsonObject("fran");
                    for (Map.Entry<String, JsonElement> entry : groupParentMap.entrySet()) {
                        String groupUUID = entry.getKey();
                        String groupParent = entry.getValue().getAsString();

                        if (groupParent.equals("")) continue;

                        try {
                            ParentType type = ParentType.valueOf(groupParent);
                            groupLookup.get(groupUUID).parentType = type;
                        } catch (IllegalArgumentException e) {
                            FranguraMod.LOGGER.error("Invalid parent type while loading bbmodel: %s".formatted(groupParent));
                        }
                    }
                }
            }
        }

        printPartsRecursively(modelElements, 0);
    }

    public void printPartsRecursively(ArrayList<ModelElement> partList, int depth) {
        String _d = " ".repeat(depth);
        for(ModelElement part : partList) {
            String out = _d+part.name;
            if (part instanceof ModelGroup) {
                out += " [%s]".formatted(((ModelGroup) part).parentType.name());
            }

            FranguraMod.LOGGER.info(out);
            if (part instanceof ModelGroup) {
                printPartsRecursively(((ModelGroup) part).children, depth+4);
            }
        }
    }

    // Parsers
    public ArrayList<ModelElement> parseOutlinerArray(JsonArray array) {
        ArrayList<ModelElement> outlinerElementList = new ArrayList<>();
        for(JsonElement entry : array) {
            ModelElement newElement = parseOutlinerEntry(entry);
            outlinerElementList.add(newElement);
        }
        return outlinerElementList;
    }
    public ModelPart parseModelPart(JsonObject part) {
        String name = part.get("name").getAsString();
        String uuid = part.get("uuid").getAsString();
        Vec3f from = parseVec3f(part.getAsJsonArray("from"));
        Vec3f to = parseVec3f(part.getAsJsonArray("to"));
        Vec3f origin = parseVec3f(part.getAsJsonArray("origin"));
        JsonObject facesObject = part.getAsJsonObject("faces");

        ModelPart.Face[] faces = new ModelPart.Face[6];

        int i = 0;
        for (String direction : org.moon.frangura.model.ModelPart.Face.types) {
            Vector4f uv = null;
            int texture = -1;

            // If the cube has this current face
            if (facesObject.has(direction)) {
                JsonObject faceEntry = facesObject.getAsJsonObject(direction);

                // Parse the UV value
                uv = parseVec4f(faceEntry.getAsJsonArray("uv"));

                // If the texture field exists
                if (faceEntry.has("texture")) {

                    // Get the texture, but make sure it's a valid value...
                    JsonElement textureField = faceEntry.get("texture");
                    if (textureField == null) continue;
                    if (textureField.isJsonNull()) continue;
                    texture = textureField.getAsInt();
                }
            }

            faces[i] = new ModelPart.Face(uv, texture);
            i++;
        }

        return new ModelPart(name, uuid, origin, from, to, faces, textureSize);
    }
    public ModelElement parseOutlinerEntry(JsonElement entry) {
        // if the entry is a group
        if (entry.isJsonObject()) {
            JsonObject entryObject = entry.getAsJsonObject();
            String name = entryObject.get("name").getAsString();
            String uuid = entryObject.get("uuid").getAsString();
            Vec3f origin = parseVec3f(entryObject.getAsJsonArray("origin"));

            ModelGroup newGroup = new ModelGroup(name, uuid, origin);
            groupLookup.put(uuid, newGroup);

            // If the group has children groups or parts..
            if (entryObject.has("children")) {
                JsonArray outlinerArray = entryObject.getAsJsonArray("children");
                newGroup.children.addAll(parseOutlinerArray(outlinerArray));
            }

            return newGroup;
        } else {
            String partUUID = entry.getAsString();
            return partLookup.get(partUUID);
        }
    }
    public static Vec2f parseVec2f(JsonArray vecArray) {
        return new Vec2f(vecArray.get(0).getAsFloat(),vecArray.get(1).getAsFloat());
    }
    public static Vec3f parseVec3f(JsonArray vecArray) {
        return new Vec3f(vecArray.get(0).getAsFloat(),vecArray.get(1).getAsFloat(),vecArray.get(2).getAsFloat());
    }
    public static Vector4f parseVec4f(JsonArray vecArray) {
        return new Vector4f(vecArray.get(0).getAsFloat(),vecArray.get(1).getAsFloat(),vecArray.get(2).getAsFloat(),vecArray.get(3).getAsFloat());
    }
}
