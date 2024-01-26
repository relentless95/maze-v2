package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GameObject extends Actor {

    private TextureRegion textureRegion;
    private Rectangle bounds;
    private float x;
    private float y;

    private Rectangle rectangle;

    private Polygon boundaryPolygon;


    public GameObject(TextureRegion textureRegion, float x, float y, Rectangle bounds, Rectangle rectangle) {
        this.textureRegion = textureRegion;
        this.bounds = bounds;
        this.x = x;
        this.y = y;
        this.rectangle = rectangle;
        // added this to be able to get the correct width and height even when scaling.it's important because of the polygons used for collision detection
        setBounds(x, y, 16 * 5, 16 * 5);
        if (boundaryPolygon == null) {
            setBoundaryRectangle();
        }
    }


    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getRectangle() {
//        rectangle.setPosition(getX(), getY());
        return rectangle;
    }

    public Rectangle getRec() {
//        System.out.println("the values of get x and get y: " + getX() + " " + getY());
        return rectangle;
    }


    // using polygons
    public void setBoundaryRectangle() {
//        float w = getWidth();
//        float h = getHeight();
        float w = 16 * 5;
        float h = 16 * 5;
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
//        System.out.println("getWidth and getHeight in gameObject class: " + getWidth() + + getHeight());
        System.out.println("In gameObject: getX(), getY(), getOriginX(), getOriginY(), getRotation(), getScaleX(), getScaleY(): "
                + getX() + " " + getY() + " " + getOriginX() + " " + getOriginY() + " " + getRotation() + " "
                + getScaleX() + " " + getScaleY());
    }


    public void setBoundaryPolygon(int numSides) {
//        float w = getWidth();
//        float h = getHeight();
        float w = 80;
        float h = 80;

        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
//            float angle = i * 6.28f / numSides;
//            float angle = (i + 0.5f)*360f/numSides;

            float angle = i * 360f / numSides;

            //x-coordinate
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2;
            //y-coordinate
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2;
        }

        boundaryPolygon = new Polygon(vertices);
    }

    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY() + 16);
        boundaryPolygon.setOrigin(getOriginX(), getOriginY() + 16);
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    //----end of using polygons


    // n order to center an Actor at a given location, we have shift it from this location
    //by half its width along the x direction and half its height along the y direction.
    public void centerAtPosition(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(GameObject other) {
        centerAtPosition(other.getX() + other.getWidth() / 2, other.getY() + other.getHeight() / 2);
    }

    public void setOpacity(float opacity) {
        this.getColor().a = opacity;
    }


}
