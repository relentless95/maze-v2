package de.tum.cit.ase.maze;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * A class that extend GameObject that sets the chest where
 * the key is located to exit the maze.
 **/
public class ExtraLives extends GameObject {

    private Animation<TextureRegion> livesAnimation;
    private TextureRegion textureRegion;
    private float stateTime; // Used to track the elapsed time for animation
    private boolean collected;

    public ExtraLives(TextureRegion textureRegion, float x, float y, float width, float height,
                      Animation<TextureRegion> livesAnimation) {
        super(textureRegion, x, y, 32, 46, new Rectangle(x, y, width, height),
                new Rectangle(x, y, width, height));
        setPosition(x, y);
        this.livesAnimation = livesAnimation;
        this.stateTime = 0;
        this.collected = false;
        this.textureRegion = textureRegion;
        setWidth(width);
        setHeight(height);

        setBoundarySquare(8 * 9, 6);
        Action hideAction = Actions.hide();
        this.addAction(hideAction);

    }

    public void setVisible(){
        Action show = Actions.visible(true);
        this.addAction(show);
    }

    public void collect() {
        collected = true;
        clearActions();
        addAction(Actions.fadeOut(0f));
        addAction(Actions.after(Actions.removeActor()));
    }

    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }


    public boolean getCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {


        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());



    }
}

