package org.foxesworld.newgame.engine.player;


import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.foxesworld.newgame.engine.providers.sound.SoundManager;
import wf.frk.f3banimation.AnimChannel;
import wf.frk.f3banimation.Animation;
import wf.frk.f3banimation.AnimationGroupControl;
import wf.frk.f3banimation.blending.BlendingFunction;
import wf.frk.f3banimation.blending.TimeFunction;
import wf.frk.f3banimation.utils.TriFunction;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ActionsControl extends AbstractControl{
    private boolean ready=false;
    private BetterCharacterControl character;
    private AnimationGroupControl anims;
    private final Map<String, List<AudioNode>> playerSounds;
    private AudioNode jumpUp, jumpDown;
    private SoundManager soundManager;
    private AssetManager assetManager;

    public ActionsControl(AssetManager assetManager, SoundManager soundManager){
        this.soundManager = soundManager;
        this.assetManager=assetManager;
        this.playerSounds = soundManager.getSoundBlock("player");
    }

    public ActionsControl(AssetManager assetManager,SoundManager soundManager,BetterCharacterControl character){
        this(assetManager,soundManager);
        this.character=character;
    }

    private void initialize() {
        if(ready) return; // initialize only once.
        ready=true;
        if(character == null) character=spatial.getControl(BetterCharacterControl.class);
        jumpUp = getRandomAudioNode("jump/takeoff");
        jumpDown = getRandomAudioNode("jump/land");
        anims=AnimationGroupControl.of(spatial);
        anims.setUseHardwareSkinning(true);
        anims.setAction("Stand",TimeFunction.newLooping(() -> 1f),BlendingFunction.newSimple(() -> 1f));
        anims.setAction("Hold",TimeFunction.newClamped(() -> 1f),BlendingFunction.newSimple(() -> 1f));

        if(character != null){
            TriFunction<AnimChannel,Animation,Float,java.lang.Boolean> isRunning=(chan, anim, tpf) -> {
                return character.getVelocity().length() > 1 && character.isOnGround();
            };
            anims.setAction("FPSRun",TimeFunction.newLooping(() -> 1f),BlendingFunction.newToggleFade(0.5f,0.5f,false,() -> 1f,isRunning));
            anims.setAction("Stand",TimeFunction.newLooping(() -> 1f),BlendingFunction.newSimple(() -> 1f));
            anims.setAction("LookUpAndDown",TimeFunction.newSteppingRangeFunction((chan, anim, tpf) -> {
                return (-FastMath.clamp(character.getViewDirection().y ,-1f,1f) )* 0.5f + 0.5f;
            }),BlendingFunction.newSimple(() -> 1f));
            anims.setAction("Jump",TimeFunction.newClamped(() -> 1f),BlendingFunction.newToggleFade(0.2f,0.1f,false,() -> 1f,(chan, anim, tpf) -> {
                return !character.isOnGround();
            }));
            anims.setAction("GunAnim",TimeFunction.newLooping(() -> {
                return character.getVelocity().length() > 1 && character.isOnGround()?1.f:0f;
            }),BlendingFunction.newSimple(() -> 1f));

        }
    }

    public Spatial shot(AssetManager assetManager,Vector3f pos,Vector3f direction,Node parent,PhysicsSpace phy){

        Node bullet=new Node("bullet");
        parent.attachChild(bullet);

        SphereCollisionShape shape=new SphereCollisionShape(0.5f);
        RigidBodyControl rb=new RigidBodyControl(shape,0.1f);
        bullet.addControl(rb);
        phy.add(rb);
        rb.setGravity(Vector3f.ZERO);
        rb.setPhysicsLocation(pos);

        rb.setLinearVelocity(direction.mult(10f));
        //if(plasmaSound!=null)plasmaSound.playInstance();
        //Sound sound = NewGame.getSOUND();
        //AudioNode snd = sound.getSoundNode("player/shootAudio", false, 2.0f, 20.0f, 1.0f);
        //snd.play();
        AudioNode test = getRandomAudioNode("action");
        test.play();
        return bullet;
    }

    private float t=0;
    private boolean wasOnGround=true;

    @Override
    protected void controlUpdate(float tpf) {
        initialize();
        if(!ready) return;

        if(character != null){
            if(jumpUp != null){
                if(!character.isOnGround() && wasOnGround) jumpUp.playInstance();
                if(character.isOnGround() && !wasOnGround) jumpDown.playInstance();
                jumpUp = getRandomAudioNode("jump/takeoff");
                wasOnGround=character.isOnGround();
            }
            /*
            if(footsteps != null){
                boolean walking=(character.isOnGround() && character.getVelocity().length() > 0.1);
                if(walking){
                    t+=tpf;
                    if(t > 0.3){
                        t=0;
                        footsteps.playInstance();
                    }
                }
            } */
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    public AudioNode getRandomAudioNode(String event) {
        Random random = new Random();
        List<AudioNode> audioNodes = playerSounds.get(event);
        if (audioNodes != null && !audioNodes.isEmpty()) {
            int randomIndex = random.nextInt(audioNodes.size());
            return audioNodes.get(randomIndex);
        }
        return null;
    }

}