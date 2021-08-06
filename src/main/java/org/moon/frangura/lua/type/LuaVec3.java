package org.moon.frangura.lua.type;

import org.terasology.jnlua.TypedJavaObject;

public class LuaVec3 implements TypedJavaObject {

    public final double x, y, z;

    public LuaVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "x:%s, y:%s, z:%s".formatted(x, y, z);
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
