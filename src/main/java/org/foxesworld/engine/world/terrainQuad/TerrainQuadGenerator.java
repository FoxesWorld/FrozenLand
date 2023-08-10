package org.foxesworld.engine.world.terrainQuad;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.util.BufferUtils;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import org.foxesworld.engine.utils.CollisionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TerrainQuadGenerator extends TerrainQuadAbstract {
    private Vector3f[] vertices;
    private SimpleApplication app;
    private static final Logger logger = LoggerFactory.getLogger(TerrainQuadGenerator.class);

    public TerrainQuadGenerator(SimpleApplication app) {
        this.app = app;
        setAssetManager(app.getAssetManager());
        setRootNode(app.getRootNode());
        setCollisionHelper(new CollisionHelper());
    }

    public TerrainQuad generateTerrain(Material mat, int gridSize, float stepSize, float frequency, int numOctaves, float textureScale) {
        setGridSize(gridSize);
        setStepSize(stepSize);
        setFrequency(frequency);
        setNumOctaves(numOctaves);
        setTextureScale(textureScale);
        TerrainQuad terrainQuad = new TerrainQuad("TerrainQuad", 65, gridSize + 1, null);
        terrainQuad.setMaterial(mat);
        terrainQuad.setLocalScale(stepSize, 1, stepSize);

        Node terrainNode = new Node("TerrainNode");
        terrainNode.attachChild(terrainQuad);
        app.getRootNode().attachChild(terrainNode);

        CollisionHelper collisionHelper = new CollisionHelper();
        setCollision(terrainQuad);

        return terrainQuad;
    }

    private void setCollision(TerrainQuad terrainQuad) {
        TerrainLodControl lodControl = new TerrainLodControl(terrainQuad, app.getCamera());
        terrainQuad.addControl(lodControl);

        BulletAppState bulletAppState = new BulletAppState();
        app.getStateManager().attach(bulletAppState);

        CollisionShape terrainCollisionShape = getCollisionHelper().terrainCollision(terrainQuad);

        RigidBodyControl rigidBodyControl = new RigidBodyControl(terrainCollisionShape, 0);
        terrainQuad.addControl(rigidBodyControl);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);

        // Place trees on the terrain
        placeTrees(terrainQuad, 64); // numTrees is the number of trees you want to place
    }

    private void placeTrees(Node terrainNode, int numTrees) {
        logger.info("Placing trees...");
        Spatial treeModel = app.getAssetManager().loadModel("Models/Tree/Tree.mesh.j3o"); // Replace with the path to your tree model
        // You can use the terrain size to limit the range of tree placement
        float terrainSize = 65 * getStepSize();

        Random random = new Random();

        for (int i = 0; i < numTrees; i++) {
            // Randomly select coordinates for tree placement on the terrain
            float x = random.nextFloat() * terrainSize - terrainSize * 0.5f;
            float z = random.nextFloat() * terrainSize - terrainSize * 0.5f;

            // Find the height (y) of the terrain at the selected coordinates
            float y = getHeight(x, z);

            Spatial treeInstance = treeModel.clone();
            treeInstance.scale(1f);
            treeInstance.setLocalTranslation(x, y, z);
            terrainNode.attachChild(treeInstance);

            // Generate collision shape for the tree model
            CollisionShape treeCollisionShape = getCollisionHelper().objectCollision(treeInstance);

            // Create a RigidBodyControl for the tree and attach the collision shape
            RigidBodyControl treeRigidBody = new RigidBodyControl(treeCollisionShape, 0.0f); // Non-zero mass for dynamic object
            treeInstance.addControl(treeRigidBody);

            // Add the RigidBodyControl to the PhysicsSpace to enable dynamic collision
            getBulletAppState().getPhysicsSpace().add(treeRigidBody);
        }
    }

    private float getHeight(float x, float z) {
        int gridSize = 64;
        float stepSize = 1.0f;

        // Convert world coordinates (x, z) to grid vertex coordinates
        int i = (int) ((x + gridSize * stepSize * 0.5f) / stepSize);
        int j = (int) ((z + gridSize * stepSize * 0.5f) / stepSize);

        // Limit i and j values to ensure they don't go out of grid bounds
        i = Math.max(0, Math.min(i, gridSize));
        j = Math.max(0, Math.min(j, gridSize));

        // Find the four nearest grid vertices
        Vector3f v00 = vertices[i * (gridSize + 1) + j];
        Vector3f v10 = vertices[(i + 1) * (gridSize + 1) + j];
        Vector3f v01 = vertices[i * (gridSize + 1) + j + 1];
        Vector3f v11 = vertices[(i + 1) * (gridSize + 1) + j + 1];

        // Bilinear interpolation to calculate the height based on the nearest vertices
        float u = (x - v00.x) / stepSize;
        float v = (z - v00.z) / stepSize;

        // Interpolate height horizontally
        float h0 = v00.y * (1 - u) + v10.y * u;
        float h1 = v01.y * (1 - u) + v11.y * u;

        // Interpolate height vertically
        float height = h0 * (1 - v) + h1 * v;

        return height;
    }

    protected Mesh generateTerrainMesh() {
        int gridSize = getGridSize();
        float stepSize = getStepSize();
        float frequency = getFrequency();
        // Create mesh and configure its vertices and triangle indices
        Mesh terrainMesh = new Mesh();
        Vector3f[] vertices = new Vector3f[(gridSize + 1) * (gridSize + 1)];
        Vector2f[] texCoord = new Vector2f[(gridSize + 1) * (gridSize + 1)]; // Add array for texture coordinates

        // Create noise module for height generation
        ModuleFractal fractal = new ModuleFractal();
        fractal.setAllSourceTypes(ModuleBasisFunction.BasisType.GRADIENT, ModuleBasisFunction.InterpolationType.LINEAR);
        fractal.setNumOctaves(getNumOctaves());
        fractal.setFrequency(frequency);

        for (int i = 0; i <= gridSize; i++) {
            for (int j = 0; j <= gridSize; j++) {
                float x = -gridSize * stepSize * 0.5f + i * stepSize;
                float z = -gridSize * stepSize * 0.5f + j * stepSize;

                // Generate noise for current position (x, z)
                float noiseValue = (float) fractal.get(x, 0, z);

                // Create additional noise module for hill generation
                ModuleFractal hillFractal = new ModuleFractal();
                hillFractal.setAllSourceTypes(ModuleBasisFunction.BasisType.GRADIENT, ModuleBasisFunction.InterpolationType.LINEAR);
                hillFractal.setNumOctaves(4);
                hillFractal.setFrequency(0.1f);

                // Generate hill height for current position (x, z)
                float hillHeight = (float) hillFractal.get(x * 0.2, 0, z * 0.2) * 10;

                float y = noiseValue * 15 + hillHeight;
                vertices[i * (gridSize + 1) + j] = new Vector3f(x, y, z);
                this.vertices = vertices;

                // Generate texture coordinates in range [0, 1]
                float u = (float) i / gridSize * getTextureScale();
                float v = (float) j / gridSize * getTextureScale();
                texCoord[i * (gridSize + 1) + j] = new Vector2f(u, v);
            }
        }

        // Create triangle indices
        int[] indices = new int[gridSize * gridSize * 6];
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

        // Set vertices, texture coordinates, and indices to the mesh
        terrainMesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        terrainMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        terrainMesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));

        terrainMesh.updateBound();
        terrainMesh.updateCounts();
        setTerrainMesh(terrainMesh);

        return terrainMesh;
    }

    public static class TerrainAndMaterial {
        public final Geometry terrainGeometry;
        public final Material terrainMaterial;

        public TerrainAndMaterial(Geometry terrainGeometry, Material terrainMaterial) {
            this.terrainGeometry = terrainGeometry;
            this.terrainMaterial = terrainMaterial;
        }
    }
}
