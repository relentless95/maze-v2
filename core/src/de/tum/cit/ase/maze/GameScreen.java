package de.tum.cit.ase.maze;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Camera;


/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private Stage stage;
    private Player player;
    private float sinusInput = 0f;
    private TiledMapRenderer renderer;
    private Viewport viewport;
    private HUD hud;
    private static float unitScale = 5f;
    private MapFileReader mapCreator;
    private boolean isPaused;
    private float savedCameraX;
    private float savedCameraY;
    private ShapeRenderer shapeRenderer;
    private boolean resuming;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        // Create and configure the camera for the game view
        this.game = game;

        mapCreator = new MapFileReader(game);
        mapCreator.loadMap();

        this.player = new Player(new Vector2(mapCreator.getEntranceX() * 5 * 16, mapCreator.getEntranceY() * 5 * 16));

        this.player.setGame(game);

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Initialize camera position
        initializeCameraPosition();

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        viewport = new ScreenViewport(camera);//
        viewport.apply(true);
        viewport.setWorldSize(mapCreator.getMaxX() * 16 * 5, mapCreator.getMaxY() * 16 * 5);

        stage = new Stage(viewport);

        for (MovingEnemy oneEnemy : mapCreator.getMovingEnemies()) {
            stage.addActor(oneEnemy);

        }


        hud = new HUD();
        hud.setGame(game);
        hud.setScreen(this);

        // Create the renderer with the map and unit scale
        renderer = new OrthogonalTiledMapRenderer(mapCreator.getMap(), unitScale);

        // Set the view and projection matrix for the renderer
        renderer.setView(camera);

        shapeRenderer = new ShapeRenderer();

        isPaused = false;
    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        if (!isPaused) {
            sinusInput += delta;
            // Begin sprite batch and draw game elements
            game.getSpriteBatch().setProjectionMatrix(camera.combined);
            game.getSpriteBatch().begin();


            handleInput();

            updateCamera();

            renderer.setView(camera);

            // Check for escape key press to go back to the menu
            ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
            camera.update(); // Update the camera
            renderer.render();
            //draw walls


            for (Wall wall : mapCreator.getWalls()) {
                player.preventOverlap(wall);

                // for collision detection
                if (player.overlaps(wall)) {
//                    System.out.println("called");
//                    System.out.println("overlapping");

                } else {

                }

                float wallWidth = wall.getBounds().width; // Adjust yourScalingFactor
                float wallHeight = wall.getBounds().height; // Adjust yourScalingFactor
                game.getSpriteBatch().draw(wall.getTextureRegion(), wall.getBounds().x, wall.getBounds().y, wallWidth, wallHeight);
            }


            //draw enemies
            for (Enemy enemy : mapCreator.getEnemies()) {

//                enemy.update(delta);
                player.preventOverlap(enemy);


                float enemyWidth = enemy.getBounds().width; // Adjust yourScalingFactor
                float enemyHeight = enemy.getBounds().height; // Adjust yourScalingFactor


                game.getSpriteBatch().draw(game.getEnemyAnimation().getKeyFrame(sinusInput, true), enemy.getBounds().x, enemy.getBounds().y, enemyWidth, enemyHeight);
            }

            // the traps
            for (Trap trap : mapCreator.getTraps()) {
                trap.update(delta);
                if (player.overlaps(trap) && trap.areSpikesFullyExtended()) {
//                    System.out.println("player has collided");
//                    System.out.println(trap.canDamagePlayer() + " cooldown is: " + trap.getCooldown());
                    player.receiveDamage();

                    /*if (trap.canDamagePlayer()) {
                        //            if (player.overlaps(trap)) {
                        //                System.out.println("player.overlaps(trap) && trap.areSpikesFullyExtended(): "
                        //                        + "true");
                        //                System.out.println("player overlaps with the trap: : " + player.overlaps(trap));
                        System.out.println("player has taken damage:  " + trap.canDamagePlayer());
                        //                System.out.println("player lives: " + player.getNumLives());
                        player.setNumLives(player.getNumLives() - 0.5f);
                        float remainingLives = player.getNumLives() - 0.5f;
                        System.out.println("Number of remaining lives: " + remainingLives);
                    }
                     */
                }
                float trapWidth = trap.getBounds().width;
                float trapHeight = trap.getBounds().height;


                float animationFrames = game.getTrapAnimation().getKeyFrames().length;
                float animationDuration = game.getTrapAnimation().getAnimationDuration();
                float frameDuration = animationDuration / animationFrames;

//            int currentFrameIndex = (int) ((sinusInput % animationDuration) / frameDuration);

                game.getSpriteBatch().draw(game.getTrapAnimation().getKeyFrame(sinusInput, true), trap.getBounds().x, trap.getBounds().y, trapWidth, trapHeight);
            }


            //draw chest
            for (Chest chest : mapCreator.getChests()) {


                player.preventOverlap(chest);


                // for collision detection


                float chestWidth = chest.getBounds().width; // Adjust yourScalingFactor
                float chestHeight = chest.getBounds().height; // Adjust yourScalingFactor


                if (player.overlaps(chest)) {
                    System.out.println("called");
                    System.out.println("overlapping");

                    player.setKey(true);
                    chest.setChestOpen(true);
                    game.getSpriteBatch().draw(game.getChestAnimation().getKeyFrame(sinusInput, false), chest.getBounds().x, chest.getBounds().y, chestWidth, chestHeight);
                }

                if (!chest.isChestOpen()) {
                    game.getSpriteBatch().draw(chest.getTextureRegion(), chest.getBounds().x, chest.getBounds().y, chestWidth, chestHeight);
                } else {
                    float animationDuration = game.getChestAnimation().getAnimationDuration();
                    TextureRegion lastFrame = game.getChestAnimation().getKeyFrame(animationDuration);
                    game.getSpriteBatch().draw(lastFrame, chest.getBounds().x, chest.getBounds().y, chestWidth, chestHeight);
                }

            }

            //draw entryPoint

            {
                EntryPoint entry = mapCreator.getEntryPoint();
                float entryWidth = entry.getBounds().width; // Adjust yourScalingFactor
                float entryHeight = entry.getBounds().height; // Adjust yourScalingFactor

                if (!entry.isDoorClosed()) {
                    if (!player.overlaps(entry)) {

                        entry.setDoorClosed(true);

                    } else {
                        game.getSpriteBatch().draw(game.getEntryPointAnimation().getKeyFrame(sinusInput, false),
                                entry.getBounds().x, entry.getBounds().y, entryWidth, entryHeight);
                    }
                } else {
                    float animationDuration = game.getEntryPointAnimation().getAnimationDuration();
                    TextureRegion lastFrame = game.getEntryPointAnimation().getKeyFrame(animationDuration);
                    game.getSpriteBatch().draw(lastFrame, entry.getBounds().x, entry.getBounds().y, entryWidth, entryHeight);
                }


                if (entry.isDoorClosed()) {
                    player.preventOverlap(entry);
                }


            }

            // draw the exit
            for (Exit exit : mapCreator.getExits()) {
//            System.out.println("does player have the key: " + player.hasKey());

                if (!player.hasKey()) {
                    player.preventOverlap(exit);
                }


                // for collision detection


                float exitWidth = exit.getBounds().width; // Adjust yourScalingFactor
                float exitHeight = exit.getBounds().height; // Adjust yourScalingFactor


                if (player.overlaps(exit) && player.hasKey()) {
                    System.out.println("called");
                    System.out.println("overlapping");


                    exit.setExitOpen(true);
                    game.getSpriteBatch().draw(game.getExitAnimation().getKeyFrame(sinusInput, false), exit.getBounds().x,
                            exit.getBounds().y, exitWidth, exitHeight);
                }

                if (!exit.isExitOpen()) {
                    game.getSpriteBatch().draw(exit.getTextureRegion(), exit.getBounds().x, exit.getBounds().y, exitWidth, exitHeight);
                } else {
                    float animationDuration = game.getExitAnimation().getAnimationDuration();
                    TextureRegion lastFrame = game.getExitAnimation().getKeyFrame(animationDuration);
                    game.getSpriteBatch().draw(lastFrame, exit.getBounds().x, exit.getBounds().y, exitWidth, exitHeight);
                    System.out.println("Exit position is: " + exit.getBounds().x + ", " + exit.getBounds().y + " for bounds, and " + exit.getX() + ", " + exit.getY() + " for actor coords");
                    System.out.println(player.getPlayerX() + ", " + player.getPlayerY());
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            // This code will be executed after the specified delay
                            game.goToVictoryScreen();
                        }
                    }, 1.0f); // Adjust the delay time (1.0f is an example; use your desired delay in seconds)

                }

            }
            // draw hud
            hud.draw(player.getNumberOfLives(), sinusInput);

            // draw player
            player.update(delta, sinusInput);
//            System.out.println("player position: " + "x : " + player.getX() + ", y :" + player.getY());
//            System.out.println("world height: " + viewport.getWorldHeight() + ", world width :" + viewport.getWorldWidth());
//        player.setColor(0,0,1,0);


            game.getSpriteBatch().end();
            stage.act(delta);

            stage.draw();

            game.getSpriteBatch().begin();
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            shapeRenderer.setColor(Color.RED);
//        System.out.println("player values in shape renderer: " + player.getPlayerX() + " " + player.getPlayerY() + " " + player.getRectangle().width + " " + player.getRectangle().height);
//        shapeRenderer.rect(player.getPlayerX(), player.getPlayerY(), player.getRectangle().width , player.getRectangle().height);
            shapeRenderer.rect(player.getPlayerX() + 16, player.getPlayerY() + 16, 32, 64);
//        shapeRenderer.rect(wall.getRectangle().x, wall.getRectangle().y, wall.getRectangle().width, wall.getRectangle().height);


            for (Wall wall : mapCreator.getWalls()) {
//            System.out.println("player rectangle: " +  player.getRectangle());
//            System.out.println("wall rectangle: " +    wall.getRectangle());
//            ----to delete---
//            wall.setBoundaryRectangle();
//            -----end of to delete----


                float wallWidth = wall.getBounds().width; // Adjust yourScalingFactor
                float wallHeight = wall.getBounds().height; // Adjust yourScalingFactor
//            System.out.println("wall height and width from bounds: " + wallHeight +  wallWidth);
//            System.out.println("wall.getbounds: " + wall.getBounds()) ;

                shapeRenderer.rect(wall.getBounds().x, wall.getBounds().y, wallWidth, wallHeight);


            }
            shapeRenderer.end();
            game.getSpriteBatch().end();


            handleAdditionalInput();


        }
    }

    private void lastCameraPosition() {
        camera.position.set(savedCameraX, savedCameraY, 0);
        camera.update();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.setFacingDirection(Direction.UP);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.setFacingDirection(Direction.DOWN);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.setFacingDirection(Direction.LEFT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.setFacingDirection(Direction.RIGHT);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            player.receiveDamage();
        }
    }

    private void handleAdditionalInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = true;
            savedCameraX = camera.position.x;
            savedCameraY = camera.position.y;
            game.pause();
        }
    }

    public void setInputProcessorOnlyForStage() {
        Gdx.input.setInputProcessor(stage);
    }

    public void setInputProcessor(InputProcessor inputProcessor) {
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
        viewport.update(width, height, true);
    }


    // Additional methods and logic can be added as needed for the game screen
    public void updateCamera() {
        // Assuming playerX and playerY represent the player's position in the area squares
        // Assuming playerX and playerY represent the player's position in the area squares
        float areaSquareWidth = 6f * unitScale * 16f;
        float areaSquareHeight = 3.5f * unitScale * 16f;
        // Calculate the player's square position
        int playerSquareX = MathUtils.floor(player.getPlayerX() / areaSquareWidth);
        int playerSquareY = MathUtils.floor(player.getPlayerY() / areaSquareHeight);
        // Update camera position based on player's square position
        float targetX = playerSquareX * areaSquareWidth + areaSquareWidth / 2f;
        float targetY = playerSquareY * areaSquareHeight + areaSquareHeight / 2f;
        // Use linear interpolation (lerp) to smoothly move the camera towards the target position
        float lerpFactor = 0.1f;  // Adjust the lerp factor for the desired smoothness
        camera.position.x = (float) Math.round(MathUtils.lerp(camera.position.x, targetX, lerpFactor));
        camera.position.y = (float) Math.round(MathUtils.lerp(camera.position.y, targetY, lerpFactor));
        // Set the camera's z-coordinate to 0 (assuming it's in 2D)
        camera.position.z = 0;
        // Update the camera
        camera.update();
        if (player.getPlayerX() == mapCreator.getEntranceX() * 5 * 16 && player.getPlayerY() == mapCreator.getEntranceY() * 5 * 16) {
            initializeCameraPosition();
        }
        if (resuming == true) {
            resuming = false;
            lastCameraPosition();
        }
    }

    private void initializeCameraPosition() {
        float initialX = mapCreator.getEntranceX() * 5 * 16;
        float initialY = mapCreator.getEntranceY() * 5 * 16;

        // Set camera position
        camera.position.set(initialX, initialY, 0);
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public float getSinusInput() {
        return sinusInput;
    }


    public static float getUnitScale() {
        return unitScale;
    }

    public Player getPlayer() {
        return player;
    }


    public void setResuming(boolean resuming) {
        this.resuming = resuming;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {

    }
}
