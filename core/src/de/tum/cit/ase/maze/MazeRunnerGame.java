package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * The game class may not be the most important class in our video game,
 * but is still fundamental as we load most of the assets and animations here.
 * Screen changing logic is also implemented here.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private GameOverScreen gameOverScreen;
    private VictoryScreen victoryScreen;
    private PausedScreen pauseScreen;


    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // must investigate what this is for
    private final CustomInputProcessor gameAcceptedInput;

    private Animation<TextureRegion> objectAnimation;
    private Animation<TextureRegion> trapAnimation;

    private Animation<TextureRegion> enemyAnimation;
    private Animation<TextureRegion> enemyRightAnimation;
    private Animation<TextureRegion> enemyLeftAnimation;
    private Animation<TextureRegion> enemyUpAnimation;

    private Animation<TextureRegion> chestAnimation;
    private Animation<TextureRegion> entryPointAnimation;
    private Animation<TextureRegion> exitAnimation;
    private Animation<TextureRegion> livesAnimation;
    private TextureRegion backgroundTexture;

    private TextureRegion bgGameOverTexture;

    private TextureRegion bgVictoryTexture;
    private TextureRegion bgPauseTexture;

    // file chooser
    private NativeFileChooser fileChooser;
    private Music backgroundMusic;
    private int highestScoreValue;
    private String highestScore;

    private Sound gameOverFx;
    private Sound victoryFx;
    private Sound damagedFx;
    private Sound ObjectCollectedFx;
    private Sound walkingFx;
    private Sound entryClosedFx;
    private Sound mazeExitOpenFx;
    private float globalVolume;
    private boolean hardModeOn;





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

        Texture menuBackgroundTexture = new Texture(Gdx.files.internal("backgroundImage.png"));
        this.backgroundTexture = new TextureRegion(menuBackgroundTexture);

        loadMusicAndSFX();

        goToMenu(); // Navigate to the menu screen

        this.loadObjectAnimation(0, 3, 4, 1, "things.png", 3, "object"); // to load the object animation
        this.loadObjectAnimation(6, 9, 4, 1, "things.png", 3, "trap"); // to load the object animation for spikes

        this.loadObjectAnimation(6, 9, 4, 1, "mobs.png", 3, "enemy");
        this.loadObjectAnimation(6, 9, 6, 1, "mobs.png", 3, "enemyRight");
        this.loadObjectAnimation(6, 9, 5, 1, "mobs.png", 3, "enemyLeft");
        this.loadObjectAnimation(6, 9, 7, 1, "mobs.png", 3, "enemyUp");

        this.loadSpecialAnimation(6, 8, 0, 4, "things.png", "chest");
        this.loadSpecialAnimation(0, 2, 0, 4, "things.png", "entryPoint");
        this.loadSpecialAnimation(3, 5, 0, 4, "things.png", "exit");


        Texture pauseBgTexture = new Texture(Gdx.files.internal("pausescreenbg.png"));
        this.bgPauseTexture = new TextureRegion(pauseBgTexture);

        Texture gameOverTexture = new Texture(Gdx.files.internal("gameoverscreen.png"));
        this.bgGameOverTexture = new TextureRegion(gameOverTexture);

        Texture victoryTexture = new Texture(Gdx.files.internal("victoryscreenbg.png"));
        this.bgVictoryTexture = new TextureRegion(victoryTexture);

        backgroundMusic.setLooping(true);

        backgroundMusic.setVolume(0.7f);

        backgroundMusic.play();

        loadHighestScore();

        hardModeOn = false;

    }

    private void loadMusicAndSFX(){
        globalVolume = 1f;
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("GoblinsDen.mp3"));
        gameOverFx = Gdx.audio.newSound(Gdx.files.internal("gameoversound.mp3"));
        victoryFx = Gdx.audio.newSound(Gdx.files.internal("victorysound.mp3"));
        damagedFx = Gdx.audio.newSound(Gdx.files.internal("damagedsound.mp3"));
        ObjectCollectedFx = Gdx.audio.newSound(Gdx.files.internal("collectedsoundeffect.mp3"));
        walkingFx = Gdx.audio.newSound(Gdx.files.internal("walkingsound.mp3"));
        entryClosedFx = Gdx.audio.newSound(Gdx.files.internal("entryclosedsound.mp3"));
        mazeExitOpenFx = Gdx.audio.newSound(Gdx.files.internal("exitopenedsound.mp3"));
    }

    /**
     * loading the animations.
     */

    private void loadObjectAnimation(int startCol, int endCol, int spriteRow, int spriteCol, String imageNameInQuotes, int frames, String type) {
        spriteCol = (spriteCol != 0) ? spriteCol : 1;
        spriteRow = (spriteRow != 0) ? spriteRow : 1;
        Texture thingsSheet = new Texture(Gdx.files.internal(imageNameInQuotes));
        int frameWidth = 16;
        int frameHeight = 16;

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


        if (type.equals("lives")) {
            livesAnimation = new Animation<>(0.199f, objectFrames);
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

    // started method to lead the highest score
    public void loadHighestScore() {
        Properties properties = new Properties();
        try {
            String scoreToLoad;
            if (LevelBuilder.getLevel() == null) {
                // Default to a specific level if LevelBuilder.getLevel() is null
                scoreToLoad = "level-1.properties";
            } else {
                scoreToLoad = LevelBuilder.getLevel();
            }
            FileInputStream input = new FileInputStream("highscores/" + scoreToLoad + "_highest_score.properties");
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        highestScoreValue = Integer.parseInt(properties.getProperty("highestScoreValue", "0"));
        highestScore = properties.getProperty("highestScore", "None");
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

    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (!backgroundMusic.isPlaying()){
            backgroundMusic.play();
        }
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
        if (pauseScreen != null) {
            pauseScreen.dispose(); // Dispose the menu screen if it exists
            pauseScreen = null;
        }
        if (victoryScreen != null) {
            victoryScreen.dispose(); // Dispose the menu screen if it exists
            victoryScreen = null;
        }
        if (gameOverScreen != null) {
            gameOverScreen.dispose(); // Dispose the menu screen if it exists
            gameOverScreen = null;
        }
    }

    public void pause() {
        if (getScreen() == gameScreen) {
            if (!gameScreen.isHasVictoryScreenBeenSet() && !gameScreen.isPlayerDead()) {
                pauseScreen = new PausedScreen(this);
                this.setScreen(pauseScreen); // Set the current screen to MenuScreen
            }
        }
    }

    public void goToGameOverScreen() {
        gameOverScreen = new GameOverScreen(this);
        this.setScreen(gameOverScreen); // Set the current screen to MenuScreen
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
        // When you want to use a custom input processor (e.g., for mouse input handling in other screens)
        gameScreen.setInputProcessor(gameAcceptedInput);
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    public void goToVictoryScreen(int elapsedTime) {
        victoryScreen = new VictoryScreen(this, elapsedTime, highestScoreValue, highestScore);
        this.setScreen(victoryScreen); // Set the current screen to GameScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the menu screen if it exists
            gameScreen = null;
        }
    }


    public void resumeGame() {
        gameScreen.setPaused(false);
        // When you want to use a custom input processor (e.g., for mouse input handling in other screens)
        //gameScreen.setInputProcessor(gameAcceptedInput);
        this.setScreen(gameScreen); // Set the current screen to GameScreen
        if (pauseScreen != null) {
            pauseScreen.dispose(); // Dispose the menu screen if it exists
            pauseScreen = null;
        }
    }

    // Getter/setter methods


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

    public Animation<TextureRegion> getLivesAnimation() {
        return livesAnimation;
    }


    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public NativeFileChooser getFileChooser() {
        return fileChooser;
    }


    public void setVolume(float volume) {
        backgroundMusic.setVolume(volume*0.7f);
        globalVolume = volume;
    }

    public float getVolume() {
        return globalVolume;
    }

    public TextureRegion getBackgroundTexture() {
        return backgroundTexture;
    }

    public TextureRegion getBgGameOverTexture() {
        return bgGameOverTexture;
    }

    public TextureRegion getBgVictoryTexture() {
        return bgVictoryTexture;
    }

    public TextureRegion getBgPauseTexture() {
        return bgPauseTexture;
    }

    public int getHighestScoreValue() {
        return highestScoreValue;
    }

    public void setHighestScoreValue(int highestScoreValue) {
        this.highestScoreValue = highestScoreValue;
    }

    public String getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(String highestScore) {
        this.highestScore = highestScore;
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }


    public boolean isHardModeOn() {
        return hardModeOn;
    }

    public void setHardModeOn(boolean hardModeOn){
        this.hardModeOn = hardModeOn;
    }


    public Sound getGameOverFx() {
        return gameOverFx;
    }

    public Sound getVictoryFx() {
        return victoryFx;
    }

    public Sound getDamagedFx() {
        return damagedFx;
    }

    public Sound getObjectCollectedFx() {
        return ObjectCollectedFx;
    }

    public Sound getWalkingFx() {
        return walkingFx;
    }

    public Sound getEntryClosedFx() {
        return entryClosedFx;
    }


    public Sound getMazeExitOpenFx() {
        return mazeExitOpenFx;
    }
}
