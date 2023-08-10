package org.foxesworld.engine.utils;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class CollisionHelper {

    public CollisionHelper(){

    }

    public CollisionShape objectCollision(Spatial obj) {
        if (obj instanceof Node) {
            Node treeNode = (Node) obj;
            CompoundCollisionShape compoundShape = new CompoundCollisionShape();
            BoundingBox boundingBox = (BoundingBox) treeNode.getWorldBound();
            Vector3f treeCenter = boundingBox.getCenter();

            for (Spatial child : treeNode.getChildren()) {
                if (child instanceof Geometry) {
                    Geometry geometry = (Geometry) child;
                    Mesh treeMesh = geometry.getMesh();

                    CollisionShape shape = new MeshCollisionShape(treeMesh);
                    Vector3f translation = geometry.getWorldTranslation().subtract(treeCenter);
                    Matrix3f rotation = geometry.getWorldRotation().toRotationMatrix();
                    compoundShape.addChildShape(shape, translation, rotation);
                }
            }

            return compoundShape;
        } else if (obj instanceof Geometry) {
            // For a single geometry tree model, use MeshCollisionShape
            Geometry geometry = (Geometry) obj;
            Mesh treeMesh = geometry.getMesh();

            return new MeshCollisionShape(treeMesh);
        } else {
            throw new IllegalArgumentException("Invalid tree instance type: " + obj.getClass().getName());
        }
    }



    /*public CollisionShape terrainCollission(int gridSize, float stepSize, Node terrainNode) {
        if (terrainNode.getQuantity() != 1 || !(terrainNode.getChild(0) instanceof Geometry)) {
            throw new IllegalStateException("TerrainNode should have exactly one Geometry child.");
        }
        Geometry terrainGeometry = (Geometry) terrainNode.getChild(0);
        Mesh terrainMesh = terrainGeometry.getMesh();
        return new MeshCollisionShape(terrainMesh);
    } */

    public CollisionShape terrainCollision(Node terrainNode) {
        Geometry terrainGeometry = (Geometry) terrainNode.getChild(0); // Предполагается, что у вас одна геометрия
        MeshCollisionShape meshCollisionShape = new MeshCollisionShape(terrainGeometry.getMesh());

        return meshCollisionShape;
    }
}
