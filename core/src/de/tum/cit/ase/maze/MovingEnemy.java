package de.tum.cit.ase.maze;

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


    public MovingEnemy(float x, float y, float speed, TextureRegion textureRegion,
                       Animation<TextureRegion> rightAnimation, Animation<TextureRegion> leftAnimation
            , Animation<TextureRegion> upAnimation, Animation<TextureRegion> DownAnimation, Pathfinding pathfinding) {
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
        super.act(delta);

//        float newX = getX() + speed * delta;
//        setX(newX);

        stateTime += delta;

//        if (MathUtils.randomBoolean(0.02f)) {
       // if (MathUtils.randomBoolean(delta)) {

            calculatePath(Player.getPlayerX(), Player.getPlayerY());
            System.out.println("---------------------------------------------------------------");
            System.out.println("player position: " + Math.floor(Player.getPlayerX() / (16 * 5)) +
                    ", " + Math.floor(Player.getPlayerY() / (16 * 5)));
            System.out.println("enemy is at position: " + "x = " + this.getX() / (16 * 5) + " y = " + this.getY() / (16 * 5));

       // }
//        //If the path is not empty
        if (path.size >= 2) {
            // Get the target position from the path
            System.out.println("path.get(0): " + path.get(0) +" , GameScreen.getUnitscale() * 16: " + GameScreen.getUnitScale() * 16);
            float X = path.get(0);
            float Y = path.get(1);
            float targetX = X * GameScreen.getUnitScale() * 16;
            System.out.println("path.get(1): " + path.get(1) +" , GameScreen.getUnitscale() * 16: "  + GameScreen.getUnitScale() * 16);
            float targetY = Y * GameScreen.getUnitScale() * 16;
            System.out.println("getX(): " + getX());
            System.out.println("getY(): " + getY());
            System.out.println("All tiles: " + allTiles[61]);

////            // values of the start position:
////            int startX = MathUtils.floor(Player.getPlayerX() / (16 * 5));
////            int startY = MathUtils.floor(Player.getPlayerY() / (16 * 5));

            // Calculate the distance to the target position
            float dx = targetX - getX();
            float dy = targetY - getY();
//
////            float dx = startX - getX();
////            float dy = startY - getY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
//
////                        float speed = 50f; // adjust the speed as necessary;
//
//
//
//            // If the distance is greater than zero
            if (distance > 0) {
                // Calculate the movement speed based on the delta time
                System.out.println("delta: " + delta);
                float speed = 100f;
                float vx = (speed * dx) / distance;
                float vy = (speed * dy) / distance;

                // Update the enemy's position based on the speed and delta time
                float newX = getX() + vx * delta;
                float newY = getY() + vy * delta;

                int newXcoord = (int) Math.ceil(newX/(16*GameScreen.getUnitScale()));
                int xcoord = (int) Math.ceil(getX()/(16*GameScreen.getUnitScale()));
                int newYcoord = (int) Math.ceil(newY/(16*GameScreen.getUnitScale()));
                int ycoord = (int) Math.ceil(getY()/(16*GameScreen.getUnitScale()));
                boolean walkableX = allTiles[(ycoord*15) + newXcoord];
                boolean walkableY = allTiles[(newYcoord*15) + xcoord];

                System.out.println("newX: " + newX + " newY: " + newY);
                System.out.println("targetX: " + targetX + " targetY: " + targetY);
                System.out.println("getX: " + getX() + " getY: " + getY());
                System.out.println("xcoord: " + xcoord + " ycoord: " + ycoord);
                System.out.println("newXcoord: " + newXcoord + " newYcoord: " + newYcoord);
                System.out.println("walkableX: " + walkableX + " walkableY: " + walkableY);


//
                // Update the enemy's position
                if (distance >= 16 * GameScreen.getUnitScale()) {
                    if(walkableX) {
                        setPosition(newX, getY());
                    } else {
                        setPosition(getX(), newY);
                    }

                }

                // Check if the enemy has reached the current target point
                //if (Math.abs(newX - targetX) < 1 && Math.abs(newY - targetY) < 1) {
                if (Math.abs(newX - targetX) < 1&& Math.abs(newY - targetY) < 1) {
//                    // Remove both x and y from the path
                    path.removeIndex(0);
                    path.removeIndex(0);
                }
//
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
            batch.draw(rightAnimation.getKeyFrame(stateTime, true), getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                    getHeight(), getScaleX(),
                    getScaleY(), getRotation());
        } else {
            batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(),
                    getScaleY(), getRotation());
        }
    }

    private void calculatePath(float playerX, float playerY) {
        // calculate the path using pathfinding
//        int startX = MathUtils.floor(getBounds().x / GameScreen.getUnitScale() * 16);
//        int startY = MathUtils.floor(getBounds().y / GameScreen.getUnitScale() * 16);
//        int targetX = MathUtils.floor(player.getPlayerX() / GameScreen.getUnitScale() * 16);
//        int targetY = MathUtils.floor(player.getPlayerY() / GameScreen.getUnitScale() * 16);


        int startX = MathUtils.floor(getX() / (16 * 5));
        int startY = MathUtils.floor(getY() / (16 * 5));
        int targetX = MathUtils.floor(Player.getPlayerX() / (16 * 5));
        int targetY = MathUtils.floor(Player.getPlayerY() / (16 * 5));

        System.out.println(" inside calculate path,  targetX: " + targetX + " targetY: " + targetY);

//        int targetX = MathUtils.floor(getX() / (16 * 5));
//        int targetY = MathUtils.floor(getY() / (16 * 5));
//        int startX = MathUtils.floor(Player.getPlayerX() / (16 * 5));
//        int startY = MathUtils.floor(Player.getPlayerY() / (16 * 5));

//        System.out.println("getBounds().x: " + getX() + " getBounds().y: " + getY());
//        System.out.println("startX: " + startX + " startY: " + startY + " targetX: " + targetX + " targetY: " + targetY);
//        System.out.println("startX: " + startX + " startY: " + startY + " targetX: " + targetX + " targetY: " + targetY);
//        System.out.println("playerX/ (16 * 5): " + Math.floor(Player.getPlayerX() / (16 * 5)) + " playerY/(16*5):" + Math.floor(Player.getPlayerY() / (16 * 5)));


        path = pathfinding.findPath(startX, startY, targetX, targetY);
        System.out.println("the path: " + path);
        System.out.println("startX: " + startX + " startY: " + startY + " targetX: " + targetX + " targetY: " + targetY);

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
