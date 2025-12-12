package com.phoen1x.storagedelight.mixin.register;

import com.axperty.storagedelight.registry.EntityTypesRegistry;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityTypesRegistry.class)
public class StorageDelightEntityTypesMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void polymerify(String path, BlockEntityType<?> type, CallbackInfoReturnable<BlockEntityType<?>> cir) {
        PolymerBlockUtils.registerBlockEntity(type);
    }
}