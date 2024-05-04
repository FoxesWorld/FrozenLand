package org.foxesworld.frozenlands.engine.world.terrain.gen.tree;

import static org.foxesworld.frozenlands.engine.config.Constants.RAY_DOWN;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.foxesworld.frozenlands.FrozenLands;
import org.foxesworld.frozenlands.engine.Kernel;
import org.foxesworld.frozenlands.engine.config.Constants;
import org.foxesworld.frozenlands.engine.utils.LodUtils;
import org.foxesworld.frozenlands.engine.utils.Utils;

public class TreeGen {
    private Spatial treeModel;
    private Kernel kernel;

    public TreeGen(Kernel kernel) {
        this.kernel = kernel;
        this.initializeTreeModel();
    }

    private void initializeTreeModel() {
        treeModel = kernel.getAssetManager().loadModel("Models/Fir1/fir1_androlo.j3o");
        treeModel.setShadowMode(RenderQueue.ShadowMode.Cast);
        LodUtils.setUpTreeModelLod(treeModel);
    }

    private RigidBodyControl createCollisionControl(Spatial spatial) {
        CollisionShape shape = CollisionShapeFactory.createMeshShape(spatial);
        RigidBodyControl control = new RigidBodyControl(shape, 0);
        spatial.addControl(control);
        kernel.getBulletAppState().getPhysicsSpace().add(control);
        return control;
    }

    public List<Spatial> setupTrees() {
        int forestSize = (int) Utils.getRandomNumberInRange(500, 800); // Adjust forest density
        List<Spatial> quadForest = new ArrayList<>(forestSize);
        for (int i = 0; i < forestSize; i++) {
            Spatial treeModelCustom = treeModel.clone();
            float scaleFactor = Utils.getRandomNumberInRange(0.1f, 1.5f); // Adjust scale range
            //treeModelCustom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            //treeModelCustom.setCullHint(Spatial.CullHint.Never);
            treeModelCustom.scale(scaleFactor, scaleFactor, scaleFactor);
            quadForest.add(treeModelCustom);
        }

        return quadForest;
    }

    public void positionTrees(TerrainQuad quad) {
        List<Spatial> quadForest = quad.getUserData("quadForest");
        if (quadForest == null) {
            quadForest = setupTrees();

            Stream<Spatial> stream = quadForest.stream();
            stream.forEach(treeNode -> {
                int generated = -1;
                while (generated++ < 0) {
                    CollisionResults results = new CollisionResults();

                    float y = kernel.getPlayer().getPlayerPosition().y;
                    if (y < Constants.WATER_LEVEL_HEIGHT)
                        y = 0;

                    Vector3f start = new Vector3f(
                            kernel.getPlayer().getPlayerPosition().x
                                    + Utils.getRandomNumberInRange(-800, 800), // Adjust position range
                            y,
                            kernel.getPlayer().getPlayerPosition().z
                                    + Utils.getRandomNumberInRange(-800, 800)); // Adjust position range
                    Ray ray = new Ray(start, RAY_DOWN);

                    quad.collideWith(ray, results);
                    CollisionResult hit = results.getClosestCollision();
                    if (hit != null) {
                        if (hit.getContactPoint().y > Constants.WATER_LEVEL_HEIGHT) {
                            Vector3f plantLocation = new Vector3f(hit.getContactPoint().x,
                                    hit.getContactPoint().y, hit.getContactPoint().z);
                            Quaternion rotation = new Quaternion().fromAngleAxis(
                                    Utils.getRandomNumberInRange(-15f, 15f) * FastMath.DEG_TO_RAD,
                                    new Vector3f(0, 1, 0)); // Rotate around Y-axis
                            treeNode.setLocalTranslation(plantLocation);
                            treeNode.setLocalRotation(rotation);

                            kernel.getRootNode().attachChild(treeNode);
                            createCollisionControl(treeNode); // Generate collision for the tree
                            FrozenLands.logger.debug("Attached "
                                    + treeNode.hashCode()
                                    + treeNode.getLocalTranslation().toString());
                            break;
                        }
                    } else {
                        FrozenLands.logger.debug("Placement MISS "
                                + treeNode.hashCode()
                                + treeNode.getLocalTranslation().toString());
                    }
                }
            });
        } else {
            Stream<Spatial> stream = quadForest.stream();
            stream.forEach(treeNode -> {
                FrozenLands.logger.debug("Attached again "
                        + treeNode.hashCode() + treeNode.getLocalTranslation().toString());
                kernel.getRootNode().attachChild(treeNode);
            });
        }

        quad.setUserData("quadForest", quadForest);
    }
}
