package de.tum.cit.ase.maze;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


public class Chest extends GameObject {

    private Animation<TextureRegion> chestAnimation;
    private float stateTime; // Used to track the elapsed time for animation
    private boolean chestOpen;

    public Chest(TextureRegion textureRegion, float x, float y, float width, float height, Animation<TextureRegion> chestAnimation) {
        super(textureRegion, x, y, new Rectangle(x, y, width, height), new Rectangle(x, y, width, height));
        this.chestAnimation = chestAnimation;
        this.stateTime = 0;
        this.chestOpen = false;
        setBoundaryPolygon(10);

    }

    public void update(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        return chestAnimation.getKeyFrame(stateTime, true);
    }

    public boolean isChestOpen() {
        return chestOpen;
    }

    public void setChestOpen(boolean chestOpen) {
        this.chestOpen = chestOpen;
    }
}

