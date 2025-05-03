package top.redlnn.fpl.mixin;

import carpet.patches.EntityPlayerMPFake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.redlnn.fpl.config.FakePlayerLimitConfig;

import java.util.ArrayList;

@Mixin(EntityPlayerMPFake.class)
public class EntityPlayerMPFakeMixin {

    @Inject(method = "kill(Lnet/minecraft/text/Text;)V", at = @At("TAIL"), remap = false)
    private void killMixin(CallbackInfo ci) {
        EntityPlayerMPFake player = (EntityPlayerMPFake) (Object) this;

        FakePlayerLimitConfig config = FakePlayerLimitConfig.safeRead();
        String playerNameToRemove = player.getName().getString();

        // 优化嵌套循环，直接查找并移除
        config.fakePlayerMaps.entrySet().removeIf(entry -> {
            ArrayList<String> playerList = entry.getValue();
            boolean removed = playerList.removeIf(name -> name.equalsIgnoreCase(playerNameToRemove));
            return removed && playerList.isEmpty(); // 如果列表为空，移除整个条目
        });

        FakePlayerLimitConfig.save(config);
    }
}
