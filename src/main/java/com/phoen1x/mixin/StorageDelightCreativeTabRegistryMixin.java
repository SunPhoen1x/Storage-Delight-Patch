package com.phoen1x.mixin;

import com.axperty.storagedelight.registry.CreativeTabRegistry;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeTabRegistry.class)
public class StorageDelightCreativeTabRegistryMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;register(Lnet/minecraft/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object polymerify(Registry<?> registry, Identifier id, Object entry) {
        PolymerItemGroupUtils.registerPolymerItemGroup(id, (ItemGroup) entry);
        return entry;
    }
}