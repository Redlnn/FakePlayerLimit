package top.redlnn.fpl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import top.redlnn.fpl.config.FakePlayerLimitConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FakePlayerLimitMod implements ModInitializer {
    public static final String MOD_ID = "fake-player-limit";
    public static final String MOD_NAME = "Fake Player Limit";
    public static final FakePlayerLimitLogger LOGGER = FakePlayerLimitLogger.getInstance();

    public static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), MOD_ID + ".json");

    @Override
    public void onInitialize() {
        String version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
        FakePlayerLimitConfig config;

        try {
            config = FakePlayerLimitConfig.read();
            LOGGER.info("v{} loaded!", version);
        } catch (IOException e) {
            LOGGER.error("配置文件读取失败，将不可用！", e);
            return;
        }

        ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
            config.fakePlayerMaps = new HashMap<>();
            FakePlayerLimitConfig.save(config);
            LOGGER.info("数据保存完成");
        }));
        FakePlayerLimitServer.init();
    }
}
