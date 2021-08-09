package org.moon.frangura.model;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;

public class ModelPart extends ModelElement {
    public Vec3f from;
    public Vec3f to;
    public Face[] faces;
    public float inflate = 0;

    public Vec2f textureSize;

    public ModelPart(String name, String uuid, Vec3f origin, Vec3f from, Vec3f to, Face[] faces, Vec2f textureSize) {
        super(name, uuid, origin);
        this.name = name;
        this.uuid = uuid;
        this.origin = origin;
        this.from = from;
        this.to = to;
        this.faces = faces;
        this.textureSize = textureSize;
    }
    public record Face(Vector4f uv, int texture) {
        public static final String[] types = new String[]{"north","east","south","west","up","down"};
    }

    // Transforms and rendering data
    public Vec3f pivot = new Vec3f(0f,0f,0f);
    public Vec3f pos = new Vec3f(0f,0f,0f);
    public Vec3f rot = new Vec3f(0f,0f,0f);
    public Vec3f scale = new Vec3f(1f,1f,1f);
    public Vec3f color = new Vec3f(1f,1f,1f);
    public Vec2f uvOffset = new Vec2f(0f,0f);
    public float alpha = 1.0f;
    public boolean visible = true;
    public boolean shouldRender = true;

    // Copied from code on the internet that i don't quite understand yet
    public FloatList vertices = new FloatArrayList();
    public int vertexCount = 0;
    public Matrix4f lastModelMatrix = new Matrix4f();
    public Matrix3f lastNormalMatrix = new Matrix3f();
    public Matrix4f lastModelMatrixInverse = new Matrix4f();
    public Matrix3f lastNormalMatrixInverse = new Matrix3f();

    public void rebuildVertices() {
        this.vertices.clear();
        this.vertexCount = 0;


        Vec3f from = this.from.copy();
        Vec3f to = this.to.copy();

        Vec3f mid = new Vec3f(
                MathHelper.lerp(0.5f, from.getX(), to.getX()),
                MathHelper.lerp(0.5f, from.getY(), to.getY()),
                MathHelper.lerp(0.5f, from.getZ(), to.getZ())
        );

        from.subtract(mid);
        from.add(-this.inflate, -this.inflate, -this.inflate);
        from.add(mid);

        to.subtract(mid);
        to.add(this.inflate, this.inflate, this.inflate);
        to.add(mid);

        for(int i = 0; i < faces.length; i++) {
            Face face = faces[i];
            if (face.texture == -1) continue;

            // TODO rotate
            float rotation = 0;

            switch (i) {
                // North
                case 0 -> generateFace(
                        new Vec3f(-from.getX(), -from.getY(), from.getZ()),
                        new Vec3f(-to.getX(),   -from.getY(), from.getZ()),
                        new Vec3f(-to.getX(),   -to.getY(),   from.getZ()),
                        new Vec3f(-from.getX(), -to.getY(),   from.getZ()),
                        face.uv,
                        textureSize.x, textureSize.y
                );
                // East
                case 1 -> generateFace(
                        new Vec3f(-to.getX(), -from.getY(), from.getZ()),
                        new Vec3f(-to.getX(), -from.getY(), to.getZ()),
                        new Vec3f(-to.getX(), -to.getY(),   to.getZ()),
                        new Vec3f(-to.getX(), -to.getY(),   from.getZ()),
                        face.uv,
                        textureSize.x, textureSize.y
                );
                // South
                case 2 -> generateFace(
                        new Vec3f(-to.getX(),   -from.getY(), to.getZ()),
                        new Vec3f(-from.getX(), -from.getY(), to.getZ()),
                        new Vec3f(-from.getX(), -to.getY(),   to.getZ()),
                        new Vec3f(-to.getX(),   -to.getY(),   to.getZ()),
                        face.uv,
                        textureSize.x, textureSize.y
                );
                // West
                case 3 -> generateFace(
                        new Vec3f(-from.getX(), -from.getY(), to.getZ()),
                        new Vec3f(-from.getX(), -from.getY(), from.getZ()),
                        new Vec3f(-from.getX(), -to.getY(),   from.getZ()),
                        new Vec3f(-from.getX(), -to.getY(),   to.getZ()),
                        face.uv,
                        textureSize.x, textureSize.y
                );
                // Up
                case 4 -> generateFace(
                        new Vec3f(-to.getX(),   -to.getY(), to.getZ()),
                        new Vec3f(-from.getX(), -to.getY(), to.getZ()),
                        new Vec3f(-from.getX(), -to.getY(), from.getZ()),
                        new Vec3f(-to.getX(),   -to.getY(), from.getZ()),
                        face.uv,
                        textureSize.x, textureSize.y
                );
                // Down
                case 5 -> generateFace(
                        new Vec3f(-to.getX(),   -from.getY(), from.getZ()),
                        new Vec3f(-from.getX(), -from.getY(), from.getZ()),
                        new Vec3f(-from.getX(), -from.getY(), to.getZ()),
                        new Vec3f(-to.getX(),   -from.getY(), to.getZ()),
                        face.uv,
                        textureSize.x, textureSize.y
                );
            }
        }

    }

    public void generateFace(Vec3f a, Vec3f b, Vec3f c, Vec3f d, Vector4f uv, float texWidth, float texHeight) {
        Vec3f nA = b.copy();
        nA.subtract(a);
        Vec3f nB = c.copy();
        nB.subtract(a);
        nA.cross(nB);
        nA.normalize();

        // Top left
        float x1 = uv.getX();
        float y1 = uv.getY();

        // Bottom right
        float x2 = uv.getZ();
        float y2 = uv.getW();

        addVertex(b, x1 / texWidth, y1 / texHeight, nA);
        addVertex(a, x2 / texWidth, y1 / texHeight, nA);
        addVertex(d, x1 / texWidth, y2 / texHeight, nA);
        addVertex(c, x2 / texWidth, y2 / texHeight, nA);
    }
    public void addVertex(Vec3f vert, float u, float v, Vec3f normal) {
        this.vertices.add(vert.getX() / 16.0f);
        this.vertices.add(vert.getY() / 16.0f);
        this.vertices.add(vert.getZ() / 16.0f);
        this.vertices.add(u);
        this.vertices.add(v);
        this.vertices.add(-normal.getX());
        this.vertices.add(-normal.getY());
        this.vertices.add(-normal.getZ());
        this.vertexCount++;
    }

    public int render(int leftToRender, MatrixStack matrices, MatrixStack transformStack, VertexConsumer vertices, int light, int overlay, float u, float v, Vec3f prevColor, float alpha) {

        if (!this.visible || !this.shouldRender) return leftToRender;

        matrices.push();
        transformStack.push();

        this.lastModelMatrix = transformStack.peek().getModel().copy();
        this.lastNormalMatrix = transformStack.peek().getNormal().copy();

        this.lastModelMatrixInverse = this.lastModelMatrix.copy();
        this.lastModelMatrixInverse.invert();
        this.lastNormalMatrixInverse = this.lastNormalMatrix.copy();
        this.lastModelMatrixInverse.invert();

        Matrix4f modelMatrix = matrices.peek().getModel();
        Matrix3f normalMatrix = matrices.peek().getNormal();

        u += this.uvOffset.x;
        v += this.uvOffset.y;

        Vec3f newColor = color.copy();
        newColor.multiplyComponentwise(prevColor.getX(), prevColor.getY(), prevColor.getZ());

        for (int i = 0; i < this.vertexCount; i++) {
            int index = i * 8;

            Vector4f vertex = new Vector4f(
                    this.vertices.getFloat(index++),
                    this.vertices.getFloat(index++),
                    this.vertices.getFloat(index++),
                    1
            );

            float vertexU = this.vertices.getFloat(index++);
            float vertexV = this.vertices.getFloat(index++);

            Vec3f normal = new Vec3f(
                    this.vertices.getFloat(index++),
                    this.vertices.getFloat(index++),
                    this.vertices.getFloat(index++)
            );

            vertex.transform(modelMatrix);
            normal.transform(normalMatrix);

            // Push the new vertex
            vertices.vertex(
                    vertex.getX(), vertex.getY(), vertex.getZ(),
                    newColor.getX(), newColor.getY(), newColor.getZ(), alpha,
                    vertexU + u, vertexV + v,
                    overlay, light,
                    normal.getX(), normal.getY(), normal.getZ()
            );

            // Remove each face from what's left to render
            if (i % 4 == 0) {
                leftToRender -= 4;
                if (leftToRender <= 0) break;
            }
        }

        return 1;
    }
}
