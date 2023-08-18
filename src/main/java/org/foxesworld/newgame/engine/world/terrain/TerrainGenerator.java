package org.foxesworld.newgame.engine.world.terrain;

import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.simsilica.arboreal.BranchParameters;
import com.simsilica.arboreal.Tree;
import com.simsilica.arboreal.TreeGenerator;
import com.simsilica.arboreal.TreeParameters;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleFractal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TerrainGenerator {

    private BulletAppState bulletAppState;
    private Node rootNode;
    private int gridSize = 128;
    private float stepSize = 5;
    private Logger logger = LoggerFactory.getLogger(TerrainGenerator.class);

    public TerrainGenerator(Node rootNode, BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
        this.rootNode = rootNode;
    }

    protected float[] generateHillyHeightMap(Vector3f playerPosition) {
        float[] heightMap = new float[(gridSize + 1) * (gridSize + 1)];
        ModuleFractal fractal = new ModuleFractal();
        fractal.setAllSourceTypes(ModuleBasisFunction.BasisType.GRADIENT, ModuleBasisFunction.InterpolationType.LINEAR);
        fractal.setNumOctaves(4); // Увеличьте количество октав для более выраженных холмов
        fractal.setFrequency(0.04); // Увеличьте частоту для более мелких деталей

        float terrainSize = gridSize * stepSize;
        float playerX = playerPosition.x;
        float playerZ = playerPosition.z;

        for (int i = 0; i <= gridSize; i++) {

            for (int j = 0; j <= gridSize; j++) {
                float x = -terrainSize * 0.5f + i * stepSize;
                float z = -terrainSize * 0.5f + j * stepSize;

                float distanceToPlayer = new Vector3f(playerX - x, 0, playerZ - z).length();

                float hillHeight = (float) (fractal.get(x, 0, z) * Math.max(1 - distanceToPlayer * 0.005, 0));

                heightMap[i * (gridSize + 1) + j] = hillHeight;
            }
        }

        return heightMap;
    }
    public TerrainQuad generateHillyTerrain(Vector3f playerPosition, Material terrainMaterial) {
        float[] heightMap = generateHillyHeightMap(playerPosition);
        TerrainQuad terrain = new TerrainQuad("terrain", gridSize + 1, gridSize + 1, heightMap);
        terrain.setMaterial(terrainMaterial);
        // Generate collision shape based on the height map
        HeightfieldCollisionShape collisionShape = new HeightfieldCollisionShape(heightMap, terrain.getLocalScale());
        RigidBodyControl rigidBodyControl = new RigidBodyControl(collisionShape, 0);
        terrain.addControl(rigidBodyControl);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        rootNode.attachChild(terrain);

        return terrain;
    }
}
