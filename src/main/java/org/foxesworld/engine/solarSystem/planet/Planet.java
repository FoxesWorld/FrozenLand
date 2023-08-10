package org.foxesworld.engine.solarSystem.planet;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import org.foxesworld.engine.texture.LoadTexture;
import org.foxesworld.engine.texture.TextureWrap;
import org.foxesworld.engine.utils.CollisionHelper;

public class Planet extends PlanetAbstract {

    protected  LoadTexture planetTexture;

    public Planet(SimpleApplication game, BulletAppState bulletAppState, String name) {
        planetTexture = new LoadTexture(game, "Common/MatDefs/Light/Lighting.j3md");
        setBulletAppState(bulletAppState);
        setRootNode(game.getRootNode());
        setPlanetName(name);
    }

    @Override
    public void setGeometry(int zSamples, int radialSamples, float radius) {
        setPlanet(new Geometry(getPlanetName(), new Sphere(zSamples, radialSamples, radius)));
    }

    @Override
    public void addTexture(String mapType, String texture, TextureWrap wrap){
        planetTexture.addTexture(mapType, texture, wrap);
    }

    @Override
    public void addPlanet() {
        Node planetNode = new Node(getPlanetName());
        planetNode.attachChild(getPlanet());
        planetNode.setMaterial(planetTexture.getMaterial());
        CollisionHelper collision = new CollisionHelper();
        CollisionShape treeCollisionShape = collision.objectCollision(planetNode);
        RigidBodyControl rigid = new RigidBodyControl(treeCollisionShape, 0.0f);
        planetNode.addControl(rigid);
        getBulletAppState().getPhysicsSpace().add(rigid);
        getRootNode().attachChild(planetNode);
    }

}
