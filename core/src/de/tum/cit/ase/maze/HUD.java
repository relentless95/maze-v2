package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


/**
 * The HUD class is a screen which is projected on top of the current gameScreen class,
 * using the screen properties we can draw actors on its stage and update them to display
 * the key animation and the loss of lives.
 */
public class HUD implements Screen {
        private final Texture playerLife;
        private final Texture key;
        private final Texture lockedKey;
        private Image lockedKeyImg;
        private TextureRegion lockedKeyReg;

        private GameScreen gameScreen;
        private final MazeRunnerGame game;
        private final static float heartSpacing = 8;
        private final static float heartWidth = 96;
        private static final float heartHeight = 96;
        private static final int keyWidthAndHeight = 32;
        private TextureRegion heartRegion;
        private TextureRegion lostHeartRegion;
        private Animation<TextureRegion> keyAnimation;
        private Stage stage;
        private Image[] heartImages;
        private animatedActorStage keyAnimationActor;
        private Label chronometer;

        private Table table;
    public HUD(MazeRunnerGame game) {

        this.game = game;
        stage = new Stage(new ScreenViewport(), game.getSpriteBatch());

        table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title


        playerLife = new Texture(Gdx.files.internal("objects.png"));
        key = new Texture(Gdx.files.internal("key.png"));
        lockedKey = new Texture(Gdx.files.internal("lockedkeysprite.png"));

        lockedKeyReg = new TextureRegion(lockedKey);
        lockedKeyImg = new Image(lockedKeyReg);
        lockedKeyImg.setSize( keyWidthAndHeight*2f, keyWidthAndHeight*2.5f);

        heartRegion = new TextureRegion(playerLife, Player.getFrameWidth() * 4, 0,
                Player.getFrameWidth(), 20);
        lostHeartRegion = new TextureRegion(playerLife, Player.getFrameWidth() * 8, 0,
                Player.getFrameWidth(), 20);

        float startX = Gdx.graphics.getWidth();
        float posY = Gdx.graphics.getHeight() - 110;

        heartImages = new Image[4];

        for (int i = 0; i < 3; i++) {
            heartImages[i] = new Image(new TextureRegion(heartRegion));
            heartImages[i].setSize(heartWidth, heartHeight);
            float posX = startX + i * (heartWidth + heartSpacing) - 306;
            heartImages[i].setPosition(posX, posY);
            stage.addActor(heartImages[i]);
        }

        keyAnimation = AnimationLoader.loadAnimation(key, 24, keyWidthAndHeight, keyWidthAndHeight,
                0, 0, 0.1f, true);
        keyAnimationActor = new animatedActorStage(keyAnimation, 8, posY);
        keyAnimationActor.setSize(keyWidthAndHeight*3, keyWidthAndHeight*3);
        lockedKeyImg.setPosition(16, posY + 16);
    }



    public void update(float delta, int numberOfLives) {

        // Update Images for Hearts
        for (int i = 0; i < 3; i++) {
            heartImages[i].setDrawable((i < numberOfLives) ? new TextureRegionDrawable(new TextureRegion(heartRegion))
                    : new TextureRegionDrawable(new TextureRegion(lostHeartRegion)));
        }

        // Update AnimationDrawable for Key Animation
        if (gameScreen != null) {
            if(gameScreen.getPlayer().hasKey()) {
                lockedKeyImg.remove();
                stage.addActor(keyAnimationActor);
            }
            else
            {
                stage.addActor(lockedKeyImg);
            }
            chronometer = new Label(TimeUtils.formatTime(gameScreen.elapsedTime()), game.getSkin(), "title");
        }

        table.add(chronometer).row();
        table.top();
        stage.act(delta);
        stage.draw();
        //To reset the chronometer's state in each cycle
        table.clear();

    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    public void dispose(){
        stage.dispose();
    }
}
