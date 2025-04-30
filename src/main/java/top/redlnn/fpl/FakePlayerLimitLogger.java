package top.redlnn.fpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static top.redlnn.fpl.FakePlayerLimitMod.MOD_NAME;

public class FakePlayerLimitLogger {
    private static final FakePlayerLimitLogger INSTANCE = new FakePlayerLimitLogger();

    private final String PREFIX = String.format("[%s] ", MOD_NAME);
    private final Logger logger;

    private FakePlayerLimitLogger() {
        this.logger = LoggerFactory.getLogger(MOD_NAME);
    }

    public static FakePlayerLimitLogger getInstance() {
        return INSTANCE;
    }

    public void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    public void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    public void error(String message, Object... args) {
        log(LogLevel.ERROR, message, args);
    }

    private void log(LogLevel level, String message, Object... args) {
        if (message == null || message.isBlank()) {
            return;
        }
        String formattedMessage = PREFIX + message;
        switch (level) {
            case INFO -> logger.info(formattedMessage, args);
            case WARN -> logger.warn(formattedMessage, args);
            case ERROR -> logger.error(formattedMessage, args);
        }
    }

    private enum LogLevel {INFO, WARN, ERROR}
}
