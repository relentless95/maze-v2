package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
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
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It's very similar to the rest of screen classes, except we have a file chooser, which additionally sets as default directory the
 * path to the game's repository.
 */
public class MenuScreen implements Screen {

    private final Stage stage;

    // file handle
    public static FileHandle selectedFile;
    // preferences a simple way to store small data to the application like settings, small game state saves and so on. works like a hashmap.
    public Preferences prefs;
    private Image backgroundTexture;

    private final MazeRunnerGame game;
    private Table table;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {

        this.game = game;
        var camera = new OrthographicCamera();

        camera.zoom = 1.5f; // Set camera zoom for a closer view
        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera

        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        backgroundTexture = new Image(game.getBackgroundTexture());
        backgroundTexture.setScale(2.5f);
        backgroundTexture.setPosition(0, 0, 0); // the last zero is the alignment
        stage.addActor(backgroundTexture);

        table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage
        // Add a label as a title
        table.add(new Label("ARTEMAZE", game.getSkin(), "title")).padBottom(80).row();
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

        TextButton goToGameButton = new TextButton("Start new game", game.getSkin());
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(); // Change to the game screen when button is pressed
            }
        });
        table.add(goToGameButton).width(300).row();

        // create a new button to load the map;
        TextButton chooseMapFile = new TextButton("Select a map", game.getSkin());
        table.add().padBottom(30).row(); // to add space between the two buttons
        table.add(chooseMapFile).width(300).row();

        table.add(quitButton).width(300);

        chooseMapFile.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        NativeFileChooserConfiguration conf = mapFileChooserConfiguration();
                        conf.title = "Select map file";
                        conf.directory = Gdx.files.absolute(System.getProperty("user.dir")+ "/maps"); // go to  maps
                        // directory at once

                        game.getFileChooser().chooseFile(conf, new NativeFileChooserCallback() {
                                    @Override
                                    public void onFileChosen(FileHandle file){
                                        selectedFile = file;
                                        if (file != null) {
                                            prefs.putString("lastMap", file.parent().file().getAbsolutePath());
                                        }
                                    }

                                    @Override
                                    public void onCancellation() {
                                        selectedFile = null;
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        selectedFile = null;
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
                });
        table.add().padBottom(60).row(); // to add space between the two buttons
        table.add().padBottom(60).row(); // to add space between the two buttons
        Label volume = new Label("Set volume:", game.getSkin(), "title");
        volume.setFontScale(0.5f);
        table.add(volume).padBottom(80).row();


        Slider volumeSlider = new Slider(0f, 1f, 0.1f, false, game.getSkin());
        table.add(volumeSlider).padBottom(30).row();
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

        TextButton hardMode = new TextButton("Hard mode", game.getSkin());
        table.add().padBottom(30).row(); // to add space between the two buttons
        table.add(hardMode).width(250).row();
        hardMode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setHardModeOn(true); // Change to the game screen when button is pressed
            }
        });

        //  implementing the file choosing
        prefs = Gdx.app.getPreferences("MazeRunnerGame");

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

    public NativeFileChooserConfiguration mapFileChooserConfiguration() {
        NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
        conf.directory = Gdx.files.absolute(prefs.getString("lastMap", Gdx.files.isExternalStorageAvailable() ?
                Gdx.files.getExternalStoragePath() : (Gdx.files.isLocalStorageAvailable() ?
                Gdx.files.getLocalStoragePath() : System.getProperty("user.home"))));

        conf.nameFilter = new FilenameFilter() {

            final String[] extensions = {".properties", ".tmx"};

            @Override
            public boolean accept(File dir, String name) {
                int i = name.lastIndexOf(".");
                if (i > 0 && i < name.length() - 1) {
                    String desiredExtension = name.substring(i + 1).toLowerCase(Locale.ENGLISH);
                    for (String extension : extensions) {
                        if (desiredExtension.equals(extension)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        conf.mimeFilter = "text/x-java-properties";

        return conf;
    }

    public static FileHandle getSelectedFile() {
        return selectedFile;
    }

}
