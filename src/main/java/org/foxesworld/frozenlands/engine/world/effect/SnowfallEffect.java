package org.foxesworld.frozenlands.engine.world.effect;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import org.foxesworld.frozenlands.engine.Kernel;

public class SnowfallEffect extends ParticleEmitter {

    public SnowfallEffect(Kernel kernel) {
        super("Snow", ParticleMesh.Type.Triangle, 2000);
        Material snowMat = new Material(kernel.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        snowMat.setTexture("Texture", kernel.getAssetManager().loadTexture("textures/snowflake.png"));
        setMaterial(snowMat);
        setImagesX(1);
        setImagesY(1);
        setStartColor(new ColorRGBA(1f, 1f, 1f, 1f));
        setEndColor(new ColorRGBA(1f, 1f, 1f, 0.3f));
        setStartSize(0.05f);
        setEndSize(0.05f);
        setGravity(0, -0.5f, 0);
        setLowLife(8);
        setHighLife(20);
        getParticleInfluencer().setInitialVelocity(new Vector3f(0, -0.1f, 0));
        getParticleInfluencer().setVelocityVariation(0.1f);
    }
}