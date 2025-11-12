package com.kevinthegreat.etiquetteenderman.mixin;

import com.kevinthegreat.etiquetteenderman.EndermanAccessor;
import com.kevinthegreat.etiquetteenderman.EtiquetteEnderman;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Comparator;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin {
    @Inject(method = "handleCustomClickAction", at = @At("HEAD"), cancellable = true)
    private void handleEndermanConsent(ServerboundCustomClickActionPacket serverboundCustomClickActionPacket, CallbackInfo ci) {
        if (!serverboundCustomClickActionPacket.id().getNamespace().equals(EtiquetteEnderman.MOD_ID)) return;
        ServerPlayer player = ((ServerGamePacketListenerImpl) (Object) this).getPlayer();
        EnderMan enderman = Collections.min(player.level().getEntities(EntityTypeTest.forClass(EnderMan.class), c -> ((EndermanAccessor) c).etiquetteEnderman$askedForConsent()), Comparator.<EnderMan>comparingDouble(c -> c.distanceToSqr(player)));
        if (enderman == null) return;

        if (serverboundCustomClickActionPacket.id().equals(EtiquetteEnderman.YES)) {
            ((EndermanAccessor) enderman).etiquetteEnderman$setConsent(true);
        } else if (serverboundCustomClickActionPacket.id().equals(EtiquetteEnderman.NO)) {
            ((EndermanAccessor) enderman).etiquetteEnderman$setConsent(false);
        }
        ci.cancel();
    }
}
