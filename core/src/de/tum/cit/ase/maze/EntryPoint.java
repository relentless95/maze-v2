package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class EntryPoint extends GameObject {


    private Animation<TextureRegion> entryPointAnimation;
    private float stateTime; // Used to track the elapsed time for animation
    private boolean doorClosed;

//    private boolean animationPlayed;

    public EntryPoint(TextureRegion textureRegion, float x, float y, float width, float height, Animation<TextureRegion> entryPointAnimation) {
        super(textureRegion, x, y, new Rectangle(x, y, width, height), new Rectangle(x, y, width, height));
        this.entryPointAnimation = entryPointAnimation;
        this.stateTime = 0;
//        this.doorOpen = false;
        this.doorClosed = false;
//        this.animationPlayed = false;
        setBoundaryPolygon(10);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        return entryPointAnimation.getKeyFrame(stateTime, true);
    }

    public boolean isDoorClosed() {
        return doorClosed;
    }


    public void setDoorClosed(boolean doorClosed) {
        this.doorClosed = doorClosed;
    }

//    public boolean isAnimationPlayed() {
//        return animationPlayed;
//    }
//
//    public void setAnimationPlayed(boolean animationPlayed) {
//        this.animationPlayed = animationPlayed;
//    }
}



