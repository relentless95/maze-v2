package de.tum.cit.ase.maze;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 *A class that extend GameObject that sets the chest where
 * the key is located to exit the maze.
 **/
public class Chest extends GameObject {

    private Animation<TextureRegion> chestAnimation;
    private float stateTime; // Used to track the elapsed time for animation
    private boolean chestOpen;

    public Chest(TextureRegion textureRegion, float x, float y, float width, float height, Animation<TextureRegion> chestAnimation) {
        super(textureRegion, x, y, 32, 46, new Rectangle(x,y, width, height), new Rectangle(x,y, width, height));
        this.chestAnimation = chestAnimation;
        this.stateTime = 0;
        this.chestOpen = false;
        setBoundarySquare(8*9, 6);
    }

    public void update(float delta) {
        stateTime += delta;
    }


    public boolean isChestOpen() {
        return chestOpen;
    }

    public void setChestOpen(boolean chestOpen) {
        this.chestOpen = chestOpen;
    }
}

