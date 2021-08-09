package org.moon.frangura.model;

import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;

public class ModelGroup extends ModelElement {
    public ArrayList<ModelElement> children;
    public ParentType parentType = ParentType.Unset;

    public ModelGroup(String name, String uuid, Vec3f origin) {
        super(name, uuid, origin);
        this.children = new ArrayList<>();

        try {
            this.parentType = ParentType.valueOf(name);
        } catch (Exception ignored) {}
    }

}
