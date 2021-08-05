package org.moon.frangura.lua.api;

import org.moon.frangura.lua.LuaScript;
import org.terasology.jnlua.TypedJavaObject;

public class FileAPI implements TypedJavaObject {

    public void test() {

        System.out.println("what the how");

    }



    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public Class<?> getType() {
        return this.getType();
    }

    @Override
    public boolean isStrong() {
        return true;
    }
}
