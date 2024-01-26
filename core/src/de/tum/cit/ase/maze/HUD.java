package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class HUD implements Screen {
        private Texture playerLife;
        private Texture key;
        private GameScreen screen;
        private MazeRunnerGame game;
        private static float heartSpacing = 8;
        private static float heartWidth = 96;
        private static float heartHeight = 96;
        private TextureRegion heartRegion;
        private TextureRegion lostHeartRegion;
        private Animation<TextureRegion> keyAnimation;
        private static int keyWidthAndHeight = 32;
        private static float scale = GameScreen.getUnitScale()*(0.5f);
        private Stage stage;
        public HUD(){
            playerLife = new Texture(Gdx.files.internal("objects.png"));
            key = new Texture(Gdx.files.internal("key.png"));
            heartRegion = new TextureRegion(playerLife, Player.getFrameWidth() * 4, 0,
                    Player.getFrameWidth(), 20);
            lostHeartRegion = new TextureRegion(playerLife, Player.getFrameWidth() * 8, 0,
                    Player.getFrameWidth(), 20);
        }

        public void draw(int numberOfLives, float sinusInput) {
            float startX = screen.getCamera().position.x;
            float posY = screen.getCamera().position.y + 225;
            for (int i = 0; i < 3; i++) {
                float posX = startX + i * (heartWidth + heartSpacing) + 280;
                TextureRegion currentHeartRegion = (i < numberOfLives) ? heartRegion : lostHeartRegion;
                game.getSpriteBatch().draw(currentHeartRegion, posX, posY, heartWidth, heartHeight);
            }
            keyAnimation = AnimationLoader.loadAnimation(key, 24, keyWidthAndHeight, keyWidthAndHeight, 0, 0, 0.1f);
            if (screen.getPlayer().hasKey()) {
                game.getSpriteBatch().draw(keyAnimation.getKeyFrame(sinusInput, true),
                        startX - (16 * 14 * scale), posY + 30, keyWidthAndHeight * scale, keyWidthAndHeight * scale);
            }
        }
        public void setScreen(GameScreen screen) {
            this.screen = screen;
        }

        public void setGame(MazeRunnerGame game) {
            this.game = game;
        }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public void dispose(){
        }
}
