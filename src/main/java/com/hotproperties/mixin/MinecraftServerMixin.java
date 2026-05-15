package com.hotproperties.mixin;

import com.hotproperties.PropertiesManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "areCommandBlocksEnabled", at = @At("HEAD"), cancellable = true)
    private void onAreCommandBlocksEnabled(CallbackInfoReturnable<Boolean> cir) {
        Boolean liveEnabled = PropertiesManager.getLiveCommandBlocksEnabled();
        if (liveEnabled != null) {
            cir.setReturnValue(liveEnabled);
        }
    }
}
