package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.IntArray;


/**
 * The class where enemy movement using the path finding algorithm is used. The game has 2 type of enemies. Moving
 * and stationary moving
 **/
public class MovingEnemy extends Actor {
    private float speed;
    private TextureRegion textureRegion;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private float stateTime;
    private Pathfinding pathfinding;
    private IntArray path;
    private boolean[] allTiles;
    private float offsetX, offsetY;
    private Polygon boundaryPolygon;
    private Animation<TextureRegion> currentAnimation;


    public MovingEnemy(float x, float y, float offsetX, float offsetY, float speed, TextureRegion textureRegion,
                       Animation<TextureRegion> rightAnimation, Animation<TextureRegion> leftAnimation,
                       Animation<TextureRegion> upAnimation, Animation<TextureRegion> downAnimation,
                       Pathfinding pathfinding) {
        setPosition(x, y);
        this.speed = speed;
        this.textureRegion = textureRegion;
        this.pathfinding = pathfinding;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        // animations
        this.rightAnimation = rightAnimation;
        this.leftAnimation = leftAnimation;
        this.upAnimation = upAnimation;
        this.downAnimation = downAnimation;
        this.currentAnimation = downAnimation;

        setBoundarySquare(8 * 9, 6);
        this.path = new IntArray();
        setSize(textureRegion.getRegionWidth() * 5, textureRegion.getRegionHeight() * 5);
        float stateTime = 0f;
        allTiles = MapFileReader.getAllTiles();
    }

    public void act(float delta) {
        // delta is the time in seconds since the last frame
        super.act(delta);
        stateTime += delta;

        float playerPositionX = Player.getPlayerX();
        float playerPositionY = Player.getPlayerY();

        int playerBoxPositionX = Math.round(playerPositionX / (16 * GameScreen.getUnitScale()));
        int playerBoxPositionY = Math.round(playerPositionY / (16 * GameScreen.getUnitScale()));

        float enemyPositionX = getX();
        float enemyPositionY = getY();

        int enemyBoxPositionX = Math.round(enemyPositionX / (16 * GameScreen.getUnitScale()));
        int enemyBoxPositionY = Math.round(enemyPositionY / (16 * GameScreen.getUnitScale()));


        calculatePath(playerBoxPositionX, playerBoxPositionY, enemyBoxPositionX, enemyBoxPositionY);


        if (path.size >= 2) {
            int nextBoxY = path.get(0);
            int nextBoxX = path.get(1);


            float nextPositionX = nextBoxX * 16 * GameScreen.getUnitScale();
            float nextPositionY = nextBoxY * 16 * GameScreen.getUnitScale();

            float dx = nextPositionX - enemyPositionX;
            float dy = nextPositionY - enemyPositionY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            float speed = 50f;
            float speedX = (speed / distance) * dx;
            float speedY = (speed / distance) * dy;

            if (distance >= 0) {
                float newEnemyPositionX = enemyPositionX + (speedX * delta);
                float newEnemyPositionY = enemyPositionY + (speedY * delta);

                setPosition(newEnemyPositionX, newEnemyPositionY);
            }

        }

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

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (speed > 0) {
            batch.draw(currentAnimation.getKeyFrame(stateTime, true), getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        } else {
            batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }

    private void calculatePath(int playerBoxX, int playerBoxY, int enemyBoxX, int enemyBoxY) {
        // calculate the path using pathfinding

        path = pathfinding.findPath(enemyBoxX, enemyBoxY, playerBoxX, playerBoxY);
        path.reverse(); // [(y1,x1), (y2,x2), ...]

        // update the current animation based on the movement direction
        if (path.size >= 2) {
            int nextBoxY = path.get(0);
            int nextBoxX = path.get(1);

            if (nextBoxY > enemyBoxY) {
                currentAnimation = upAnimation;
            } else if (nextBoxY < enemyBoxY) {
                currentAnimation = downAnimation;
            } else if (nextBoxX > enemyBoxX) {
                currentAnimation = rightAnimation;
            } else if (nextBoxX < enemyBoxX) {
                currentAnimation = leftAnimation;
            }
        }
    }
}
