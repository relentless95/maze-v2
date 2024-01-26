package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import javax.swing.plaf.TextUI;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;

    // file handle
    public static FileHandle selectedFile;
    // preferences a simple way to store small data to the application like settings, small game state saves and so on. works like a hashmap.
    Preferences prefs;
    public MazeRunnerGame game;

    private ShapeRenderer shapeRenderer;
    private TextureRegion backgroundTexture;


    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        this.game = game;
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
// for the file chooser
        backgroundTexture = game.getBackgroundTexture();


        Image backgroundImage = new Image(game.getBackgroundTexture());
//        System.out.println("x and y of the image: " + backgroundImage.getImageX() + ", " + backgroundImage.getImageY() + backgroundImage.getOriginX() +
//                backgroundImage.getOriginY());
//        System.out.println("background image width and height: " + backgroundImage.getWidth() + backgroundImage.getHeight());

//        System.out.println("stage x and y: " + stage.getX);
//        backgroundImage.setFillParent(true);
//        backgroundImage.setScaling(Scaling.fit);
//        backgroundImage.setScaling(Scaling.fill);
//        backgroundImage.setSize(stage.getWidth(), stage.getHeight());
//        backgroundImage.setPosition(0,0);
//        backgroundImage.setSize(1.0f* backgroundImage.getImageHeight(), 1.0f * backgroundImage.getWidth());
//        backgroundImage.setSize(2000, 2000);
//        System.out.println("x and y of the image: " + backgroundImage.getImageX() + ", " + backgroundImage.getImageY() + backgroundImage.getOriginX() +
//                backgroundImage.getOriginY());
//        stage.addActor(backgroundImage);


        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage

        shapeRenderer = new ShapeRenderer();

        System.out.println(stage.getHeight() + " " + stage.getWidth());

        // Add a label as a title
        table.add(new Label("Artemaze", game.getSkin(), "title")).padBottom(80).row();


        //quit
        TextButton quitButton = new TextButton("Quit", game.getSkin());
//        quitButton.setPosition(500, 150);
//        uiStage.addActor(quitButton);

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
        table.add(goToGameButton).width(300).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(); // Change to the game screen when button is pressed
            }
        });
        TextButton Continue = new TextButton("Continue", game.getSkin());
        if (game.getGameScreen() != null) {
            table.add(Continue).width(300).row();
            Continue.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.resumeGame(); // Change to the game screen when button is pressed
                }
            });
        }
        // create a new button to load the map;
        TextButton chooseMapFile = new TextButton("Choose Map file", game.getSkin());
        table.add().padBottom(30).row(); // to add space between the two buttons
        table.add(chooseMapFile).padBottom(30).width(300).row();

        table.add(quitButton).padBottom(30).width(300).row();

        table.columnDefaults(2).padRight(20);

        Label volumeLabel = new Label("Volume: ", game.getSkin(), "title");
        volumeLabel.setFontScale(0.5f);
        table.add(volumeLabel).padBottom(10).row();



        chooseMapFile.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        NativeFileChooserConfiguration conf = mapFileChooserConfiguration();
                        conf.title = "Select map file";

                        game.getFileChooser().chooseFile(conf, new NativeFileChooserCallback() {
                                    @Override
                                    public void onFileChosen(FileHandle file) throws NullPointerException {
                                        selectedFile = file;
                                        if (file == null) {
                                            throw new NullPointerException();
                                        } else {
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
        Slider volumeSlider = new Slider(0f, 1f, 0.1f, false, game.getSkin());
        volumeSlider.setValue(0.5f);
        table.add(volumeSlider).row();
        volumeSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        float newVolume = volumeSlider.getValue();
                        game.setVolume(newVolume);
                    }
                }
        );

        //  implementing the file choosing
        prefs = Gdx.app.getPreferences("MazeRunnerGame");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        game.getSpriteBatch().setProjectionMatrix(stage.getCamera().combined);
        // Draw stage borders
//        drawStageBorders();
        drawBackground();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
//        updateBackgroundImageScaling();
    }

//    private void updateBackgroundImageScaling() {
//        Image backgroundImage = findBackgroundImage();
//        if (backgroundImage != null) {
//            backgroundImage.setScaling(Scaling.fill);
////            TextureRegionDrawable drawable = (TextureRegionDrawable) backgroundImage.getDrawable();
////            float imageWidth = drawable.getMinWidth();
////            float imageHeight = drawable.getMinHeight();
//
//
//            backgroundImage.setSize(stage.getWidth(), stage.getHeight());
//            System.out.println(" stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight() : " + stage.getViewport().getWorldWidth() + stage.getViewport().getWorldHeight());
////            if(stageAspectRatio > imageAspectRatio)
//
//
//        }
//    }

//    private Image findBackgroundImage() {
//        for (Actor actor : stage.getActors()) {
//            if (actor instanceof Image) {
//                Image image = (Image) actor;
//                if (image.getDrawable() instanceof TextureRegionDrawable) {
//                    TextureRegionDrawable drawable = (TextureRegionDrawable) image.getDrawable();
//                    if (drawable.getRegion() == game.getBackgroundTexture()) {
//                        System.out.println("yes!!!!!!!!");
//                        return image;
//                    }
//                }
//            }
//
//        }
//        System.out.println("Nooooooo!!!!!");
//        return null;
//    }

    private void drawStageBorders() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.RED);

        float stageWidth = stage.getViewport().getWorldWidth();
        float stageHeight = stage.getViewport().getWorldHeight();

        shapeRenderer.rect(0, 0, stageWidth, stageHeight);

        shapeRenderer.end();
    }

    private void drawBackground() {
        game.getSpriteBatch().begin();
        game.getSpriteBatch().draw(backgroundTexture, -500, -250, 3000, 3000);
        game.getSpriteBatch().end();
    }


    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
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
