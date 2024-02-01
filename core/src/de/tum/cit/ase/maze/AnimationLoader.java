package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**A class with a static method that makes loading any animation much easier. It receives all the parameters needed to fill a TextureRegion array, which is the component
of an animation object in LibGDX.KeyAnimation*/
public class AnimationLoader {

    public static Animation<TextureRegion> loadAnimation(Texture animationSheet, int animationFrames, int frameWidth, int frameHeight, int initialColumn, int row, float animationDuration, boolean leftToRight){
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);
        if (leftToRight){
            for (int col = initialColumn; col < animationFrames; col++) {
                frames.add(new TextureRegion(animationSheet, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
            }
        }
        else{
            for (int col = initialColumn + animationFrames; col > initialColumn; col--) {
                frames.add(new TextureRegion(animationSheet, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
            }
        }
        return new Animation<>(animationDuration, frames);
    }
}
