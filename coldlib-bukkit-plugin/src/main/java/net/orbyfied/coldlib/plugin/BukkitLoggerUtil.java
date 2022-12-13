package net.orbyfied.coldlib.plugin;

import net.orbyfied.j8.util.logging.EventLevel;

import java.util.logging.Level;

public class BukkitLoggerUtil {

    public static Level getLogLevel(EventLevel level) {
        return Level.parse(level.name());
    }

}
