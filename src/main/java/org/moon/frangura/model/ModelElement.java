package org.moon.frangura.model;

import net.minecraft.util.math.Vec3f;

public class ModelElement {
    public String name;
    public String uuid;
    public Vec3f origin;

    ModelElement(String name, String uuid, Vec3f origin) {
        this.name = name;
        this.uuid = uuid;
        this.origin = origin;
    }
}
