package com.phoen1x.impl.block;

import com.phoen1x.impl.func.TriFunction;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public record SDFactoryBlock(
        BlockState clientState,
        boolean tick,
        TriFunction<BlockState, ServerWorld, BlockPos, BlockModel> modelFunction
) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {

    public static final SDFactoryBlock BARRIER = new SDFactoryBlock(
            Blocks.BARRIER.getDefaultState(),
            false,
            (state, world, pos) -> BlockStateModel.longRange(state, pos)
    );

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return clientState;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, world, pos);
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public SDFactoryBlock withModel(TriFunction<BlockState, ServerWorld, BlockPos, BlockModel> modelFunction) {
        return new SDFactoryBlock(this.clientState, this.tick, modelFunction);
    }

    public SDFactoryBlock withTick(boolean tick) {
        return new SDFactoryBlock(this.clientState, tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(
            BlockState state,
            ServerPlayerEntity player,
            Hand hand,
            ItemStack stack,
            ServerWorld world,
            BlockHitResult blockHitResult
    ) {
        return true;
    }
}
