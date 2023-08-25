package org.foxesworld.newgame.engine.world.terrain.gen.terrain;

import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.*;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import org.foxesworld.newgame.engine.KernelInterface;
import org.foxesworld.newgame.engine.config.Constants;
import org.foxesworld.newgame.engine.world.terrain.gen.tree.TreeGen;

import java.util.List;
import java.util.stream.Stream;

public class TerrainGenHelper {

    private KernelInterface kernelInterface;
    private  TerrainQuad terrain;

    public TerrainGenHelper(KernelInterface kernelInterface, TerrainQuad terrain){
        this.kernelInterface = kernelInterface;
        this.terrain = terrain;
    }

    void setupScale() {
        terrain.setLocalScale(Constants.TERRAIN_SCALE_X, Constants.TERRAIN_SCALE_Y,
                Constants.TERRAIN_SCALE_Z);
    }

    void setupPosition() {
        terrain.setLocalTranslation(0, 0, 0);
    }

    void setUpLODControl() {
        TerrainLodControl control =
                new TerrainGridLodControl(this.terrain, kernelInterface.getCamera());
        control.setLodCalculator(
                new DistanceLodCalculator(257, 2.7f));
        this.terrain.addControl(control);
    }

    void setUpCollision() {
        ((TerrainGrid) terrain).addListener(new TerrainGridListener() {
            @Override
            public void gridMoved(Vector3f newCenter) {}

            @Override
            public void tileAttached(Vector3f cell, TerrainQuad quad) {
                TreeGen treeGen = new TreeGen(kernelInterface);
                while (quad.getControl(RigidBodyControl.class) != null) {
                    quad.removeControl(RigidBodyControl.class);
                }
                quad.addControl(new RigidBodyControl(
                        new HeightfieldCollisionShape(
                                quad.getHeightMap(), terrain.getLocalScale()),
                        0));
                kernelInterface.getBulletAppState().getPhysicsSpace().add(quad);
                treeGen.positionTrees(quad, true);
            }

            @Override
            public void tileDetached(Vector3f cell, TerrainQuad quad) {
                if (quad.getControl(RigidBodyControl.class) != null) {
                    kernelInterface.getBulletAppState().getPhysicsSpace().remove(quad);
                    quad.removeControl(RigidBodyControl.class);
                }
                List<Spatial> quadForest = quad.getUserData("quadForest");
                Stream<Spatial> stream = quadForest.stream();
                stream.forEach(treeNode -> {
                    kernelInterface.getLogger().info("Detached " + treeNode.hashCode() + treeNode.getLocalTranslation().toString());
                    kernelInterface.getRootNode().detachChild(treeNode);
                });
            }
        });
    }
}
