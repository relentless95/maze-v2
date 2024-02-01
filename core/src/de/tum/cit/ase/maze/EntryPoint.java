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
        super(textureRegion, x, y, 32, 48, new Rectangle(x,y, width, height), new Rectangle(x,y, width, height));
        this.entryPointAnimation = entryPointAnimation;
        this.stateTime = 0;
        this.doorClosed = false;
        setBoundarySquare(8*10, 8*4);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public boolean isDoorClosed() {
        return doorClosed;
    }


    public void setDoorClosed(boolean doorClosed) {
        this.doorClosed = doorClosed;
    }

}



