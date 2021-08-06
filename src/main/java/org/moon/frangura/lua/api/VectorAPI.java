package org.moon.frangura.lua.api;

import org.moon.frangura.lua.type.LuaVec3;

public class VectorAPI {

    public LuaVec3 of(double x, double y, double z) {
        return new LuaVec3(x, y, z);
    }

}
