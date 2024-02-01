package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;



/**
 * Wall is a class that extends GameObject. It is used to set up the walls in the game.
 */
public class Wall extends GameObject{

    public Wall(TextureRegion textureRegion, float x, float y, float width, float height){
        super(textureRegion, x, y, 32, 48, new Rectangle(x,y, width, height), new Rectangle(x,y, width, height));
        setBoundarySquare(8*10, 8*4);
    }
}
