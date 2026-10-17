package me.spectral8420.noelytraduringcombat.mixin;

import me.spectral8420.noelytraduringcombat.combat.CombatTracker;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerFlightMixin {
    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void nedc$onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (!player.level().isClientSide() && CombatTracker.isFlightBlocked(player)) {
            CombatTracker.notifyPlayer(player);
            cir.setReturnValue(false);
        }
    }
}
