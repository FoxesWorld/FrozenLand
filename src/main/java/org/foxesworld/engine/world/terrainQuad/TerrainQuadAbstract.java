package org.foxesworld.engine.world.terrainQuad;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import org.foxesworld.engine.utils.CollisionHelper;

public abstract class TerrainQuadAbstract {
    private AssetManager assetManager;
    private Node rootNode;
    private BulletAppState bulletAppState;
    private int gridSize;
    private float stepSize;
    private float frequency;
    private int numOctaves;
    private float textureScale;
    private CollisionHelper collisionHelper;
    private Mesh terrainMesh;

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public int getGridSize() {
        return gridSize;
    }

    public float getStepSize() {
        return stepSize;
    }

    public float getFrequency() {
        return frequency;
    }

    public int getNumOctaves() {
        return numOctaves;
    }

    public float getTextureScale() {
        return textureScale;
    }

    public CollisionHelper getCollisionHelper() {
        return collisionHelper;
    }

    public Mesh getTerrainMesh() {
        return terrainMesh;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void setStepSize(float stepSize) {
        this.stepSize = stepSize;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setNumOctaves(int numOctaves) {
        this.numOctaves = numOctaves;
    }

    public void setTextureScale(float textureScale) {
        this.textureScale = textureScale;
    }

    public void setCollisionHelper(CollisionHelper collisionHelper) {
        this.collisionHelper = collisionHelper;
    }

    public void setTerrainMesh(Mesh terrainMesh) {
        this.terrainMesh = terrainMesh;
    }
}
