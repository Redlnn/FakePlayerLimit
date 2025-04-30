package icu.harmo.fpl.mixin;

import carpet.commands.PlayerCommand;
import carpet.utils.Messenger;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import icu.harmo.fpl.FakePlayerLimitSettings;
import icu.harmo.fpl.config.FakePlayerLimitConfig;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@Mixin(PlayerCommand.class)
public abstract class PlayerCommandMixin {

    @Inject(method = "cantSpawn", at = @At("TAIL"), remap = false, cancellable = true)
    private static void cantSpawnMixin(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Boolean> cir, @Local String playerName) throws IOException {
        String sourcePlayer = Objects.requireNonNull(context.getSource().getPlayer()).getName().getString();

        if (playerName.length() > 16) {
            Messenger.m(context.getSource(), "rb Player name: " + playerName + " is too long");
            cir.setReturnValue(true);
            return;
        }

        FakePlayerLimitConfig config = FakePlayerLimitConfig.read();
        var playerFakePlayer = config.fakePlayerMaps.getOrDefault(sourcePlayer, new ArrayList<>());

        if (playerFakePlayer.size() >= FakePlayerLimitSettings.fakePlayerCanSpawnPerPlayer) {
            Messenger.m(context.getSource(), "rb You can only spawn " + FakePlayerLimitSettings.fakePlayerCanSpawnPerPlayer + " fake player(s).");
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "spawn", at = @At(value = "TAIL"), remap = false)
    private static void spawnMixin(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir, @Local String playerName) throws IOException {
        if (cir.getReturnValue() != 1) return;

        String sourcePlayer = Objects.requireNonNull(context.getSource().getPlayer()).getName().getString();

        FakePlayerLimitConfig config = FakePlayerLimitConfig.read();
        config.fakePlayerMaps.computeIfAbsent(sourcePlayer, k -> new ArrayList<>()).add(playerName);
        FakePlayerLimitConfig.save(config);
    }

    @Inject(method = "kill", at = @At(value = "TAIL"), remap = false)
    private static void killMixin(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir, @Local ServerPlayerEntity player) throws IOException {
        if (cir.getReturnValue() != 1) return;

        FakePlayerLimitConfig config = FakePlayerLimitConfig.read();
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
