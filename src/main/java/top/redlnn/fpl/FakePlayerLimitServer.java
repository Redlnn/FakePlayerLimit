package top.redlnn.fpl;

import carpet.CarpetExtension;
import carpet.CarpetServer;

import java.util.Map;

public class FakePlayerLimitServer implements CarpetExtension {
    private static final FakePlayerLimitServer INSTANCE = new FakePlayerLimitServer();

    private FakePlayerLimitServer() {
    }

    public static void init() {
        CarpetServer.manageExtension(INSTANCE);
    }

    @Override
    public String version() {
        return FakePlayerLimitMod.MOD_ID;
    }

    @Override
    public void onGameStarted() {
        FakePlayerLimitSettings.initialize();
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return FakePlayerLimitSettings.TRANSLATION_MAP;
    }
}
