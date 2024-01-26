package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Trap extends GameObject {


    private Animation<TextureRegion> trapAnimation;
    private float stateTime; // Used to track the elapsed time for animation
    private TrapState trapState;
    private boolean spikesFullyExtended;
    private boolean damagePlayer = false;
//    private boolean spikesExtendedCycle;

    private static float COOLDOWN_TIME = 5;
    private float cooldown;
    public Trap(TextureRegion textureRegion, float x, float y, float width, float height, Animation<TextureRegion> trapAnimation) {
        super(textureRegion, x, y, new Rectangle(x, y, width, height), new Rectangle(x, y, width, height));
        this.trapAnimation = trapAnimation;
        this.stateTime = 0;
        this.spikesFullyExtended = false;
        cooldown = 0;
//        this.spikesExtendedCycle = false;

    }

    public void update(float delta) {
        stateTime += delta;
        updateTrapState();
        cooldown -= delta;
    }

    public boolean canDamagePlayer() {
        if (cooldown <= 1){
            cooldown = COOLDOWN_TIME;
            return true;
        }
        else{
            return false;
        }
    }

    public TextureRegion getCurrentFrame() {
//        return trapAnimation.getKeyFrame(stateTime);
        return trapAnimation.getKeyFrame(stateTime, true);

    }

//    public int getCurrentFrameIndex(){

//        float animationFrames = trapAnimation.getKeyFrames().length;
//        float animationDuration = trapAnimation.getAnimationDuration();
//        float frameDuration = animationDuration / animationFrames;

//        int currentFrameIndex = (int) ((stateTime % animationDuration) / frameDuration);
//        return trapAnimation.getKeyFrameIndex(stateTime);
//        System.out.println("from the trap class: " + currentFrameIndex);
//        return  currentFrameIndex;
//    }
//}

    private void updateTrapState() {
        // get the current frame from the trap's animation


//        TextureRegion currentFrame = getCurrentFrame();
//        float animationFrames = trapAnimation.getKeyFrames().length;
//        float animationDuration = trapAnimation.getAnimationDuration();
//        float frameDuration = animationDuration / animationFrames; // Duration per frame
//
//        int currentFrameIndex = (int) ((stateTime % animationDuration) / frameDuration);
//        System.out.println("from the trap class: " + currentFrameIndex);


//        return trapAnimation.getKeyFrameIndex(stateTime);
//        TextureRegion currentFrame = getCurrentFrame();
//        int currentFrameIndex = trapAnimation.getKeyFrameIndex(stateTime);
//        System.out.println(currentFrameIndex);
            if (isThirdFrame()) {
//            trapState = TrapState.FULLY_EXTENDED;
                spikesFullyExtended = true;
//            spikesExtendedCycle = true;
//                System.out.println("spikes are fully extended!!!");

            } else if (!isThirdFrame()) {
//            trapState = TrapState.HIDDEN;
            spikesFullyExtended = false;
//            spikesExtendedCycle = false;
//                System.out.println("spikes are not out");
            }

    }

    private TrapState getTrapState() {
        return trapState;
    }

    public void setTrapState(TrapState trapState) {
        this.trapState = trapState;
    }

    public boolean areSpikesFullyExtended() {
        return spikesFullyExtended;
    }

    private boolean isThirdFrame() {
        // third frame is the 2nd index
        int thirdFrameIndex = 2;
//        int currentFrameIndex = trapAnimation.getKeyFrameIndex(stateTime);

        float animationFrames = trapAnimation.getKeyFrames().length;
        float animationDuration = trapAnimation.getAnimationDuration();
        float frameDuration = animationDuration / animationFrames; // Duration per frame

        int currentFrameIndex = (int) ((stateTime % animationDuration) / frameDuration);
//        System.out.println("from the trap class: " + currentFrameIndex);

        return currentFrameIndex == thirdFrameIndex;
    }

    public float getCooldown() {
        return cooldown;
    }
}



