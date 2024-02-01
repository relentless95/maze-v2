package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;


/**
 * The second most important class for our game,
 * as we have many useful attributes that practically let us
 * know all the actions and states of the player.
 * It's also the class that compares collisions.
 */
public class Player extends Actor {
    private final float speed;
    private static float playerX, playerY;
    private final static int frameWidth = 16;
    private final static int frameHeight = 32;
    private Direction facingDirection;
    private static int animationFrames = 4;
    private Animation<TextureRegion> currentAnimation;
    private Animation<TextureRegion> upAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;

    private Animation<TextureRegion> currentDamagedAnimation;
    private Animation<TextureRegion> upDamagedAnimation;
    private Animation<TextureRegion> downDamagedAnimation;
    private Animation<TextureRegion> leftDamagedAnimation;
    private Animation<TextureRegion> rightDamagedAnimation;
    private final MazeRunnerGame game;
    private int numberOfLives;

    private boolean key;
    private float damage;


    //    private Rectangle body;
    private Polygon boundaryPolygon;
    private Rectangle rectangle;

    private final float COOLDOWN_TIME = 4;
    private final float COOLDOWNSFX_TIME = 0.4f;
    private float cooldown;
    private float cooldownSfx;

    private boolean canMove;

    private float timeAlive;
    private float timeOfDamageTaken;

    private boolean damaged;

    private final Texture walkSheet;

    private GameState playingState;

    private float duration;

    private boolean died;

    public Player(Vector2 startPosition, MazeRunnerGame game) {
        super();
        speed = 10 * frameWidth; //Our speed will be the number of tiles we can move. In this case, 5 (32 is tile size).
        facingDirection = Direction.RIGHT;
        playerX = startPosition.x;
        playerY = startPosition.y;
        walkSheet = new Texture(Gdx.files.internal("character.png"));
        loadAnimations();
        currentAnimation = downAnimation;
        currentDamagedAnimation = downDamagedAnimation;

        setBounds(playerX, playerY, frameWidth, frameHeight); //Defining the bounds of the actor class to fit with the sprite representation.
        setBoundaryPolygon();

        this.setPosition(playerX, playerY);
        numberOfLives = 3;
        key = false;
        damage = 0.5f;

        cooldown = 0;
        cooldownSfx = COOLDOWNSFX_TIME;
        timeAlive = 0;
        timeOfDamageTaken = -1;
//
        canMove = true;
        damaged = false;
        playingState = GameState.NORMAL;

        this.game = game;
    }


    public void drawCurrentAnimation(float sinusInput, float delta) {
        /*The method we use to draw, additionally the if moving is
        included, so we save code and display the idle sprite directly
        if our player is not moving
         * */
        if (checkMoving() || playingState == GameState.CUTSCENE || playingState == GameState.VICTORY) {
            if (damaged) {
                game.getSpriteBatch().draw(currentDamagedAnimation.getKeyFrame(sinusInput, true), playerX, playerY, 64, 128);
            } else {
                game.getSpriteBatch().draw(currentAnimation.getKeyFrame(sinusInput, true), playerX, playerY, 64, 128);
            }
        } else {
            if (damaged) {
                game.getSpriteBatch().draw(currentDamagedAnimation.getKeyFrame(sinusInput, false), playerX, playerY, 64, 128);
            } else {
                game.getSpriteBatch().draw(currentAnimation.getKeyFrame(delta), playerX, playerY, 64, 128);
            }
        }
    }

    public boolean checkMoving() {
        /*This method checks the state of our player.
         * If it's moving we can display the animations accordingly and move, if not,
         * the player stays in the same place and only displays the static sprite*/
        return canMove && (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.RIGHT));
    }

    public void update(float delta, float sinusInput) { //All possible interactions and status changes of player.
        super.act(delta);
        if (checkMoving() || playingState == GameState.CUTSCENE || playingState == GameState.VICTORY) {
            move(delta);
            setCurrentAnimation();
            if (cooldownSfx <= 0){
                game.getWalkingFx().play(game.getVolume()*0.8f);
                cooldownSfx = COOLDOWNSFX_TIME;
            }
        }
        drawCurrentAnimation(sinusInput, delta);
        cooldownSfx -= delta;
        cooldown -= delta;
        timeAlive += delta;

    }

    public void move(float delta) {
        float movement = delta;
        if (playingState == GameState.CUTSCENE || playingState == GameState.VICTORY) {
            movement *= duration;
        } else {
            movement *= speed; //We use the rendering delta time value to get the value of displacement.
        }
        switch (facingDirection) {
            case UP -> this.setPosition(playerX, playerY += movement);
            case DOWN -> this.setPosition(playerX, playerY -= movement);
            case LEFT -> this.setPosition(playerX -= movement, playerY);
            case RIGHT -> this.setPosition(playerX += movement, playerY);
        }
    }

    public void receiveDamage() {
        //Change stage and display new Game over screen.
        if (cooldown <= 0) {
            if (numberOfLives == 1) {
                died = true;
            }
            // Move this line after the if block
            cooldown = COOLDOWN_TIME;
            timeOfDamageTaken = timeAlive;
            damaged = true;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // This code will be executed after the specified delay
                    damaged = false;
                }
            }, 0.18f); // 1 second delay
            // Subtract one life here
            game.getDamagedFx().play(game.getVolume());
            numberOfLives--;
        }
    }


    private void loadAnimations() {
        // libGDX internal Array instead of ArrayList because of performance
        // Create all the animations.
        downAnimation = AnimationLoader.loadAnimation(walkSheet, animationFrames, frameWidth, frameHeight, 0, 0, 0.1f, true);
        rightAnimation = AnimationLoader.loadAnimation(walkSheet, animationFrames, frameWidth, frameHeight, 0, 1, 0.1f, true);
        upAnimation = AnimationLoader.loadAnimation(walkSheet, animationFrames, frameWidth, frameHeight, 0, 2, 0.1f, true);
        leftAnimation = AnimationLoader.loadAnimation(walkSheet, animationFrames, frameWidth, frameHeight, 0, 3, 0.1f, true);
        downDamagedAnimation = AnimationLoader.loadAnimation(walkSheet, 3, frameWidth, frameHeight, 4, 0, 0.1f, false);
        rightDamagedAnimation = AnimationLoader.loadAnimation(walkSheet, 3, frameWidth, frameHeight, 4, 1, 0.1f, false);
        upDamagedAnimation = AnimationLoader.loadAnimation(walkSheet, 3, frameWidth, frameHeight, 4, 2, 0.1f, false);
        leftDamagedAnimation = AnimationLoader.loadAnimation(walkSheet, 3, frameWidth, frameHeight, 4, 3, 0.1f, false);
    }


    public boolean overlapsEnemy(MovingEnemy other) {
        if (boundaryPolygon != null) {
            Polygon poly1 = this.getBoundaryPolygon();
            Polygon poly2 = other.getBoundarySquaredPolygon();

            // initial test to improve performance
            if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                return false;
            }

            return Intersector.overlapConvexPolygons(poly1, poly2);
        } else {
            return false;
        }
    }


    public boolean overlaps(GameObject other) {
        if (boundaryPolygon != null) {
            Polygon poly1 = this.getBoundaryPolygon();
            Polygon poly2 = other.getBoundarySquaredPolygon();

            if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                return false;
            }

            return Intersector.overlapConvexPolygons(poly1, poly2);
        } else {
            return false;
        }
    }


    // To prevent overlap (basically make the game harder).
    public Vector2 preventOverlapMovingEnemies(MovingEnemy other) {
        if (boundaryPolygon != null) {
            Polygon poly1 = this.getBoundaryPolygon();
            Polygon poly2 = other.getBoundarySquaredPolygon();

            // Initial test to improve performance
            if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                return null;
            }

            Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

            boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

            if (!polygonOverlap) {
                return null;
            }

            setPlayerX(this.getX() + mtv.normal.x * mtv.depth);
            setPlayerY(this.getY() + mtv.normal.y * mtv.depth);

            return mtv.normal;
        } else {
            return null;
        }
    }

    // To prevent overlap
    public Vector2 preventOverlap(GameObject other) {
        if (boundaryPolygon != null) {
            Polygon poly1 = this.getBoundaryPolygon();
            Polygon poly2 = other.getBoundarySquaredPolygon();

            // Initial test to improve performance
            if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle())) {
                return null;
            }

            Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

            boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

            if (!polygonOverlap) {
                return null;
            }

            setPlayerX(this.getX() + mtv.normal.x * mtv.depth);
            setPlayerY(this.getY() + mtv.normal.y * mtv.depth);

            return mtv.normal;
        } else {
            return null;
        }
    }


    public void allowOverlap() {
        this.boundaryPolygon = null;
    }


    public void setFacingDirection(Direction direction) {
        this.facingDirection = direction;
    }

    public static int getFrameWidth() {
        return frameWidth;
    }

    public static float getPlayerX() {
        return playerX;
    }

    public void setPlayerX(float playerX) {
        this.playerX = playerX;
    }

    public static float getPlayerY() {
        return playerY;
    }

    public void setPlayerY(float playerY) {
        this.playerY = playerY;
    }


    public int getNumberOfLives() {
        return numberOfLives;
    }

    public void setNumberOfLives(int numberOfLives) {
        this.numberOfLives = numberOfLives;
    }

    public boolean hasDied() {
        return died;
    }

    // using polygons
    public void setBoundaryPolygon() {
        float w = 32;
        float h = 64;
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }


    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX() + 8, getY() + 8);
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    //----end of using polygons


    public void setCurrentAnimation() {
        /*We set the animation state to display
         * according to the direction the player is trying to move.
         * */
        switch (facingDirection) {
            case UP:
                currentAnimation = upAnimation;
                currentDamagedAnimation = upDamagedAnimation;
                break;
            case RIGHT:
                currentAnimation = rightAnimation;
                currentDamagedAnimation = rightDamagedAnimation;
                break;
            case LEFT:
                currentAnimation = leftAnimation;
                currentDamagedAnimation = leftDamagedAnimation;
                break;
            case DOWN:
                currentAnimation = downAnimation;
                currentDamagedAnimation = downDamagedAnimation;
                break;
        }
    }


    public boolean hasKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }


    public void setCutscene(GameState cutscene, float duration) {
        playingState = cutscene;
        this.duration = duration;
    }

    public GameState getPlayingState() {
        return playingState;
    }

    public void setPlayingState(GameState playingState) {
        this.playingState = playingState;
    }
}
