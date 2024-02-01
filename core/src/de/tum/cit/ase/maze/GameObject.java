package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;


/**The parent game object class, it's called in overlapping methods
 * in order to get the specific information about the subclasses, e.g. enemies.
 */
public class GameObject extends Actor {

    private TextureRegion textureRegion;
    private Rectangle bounds;
    private float x;
    private float y;
    private Rectangle rectangle;
    private Polygon boundaryPolygon;
    private int offsetX, offsetY;


    public GameObject(TextureRegion textureRegion, float x, float y, int offsetX, int offsetY, Rectangle bounds, Rectangle rectangle) {
        this.textureRegion = textureRegion;
        this.bounds = bounds;
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rectangle = rectangle;
        // added this to be able to get the correct width and height even when scaling.
        // it's important because of the polygons used for collision detection
        setBounds(x, y, 16 * GameScreen.getUnitScale(), GameScreen.getUnitScale());
        setBoundarySquare(80, 48);

    }


    public void setBoundarySquare(float width, float height) {

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        float[] vertices = {
                -halfWidth, -halfHeight,
                halfWidth, -halfHeight,
                halfWidth, halfHeight,
                -halfWidth, halfHeight
        };

        boundaryPolygon = new Polygon(vertices);
    }


    public Polygon getBoundarySquaredPolygon() {
        boundaryPolygon.setPosition(getX() + offsetX, getY() + offsetY);
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    //----end of using polygons

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public Rectangle getBounds() {
        return bounds;
    }




}
