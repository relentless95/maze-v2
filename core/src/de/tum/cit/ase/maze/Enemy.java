package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends GameObject {


    private Animation<TextureRegion> EnemyAnimation;
    private float stateTime; // Used to track the elapsed time for animation

    private static float speed = 10;
    private Polygon boundaryPolygon;
    private Rectangle rectangle;
    private Direction facingDirection;
    private Vector2 directionVector;
    private boolean foundObstacle;


    public Enemy(TextureRegion textureRegion, float x, float y, float width, float height, Animation<TextureRegion> EnemyAnimation) {
        super(textureRegion, x, y, 32, 46, new Rectangle(x,y, width, height), new Rectangle(x,y, width, height));
        this.EnemyAnimation = EnemyAnimation;
        this.directionVector = new Vector2(x, y);
        this.stateTime = 0;
        setBoundarySquare(8*5, 8);
    }

    public void update(float delta, Player player) {
        stateTime += delta;
    }



    public TextureRegion getCurrentFrame() {
        return EnemyAnimation.getKeyFrame(stateTime, true);
    }


}



