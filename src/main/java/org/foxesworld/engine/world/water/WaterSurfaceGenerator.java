package org.foxesworld.engine.world.water;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

public class WaterSurfaceGenerator {
    private SimpleApplication app;

    public WaterSurfaceGenerator(SimpleApplication app) {
        this.app = app;
    }

    public Geometry createWaterSurface(float waterLevel) {
        Mesh waterMesh = generateWaterMesh(waterLevel);
        Material waterMaterial = loadWaterMaterial();

        Geometry waterSurface = new Geometry("WaterSurface", waterMesh);
        waterSurface.setMaterial(waterMaterial);

        return waterSurface;
    }

    private Mesh generateWaterMesh(float waterLevel) {
        int gridSize = 10; // Размер сетки
        float size = 100; // Размер водной поверхности

        // Создание вершин и индексов сетки водной поверхности
        Mesh waterMesh = new Mesh();
        Vector3f[] vertices = new Vector3f[(gridSize + 1) * (gridSize + 1)];
        int[] indices = new int[gridSize * gridSize * 6];

        for (int i = 0; i <= gridSize; i++) {
            for (int j = 0; j <= gridSize; j++) {
                float x = size * ((float) i / gridSize - 0.5f);
                float z = size * ((float) j / gridSize - 0.5f);
                float y = waterLevel;

                vertices[i * (gridSize + 1) + j] = new Vector3f(x, y, z);
            }
        }

        int idx = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int topLeft = i * (gridSize + 1) + j;
                int bottomLeft = (i + 1) * (gridSize + 1) + j;

                // Triangle 1 vertices (reversed order)
                indices[idx++] = topLeft + 1;
                indices[idx++] = bottomLeft;
                indices[idx++] = topLeft;

                // Triangle 2 vertices (reversed order)
                indices[idx++] = bottomLeft + 1;
                indices[idx++] = bottomLeft;
                indices[idx++] = topLeft + 1;
            }
        }

        waterMesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        waterMesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        waterMesh.updateBound();

        return waterMesh;
    }

    private Material loadWaterMaterial() {
        Material waterMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        waterMaterial.setColor("Color", new ColorRGBA(0.0078f, 0.3176f, 0.5f, 1.0f));
        return waterMaterial;
    }
}
