package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * The GameScreen class is responsible for rendering the gameplay screen, it iterates through cycles and runs methods
 * all over again, unless we set isPaused as true.
 * It's our most important class, as we handle most of the game
 * dinamics and events s.a player dying or colliding with any object here.
 */
public class GameScreen implements Screen {
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private Stage stage;
    private final Player player;
    private long startTime;

    private float sinusInput = 0f;
    private TiledMapRenderer renderer;
    private Viewport viewport;
    private HUD hud;
    private static float unitScale = 5f;
    private final MapFileReader mapCreator;
    private boolean isPaused;
    private float savedCameraX;
    private float savedCameraY;
    private boolean hasVictoryScreenBeenSet;
    private final float initialX;
    private final float initialY;
    private long elapsedTime;
    private final float LERP_FACTOR;
    private boolean playerDead;
    boolean playSoundChest;
    boolean playEntryClosed;
    boolean playExitOpened;
    boolean playGameOverFx;




    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        // Create and configure the camera for the game view
        this.game = game;

        LERP_FACTOR = 0.1f;

        mapCreator = new MapFileReader(game);
        mapCreator.loadMap();

        // for scalling the game
        initialX = mapCreator.getEntranceX() * 5 * 16;
        initialY = mapCreator.getEntranceY() * 5 * 16;

        this.player = new Player(new Vector2(initialX, initialY), game);

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Initialize camera position
        initializeCameraPosition();

        viewport = new ScreenViewport(camera);

        viewport.apply(false);

        viewport.setWorldSize(mapCreator.getMaxX() * 16 * unitScale, mapCreator.getMaxY() * 32 * unitScale);

        stage = new Stage(viewport);

//        draw HUD
        hud = new HUD(game);

        hud.setGameScreen(this);

        // Create the renderer with the map and unit scale
        renderer = new OrthogonalTiledMapRenderer(mapCreator.getMap(), unitScale);

        // Set the view and projection matrix for the renderer
        renderer.setView(camera);

        isPaused = false;

        startTime = TimeUtils.millis();

        elapsedTime = 0;

        // rendering this here because it needs to be initalizedd before passing to the stage
        for (MovingEnemy oneEnemy : mapCreator.getMovingEnemies()) {
            stage.addActor(oneEnemy);

        }

        for (ExtraLives extralife : mapCreator.getExtraLives()) {
            stage.addActor(extralife);
        }

        playerDead = false;

        playSoundChest = false;

        playEntryClosed = false;

        playExitOpened = false;

        playGameOverFx = true;

    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        if (!isPaused) {
            hasPlayerDied();

            elapsedTime = TimeUtils.timeSinceMillis(startTime);

            sinusInput += delta;
            // Begin sprite batch and draw game elements
            game.getSpriteBatch().setProjectionMatrix(camera.combined);
            game.getSpriteBatch().begin();

            handleInput();

            updateCamera();

            renderer.setView(camera);

            // Check for escape key press to go back to the menu
            ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
            // Update the camera
            renderer.render();
            //draw walls

            renderGameElements(delta, sinusInput);
            // draw player

            player.update(delta, sinusInput);

            game.getSpriteBatch().end();

            hud.update(delta, player.getNumberOfLives());

            pauseOnEsc();

        }
    }

    private void lastCameraPosition() {
        camera.position.x = savedCameraX;
        camera.position.y = savedCameraY;
        updateCamera();
    }

    private void hasPlayerDied() {
        if (player.hasDied()) {
            isPaused = true;
            playerDead = true;
            game.getBackgroundMusic().stop();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    game.getGameOverFx().play(game.getVolume());
                    game.goToGameOverScreen();
                }
            }, 1.5f);
        }
    }

    private void handleInput() {
        if (player.getPlayingState() == GameState.NORMAL) {
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
        }
    }

    public boolean isHasVictoryScreenBeenSet() {
        return hasVictoryScreenBeenSet;
    }


    private void pauseOnEsc() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !hasVictoryScreenBeenSet) {
            game.pause();
        }
    }


    public void updateCamera() {
        if (!hasVictoryScreenBeenSet) {
            float areaSquareWidth = 6f * unitScale * 16f;
            float areaSquareHeight = 3.5f * unitScale * 16f;
            // Calculate the player's square position
            int playerSquareX = MathUtils.floor(player.getPlayerX() / areaSquareWidth);
            int playerSquareY = MathUtils.floor(player.getPlayerY() / areaSquareHeight);
            // Update camera position based on player's square position
            float targetX = playerSquareX * areaSquareWidth + areaSquareWidth / 2f;
            float targetY = playerSquareY * areaSquareHeight + areaSquareHeight / 2f;
            // Adjust the lerp factor for the desired smoothness
            camera.position.x = MathUtils.lerp(camera.position.x, targetX, LERP_FACTOR);
            camera.position.y = MathUtils.lerp(camera.position.y, targetY, LERP_FACTOR);
            // Set the camera's z-coordinate to 0 (assuming it's in 2D)
            camera.position.z = 0;
            // Update the camera
            camera.update();
        }
        if (player.getPlayerX() == mapCreator.getEntranceX() * 5 * 16 && player.getPlayerY() == mapCreator.getEntranceY() * 5 * 16
                && !hasVictoryScreenBeenSet && player.getPlayingState() != GameState.VICTORY) {
            initializeCameraPosition();
        }
    }

    private void initializeCameraPosition() {
        // Set camera position
        camera.position.set(initialX, initialY, 0);
        camera.update();
    }

    public void renderGameElements(float delta, float sinusInput) {
        TextureRegion wallTexture = null;

        for (Wall wall : mapCreator.getWalls()) {
            player.preventOverlap(wall);

            wallTexture = wall.getTextureRegion();

            // for collision detection

            float wallWidth = wall.getBounds().width; // Adjust yourScalingFactor
            float wallHeight = wall.getBounds().height; // Adjust yourScalingFactor
            game.getSpriteBatch().draw(wallTexture, wall.getBounds().x, wall.getBounds().y, wallWidth, wallHeight);
        }

        Wall invisibleWall = new Wall(wallTexture, (mapCreator.getEntranceX() - 1) * 16 * GameScreen.getUnitScale(),
                (mapCreator.getEntranceY()) * 16 * GameScreen.getUnitScale(), 16, 16);

        player.preventOverlap(invisibleWall);

        //draw enemies
        for (Enemy enemy : mapCreator.getEnemies()) {
            if (game.isHardModeOn()){
                player.preventOverlap(enemy);
            }
            if (player.overlaps(enemy)) {
                player.receiveDamage();
            }

            float enemyWidth = enemy.getBounds().width; // Adjust yourScalingFactor
            float enemyHeight = enemy.getBounds().height; // Adjust yourScalingFactor


            game.getSpriteBatch().draw(game.getEnemyAnimation().getKeyFrame(sinusInput, true), enemy.getBounds().x, enemy.getBounds().y, enemyWidth, enemyHeight);
        }

        for (MovingEnemy movingEnemy : mapCreator.getMovingEnemies()) {
            if (player.overlapsEnemy(movingEnemy)) {
                player.receiveDamage();
            }
            if (game.isHardModeOn()) {
                player.preventOverlapMovingEnemies(movingEnemy);
            }

        }

        // the traps
        for (Trap trap : mapCreator.getTraps()) {
            trap.update(delta);
            if (player.overlaps(trap) && trap.areSpikesFullyExtended()) {
                player.receiveDamage();

            }
            float trapWidth = trap.getBounds().width;
            float trapHeight = trap.getBounds().height;


            game.getSpriteBatch().draw(game.getTrapAnimation().getKeyFrame(sinusInput, true), trap.getBounds().x, trap.getBounds().y, trapWidth, trapHeight);
        }


        //draw chest
        for (Chest chest : mapCreator.getChests()) {

            player.preventOverlap(chest);

            // for collision detection


            float chestWidth = chest.getBounds().width; // Adjust yourScalingFactor
            float chestHeight = chest.getBounds().height; // Adjust yourScalingFactor

            // for the chest animation
            if (player.overlaps(chest) && !player.hasKey()) {
                player.setKey(true);
                chest.setChestOpen(true);
                game.getSpriteBatch().draw(game.getChestAnimation().getKeyFrame(sinusInput, false), chest.getBounds().x,
                        chest.getBounds().y, chestWidth, chestHeight);
            }
            if (!chest.isChestOpen()) {
                game.getSpriteBatch().draw(chest.getTextureRegion(), chest.getBounds().x, chest.getBounds().y, chestWidth,
                        chestHeight);
                playSoundChest = true;
            } else {
                float animationDuration = game.getChestAnimation().getAnimationDuration();
                TextureRegion lastFrame = game.getChestAnimation().getKeyFrame(animationDuration);
                game.getSpriteBatch().draw(lastFrame, chest.getBounds().x, chest.getBounds().y, chestWidth, chestHeight);
                if (playSoundChest){
                        game.getObjectCollectedFx().play(game.getVolume());
                        playSoundChest = false;
                }
            }

        }

        //render entryPoint
        EntryPoint entry = mapCreator.getEntryPoint();
        float entryWidth = entry.getBounds().width; // Adjust yourScalingFactor
        float entryHeight = entry.getBounds().height; // Adjust yourScalingFactor

        if (player.overlaps(entry) && !entry.isDoorClosed()) {
            game.getSpriteBatch().draw(game.getEntryPointAnimation().getKeyFrame(0, false),
                    entry.getBounds().x, entry.getBounds().y, entryWidth, entryHeight);
            setInputProcessor(null);
            player.setCutscene(GameState.CUTSCENE, 64f);
            playEntryClosed = true;
        } else {
            player.setCutscene(GameState.NORMAL, 0f);
            CustomInputProcessor gameAcceptedInput = new CustomInputProcessor(false, false, true, false,
                    false, false, false, false, false);
            setInputProcessor(gameAcceptedInput);
            float animationDuration = game.getEntryPointAnimation().getAnimationDuration();
            TextureRegion lastFrame = game.getEntryPointAnimation().getKeyFrame(animationDuration);
            game.getSpriteBatch().draw(lastFrame, entry.getBounds().x, entry.getBounds().y, entryWidth, entryHeight);
            if (playEntryClosed){
                game.getEntryClosedFx().play(game.getVolume());
                playEntryClosed = false;
            }
            entry.setDoorClosed(true);
            player.preventOverlap(entry);
        }

        for (Exit exit : mapCreator.getExits()) {


            if (!player.hasKey()) {
                player.preventOverlap(exit);
            }

            // for collision detection
            float exitWidth = exit.getBounds().width; // Adjust yourScalingFactor
            float exitHeight = exit.getBounds().height; // Adjust yourScalingFactor


            if (player.overlaps(exit) && player.hasKey()) {
                game.getBackgroundMusic().stop();
                exit.setExitOpen(true);
                game.getSpriteBatch().draw(game.getExitAnimation().getKeyFrame(sinusInput, false), exit.getBounds().x,
                        exit.getBounds().y, exitWidth, exitHeight);
                playExitOpened = true;
            }
            if (!exit.isExitOpen()) {
                game.getSpriteBatch().draw(exit.getTextureRegion(), exit.getBounds().x, exit.getBounds().y, exitWidth, exitHeight);
            } else {
                player.allowOverlap();
                player.setPlayingState(GameState.VICTORY);
                player.setCutscene(GameState.VICTORY, 256f);
                setInputProcessor(null);
                float animationDuration = game.getExitAnimation().getAnimationDuration();
                if (playExitOpened){
                    game.getMazeExitOpenFx().play(game.getVolume());
                    playExitOpened = false;
                }
                TextureRegion lastFrame = game.getExitAnimation().getKeyFrame(animationDuration);
                game.getSpriteBatch().draw(lastFrame, exit.getBounds().x, exit.getBounds().y, exitWidth, exitHeight);
                if (!hasVictoryScreenBeenSet) {
                    hasVictoryScreenBeenSet = true;
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            game.getVictoryFx().play(game.getVolume());
                            game.goToVictoryScreen(elapsedTime());
                        }
                    }, 2f);
                }
            }
        }


        for (ExtraLives extraLife : mapCreator.getExtraLives()) {
            if(player.hasKey()){
                extraLife.setVisible();

            }
            if(player.getNumberOfLives() != 3 && player.overlaps(extraLife) && !extraLife.getCollected()){
                    extraLife.collect();
                    game.getObjectCollectedFx().play(game.getVolume());
                    player.setNumberOfLives(player.getNumberOfLives() + 1);
            }
        }

        //for the moving enemies and extralives;
        stage.act(delta);
        stage.draw();
    }

    public void setInputProcessor(InputProcessor inputProcessor) {
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }


    public void setPaused(boolean paused) {
        isPaused = paused;
    }


    public static float getUnitScale() {
        return unitScale;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void pause() {
        isPaused = true;
        savedCameraX = camera.position.x;
        savedCameraY = camera.position.y;
    }

    @Override
    public void resume() {
        //Added this validation as the game screen itself is never disposed until gameOver or win, so if we close the
        // window and reopen it will move the camera regardless the game is paused, which causes visual artifacts
        if (!isPaused && !hasVictoryScreenBeenSet && player.getPlayingState() != GameState.VICTORY) {
            lastCameraPosition();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        hud.dispose();
    }

    public int elapsedTime() {
        return (int) (elapsedTime / 1000);
    }

    public boolean isPlayerDead() {
        return playerDead;
    }
}
