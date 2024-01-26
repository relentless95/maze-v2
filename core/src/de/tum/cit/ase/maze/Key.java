package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Key {
    private Texture playerKey;
    private GameScreen screen;
    private MazeRunnerGame game;
    private static float heartSpacing = 8;
    private static float heartWidth = 96;
    private static float heartHeight = 96;
    private TextureRegion heartRegion;
    private TextureRegion lostHeartRegion;
    public Key(){
        playerKey = new Texture(Gdx.files.internal("objects.png"));
        heartRegion = new TextureRegion(playerKey, Player.getFrameWidth() * 4, 0,
                Player.getFrameWidth(), 20);
        lostHeartRegion = new TextureRegion(playerKey, Player.getFrameWidth() * 8, 0,
                Player.getFrameWidth(), 20);
    }

    /*public void draw(int numberOfLives) {
        int lostHearts = 3 - numberOfLives;
        float totalWidth = 3 * (heartWidth + heartSpacing);
        float startX = screen.getCamera().position.x;
        for (int i = 0; i < 3; i++) {
            float posX = startX + i * (heartWidth + heartSpacing) - 100;
            float posY = screen.getCamera().position.y + 200;
            TextureRegion currentHeartRegion = (i < numberOfLives) ? heartRegion : lostHeartRegion;
            game.getSpriteBatch().draw(currentHeartRegion, posX, posY, heartWidth, heartHeight);
        }
    }
    */

    public void setScreen(GameScreen screen) {
        this.screen = screen;
    }

    public void setGame(MazeRunnerGame game) {
        this.game = game;
    }
}
