package icu.harmo.fpl.config;

import icu.harmo.fpl.FakePlayerLimitMod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FakePlayerLimitConfig {
    public HashMap<String, ArrayList<String>> fakePlayerMaps = new HashMap<>();

    public static void save(FakePlayerLimitConfig config) throws IOException {
        ConfigManager.writeFile(FakePlayerLimitMod.CONFIG_FILE, config);
    }

    public static FakePlayerLimitConfig read() throws IOException {
        if (!FakePlayerLimitMod.CONFIG_FILE.exists()) {
            FakePlayerLimitConfig config = new FakePlayerLimitConfig();
            save(config);
            return config;
        }
        return ConfigManager.readFile(FakePlayerLimitMod.CONFIG_FILE, FakePlayerLimitConfig.class);
    }
}
