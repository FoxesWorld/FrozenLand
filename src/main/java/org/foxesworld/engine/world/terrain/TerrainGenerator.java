package org.foxesworld.engine.world.terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.util.BufferUtils;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import org.foxesworld.engine.utils.CollisionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TerrainGenerator extends TerrainAbstract {
    private Vector3f[] vertices;
    private Vector3f spawnPoint;
    private float waterLevel;
    private static final Logger logger = LoggerFactory.getLogger(TerrainGenerator.class);

    public TerrainGenerator(SimpleApplication app) {
        setAssetManager(app.getAssetManager());
        setRootNode(app.getRootNode());
        setCollission(new CollisionHelper());

        setBulletAppState(app.getStateManager().getState(BulletAppState.class));
        if (getBulletAppState() == null) {
            throw new IllegalStateException("BulletAppState is not attached to the SimpleApplication.");
        }
    }

    @Override
    public TerrainMaterial generateTerrain(Material mat) {
        Mesh terrainMesh = generateTerrainMesh();
        Geometry terrainGeometry = new Geometry("Terrain", terrainMesh);
        terrainGeometry.setMaterial(mat);

        return new TerrainMaterial(terrainGeometry, mat);
    }

    @Override
    public Node generateTerrainWithCollision(float waterLevel, int gridSize, float stepSize, float frequency, int numOctaves, float textureScale, Material mat) {
        Node terrainNode = new Node("TerrainNode");
        setGridSize(gridSize);
        setStepSize(stepSize);
        setFrequency(frequency);
        setNumOctaves(numOctaves);
        setTextureScale(textureScale);
        this.waterLevel = waterLevel;

        TerrainMaterial terrainMaterial = generateTerrain(mat);
        Geometry terrainGeometry = terrainMaterial.terrainGeometry;
        terrainNode.attachChild(terrainGeometry);
        getRootNode().attachChild(terrainNode);

        CollisionShape terrainCollisionShape = getCollission().terrainCollision(terrainNode);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(terrainCollisionShape, 0);
        terrainNode.addControl(rigidBodyControl);
        getBulletAppState().getPhysicsSpace().add(rigidBodyControl);

        logger.info("Generating collision shape for terrain...");
        logger.info("Grid Size: " + gridSize);
        logger.info("Step Size: " + stepSize);
        spawnPoint = getRandomSpawnPointAboveWater(waterLevel);

        terrainNode.addControl(new RigidBodyControl(terrainCollisionShape, 0));

        placeGeneratables(terrainNode, waterLevel, 64);

        return terrainNode;
    }


    //TODO Move to spatialGen
    protected void placeGeneratables(Node terrainNode, float waterLevel, int chancePerChunk) {
        logger.info("Placing generetables...");
        Spatial treeModel = getAssetManager().loadModel("Models/Tree/Tree.mesh.j3o"); // Замените путь на путь к вашей модели дерева
        float terrainSize = getGridSize() * getStepSize();

        Random random = new Random();

        for (int i = 0; i < chancePerChunk; i++) {
            float x = random.nextFloat() * terrainSize - terrainSize * 0.5f;
            float z = random.nextFloat() * terrainSize - terrainSize * 0.5f;
            float y = getHeight(x, z);
            if (y > waterLevel) { //If above water level (Later select generatables for water and surface)

                Spatial treeInstance = treeModel.clone();
                treeInstance.scale(1f);

                float rotationAngle = random.nextFloat() * FastMath.TWO_PI;
                treeInstance.rotate(0, rotationAngle, 0);

                treeInstance.setLocalTranslation(x, y, z);
                terrainNode.attachChild(treeInstance);

                CollisionShape treeCollisionShape = getCollission().objectCollision(treeInstance);

                RigidBodyControl treeRigidBody = new RigidBodyControl(treeCollisionShape, 0.0f);
                treeInstance.addControl(treeRigidBody);

                getBulletAppState().getPhysicsSpace().add(treeRigidBody);
            }
        }
    }


    protected float getHeight(float x, float z) {
        int gridSize = getGridSize();
        float stepSize = getStepSize();

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

        // Apply rounding at the edges for island-like shape
        float islandRadius = gridSize * stepSize * 0.4f;
        float distanceToCenter = new Vector2f(x, z).length();
        float roundFactor = FastMath.clamp(1 - (distanceToCenter / islandRadius), 0, 1);
        float roundValue = roundFactor * 10; // Adjust this value as needed

        height -= roundValue;

        // Ensure the height doesn't go below water level
        height = Math.max(height, waterLevel);

        return height;
    }


    @Override
    protected Mesh generateTerrainMesh() {
        int gridSize = getGridSize();
        float stepSize = getStepSize();
        float frequency = getFrequency();
        float islandRadius = gridSize * stepSize * 0.4f; // Adjust this value as needed
        float roundFactor = 10; // Adjust this value as needed

        // Create mesh and configure its vertices and triangle indices
        Mesh terrainMesh = new Mesh();
        vertices = new Vector3f[(gridSize + 1) * (gridSize + 1)];
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

                // Apply rounding at the edges for island-like shape
                float distanceToCenter = new Vector2f(x, z).length();
                float roundValue = FastMath.clamp(1 - (distanceToCenter / islandRadius), 0, 1) * roundFactor;

                y -= roundValue;

                // Ensure the height doesn't go below water level
                y = Math.max(y, waterLevel);

                vertices[i * (gridSize + 1) + j] = new Vector3f(x, y, z);

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


    @Override
    protected float[] generateHeightMap(int gridSize, float stepSize) {
        // Calculate height values and populate the height map array
        // You can use a similar loop as in your generateTerrainMesh method
        // to calculate the height values based on your noise functions
        // and store them in the heightMap array
        // ...

        return new float[(gridSize + 1) * (gridSize + 1)];
    }

    private Vector3f getRandomSpawnPointAboveWater(float waterLevel) {
        int gridSize = getGridSize();
        float stepSize = getStepSize();
        float terrainSize = gridSize * stepSize;

        Random random = new Random();

        while (true) {
            float x = random.nextFloat() * terrainSize - terrainSize * 0.5f;
            float z = random.nextFloat() * terrainSize - terrainSize * 0.5f;
            float y = getHeight(x, z);

            if (y > waterLevel) {
                return new Vector3f(x, y, z);
            }
        }
    }

    public static class TerrainMaterial {
        public final Geometry terrainGeometry;
        public final Material terrainMaterial;

        public TerrainMaterial(Geometry terrainGeometry, Material terrainMaterial) {
            this.terrainGeometry = terrainGeometry;
            this.terrainMaterial = terrainMaterial;
        }
    }

    public Vector3f getSpawnPoint() {
        return spawnPoint;
    }
}
