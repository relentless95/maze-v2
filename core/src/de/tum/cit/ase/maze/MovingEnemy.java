package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.IntArray;
import org.w3c.dom.Text;

public class MovingEnemy extends Actor {
    private float speed;
    private TextureRegion textureRegion;
    private Animation<TextureRegion> rightAnimation;
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> DownAnimation;
    private Animation<TextureRegion> LeftAnimation;
    private float stateTime;
    private Pathfinding pathfinding;
    private IntArray path;
    //    private Player player;
    private boolean[] allTiles;


    public MovingEnemy(float x, float y, float speed, TextureRegion textureRegion, Animation<TextureRegion> rightAnimation, Animation<TextureRegion> leftAnimation, Animation<TextureRegion> upAnimation, Animation<TextureRegion> DownAnimation, Pathfinding pathfinding) {
        setPosition(x, y);
        this.speed = speed;
        this.textureRegion = textureRegion;
        this.rightAnimation = rightAnimation;
        this.pathfinding = pathfinding;
//        this.player = player;
//        setBoundaryPolygon(10);
        this.path = new IntArray();
        setSize(textureRegion.getRegionWidth() * 5, textureRegion.getRegionHeight() * 5);
        float stateTime = 0f;
        allTiles = MapFileReader.getAllTiles();
        System.out.println("moving enemies width: " + textureRegion.getRegionWidth() + "moving enemies height: " + textureRegion.getRegionHeight());
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

        System.out.println("PlayerPosition: (" + playerPositionX + ", " + playerPositionY + ")");
        System.out.println("PlayerBoxPosition: (" + playerBoxPositionX + ", " + playerBoxPositionY + ")");
        System.out.println("EnemyBoxPosition: (" + enemyBoxPositionX + ", " + enemyBoxPositionY + ")");
        System.out.println("EnemyPosition: (" + enemyPositionX + ", " + enemyPositionY + ")");

        calculatePath(playerBoxPositionX, playerBoxPositionY, enemyBoxPositionX, enemyBoxPositionY);

        // Set enemy position to player position
        // if (playerBoxPositionX == enemyBoxPositionX && playerBoxPositionY == enemyBoxPositionY) {
        //     setPosition(playerPositionX, playerPositionY);
        //     return;
        // }

        if (path.size >= 2) {
            int nextBoxY = path.get(0);
            int nextBoxX = path.get(1);

            System.out.println("NextBox: (" + nextBoxX + ", " + nextBoxY + ")");

            float nextPositionX = nextBoxX * 16 * GameScreen.getUnitScale();
            float nextPositionY = nextBoxY * 16 * GameScreen.getUnitScale();

            float dx = nextPositionX - enemyPositionX;
            float dy = nextPositionY - enemyPositionY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            System.out.println("Distance: " + distance);

            float speed = 50f;
            float speedX = (speed / distance) * dx;
            float speedY = (speed / distance) * dy;

            if (distance >= 0) {
                float newEnemyPositionX = enemyPositionX + (speedX * delta);
                float newEnemyPositionY = enemyPositionY + (speedY * delta);

                setPosition(newEnemyPositionX, newEnemyPositionY);
                System.out.println("NewEnemyPosition: (" + newEnemyPositionX + ", " + newEnemyPositionX + ")");
            }

        }

    }

    // Interpolation function
    private float interpolate(float start, float end, float alpha) {
        return start + alpha * (end - start);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (speed > 0) {
            batch.draw(rightAnimation.getKeyFrame(stateTime, true), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        } else {
            batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }

    private void calculatePath(int playerBoxX, int playerBoxY, int enemyBoxX, int enemyBoxY) {
        // calculate the path using pathfinding

        path = pathfinding.findPath(enemyBoxX, enemyBoxY, playerBoxX, playerBoxY);
        path.reverse(); // [(y1,x1), (y2,x2), ...]
        System.out.println("the path: " + path);
    }


    private void moveAlongPath(float delta) {
//        path.isEmpty()
//        System.out.println("path size: " + path.size);
        if (path.size >= 2) {

//            float targetX = path.get(3) * GameScreen.getUnitScale() * 16;
//            float targetY = path.get(4) * GameScreen.getUnitScale() * 16;

//            setPosition(targetX, targetY);

            System.out.println("path.get(0): " + path.get(0));
            System.out.println("path.get(1): " + path.get(1));

            for (int i = 0; i < path.size; i += 2) {
                float targetX = path.get(i) * GameScreen.getUnitScale() * 16;
                float targetY = path.get(i + 1) * GameScreen.getUnitScale() * 16;

                setPosition(targetX, targetY);
            }


//            System.out.println("move along path " + "x: " + targetX + ", y: " + targetY);
//
//            float speed = 50f; // adjust the speed as necessary;
//            float dx = targetX - getX();
//            System.out.println("targetX : " + targetX + " , getX: " + getX());
//            float dy = targetY - getY();
//            System.out.println("targety : " + targetY + " , getY: " + getY());
//            dy += 1000;
//            dx += 1000;
//
//            float distance = (float) Math.sqrt(dx * dx + dy * dy);
//            System.out.println("dy: " + dy + " dx: " + dx);
//
//            System.out.println("distance: " + distance);
//            System.out.println("speed: " + speed);
//
//
//            if (distance > 0) {
//                float vx = (speed * dx) / distance;
//                float vy = (speed * dy) / distance;
//                float newX = getX() + vx * delta;
//                float newY = getY() + vy * delta;
//                System.out.println("newX: " + newX + " newY: " + newY);
//
//                // update the enemy position;
//                setPosition(newX, newY);
//
//                // check if the enemy has reached the current target point
//                if (Math.abs(newX - targetX) < 1 && Math.abs(newY - targetY) < 1) {
//                    path.removeIndex(0);
//                }

//                System.out.println("new position x:" + newX + " , new positon Y: " + newY);
//                System.out.println("new position x:" + newX / (16 * 5) + " , new positon Y: " + newY / (16 * 5));
            System.out.println("new position x:" + getX() / (16 * 5) + " , new positon Y: " + getY() / (16 * 5));
        }

//        }
    }
}
