package org.foxesworld.newgame.engine.world.terrain;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireBox;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HeightMap;
import org.foxesworld.newgame.engine.providers.material.MaterialManager;

public class TerrainGenerator {

    private BulletAppState bulletAppState;
    private Node rootNode;

    public TerrainGenerator(BulletAppState bulletAppState, Node rootNode) {
        this.bulletAppState = bulletAppState;
        this.rootNode = rootNode;
    }

    public void generateChunkTerrain(ChunkManager chunkManager) {
        int chunkSize = chunkManager.getChunkSize();
        Vector3f chunkPosition = chunkManager.getChunkPosition();
        MaterialManager materialManager = chunkManager.getMaterialManager();
        HeightMap heightMap = generateHeightMap(chunkPosition, chunkSize);
        TerrainQuad terrain = new TerrainQuad("section-" + chunkManager.getChunkNum(), 65, chunkSize + 1, heightMap.getHeightMap());
        terrain.setMaterial(materialManager.getMaterial("soil"));

        float[] heightArray = heightMap.getHeightMap();
        HeightfieldCollisionShape collisionShape = new HeightfieldCollisionShape(heightArray, terrain.getLocalScale());
        RigidBodyControl rigidBodyControl = new RigidBodyControl(collisionShape, 0);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        terrain.setLocalTranslation(chunkPosition);
        rootNode.attachChild(terrain);
        terrain.addControl(rigidBodyControl);

        if (chunkManager.isShowBorder()) {
            WireBox wireBox = new WireBox(chunkSize * 0.5f, terrain.getLocalScale().y, chunkSize * 0.5f);
            Geometry wireBoxGeom = new Geometry("wireBox-" + chunkManager.getChunkNum(), wireBox);
            wireBoxGeom.setLocalTranslation(chunkPosition);
            wireBoxGeom.setMaterial(materialManager.getMaterial("sand"));
            rootNode.attachChild(wireBoxGeom);
        }
        chunkManager.addChunktoMap(chunkPosition, terrain);
    }

    public HeightMap generateHeightMap(Vector3f chunkPosition, int chunkSize) {
        AbstractHeightMap heightMap = new AbstractHeightMap() {
            @Override
            public boolean load() {
                int width = chunkSize + 1;
                int height = chunkSize + 1;
                this.heightData = new float[width * height];

                // Calculate noise parameters
                float scale = 0.01f; // Controls the frequency of the noise
                int seed = 566767896;    // Seed for the random number generator

                /*
                // Generate height data using Perlin noise
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < height; z++) {
                        float normalizedX = (chunkPosition.x + x) * scale;
                        float normalizedZ = (chunkPosition.z + z) * scale;

                        float noiseValue = ImprovedNoise.noise(normalizedX, normalizedZ, seed);
                        float heightValue = noiseValue * 50f; // Scale the noise to control height

                        setHeightAtPoint((float) x, z, (int) heightValue);
                    }
                } */

                normalizeTerrain(0.5f); // Normalize the terrain heights
                return true;
            }
        };

        heightMap.load();
        return heightMap;
    }
}
