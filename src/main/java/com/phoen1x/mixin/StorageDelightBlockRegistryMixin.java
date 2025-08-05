package com.phoen1x.mixin;

import com.axperty.storagedelight.StorageDelight;
import com.axperty.storagedelight.registry.BlockRegistry;
import com.phoen1x.impl.block.StorageDelightStatePolymerBlock;
import com.phoen1x.impl.item.StorageDelightPolyBaseItem;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(BlockRegistry.class)
public class StorageDelightBlockRegistryMixin {
    @Inject(method = "registerBlock", at = @At("TAIL"))
    private static void polymerify(String path, Function<net.minecraft.block.AbstractBlock.Settings, Block> factory, net.minecraft.block.AbstractBlock.Settings settings, CallbackInfoReturnable<Block> cir) {
        polymerify(path, cir.getReturnValue());
    }

    @Inject(method = "registerItem", at = @At("TAIL"))
    private static void polymerifyItem(String path, Function<Item.Settings, Item> factory, Item.Settings settings, CallbackInfoReturnable<Item> cir) {
        PolymerItem polymerItem = new StorageDelightPolyBaseItem(cir.getReturnValue());
        PolymerItem.registerOverlay(cir.getReturnValue(), polymerItem);
    }

    @Unique
    private static void polymerify(String path, Block block) {
        Identifier blockId = Identifier.of(StorageDelight.MOD_ID, path);
        BlockStateModelManager.addBlock(blockId, block);
        
        PolymerBlock overlay = null;
        if (path.endsWith("_drawer") || path.endsWith("_drawer_with_door") || path.endsWith("_drawer_with_books") || 
            path.endsWith("_cabinet") || path.endsWith("_cabinet_with_glass_doors") || path.endsWith("_single_door_cabinet")) {
            overlay = StorageDelightStatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        } else if (path.endsWith("_bookshelf_with_door")) {
            overlay = StorageDelightStatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        } else if (path.endsWith("_small_drawers")) {
            overlay = StorageDelightStatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        }

        if (overlay == null) {
            overlay = com.phoen1x.impl.block.StorageDelightBaseFactoryBlock.BARRIER;
        }

        PolymerBlock.registerOverlay(block, overlay);
        if (overlay instanceof BlockWithElementHolder blockWithElementHolder) {
            BlockWithElementHolder.registerOverlay(block, blockWithElementHolder);
        }
    }
}