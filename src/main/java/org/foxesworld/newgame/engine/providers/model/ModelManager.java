package org.foxesworld.newgame.engine.providers.model;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.foxesworld.newgame.engine.Kernel;

public class ModelManager {

    private final AssetManager assetManager;
    private final BulletAppState bulletAppState;
    private final  Node rootNode;

    public ModelManager(AssetManager assetManager, BulletAppState bulletAppState, Node rootNode) {
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        this.rootNode = rootNode;
    }

    public Spatial addModels(Node rootNode, String modelPath, String material) {
        Spatial model = loadModel(modelPath);
        rootNode.attachChild(model);
        //model.setMaterial(Kernel.aterials.get(material));

        RigidBodyControl collisionControl = createCollisionControl(model);
        rootNode.addControl(collisionControl);

        return model;
    }

    private Spatial loadModel(String modelPath) {
        Spatial model = assetManager.loadModel(modelPath);
        model.setName("Model");
        return model;
    }

    private RigidBodyControl createCollisionControl(Spatial spatial) {
        CollisionShape shape = CollisionShapeFactory.createMeshShape((Node) spatial);
        RigidBodyControl control = new RigidBodyControl(shape, 0);
        spatial.addControl(control);
        bulletAppState.getPhysicsSpace().add(control);
        return control;
    }
}
