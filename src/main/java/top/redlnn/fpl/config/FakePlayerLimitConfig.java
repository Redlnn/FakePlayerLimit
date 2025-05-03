package top.redlnn.fpl.config;

import top.redlnn.fpl.FakePlayerLimitLogger;
import top.redlnn.fpl.FakePlayerLimitMod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FakePlayerLimitConfig {
    private static final FakePlayerLimitLogger LOGGER = FakePlayerLimitLogger.getInstance();
    public HashMap<String, ArrayList<String>> fakePlayerMaps = new HashMap<>();

    public static void save(FakePlayerLimitConfig config) {
        try {
            ConfigManager.writeFile(FakePlayerLimitMod.CONFIG_FILE, config);
        } catch (IOException e) {
            LOGGER.error("保存配置文件失败", e);
        }
    }

    public static FakePlayerLimitConfig read() throws IOException {
        if (!FakePlayerLimitMod.CONFIG_FILE.exists()) {
            FakePlayerLimitConfig config = new FakePlayerLimitConfig();
            save(config);
            return config;
        }
        return ConfigManager.readFile(FakePlayerLimitMod.CONFIG_FILE, FakePlayerLimitConfig.class);
    }

    public static FakePlayerLimitConfig safeRead() {
        try {
            return read();
        } catch (IOException e) {
            LOGGER.error("读取配置文件失败，使用默认配置文件覆盖", e);
            FakePlayerLimitConfig config = new FakePlayerLimitConfig();
            save(config);
            return config;
        }
    }
}
