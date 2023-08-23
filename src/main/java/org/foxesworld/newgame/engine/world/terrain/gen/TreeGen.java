package org.foxesworld.newgame.engine.world.terrain.gen;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import org.foxesworld.newgame.engine.KernelInterface;
import org.foxesworld.newgame.engine.config.Constants;
import org.foxesworld.newgame.engine.utils.LodUtils;
import org.foxesworld.newgame.engine.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.foxesworld.newgame.engine.config.Constants.RAY_DOWN;


public class TreeGen {

    private Spatial treeModel;
    private KernelInterface kernelInterface;

    public TreeGen(KernelInterface kernelInterface){
        this.kernelInterface = kernelInterface;
        this.initializeTreeModel();
    }

    private void initializeTreeModel() {
        treeModel = kernelInterface.getAssetManager().loadModel("Models/Fir1/fir1_androlo.j3o");
        treeModel.setShadowMode(RenderQueue.ShadowMode.Cast);
        LodUtils.setUpTreeModelLod(treeModel);
    }

    private RigidBodyControl createCollisionControl(Spatial spatial) {
        CollisionShape shape = CollisionShapeFactory.createMeshShape(spatial);
        RigidBodyControl control = new RigidBodyControl(shape, 0);
        spatial.addControl(control);
        kernelInterface.getBulletAppState().getPhysicsSpace().add(control);
        return control;
    }

    public List<Spatial> setupTrees() {
        int forestSize = (int) Utils.getRandomNumberInRange(3999, 4000);
        List<Spatial> quadForest = new ArrayList<>(forestSize);
        for (int i = 0; i < forestSize; i++) {
            Spatial treeModelCustom = treeModel.clone();
            treeModelCustom.scale(1 + Utils.getRandomNumberInRange(1, 10), 1 + Utils.getRandomNumberInRange(1, 10), 1 + Utils.getRandomNumberInRange(1, 10));
            quadForest.add(treeModelCustom);
        }

        return quadForest;
    }

    public void positionTrees(TerrainQuad quad, boolean parallel) {
        List<Spatial> quadForest = quad.getUserData("quadForest");
        if (quadForest == null) {
            quadForest = setupTrees();

            Stream<Spatial> stream = quadForest.stream();
            stream.forEach(treeNode -> {
                int generated = -1;
                while (generated++ < 0) {
                    CollisionResults results = new CollisionResults();

                    float y = kernelInterface.getPlayer().getPlayerPosition().y;
                    if (y < Constants.WATER_LEVEL_HEIGHT)
                        y = 0;

                    Vector3f start = new Vector3f(kernelInterface.getPlayer().getPlayerPosition().x + Utils.getRandomNumberInRange(-1000, 1000), y, kernelInterface.getPlayer().getPlayerPosition().z + Utils.getRandomNumberInRange(-1000, 1000));
                    Ray ray = new Ray(start, RAY_DOWN);

                    quad.collideWith(ray, results);
                    CollisionResult hit = results.getClosestCollision();
                    if (hit != null) {
                        if (hit.getContactPoint().y > Constants.WATER_LEVEL_HEIGHT) {
                            Vector3f plantLocation = new Vector3f(hit.getContactPoint().x, hit.getContactPoint().y, hit.getContactPoint().z);
                            treeNode.setLocalTranslation(plantLocation.x, plantLocation.y, plantLocation.z);
                            treeNode.setLocalRotation(new Quaternion().fromAngleAxis(Utils.getRandomNumberInRange(-6.5f, 6.5f) * FastMath.DEG_TO_RAD, new Vector3f(1, 0, 1)));

                            kernelInterface.getRootNode().attachChild(treeNode);
                            kernelInterface.getLogger().debug("Attached " + treeNode.hashCode() + treeNode.getLocalTranslation().toString());
                            break;
                        }
                    } else {
                        kernelInterface.getLogger().debug("Placement MISS " + treeNode.hashCode() + treeNode.getLocalTranslation().toString());
                    }
                }
            });
        } else {
            Stream<Spatial> stream = quadForest.stream();
            stream.forEach(treeNode -> {
                kernelInterface.getLogger().debug("Attached again " + treeNode.hashCode() + treeNode.getLocalTranslation().toString());
                kernelInterface.getRootNode().attachChild(treeNode);
            });
        }

        quad.setUserData("quadForest", quadForest);
    }
}
