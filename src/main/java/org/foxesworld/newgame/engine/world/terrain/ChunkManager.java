package org.foxesworld.newgame.engine.world.terrain;

import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;
import org.foxesworld.newgame.engine.providers.material.MaterialManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChunkManager {

    private TerrainGenerator terrainGenerator;
    private Map<Vector3f, TerrainQuad> chunkMap = new HashMap<>();
    private long chunkNum = 0;
    private boolean showBorder;
    private final int chunkSize = 32;
    private Vector3f chunkPosition;
    private final float chunkLoadDistance;
    private MaterialManager materialManager;

    public ChunkManager(TerrainGenerator terrainGenerator, float chunkLoadDistance, MaterialManager materialManager) {
        this.terrainGenerator = terrainGenerator;
        this.chunkLoadDistance = chunkLoadDistance;
        this.materialManager = materialManager;
    }

    public void generateAndLoadChunks(Vector3f playerPosition, boolean showBorder) {
        Vector3f newChunkPosition = calculateChunkPosition(playerPosition);
        this.showBorder = showBorder;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                chunkPosition = newChunkPosition.add(i * chunkSize, 0, j * chunkSize);
                if (!chunkMap.containsKey(chunkPosition) && playerPosition.distance(chunkPosition) <= chunkLoadDistance) {
                    terrainGenerator.generateChunkTerrain(this);
                    System.out.println("Adding chunk " + chunkNum+ ' ' + chunkPosition);
                    chunkNum+=1;
                }
            }
        }
        //unloadOldChunks(newChunkPosition, playerPosition);
    }

    public Vector3f calculateChunkPosition(Vector3f playerPosition) {
        int x = Math.round(playerPosition.x / chunkSize) * chunkSize;
        int z = Math.round(playerPosition.z / chunkSize) * chunkSize;
        return new Vector3f(x, 0, z);
    }

    public void unloadOldChunks(Vector3f newChunkPosition, Vector3f playerPosition) {
        Set<Vector3f> chunksToRemove = chunkMap.keySet().stream()
                .filter(chunkPosition -> chunkPosition.distance(newChunkPosition) > chunkLoadDistance)
                .collect(Collectors.toSet());

        for (Vector3f chunkPosition : chunksToRemove) {
            if (chunkPosition.distance(playerPosition) > chunkLoadDistance) {
                TerrainQuad terrainQuad = chunkMap.remove(chunkPosition);
                terrainQuad.removeFromParent();
            }
        }
    }

    public void addChunktoMap(Vector3f chunkPos, TerrainQuad terrain){
        chunkMap.put(chunkPos, terrain);
    }

    public long getChunkNum() {
        return chunkNum;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public Vector3f getChunkPosition() {
        return chunkPosition;
    }

    public boolean isShowBorder() {
        return showBorder;
    }

    public MaterialManager getMaterialManager() {
        return materialManager;
    }
}
