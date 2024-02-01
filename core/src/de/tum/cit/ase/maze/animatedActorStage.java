package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**A custom class only used to display the key animation
 * which is as an actor to be drawn by the HUD's stage*/
public class animatedActorStage extends Actor {
    private final Animation<TextureRegion> animation;
    private float stateTime;

    public animatedActorStage(Animation<TextureRegion> animation, float x, float y) {
        this.animation = animation;
        this.setX(x);
        this.setY(y);
        this.setWidth(animation.getKeyFrame(0).getRegionWidth());
        this.setHeight(animation.getKeyFrame(0).getRegionHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(animation.getKeyFrame(stateTime, true), getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
