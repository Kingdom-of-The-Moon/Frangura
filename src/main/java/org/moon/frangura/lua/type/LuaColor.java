package org.moon.frangura.lua.type;

import org.terasology.jnlua.TypedJavaObject;

public class LuaColor implements TypedJavaObject {

    private float r, g, b;
    public LuaColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }


    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public Class<?> getType() {
        return getType();
    }

    @Override
    public boolean isStrong() {
        return true;
    }
}
