package org.moon.frangura.lua.api;

import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;
import org.terasology.jnlua.TypedJavaObject;

import java.util.Objects;

public class VectorAPI {

    public LuaVec3 of(double x, double y, double z) {
        return new LuaVec3(x, y, z);
    }

    public class LuaVec3 implements TypedJavaObject, JavaReflector {

        public final double x, y, z;

        public LuaVec3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LuaVec3 luaVec3 = (LuaVec3) o;
            return Double.compare(luaVec3.x, x) == 0 && Double.compare(luaVec3.y, y) == 0 && Double.compare(luaVec3.z, z) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
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

        @Override
        public JavaFunction getMetamethod(Metamethod metamethod) {
            switch (metamethod) {
                case ADD: return (state) -> {
                    LuaVec3 vecB = state.checkJavaObject(1, getClass());
                    state.pushJavaObject(of(this.x + vecB.x, this.y + vecB.y, this.z + vecB.z));
                    return 1;
                };
                case SUB: return (state) -> {
                    LuaVec3 vecB = state.checkJavaObject(1, getClass());
                    state.pushJavaObject(of(this.x - vecB.x, this.y - vecB.y, this.z - vecB.z));
                    return 1;
                };
                case MUL: return (state) -> {
                    LuaVec3 vecB = state.checkJavaObject(1, getClass());
                    state.pushJavaObject(of(this.x * vecB.x, this.y * vecB.y, this.z * vecB.z));
                    return 1;
                };
                case DIV: return (state) -> {
                    LuaVec3 vecB = state.checkJavaObject(1, getClass());
                    state.pushJavaObject(of(this.x / vecB.x, this.y / vecB.y, this.z / vecB.z));
                    return 1;
                };
                case MOD: return (state) -> {
                    LuaVec3 vecB = state.checkJavaObject(1, getClass());
                    state.pushJavaObject(of(this.x % vecB.x, this.y % vecB.y, this.z % vecB.z));
                    return 1;
                };
                case POW: return (state) -> {
                    LuaVec3 vecB = state.checkJavaObject(1, getClass());
                    state.pushJavaObject(of(Math.pow(this.x,vecB.x), Math.pow(this.y, vecB.y), Math.pow(this.z, vecB.z)));
                    return 1;
                };
                case INDEX: return (state) -> {
                    int index = (int) state.checkInteger(1);
                    if (index == 1) state.pushNumber(this.x);
                    if (index == 2) state.pushNumber(this.y);
                    if (index == 3) state.pushNumber(this.z);

                    return 1;
                };
                case EQ: return (state) -> {
                    LuaVec3 vecB = state.checkJavaObject(1, getClass());
                    state.pushBoolean(this.equals(vecB));
                    return 1;
                };
            }
            return null;
        }
    }

}
