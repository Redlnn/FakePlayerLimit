package top.redlnn.fpl;

import carpet.CarpetServer;
import carpet.api.settings.Rule;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FakePlayerLimitSettings {
    public static final Map<String, String> TRANSLATION_MAP;
    private static final String CATEGORIES = "harmoland";

    @Rule(categories = CATEGORIES)
    @RuleDescription("每个玩家可以生成的假人数量")
    public static int fakePlayerCanSpawnPerPlayer = 1;

    static {
        TRANSLATION_MAP = initializeTranslationMap();
    }

    private FakePlayerLimitSettings() {
    }

    public static void initialize() {
        CarpetServer.settingsManager.parseSettingsClass(FakePlayerLimitSettings.class);
    }

    private static Map<String, String> initializeTranslationMap() {
        Map<String, String> map = new ConcurrentHashMap<>();
        for (Field rule : FakePlayerLimitSettings.class.getFields()) {
            if (rule.isAnnotationPresent(Rule.class)) {
                RuleDescription desc = rule.getAnnotation(RuleDescription.class);
                if (desc == null) {
                    throw new AssertionError("Rule " + rule.getName() + " is not annotated with @RuleDescription.");
                }
                map.put("carpet.rule." + rule.getName() + ".desc", desc.value());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @Documented
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface RuleDescription {
        String value();
    }
}
