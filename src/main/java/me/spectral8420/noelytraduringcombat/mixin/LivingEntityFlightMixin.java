package me.spectral8420.noelytraduringcombat.mixin;

import me.spectral8420.noelytraduringcombat.combat.CombatTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFlightMixin {
    @Inject(method = "updateFallFlying", at = @At("HEAD"))
    private void nedc$onUpdateFallFlying(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ServerPlayer player) {
            if (player.isFallFlying() && CombatTracker.isFlightBlocked(player)) {
                player.stopFallFlying();
                CombatTracker.notifyPlayer(player);
            }
        }
    }
}
