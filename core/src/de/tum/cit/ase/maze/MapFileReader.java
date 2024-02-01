package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A very complex class, receives the file of a map and using properties associated
 * to it, assigns coordinates and class types to every tile to be displayed in the game.
 */
public class MapFileReader {
    private int maxX;
    private int maxY;

    //    private int minX;
//    private int minY;
    private TiledMap map;
    private Texture tiles;
    private int entranceX;
    private int entranceY;

    //    private Map<Integer, GameObject> tileObjectMap;
    private GameObject gameObject;
    private ArrayList<Wall> walls;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Chest> chests;
    private ArrayList<Trap> traps;
    private ArrayList<Enemy> enemies;

    private ArrayList<ExtraLives> extraLives;
    private ArrayList<MovingEnemy> MovingEnemies;

    //    private ArrayList<EntryPoint> entryPoint;
    private EntryPoint entryPoint;
    private ArrayList<Exit> exits;

    private MazeRunnerGame game;
    private Pathfinding pathfinding;

    private static boolean[] allTiles;

//    private static LevelBuilder level;

    public MapFileReader(MazeRunnerGame game) {
        this.maxX = 0;
        this.maxY = 0;
        this.entranceX = 0;
        this.entranceY = 0;
//        this.tileObjectMap = null;
        this.walls = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.MovingEnemies = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.extraLives = new ArrayList<>();

        this.entryPoint = null;  //oscar added this
//        this.entryPoint  = new EntryPoint();
        this.exits = new ArrayList<>();

//        if (level == null) {
//            level = new LevelBuilder();
//        }

        this.game = game;


    }

    public static Properties readMapProperties() {

        Properties properties = new Properties();
        try {
            FileInputStream input;
            if (MenuScreen.getSelectedFile() == null) {
                File filePath = new File(System.getProperty("user.dir") + File.separator + "maps" + File.separator + "level-1.properties");
                input = new FileInputStream(filePath);
                String thePath = filePath.getAbsolutePath();
                String[] splitedPath = thePath.split("/");
                String theSelectedFile = splitedPath[splitedPath.length - 1].trim();
                LevelBuilder.buildLevel(theSelectedFile);




//                level.setLevel(theSelectedFile);


            } else {
                String[] splitedPath = MenuScreen.getSelectedFile().toString().split("/");
                String theSelectedFile = splitedPath[splitedPath.length - 1].trim();
//                level.setLevel(theSelectedFile);
//                level = new LevelBuilder(theSelectedFile);
                LevelBuilder.buildLevel(theSelectedFile);

                input = new FileInputStream(MenuScreen.getSelectedFile().toString());
            }

            properties.load(input);
            input.close(); // Always close the FileInputStream

        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    public void loadMap() {
        GameObject gameObject1 = gameObject;
        Wall wall;
        Obstacle obstacle;
        Trap trap;
        ExtraLives extralife;
        Enemy enemy;
        MovingEnemy movingEnemy;
        Chest chest;
        Exit exit;


        // calling the method to read the property file from the MapFileReader class
        Properties properties = readMapProperties();
        for (int x = 0; x < 150; x++) {
            for (int y = 0; y < 150; y++) {
                String xy = x + "," + y;
                String property = properties.getProperty(xy);
                if (property != null) {
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);

                }
            }
        }
        {
            // to load the tiles
            tiles = new Texture(Gdx.files.internal("basictiles.png")); // load the image

            // to load things
            Texture things = new Texture(Gdx.files.internal("things.png"));

            // to load objects
            Texture objects = new Texture(Gdx.files.internal("objects.png"));

            // to load mobs
            Texture mobs = new Texture(Gdx.files.internal("mobs.png"));

            // split the things
            TextureRegion[][] splitThings = TextureRegion.split(things, 16, 16);
            // to select the obstacle Image
            TextureRegion obstacleImage = splitThings[4][0];

            // to select the chest image
            TextureRegion chestImage = splitThings[0][6];
            TextureRegion chestOpenImage = splitThings[4][8];

            // to select the trap image
            TextureRegion trapImage = splitThings[4][6];

            //to select entry image
//            TextureRegion entryPointImage = splitThings[0][0];

            TextureRegion entryPointImage = splitThings[3][2];

            //toSelect exit Image
            TextureRegion exitImage = splitThings[0][3];

            // spit mobs
            TextureRegion[][] splitMobs = TextureRegion.split(mobs, 16, 16);

            //to select the enemy image;
            TextureRegion mobImage = splitMobs[4][6];

            // split the object image
            TextureRegion[][] splitObjects = TextureRegion.split(objects, 16, 16);
            // to select the life Image
            TextureRegion livesImage = splitObjects[3][0];


            // to create the 2d array containing our sprite images
            /* textureRegion Constructs a region with the same texture as the specified region and sets the coordinates relative to the specified region.
             the ".split" Helper function creates a 2d array containing tiles out of the given Texture starting from the top left corner going to the right
             and ending in the bottom right corner.
             */
            TextureRegion[][] splitTiles = TextureRegion.split(tiles, 16, 16);
            map = new TiledMap(); // creates a new instance of a tiled map class
            MapLayers layers = map.getLayers(); //
            for (int l = 0; l < 1; l++) { // we don't need three layers
                // + 1 is used to account for the fact that the tile indices starts from 0;
                TiledMapTileLayer layer = new TiledMapTileLayer(maxX + 1, maxY + 1, 16, 16);
                // adding properties to the layer
                layer.getProperties().put("width", maxX + 1);
                layer.getProperties().put("height", maxY + 1);
                layer.getProperties().put("tileWidth", 16);
                layer.getProperties().put("tileHeight", 16);
                Map<Integer, TextureRegion> m = new HashMap<Integer, TextureRegion>();
//                m.put(0, splitTiles[0][2]); // Wall
                m.put(0, splitTiles[LevelBuilder.getWallPosition()[0]][LevelBuilder.getWallPosition()[1]]); // Wall
                m.put(1, splitTiles[6][0]); // Entry point
                m.put(2, splitTiles[6][3]); // Exit
                m.put(3, splitTiles[1][1]); // Trap
                m.put(4, splitTiles[7][4]); // Enemy
                m.put(5, splitTiles[4][4]); // Key
//                m.put(6, splitTiles[2][6]); // black
//                m.put(6, splitTiles[8][0]); // grass
                m.put(6, splitTiles[LevelBuilder.getFloorPosition()[0]][LevelBuilder.getFloorPosition()[1]]); // grass
                m.put(7, splitTiles[2][6]); // lives
                // the size of the map is the maximum number of tiles in the properties file * the tilewidth;

                // creating a 1d array of walkable tiles
                boolean[] walkableTiles = new boolean[(maxX + 1) * (maxY + 1)];


                // creating an array of walkable tile
                for (int i = 0; i < walkableTiles.length; i++) {
                    walkableTiles[i] = true;
                }

                for (int x = 0; x <= maxX + 1; x++) {
                    for (int y = 0; y <= maxY + 1; y++) {
                        String xy = x + "," + y;
                        String property = properties.getProperty(xy);
                        if (property == null) {
                            property = "6";
                        }
                        int prop = Integer.parseInt(property);
                        // values 0 to 5 represent non walkable tiles;
                        if (prop >= 0 && prop <= 5) {
                            walkableTiles[y * (maxX + 1) + x] = false;
                            allTiles = walkableTiles;
                        }

                        if (prop == 1) {
                            entranceX = x;
                            entranceY = y;
                        }
                        TextureRegion textureRegion = m.get(prop);

                        float width = 16 * GameScreen.getUnitScale(); // controls the width and height of the image being displayed
                        float height = 16 * GameScreen.getUnitScale();

                        int p;
                        int q;

                        switch (prop) {
                            case 0:
                                wall = new Wall(textureRegion, x * 16 * GameScreen.getUnitScale(), y * 16 * GameScreen.getUnitScale(), width, height);
                                walls.add(wall);
                                break;
                            case 1:
                                p = x;
                                q = y;
                                Animation<TextureRegion> entrypointAnimation = game.getEntryPointAnimation();
                                entryPoint = new EntryPoint(entryPointImage, p * 16 * GameScreen.getUnitScale(), q * 16 * GameScreen.getUnitScale(), width, height, entrypointAnimation);
                                break;
                            case 2:
                                p = x;
                                q = y;
                                Animation<TextureRegion> exitAnimation = game.getExitAnimation();
                                exit = new Exit(exitImage, p * 16 * GameScreen.getUnitScale(), q * 16 * GameScreen.getUnitScale(), width, height, exitAnimation);
                                exits.add(exit);
                                break;
                            case 3:
                                p = x;
                                q = y;
                                Animation<TextureRegion> trapAnimation = game.getTrapAnimation();
                                trap = new Trap(trapImage, p * 16 * GameScreen.getUnitScale(), q * 16 * GameScreen.getUnitScale(), width, height, trapAnimation);
                                traps.add(trap);
                                break;
                            case 4:
                                pathfinding = new Pathfinding(maxX + 1, maxY + 1, walkableTiles);

                                p = x;
                                q = y;
                                Animation<TextureRegion> enemyAnimation = game.getEnemyAnimation();
                                Animation<TextureRegion> enemyRightAnimation = game.getEnemyRightAnimation();
                                Animation<TextureRegion> enemyLeftAnimation = game.getEnemyLeftAnimation();
                                Animation<TextureRegion> enemyUpAnimation = game.getEnemyUpAnimation();

                                //Randomly decide if it's a moving enemy or not
                                boolean isMovingEnemy = MathUtils.randomBoolean();
                                if (isMovingEnemy) {
                                    movingEnemy = new MovingEnemy(p * 16 * GameScreen.getUnitScale(),
                                            q * 16 * GameScreen.getUnitScale(), 32, 46, 5f, mobImage,
                                            enemyRightAnimation, enemyLeftAnimation, enemyUpAnimation, enemyAnimation,
                                            pathfinding);
                                    MovingEnemies.add(movingEnemy);

                                } else {
                                    enemy = new Enemy(mobImage, p * 16 * GameScreen.getUnitScale(), q * 16
                                            * GameScreen.getUnitScale(), width, height, enemyAnimation);
                                    enemies.add(enemy);

                                }


//                                wall = new Wall(mobImage, x * 16 * 5, y * 16 * 5, width, height);




                                break;
                            case 5:
                                p = x;
                                q = y;
                                Animation<TextureRegion> chestAnimation = game.getChestAnimation();
                                chest = new Chest(chestImage, p * 16 * GameScreen.getUnitScale(), q * 16 * GameScreen.getUnitScale(), width, height, chestAnimation);
                                chests.add(chest);
                                break;

                            case 6:
                                break;
                            case 7:
                                p = x;
                                q = y;

                                Animation<TextureRegion> livesAnimation = game.getLivesAnimation();
                                extralife = new ExtraLives(livesImage, p * 16 * GameScreen.getUnitScale(),
                                        q * 16 * GameScreen.getUnitScale(), width, height , livesAnimation);
//                                enemyAnimation = game.getEnemyAnimation();

//                                extralife = new ExtraLives(mobImage, p * 16 * GameScreen.getUnitScale(),
//                                        q * 16 * GameScreen.getUnitScale(), width, height, livesAnimation);
//                                enemies.add(enemy);


//                                int randomNumber = (int) (Math.random() * 2) + 1;
//
//                                if (extraLives.size() == randomNumber) {
//                                    break;
//                                }
                                extraLives.add(extralife);

                                break;

                            // not needed
                            default:
//                                gameObject = new GameObject(textureRegion, x, y, bounds);
                        }
//                        GameObject wall = new GameObject(textureRegion, bounds);

                        if (prop == 0) {
                            prop = 6;

                        } else if (prop == 1) {
                            prop = 6;
                        } else if (prop == 2) {
                            prop = 6;
                        } else if (prop == 3) {
                            prop = 6;
                        } else if (prop == 4) {
                            prop = 6;
                        } else if (prop == 5) {
                            prop = 6;
                        } else if (prop == 7) {
                            prop = 6;
                        }

                        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();

                        //                        cell.setTile(new StaticTiledMapTile(wall.getTextureRegion()));
                        cell.setTile(new StaticTiledMapTile(m.get(prop)));
                        layer.setCell(x, y, cell);
                        fixBleeding(textureRegion);
                    }

                }
                layers.add(layer);
                fixBleeding(splitTiles);

            }
        }
    }

    public static void fixBleeding(TextureRegion[][] region) {
        for (TextureRegion[] array : region) {
            for (TextureRegion texture : array) {
                fixBleeding(texture);
            }
        }
    }

    public static void fixBleeding(TextureRegion region) {
        float fix = 0.01f;
        float x = region.getRegionX();
        float y = region.getRegionY();
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float invTexWidth = 1f / region.getTexture().getWidth();
        float invTexHeight = 1f / region.getTexture().getHeight();
        region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight); // Trims
    }


    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getEntranceX() {
        return entranceX;
    }

    public int getEntranceY() {
        return entranceY;
    }

    public TiledMap getMap() {
        return map;
    }


    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public ArrayList<Chest> getChests() {
        return chests;
    }


    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public ArrayList<Exit> getExits() {
        return exits;
    }

    public ArrayList<Trap> getTraps() {
        return traps;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<ExtraLives> getExtraLives() {
        return extraLives;
    }

    public ArrayList<MovingEnemy> getMovingEnemies() {
        return MovingEnemies;
    }

    public static boolean[] getAllTiles() {
        return allTiles;
    }
}
