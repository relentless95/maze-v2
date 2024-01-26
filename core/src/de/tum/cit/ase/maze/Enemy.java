package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntArray;


public class Enemy extends GameObject {


    private Animation<TextureRegion> EnemyAnimation;
    private float stateTime; // Used to track the elapsed time for animation
    private Pathfinding pathfinding;
    private IntArray path;
    private Player player;


    public Enemy(TextureRegion textureRegion, float x, float y, float width, float height, Animation<TextureRegion> EnemyAnimation, Pathfinding pathfinding) {
        super(textureRegion, x, y, new Rectangle(x, y, width, height), new Rectangle(x, y, width, height));
        this.EnemyAnimation = EnemyAnimation;
        this.stateTime = 0;
        this.pathfinding = pathfinding;
        setBoundaryPolygon(10);
        this.path = new IntArray();
//       this = player;


    }

    public void update(float delta) {
//        System.out.println("update is called");
        stateTime += delta;
        if (MathUtils.randomBoolean(0.02f)) {
//            System.out.println("player position: " + Math.floor(player.getPlayerX() / (16 * 5)) + ", " + Math.floor(player.getPlayerY() / (16 * 5)));
            calculatePath(player.getPlayerX(), player.getPlayerY());
        }
        moveAlongPath(delta);
    }


    private void calculatePath(float playerX, float playerY) {
        // calculate the path using pathfinding
//        int startX = MathUtils.floor(getBounds().x / GameScreen.getUnitScale() * 16);
//        int startY = MathUtils.floor(getBounds().y / GameScreen.getUnitScale() * 16);
//        int targetX = MathUtils.floor(player.getPlayerX() / GameScreen.getUnitScale() * 16);
//        int targetY = MathUtils.floor(player.getPlayerY() / GameScreen.getUnitScale() * 16);


//        int startX = MathUtils.floor(getBounds().x / (16 * 5));
//        int startY = MathUtils.floor(getBounds().y / (16 * 5));
//        int targetX = MathUtils.floor(player.getPlayerX() / (16 * 5));
//        int targetY = MathUtils.floor(player.getPlayerY() / (16 * 5));

        int targetX = MathUtils.floor(getBounds().x / (16 * 5));
        int targetY = MathUtils.floor(getBounds().y / (16 * 5));
        int startX = MathUtils.floor(player.getPlayerX() / (16 * 5));
        int startY = MathUtils.floor(player.getPlayerY() / (16 * 5));

//        System.out.println("getBounds().x: " + getBounds().x + " getBounds().y: " + getBounds().y);
//        System.out.println("startX: " + startX + " startY: " + startY + " targetX: " + targetX + " targetY: " + targetY);
//        System.out.println("startX: " + startX + " startY: " + startY + " targetX: " + targetX + " targetY: " + targetY);
//        System.out.println("playerX/ (16 * 5): " + startX / 16 * 5 + " playerY/(16*5):" + startY / 16 * 5);


        path = pathfinding.findPath(startX, startY, targetX, targetY);
        System.out.println("the path: " + path);
    }


    private void moveAlongPath(float delta) {
//        path.isEmpty()
        System.out.println("path size: " + path.size);
        if (path.size >= 2) {

            float targetX = path.get(0) * GameScreen.getUnitScale() * 16;
            float targetY = path.get(1) * GameScreen.getUnitScale() * 16;
            System.out.println("move along path " + "x: " + targetX + ", y: " + targetY);

            float speed = 50f; // adjust the speed as necessary;
            float dx = targetX - getBounds().x;
            System.out.println("targetX : " + targetX + " , getBounds: " + getBounds().x);
            float dy = targetY - getBounds().y;
            System.out.println("targety : " + targetY + " , getBounds: " + getBounds().y);
            dy += 1000;
            dx += 1000;

            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            System.out.println("dy: " + dy + " dx: " + dx);

            System.out.println("distance: " + distance);
            System.out.println("speed: " + speed);


            if (distance > 0) {
                float vx = (speed * dx) / distance;
                float vy = (speed * dy) / distance;
                float newX = getBounds().x + vx * delta;
                float newY = getBounds().y + vy * delta;
                System.out.println("newX: " + newX + " newY: " + newY);

                // update the enemy position;
                setPosition(newX, newY);

                // check if the enemy has reached the current target point
                if (Math.abs(newX - targetX) < 1 && Math.abs(newY - targetY) < 1) {
                    path.removeIndex(0);
                }

                System.out.println("new position x:" + newX + " , new positon Y: " + newY);
                System.out.println("new position x:" + newX/(16*5) + " , new positon Y: " + newY/(16*5));
                System.out.println("new position x:" + getBounds().x/(16*5) + " , new positon Y: " + getBounds().y/(16*5));
            }

        }
    }

    public TextureRegion getCurrentFrame() {
        return EnemyAnimation.getKeyFrame(stateTime, true);
    }


}



