package com.kevinthegreat.etiquetteenderman.mixin;

import com.kevinthegreat.etiquetteenderman.EndermanAccessor;
import com.kevinthegreat.etiquetteenderman.EtiquetteEnderman;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EnderMan.class)
public abstract class EndermanMixin extends Monster implements EndermanAccessor {
    @Unique
    private boolean asked;
    @Unique
    private boolean responded;
    @Unique
    private boolean consent;

    protected EndermanMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean etiquetteEnderman$askedForConsent() {
        return asked;
    }

    @Override
    public void etiquetteEnderman$setAskedForConsent(boolean asked) {
        this.asked = asked;
        responded = false;
    }

    @Override
    public boolean etiquetteEnderman$getResponded() {
        return responded;
    }

    @Override
    public boolean etiquetteEnderman$getConsent() {
        return consent;
    }

    @Override
    public void etiquetteEnderman$setConsent(boolean consent) {
        this.responded = true;
        this.consent = consent;
    }

    @Mixin(targets = "net.minecraft.world.entity.monster.EnderMan$EndermanLookForPlayerGoal")
    public abstract static class EndermanLookForPlayerGoalMixin {
        @Shadow
        @Nullable
        private Player pendingTarget;
        @Shadow
        @Final
        private EnderMan enderman;

        @Shadow
        public abstract void stop();

        @Expression("? <= 0")
        @ModifyExpressionValue(method = "tick",
                slice = @Slice(from = @At(value = "FIELD:ONE", target = "Lnet/minecraft/world/entity/monster/EnderMan$EndermanLookForPlayerGoal;aggroTime:I", opcode = Opcodes.GETFIELD)),
                at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0)
        )
        private boolean askForConsent(boolean original) {
            if (!original) return false;

            if (((EndermanAccessor) enderman).etiquetteEnderman$getResponded()) {
                ((EndermanAccessor) enderman).etiquetteEnderman$setAskedForConsent(false);
                if (((EndermanAccessor) enderman).etiquetteEnderman$getConsent()) {
                    return true;
                } else {
                    stop();
                    return false;
                }
            }
            if (((EndermanAccessor) enderman).etiquetteEnderman$askedForConsent()) {
                return false;
            }

            pendingTarget.openDialog(enderman.registryAccess().getOrThrow(EtiquetteEnderman.ENDERMAN_CONSENT_DIALOG_KEY));
            ((EndermanAccessor) enderman).etiquetteEnderman$setAskedForConsent(true);
            return false;
        }
    }
}
