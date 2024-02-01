package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 *A class that extend GameObject that sets the exits of the maze
 * for each map.
 **/
public class Exit extends GameObject {


    private Animation<TextureRegion> exitAnimation;
    private float stateTime; // Used to track the elapsed time for animation
    private boolean exitOpen;


    public Exit(TextureRegion textureRegion, float x, float y, float width, float height, Animation<TextureRegion> exitAnimation) {
        super(textureRegion, x, y, 32, 48, new Rectangle(x,y, width, height), new Rectangle(x,y, width, height));
        this.exitAnimation = exitAnimation;
        this.stateTime = 0;
        this.exitOpen = false;
        setBoundarySquare(8*10, 8*4);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        return exitAnimation.getKeyFrame(stateTime, true);
    }

    public boolean isExitOpen() {
        return exitOpen;
    }

    public void setExitOpen(boolean exitOpen) {
        this.exitOpen = exitOpen;
    }
}



