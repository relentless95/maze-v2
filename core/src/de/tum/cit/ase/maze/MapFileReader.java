package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MapFileReader {
    private int maxX;
    private int maxY;
    private TiledMap map;
    private Texture tiles;
    private int entranceX;
    private int entranceY;

    private Map<Integer, GameObject> tileObjectMap;
    private GameObject gameObject;
    private ArrayList<Wall> walls;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Chest> chests;
    private ArrayList<Trap> traps;
    private ArrayList<Enemy> enemies;
    private ArrayList<MovingEnemy> MovingEnemies;

    //    private ArrayList<EntryPoint> entryPoint;
    private EntryPoint entryPoint;
    private ArrayList<Exit> exits;

    private MazeRunnerGame game;
    private Pathfinding pathfinding;

    public MapFileReader(MazeRunnerGame game) {
        this.maxX = 0;
        this.maxY = 0;
        this.entranceX = 0;
        this.entranceY = 0;
        this.tileObjectMap = null;
        this.walls = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.traps = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.MovingEnemies = new ArrayList<>();
        this.chests = new ArrayList<>();
//        this.entryPoint  = new EntryPoint();
        this.exits = new ArrayList<>();

        this.game = game;
    }

    public static Properties readMapProperties() {

        Properties properties = new Properties();
        try {
            FileInputStream input;
            if (MenuScreen.getSelectedFile() == null) {
                File filePath = new File(System.getProperty("user.dir") + File.separator + "maps" + File.separator + "level-1.properties");
                input = new FileInputStream(filePath);
                System.out.println(System.getProperty("user.dir"));
            } else {
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
        Enemy enemy;
        MovingEnemy movingEnemy;
        Chest chest;
//      EntryPoint entryPoint;
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
        System.out.println("maxX : " + maxX + "maxY: " + maxY);

        {
            // to load the tiles
            tiles = new Texture(Gdx.files.internal("basictiles.png")); // load the image

            //tiles.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            // to load things
            Texture things = new Texture(Gdx.files.internal("things.png"));

            // to load mobs
            Texture mobs = new Texture(Gdx.files.internal("mobs.png"));
            System.out.println("mobs: " + mobs);

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


            // to create the 2d array containing our sprite images
            /** textureRegion Constructs a region with the same texture as the specified region and sets the coordinates relative to the specified region.
             the ".split" Helper function creates a 2d array containing tiles out of the given Texture starting from the top left corner going to the right
             and ending in the bottom right corner.
             **/
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
                m.put(0, splitTiles[0][2]); // Wall
                m.put(1, splitTiles[6][0]); // Entry point
                m.put(2, splitTiles[6][3]); // Exit
                m.put(3, splitTiles[1][1]); // Trap
                m.put(4, splitTiles[7][4]); // Enemy
                m.put(5, splitTiles[4][4]); // Key
                m.put(6, splitTiles[2][6]); // black
                System.out.println("maxX : " + maxX + "maxY: " + maxY);
                // the size of the map is the maximum number of tiles in the properties file * the tilewidth;

                // creating a 2d array of walkable tiles;
//                boolean[][] walkableTiles = new boolean[maxX + 1][maxY + 1];
                // creating a 1d array of walkable tiles
                boolean[] walkableTiles = new boolean[(maxX + 1) * (maxY + 1)];

                // creating a 1d array of walkable tiles;

//                // initializing them all to false;
//                for (int x = 0; x <= maxX; x++) {
//                    for (int y = 0; y <= maxY; y++) {
//                        walkableTiles[x][y] = false;
//                    }
//                }

                // creating an array of walkable tile
                for (int i = 0; i < walkableTiles.length; i++) {
                    walkableTiles[i] = true;
                }


                // creating the pathfinding


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
                        }

                        if (prop == 1) {
                            System.out.println("entrance:" + prop);
                            entranceX = x;
                            entranceY = y;
                            System.out.println("entranceX: " + entranceX + "entranceY: " + entranceY);
                        }
                        TextureRegion textureRegion = m.get(prop);
//                        Rectangle bounds = new Rectangle(x * 16, y * 16, 16, 16);


                        // for collision detection
//                        GameObject gameObject;
                        float width = 16 * 5; // controls the width and height of the image being displayed
                        float height = 16 * 5;

                        int p;
                        int q;

                        switch (prop) {
                            case 0:
//                                wall = new Wall(textureRegion, x, y, bounds);

                                wall = new Wall(textureRegion, x * 16 * 5, y * 16 * 5, width, height);
                                walls.add(wall);
                                break;
                            case 1:
                                p = x;
                                q = y;
                                Animation<TextureRegion> entrypointAnimation = game.getEntryPointAnimation();
                                entryPoint = new EntryPoint(entryPointImage, p * 16 * 5, q * 16 * 5, width, height, entrypointAnimation);
//                                entryPoint;
//                                che.add(chest);
                                System.out.println("the length of the entryPoint is: " + entryPoint);
                                break;
                            case 2:
                                p = x;
                                q = y;
                                Animation<TextureRegion> exitAnimation = game.getExitAnimation();
                                exit = new Exit(exitImage, p * 16 * 5, q * 16 * 5, width, height, exitAnimation);
                                exits.add(exit);
                                System.out.println("the length of the exits is: " + exits.size());
                                break;
                            case 3:
                                p = x;
                                q = y;
                                Animation<TextureRegion> trapAnimation = game.getTrapAnimation();
                                trap = new Trap(trapImage, p * 16 * 5, q * 16 * 5, width, height, trapAnimation);
                                traps.add(trap);
                                System.out.println("the length of the traps is: " + traps.size());
                                break;
                            case 4:
                                pathfinding = new Pathfinding(maxX + 1, maxY + 1, walkableTiles);

                                p = x;
                                q = y;
//                                Animation<TextureRegion> obstacleAnimation = game.getObjectAnimation();
//                                System.out.println("count of object animations : " + obstacleAnimation);
//                                TextureRegion obstacleSprite = new TextureRegion(new Texture(Gdx.files.internal("obstacle_sprite.png")));
//                                obstacle = new Obstacle(obstacleSprite, x * 16 * 5, y * 16 * 5, width, height, obstacleAnimation);

//                                obstacle = new Obstacle(obstacleImage, p * 16 * 5, q * 16 * 5, width, height, obstacleAnimation);
//                                obstacles.add(obstacle);
//                                System.out.println("the length of the obstacles is: " + obstacles.size());

                                Animation<TextureRegion> enemyAnimation = game.getEnemyAnimation();
                                Animation<TextureRegion> enemyRightAnimation = game.getEnemyRightAnimation();
                                Animation<TextureRegion> enemyLeftAnimation = game.getEnemyLeftAnimation();
                                Animation<TextureRegion> enemyUpAnimation = game.getEnemyUpAnimation();


                                enemy = new Enemy(mobImage, p * 16 * 5, q * 16 * 5, width, height, enemyAnimation, pathfinding);
//                                enemy = new Enemy(mobImage, 1*16*5, 1*16*5, width, height, enemyAnimation,
//                                        pathfinding);
                                movingEnemy = new MovingEnemy(p + 1 * 16 * 5, q + 1 * 16 * 5, 5f, mobImage,
                                        enemyRightAnimation, enemyLeftAnimation, enemyUpAnimation, enemyAnimation,
                                        pathfinding);


//                                wall = new Wall(mobImage, x * 16 * 5, y * 16 * 5, width, height);
                                if (enemies.size() >= 1) {
                                    break;
                                }
                                if(MovingEnemies.size() >= 1){
                                    break;
                                }
//                                enemies.add(enemy);
                                MovingEnemies.add(movingEnemy);

//                                System.out.println("the length of the enemies is: " + enemies.size());


                                break;
                            case 5:
                                p = x;
                                q = y;
                                Animation<TextureRegion> chestAnimation = game.getChestAnimation();
                                chest = new Chest(chestImage, p * 16 * 5, q * 16 * 5, width, height, chestAnimation);
                                chests.add(chest);
                                System.out.println("the length of the chest is: " + chests.size());
                                break;
//                            case 6:
//                                break;

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

                System.out.println("tillelayer.getWidth: " + layer.getWidth());
                System.out.println("tillelayer.getHeight: " + layer.getHeight());
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


    // some of the setters and getters are not used here....remove them maybe

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getEntranceX() {
        return entranceX;
    }

    public void setEntranceX(int entranceX) {
        this.entranceX = entranceX;
    }

    public int getEntranceY() {
        return entranceY;
    }

    public void setEntranceY(int entranceY) {
        this.entranceY = entranceY;
    }

    public TiledMap getMap() {
        return map;
    }


    public void setMap(TiledMap map) {
        this.map = map;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }


    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public void setWalls(ArrayList<Wall> walls) {
        this.walls = walls;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(ArrayList<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public ArrayList<Chest> getChests() {
        return chests;
    }

    public void setChests(ArrayList<Chest> chests) {
        this.chests = chests;
    }


    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    public ArrayList<Exit> getExits() {
        return exits;
    }

    public void setExits(ArrayList<Exit> exits) {
        this.exits = exits;
    }

    public ArrayList<Trap> getTraps() {
        return traps;
    }

    public void setTraps(ArrayList<Trap> traps) {
        this.traps = traps;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public ArrayList<MovingEnemy> getMovingEnemies() {
        return MovingEnemies;
    }
}
