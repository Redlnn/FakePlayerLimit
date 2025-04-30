package top.redlnn.fpl;

import top.redlnn.fpl.config.FakePlayerLimitConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

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

        try {
            FakePlayerLimitConfig config = FakePlayerLimitConfig.read();
            LOGGER.info("v{} loaded!", version);

            ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
                try {
                    config.fakePlayerMaps = new HashMap<>();
                    FakePlayerLimitConfig.save(config);
                    LOGGER.info("数据保存完成");
                } catch (IOException e) {
                    LOGGER.error("数据保存失败", e);
                }
            }));
        } catch (IOException e) {
            LOGGER.error("配置文件读取失败，将不可用！", e);
            return;
        }
        FakePlayerLimitServer.init();
    }
}
