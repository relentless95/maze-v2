package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;



public class Wall extends GameObject{

    public Wall(TextureRegion textureRegion, float x, float y, float width, float height){
//        super(textureRegion, x, y, bounds);
        super(textureRegion, x, y, new Rectangle(x,y, width, height), new Rectangle(x,y, width, height));
        setBoundaryPolygon(10);

    }
}
