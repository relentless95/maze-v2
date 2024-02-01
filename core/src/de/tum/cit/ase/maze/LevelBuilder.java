package de.tum.cit.ase.maze;


/**
 * This class is responsible for selecting the tiles for the floors and walls based on the user selection
 */
public class LevelBuilder {
    private static String level;
    private static int[] floorPosition;
    private static int[] wallPosition;


    public static LevelBuilder buildLevel(String level) {
        LevelBuilder levelBuilder = new LevelBuilder();
        levelBuilder.setLevel(level);
        levelBuilder.setFloorPosition(levelBuilder.floorTilePosition());
        levelBuilder.setWallPosition(levelBuilder.wallTilePosition());
        return levelBuilder;
    }


    private int[] floorTilePosition() {
        int y, x;

        if (level.equals("level-1.properties")) {
            y = 8;
            x = 0;
        } else if (level.equals("level-2.properties")) {
            y = 1;
            x = 0;
        } else if (level.equals("level-3.properties")) {
            y = 1;
            x = 3;
        } else if (level.equals("level-4.properties")) {
            y = 3;
            x = 5;
        } else if (level.equals("level-5.properties")) {
            y = 9;
            x = 3;
        } else {// Default to level 1 configuration
            y = 8;
            x = 0;
        }

        return new int[]{y, x};
    }

    private int[] wallTilePosition() {
        int y, x;

        if (level.equals("level-1.properties")) {
            y = 0;
            x = 2;
        } else if (level.equals("level-2.properties")) {
            y = 1;
            x = 7;
        } else if (level.equals("level-3.properties")) {
            y = 4;
            x = 6;
        } else if (level.equals("level-4.properties")) {
            y = 0;
            x = 2;
        } else if (level.equals("level-5.properties")) {
            y = 0;
            x = 4;
        } else {// Default to level 1 configuration
            y = 0;
            x = 2;
        }

        return new int[]{y, x};
    }


    public static String getLevel() {
        return level;
    }

    public static int[] getFloorPosition() {
        return floorPosition;
    }

    public static int[] getWallPosition() {
        return wallPosition;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setFloorPosition(int[] floorPosition) {
        this.floorPosition = floorPosition;
    }

    public void setWallPosition(int[] wallPosition) {
        this.wallPosition = wallPosition;
    }

    @Override
    public String toString() {
        return "Level{" +
                "level=" + level +
                ", floorPosition=" + floorPosition +
                ", wallPosition=" + wallPosition +
                '}';
    }

}


