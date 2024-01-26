package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationLoader {
    //A class with a static method that makes loading any animation much easier.
    public static Animation<TextureRegion> loadAnimation(Texture animationSheet, int animationFrames, int frameWidth, int frameHeight, int initialColumn, int row, float animationDuration){
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);
        for (int col = initialColumn; col < animationFrames; col++) {
            frames.add(new TextureRegion(animationSheet, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
        }
        Animation<TextureRegion> newAnimation = new Animation<>(animationDuration, frames);
        return newAnimation;
    }
}
