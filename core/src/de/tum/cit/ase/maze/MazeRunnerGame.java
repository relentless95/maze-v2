package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;


/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private VictoryScreen victoryScreen;


    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    CustomInputProcessor gameAcceptedInput;

    private Animation<TextureRegion> objectAnimation;
    private Animation<TextureRegion> trapAnimation;

    private Animation<TextureRegion> enemyAnimation;
    private Animation<TextureRegion> enemyRightAnimation;
    private Animation<TextureRegion> enemyLeftAnimation;
    private Animation<TextureRegion> enemyUpAnimation;

    private Animation<TextureRegion> chestAnimation;
    private Animation<TextureRegion> entryPointAnimation;
    private Animation<TextureRegion> exitAnimation;

    // file chooser
    private final NativeFileChooser fileChooser;
    private Music backgroundMusic;

    private TextureRegion backgroundTexture;


    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        // initializing a property for the file chooser
        this.fileChooser = fileChooser;
        gameAcceptedInput = new CustomInputProcessor(false, false, true, false,
                false, false, false, false, false);

    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin

        Texture backgroundTexture = new Texture(Gdx.files.internal("clouds.png"));
        this.backgroundTexture = new TextureRegion(backgroundTexture);

        //        this.loadObjectAnimation(4,1,"things.png", 3); // to load the object animation
//        this.loadObjectAnimation(0,3,4,1,"things.png", 3); // to load the object animation
        this.loadObjectAnimation(0, 3, 4, 1, "things.png", 3, "object"); // to load the object animation
        this.loadObjectAnimation(6, 9, 4, 1, "things.png", 3, "trap"); // to load the object animation for spikes

        this.loadObjectAnimation(6, 9, 4, 1, "mobs.png", 3, "enemy");
        this.loadObjectAnimation(6, 9, 6, 1, "mobs.png", 3, "enemyRight");
        this.loadObjectAnimation(6, 9, 5, 1, "mobs.png", 3, "enemyLeft");
        this.loadObjectAnimation(6, 9, 7, 1, "mobs.png", 3, "enemyUp");
        this.loadSpecialAnimation(6, 8, 0, 4, "things.png", "chest");
        this.loadSpecialAnimation(0, 2, 0, 4, "things.png", "entryPoint");
        this.loadSpecialAnimation(3, 5, 0, 4, "things.png", "exit");

        // Play some background music
        // Background sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundMusic.setLooping(true);
//        backgroundMusic.play();
        goToMenu(); // Navigate to the menu screen
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    public void pause() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
    }

    public void goToGameOverScreen() {
        gameOverScreen = new GameOverScreen(this);
        this.setScreen(gameOverScreen); // Set the current screen to MenuScreen
        gameOverScreen.show();
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }


    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        gameScreen = new GameScreen(this);
        this.setScreen(gameScreen); // Set the current screen to GameScreen
        // When you want to use the stage's input processor
        gameScreen.setInputProcessorOnlyForStage();
        // When you want to use a custom input processor (e.g., for mouse input handling in other screens)
        gameScreen.setInputProcessor(gameAcceptedInput);
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    public void goToVictoryScreen() {
        victoryScreen = new VictoryScreen(this);
        this.setScreen(victoryScreen); // Set the current screen to GameScreen
        // When you want to use the stage's input processor
        victoryScreen.setInputProcessorOnlyForStage();
        // When you want to use a custom input processor (e.g., for mouse input handling in other screens)
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the menu screen if it exists
            gameScreen = null;
        }
    }

    public void resumeGame() {
        gameScreen.setPaused(false);
        gameScreen.setResuming(true);
        gameScreen.setInputProcessorOnlyForStage();
        gameScreen.setInputProcessor(gameAcceptedInput);
        this.setScreen(gameScreen); // Set the current screen to GameScreen
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }


    private void loadObjectAnimation(int startCol, int endCol, int spriteRow, int spriteCol, String imageNameInQuotes, int frames, String type) {
        spriteCol = (spriteCol != 0) ? spriteCol : 1;
        spriteRow = (spriteRow != 0) ? spriteRow : 1;
        Texture thingsSheet = new Texture(Gdx.files.internal(imageNameInQuotes));
        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = frames;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> objectFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = startCol; col < endCol; col++) {
            objectFrames.add(new TextureRegion(thingsSheet, col * frameWidth, spriteRow * frameHeight, frameWidth, frameHeight));
        }
        if (type.equals("object")) {
            objectAnimation = new Animation<>(0.1f, objectFrames);

        }

        if (type.equals("trap")) {
            trapAnimation = new Animation<>(2f, objectFrames);
        }

        if (type.equals("enemy")) {
            enemyAnimation = new Animation<>(0.199f, objectFrames);
        }

        if (type.equals("enemyRight")) {
            enemyRightAnimation = new Animation<>(0.199f, objectFrames);
        }

        if (type.equals("enemyLeft")) {
            enemyLeftAnimation = new Animation<>(0.199f, objectFrames);
        }

        if (type.equals("enemyUp")) {
            enemyUpAnimation = new Animation<>(0.199f, objectFrames);
        }

        objectFrames.clear();


    }

    private void loadSpecialAnimation(int startCol, int endCol, int startRow, int endRow,
                                      String imageNameInQuotes, String type) {
        int frameWidth = 16;
        int frameHeight = 16;

        Texture spriteSheet = new Texture(Gdx.files.internal(imageNameInQuotes));
        TextureRegion[][] regions = TextureRegion.split(spriteSheet, frameWidth, frameHeight);


        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> animationFrames = new Array<TextureRegion>();

        Array<TextureRegion> reversedAnimationFrames = new Array<TextureRegion>();


        // Add all frames to the animation
//        for (int col = 0; col < animationFrames; col++) {
        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {

                animationFrames.add(regions[row][col]);
            }
        }

        for (int i = animationFrames.size - 1; i >= 0; i--) {
            reversedAnimationFrames.add(animationFrames.get(i));
        }

//        System.out.println("animationFrames.size: " + animationFrames.size);
//        System.out.println("reversedAnimationFrames.size: " + reversedAnimationFrames.size);


        Animation<TextureRegion> anim = new Animation<TextureRegion>(2f, animationFrames);
        Animation<TextureRegion> animReversed = new Animation<>(1.5f, reversedAnimationFrames);


        if (type.equals("chest")) {
            chestAnimation = anim;
        }

        if (type.equals("entryPoint")) {
            entryPointAnimation = animReversed;

        }

        if (type.equals("exit")) {
            exitAnimation = anim;
        }

        animationFrames.clear();
        reversedAnimationFrames.clear();


    }


    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getChestAnimation() {
        return chestAnimation;
    }


    public Animation<TextureRegion> getEntryPointAnimation() {
        return entryPointAnimation;
    }

    public Animation<TextureRegion> getExitAnimation() {
        return exitAnimation;
    }

    public Animation<TextureRegion> getTrapAnimation() {
        return trapAnimation;
    }


    public Animation<TextureRegion> getEnemyAnimation() {
        return enemyAnimation;
    }



    public Animation<TextureRegion> getEnemyRightAnimation() {
        return enemyRightAnimation;
    }

    public Animation<TextureRegion> getEnemyLeftAnimation() {
        return enemyLeftAnimation;
    }

    public Animation<TextureRegion> getEnemyUpAnimation() {
        return enemyUpAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public NativeFileChooser getFileChooser() {
        return fileChooser;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void setVolume(float volume) {
        backgroundMusic.setVolume(volume);
    }

    public float getVolume() {
        return backgroundMusic.getVolume();
    }

    public TextureRegion getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(TextureRegion backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }
}
