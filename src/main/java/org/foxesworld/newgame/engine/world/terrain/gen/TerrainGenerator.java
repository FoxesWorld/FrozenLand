package org.foxesworld.newgame.engine.world.terrain.gen;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.UpdateControl;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.*;
import com.jme3.terrain.geomipmap.grid.FractalTileLoader;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.noise.ShaderUtils;
import com.jme3.terrain.noise.basis.FilteredBasis;
import com.jme3.terrain.noise.filter.IterativeFilter;
import com.jme3.terrain.noise.filter.OptimizedErode;
import com.jme3.terrain.noise.filter.PerturbFilter;
import com.jme3.terrain.noise.filter.SmoothFilter;
import com.jme3.terrain.noise.fractal.FractalSum;
import com.jme3.terrain.noise.modulator.NoiseModulator;
import com.jme3.texture.Texture;
import org.foxesworld.newgame.engine.Kernel;
import org.foxesworld.newgame.engine.config.Constants;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.stream.Stream;

public class TerrainGenerator {

    private RigidBodyControl landscape;
    private TerrainQuad terrain;
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private Kernel kernel;
    private FractalSum base;
    private PerturbFilter perturb;
    private OptimizedErode therm;
    private SmoothFilter smooth;
    private IterativeFilter iterate;

    private float grassScale = 64;
    private float dirtScale = 16;
    private float rockScale = 128;
    private TerrainQuad distantTerrain;

    public  TerrainGenerator(Kernel kernel){
        this.assetManager = kernel.getAssetManager();
        this.bulletAppState = kernel.getBulletAppState();
        this.kernel = kernel;

    }

    public TerrainQuad generateTerrain() {
        // TERRAIN TEXTURE material
        Material matTerrain = new Material(this.assetManager, "MatDefs/HeightBasedTerrain.j3md");

        // Parameters to material:
        // regionXColorMap: X = 1..4 the texture that should be appliad to state X
        // regionX: a Vector3f containing the following information:
        //      regionX.x: the start height of the region
        //      regionX.y: the end height of the region
        //      regionX.z: the texture scale for the region
        //  it might not be the most elegant way for storing these 3 values, but it packs the data nicely :)
        // slopeColorMap: the texture to be used for cliffs, and steep mountain sites
        // slopeTileFactor: the texture scale for slopes
        // terrainSize: the total size of the terrain (used for scaling the texture)
        // GRASS texture
        Texture grass = this.assetManager.loadTexture("textures/terrain/splat/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        matTerrain.setTexture("region1ColorMap", grass);
        matTerrain.setVector3("region1", new Vector3f(15, 200, this.grassScale));

        // DIRT texture
        Texture dirt = this.assetManager.loadTexture("textures/terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        matTerrain.setTexture("region2ColorMap", dirt);
        matTerrain.setVector3("region2", new Vector3f(0, 20, this.dirtScale));

        // ROCK texture
        Texture rock = this.assetManager.loadTexture("textures/terrain/Rock2/rock.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        matTerrain.setTexture("region3ColorMap", rock);
        matTerrain.setVector3("region3", new Vector3f(198, 260, this.rockScale));

        matTerrain.setTexture("region4ColorMap", rock);
        matTerrain.setVector3("region4", new Vector3f(198, 260, this.rockScale));

        matTerrain.setTexture("slopeColorMap", rock);
        matTerrain.setFloat("slopeTileFactor", 32);

        matTerrain.setFloat("terrainSize", 513);

        this.base = new FractalSum();
        this.base.setRoughness(0.82f);
        this.base.setFrequency(1.2f);
        this.base.setAmplitude(1.1f);
        this.base.setLacunarity(2.12f);
        this.base.setOctaves(8);
        this.base.setScale(0.02125f);
        this.base.addModulator(new NoiseModulator() {

            @Override
            public float value(float... in) {
                return ShaderUtils.clamp(in[0] * 0.5f + 0.5f, 0, 1);
            }
        });

        FilteredBasis ground = new FilteredBasis(this.base);

        this.perturb = new PerturbFilter();
        this.perturb.setMagnitude(0.419f);

        this.therm = new OptimizedErode();
        this.therm.setRadius(1);
        this.therm.setTalus(0.711f);

        this.smooth = new SmoothFilter();
        this.smooth.setRadius(1);
        this.smooth.setEffect(0.7f);

        this.iterate = new IterativeFilter();
        this.iterate.addPreFilter(this.perturb);
        this.iterate.addPostFilter(this.smooth);
        this.iterate.setFilter(this.therm);
        this.iterate.setIterations(1);

        ground.addPreFilter(this.iterate);

        this.terrain = new TerrainGrid("terrain", 65, 1025, new FractalTileLoader(ground, 256f)) {
            protected boolean isNeighbour(int quadIndex) {
                return quadIndex == 0 || quadIndex == 1 || quadIndex == 2 || quadIndex == 3 ||
                        quadIndex == 4 || quadIndex == 8 ||
                        quadIndex == 7 || quadIndex == 11 ||
                        quadIndex == 12 || quadIndex == 13 || quadIndex == 14 || quadIndex == 15;
            }

            class UpdateQuadCacheRpg extends UpdateQuadCache {

                public UpdateQuadCacheRpg(Vector3f location) {
                    super(location);
                }

                @Override
                public void run() {
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            int quadIdx = i * 4 + j;
                            final Vector3f quadCell = location.add(quadIndex[quadIdx]);
                            TerrainQuad q = cache.get(quadCell);
                            if (q == null) {
                                if (getGridTileLoader() != null) {
                                    q = getGridTileLoader().getTerrainQuadAt(quadCell);
                                    // only clone the material to the quad if it doesn't have a material of its own
                                    if(q.getMaterial()==null) q.setMaterial(material.clone());
                                    log.log(Level.FINE, "Loaded TerrainQuad {0} from TerrainQuadGrid", q.getName());
                                }
                            }
                            cache.put(quadCell, q);


                            final int quadrant = getQuadrant(quadIdx);
                            final TerrainQuad newQuad = q;

                            if (!isNeighbour(quadIdx)) {
                                if (isCenter(quadIdx)) {
                                    // if it should be attached as a child right now, attach it
                                    getControl(UpdateControl.class).enqueue(new Callable<Object>() {
                                        // back on the OpenGL thread:
                                        @Override
                                        public Object call() throws Exception {
                                            if (newQuad.getParent() != null) {
                                                attachQuadAt(newQuad, quadrant, quadCell, true);
                                            }
                                            else {
                                                attachQuadAt(newQuad, quadrant, quadCell, false);
                                            }
                                            return null;
                                        }
                                    });
                                } else {
                                    getControl(UpdateControl.class).enqueue(new Callable<Object>() {
                                        @Override
                                        public Object call() throws Exception {
                                            removeQuad(newQuad);
                                            log.log(Level.SEVERE, "Unloaded TerrainQuad {0} from TerrainQuadGrid", newQuad.getQuadrant());
                                            return null;
                                        }
                                    });
                                }
                            }
                        }
                    }

                    getControl(UpdateControl.class).enqueue(new Callable<Object>() {
                        // back on the OpenGL thread:
                        @Override
                        public Object call() throws Exception {
                            for (Spatial s : getChildren()) {
                                if (s instanceof TerrainQuad) {
                                    TerrainQuad tq = (TerrainQuad)s;
                                    tq.resetCachedNeighbours();
                                }
                            }
                            setNeedToRecalculateNormals();
                            return null;
                        }
                    });
                }
            }
            @Override
            protected void updateChildren(Vector3f camCell) {

                int dx = 0;
                int dy = 0;
                if (currentCamCell != null) {
                    dx = (int) (camCell.x - currentCamCell.x);
                    dy = (int) (camCell.z - currentCamCell.z);
                }

                int xMin = 0;
                int xMax = 4;
                int yMin = 0;
                int yMax = 4;
                if (dx == -1) { // camera moved to -X direction
                    xMax = 3;
                } else if (dx == 1) { // camera moved to +X direction
                    xMin = 1;
                }

                if (dy == -1) { // camera moved to -Y direction
                    yMax = 3;
                } else if (dy == 1) { // camera moved to +Y direction
                    yMin = 1;
                }

                // Touch the items in the cache that we are and will be interested in.
                // We activate cells in the direction we are moving. If we didn't move
                // either way in one of the axes (say X or Y axis) then they are all touched.
                for (int i = yMin; i < yMax; i++) {
                    for (int j = xMin; j < xMax; j++) {
                        cache.get(camCell.add(quadIndex[i * 4 + j]));
                    }
                }

                // ---------------------------------------------------
                // ---------------------------------------------------

                if (cacheExecutor == null) {
                    // use the same executor as the LODControl
                    cacheExecutor = createExecutorService();
                }

                cacheExecutor.submit(new UpdateQuadCacheRpg(camCell));

                this.currentCamCell = camCell;
            }
        };

        this.terrain.setMaterial(matTerrain);
        setupPosition();
        setupScale();
        setUpLODControl();
        setUpCollision();
        terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        return terrain;
    }

    private void setupScale() {
        terrain.setLocalScale(Constants.TERRAIN_SCALE_X, Constants.TERRAIN_SCALE_Y, Constants.TERRAIN_SCALE_Z);
    }

    private void setupPosition() {
        //terrain postion
        terrain.setLocalTranslation(0, 0, 0);
    }

    private void setUpLODControl() {
        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainGridLodControl(this.terrain, kernel.getCamera());
        control.setLodCalculator(new DistanceLodCalculator(257, 2.7f)); // patch size, and a multiplier
        this.terrain.addControl(control);
    }

    private void setUpCollision() {
        ((TerrainGrid)terrain).addListener(new TerrainGridListener() {
            @Override
            public void gridMoved(Vector3f newCenter) {
            }

            @Override
            public void tileAttached(Vector3f cell, TerrainQuad quad) {
                while(quad.getControl(RigidBodyControl.class)!=null){
                    quad.removeControl(RigidBodyControl.class);
                }
                quad.addControl(new RigidBodyControl(new HeightfieldCollisionShape(quad.getHeightMap(), terrain.getLocalScale()), 0));
                bulletAppState.getPhysicsSpace().add(quad);
                //plant trees
               // app.getGameLogicCore().getInitializationDelegate().positionTrees(quad, true);
            }

            @Override
            public void tileDetached(Vector3f cell, TerrainQuad quad) {
                if (quad.getControl(RigidBodyControl.class) != null) {
                    bulletAppState.getPhysicsSpace().remove(quad);
                    quad.removeControl(RigidBodyControl.class);
                }
                List<Spatial> quadForest = quad.getUserData("quadForest");
                Stream<Spatial> stream = quadForest.stream();
                stream.forEach(treeNode -> {
                    System.out.println("Detached " + treeNode.hashCode() + treeNode.getLocalTranslation().toString());
                    //app.getGameLogicCore().getForestNode().detachChild(treeNode);
                });
            }

        });
    }
}
