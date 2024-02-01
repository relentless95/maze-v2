package de.tum.cit.ase.maze;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom class to translate seconds into mm:ss format
 * in the GameScreen.
 */
public class TimeUtils {
    public static String formatTime(int totalSeconds) {
        LocalTime time = LocalTime.ofSecondOfDay(totalSeconds);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
        return time.format(formatter);
    }

}
