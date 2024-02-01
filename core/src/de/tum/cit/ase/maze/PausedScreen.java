package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The Pause screen is a class that implements screen.
 * Uses the same logic as all other screens, there's an instance
 * of GameScreen still existing as this screen is displayed.
 */
public class PausedScreen implements Screen {
    private final Stage stage;

    private Image backgroundImage;

    private final MazeRunnerGame game;

    public PausedScreen(MazeRunnerGame game) {

        this.game = game;
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view
        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        backgroundImage = new Image(game.getBgPauseTexture());
        backgroundImage.setFillParent(true);
        backgroundImage.setScale(2f);
        backgroundImage.setPosition(0, 0, 0);
        stage.addActor(backgroundImage);

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage
        // Add a label as a title
        //quit

        TextButton quitButton = new TextButton("Quit", game.getSkin());
        quitButton.addListener(
                (Event e) -> {
                    if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown)) {
                        return false;
                    }
                    Gdx.app.exit();
                    return false;
                });

        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("Go back to menu", game.getSkin());
        table.add(goToGameButton).width(300).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Change to the game screen when button is pressed
            }
        });
        TextButton Continue = new TextButton("Continue", game.getSkin());
        table.add(Continue).width(300).row();
        Continue.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.resumeGame(); // Change to the game screen when button is pressed
                }
            });
        // create a new button to load the map;

        table.add().padBottom(60).row(); // to add space between the two buttons
        table.add().padBottom(60).row(); // to add space between the two buttons
        Label volume = new Label("Set volume:", game.getSkin(), "title");
        volume.setFontScale(0.5f);
        table.add(volume).padBottom(80).row();


        Slider volumeSlider = new Slider(0f, 1f, 0.1f, false, game.getSkin());
        table.add(volumeSlider).padBottom(30).row(); // Adjust colspan as needed
        volumeSlider.setValue(game.getVolume());
        volumeSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        float newVolume = volumeSlider.getValue();
                        game.setVolume(newVolume);
                    }
                }
        );

        if (game.isHardModeOn()){
            TextButton hardModeOff = new TextButton("Turn hard mode off", game.getSkin());
            table.add(hardModeOff).width(400).row();
            hardModeOff.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setHardModeOn(false); // Change to the game screen when button is pressed
                    hardModeOff.remove();
                }
            });
        }

        table.center();
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
    public void pause() {
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
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

}
