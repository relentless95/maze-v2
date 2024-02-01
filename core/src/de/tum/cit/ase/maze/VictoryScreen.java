package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The Victory screen is a class that implements screen.
 * Uses the same logic as all other screens, except there are
 * some values stored in game s.a the score and the elapsed time
 * of session to calculate a score for our player.
 */
public class VictoryScreen implements Screen {


    private String associatedSpeech;

    private final Stage stage;

    private String score;
    private int scoreValue;
    private int highestScoreValue;
    private String highestScore;

    private final int time;

    private final MazeRunnerGame game;

    private Image backgroundImage;

    public VictoryScreen(MazeRunnerGame game, int time, int highestScoreValue, String highestScore) {

        this.time = time;
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view
        this.game = game;

        associatedSpeech = "";
        score = " ";
        this.highestScore = highestScore;
        this.highestScoreValue = highestScoreValue;

        computeScore();

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        backgroundImage = new Image(game.getBgVictoryTexture());
        backgroundImage.setScale(2.2f);
        backgroundImage.setPosition(-500, -315);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("YOU WIN!", game.getSkin(), "title")).padBottom(80).row();
        table.add(new Label("Score: ", game.getSkin(), "title")).padBottom(80).row();
        Label scoreValue = new Label(score, game.getSkin(), "title");

        switch (score) {
            case "S":
                scoreValue.setFontScale(2f);
                scoreValue.setColor(255, 215, 0, 75);
                break;
            case "A":
                scoreValue.setFontScale(1.5f);
                scoreValue.setColor(0, 128, 0, 50);
                break;
            case "B":
                scoreValue.setFontScale(1.5f);
                scoreValue.setColor(255, 165, 0, 50);
                break;
            case "C":
                scoreValue.setFontScale(1f);
                scoreValue.setColor(255, 165, 0, 50);
                break;
            case "D":
                scoreValue.setFontScale(1f);
                scoreValue.setColor(255, 0, 0, 75);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + score);
        }

        table.add(scoreValue).padBottom(30);

        table.add().padBottom(80).row();

        // Create and add a button to go to the game screen
        TextButton goToMenu = new TextButton("Go back to menu", game.getSkin());
        goToMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });
        table.add(goToMenu).width(300).row();

        TextButton quitButton = new TextButton("Quit", game.getSkin());
        quitButton.addListener(
                (Event e) -> {
                    if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown)) {
                        return false;
                    }


                    Gdx.app.exit();
                    return false;
                });
        table.add(quitButton).width(300).row();

        table.add().padBottom(60).row();


        Label scoreText = new Label(associatedSpeech, game.getSkin(), "title");
        scoreText.setFontScale(0.4f);
        table.add(scoreText).padBottom(80).row();


        Label lblHighestScore = new Label("Highest score:", game.getSkin(), "title");
        lblHighestScore.setFontScale(0.5f);
        table.add(lblHighestScore).padBottom(80).row();


        Label highestScoreString = new Label(this.highestScore, game.getSkin(), "title");
        table.add(highestScoreString).padBottom(80).row();

        table.center();
    }

    public void computeScore() {
        if (time < 30) {
            score = "S";
            scoreValue = 100;
            associatedSpeech = "Excellent performance!";
        } else if (time < 60) {
            score = "A";
            scoreValue = 75;
            associatedSpeech = "Well done!";
        } else if (time < 120) {
            score = "B";
            scoreValue = 50;
            associatedSpeech = "Not bad at all!";
        } else if (time < 180) {
            score = "C";
            scoreValue = 25;
            associatedSpeech = "Keep practicing!";
        } else {
            score = "D";
            scoreValue = 10;
            associatedSpeech = "At least you made it out in one piece!";
        }

        if (scoreValue > highestScoreValue) {
            game.setHighestScoreValue(scoreValue);
            game.setHighestScore(score);
            //Called getter twice for the base case whenever theres no highest score
            this.highestScore = score;
            updateHigestScoreInFile();
            associatedSpeech += " you also set a NEW HIGH SCORE!";
        }
    }

    private void updateHigestScoreInFile() {

        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("highscores/"+ LevelBuilder.getLevel() +"_highest_score" +
                ".properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // update the property
        properties.setProperty("highestScoreValue", String.valueOf(game.getHighestScoreValue()));
        properties.setProperty("highestScore", game.getHighestScore());

        try (FileOutputStream output = new FileOutputStream("highscores/" +LevelBuilder.getLevel() + "_highest_score" +
                ".properties")) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(this.stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
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

}
