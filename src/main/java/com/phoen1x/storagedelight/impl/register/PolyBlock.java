package com.phoen1x.storagedelight.impl.register;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.BlockStateAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.phoen1x.storagedelight.impl.StorageDelightPatch.MOD_ID;

public class PolyBlock implements eu.pb4.factorytools.api.block.FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    private final Map<BlockState, BlockState> map;
    private final BlockState clientState;
    private final boolean tick;
    private final BiFunction<BlockState, BlockPos, BlockModel> modelFunction;

    // Конструктор для звичайного FactoryBlock
    public PolyBlock(BlockState clientState, boolean tick, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        this.map = null;
        this.clientState = clientState;
        this.tick = tick;
        this.modelFunction = modelFunction;
    }

    // Конструктор для PolymerBlock
    public PolyBlock(Map<BlockState, BlockState> map, BlockState fallbackState, BiFunction<BlockState, BlockPos, BlockModel> fallbackModel) {
        this.map = map;
        this.clientState = fallbackState;
        this.tick = false;
        this.modelFunction = fallbackModel;
    }

    // === Статичні фабрики ===
    public static final PolyBlock BARRIER =
            new PolyBlock(Blocks.BARRIER.getDefaultState(), false, BlockStateModel::longRange);

    public static PolyBlock of(Block block, BlockModelType type) {
        return of(block, type, BARRIER, x -> true);
    }

    public static PolyBlock of(Block block, BlockModelType type, PolyBlock fallback, Predicate<BlockState> canUseBlock) {
        var id = Registries.BLOCK.getId(block);
        var path = FabricLoader.getInstance().getModContainer(MOD_ID).get()
                .findPath("assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json").get();

        try {
            var decoded = BlockStateAsset.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(path)))
                    .getOrThrow().getFirst();

            var list = new ArrayList<Pair<BlockStatePredicate, List<StateModelVariant>>>();
            var cache = new HashMap<List<StateModelVariant>, BlockState>();

            BlockStateModelManager.parseVariants(block, decoded.variants().orElseThrow(), (a, b) -> list.add(new Pair<>(a, b)));
            var map = new IdentityHashMap<BlockState, BlockState>();

            for (var state : block.getStateManager().getStates()) {
                for (var pair : list) {
                    if (pair.getLeft().test(state) && canUseBlock.test(state)) {
                        map.put(state, cache.computeIfAbsent(pair.getRight(), c -> PolymerBlockResourceUtils.requestBlock(
                                type,
                                c.stream().map(x -> new PolymerBlockModel(x.model(), x.x(), x.y(), x.uvlock(), x.weigth())).toArray(PolymerBlockModel[]::new))));
                        break;
                    }
                }
            }

            return new PolyBlock(map, fallback.clientState, fallback.modelFunction);
        } catch (Throwable e) {
            return fallback;
        }
    }

    // === Реалізація інтерфейсів ===
    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        if (map != null) {
            var val = map.get(state);
            return val != null ? val : clientState;
        }
        return clientState;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        if (map != null && map.containsKey(initialBlockState))
            return null;
        return modelFunction != null ? modelFunction.apply(initialBlockState, pos) : null;
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return tick;
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }

    // === Додаткові утиліти ===
    public PolyBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new PolyBlock(this.clientState, this.tick, modelFunction);
    }

    public PolyBlock withTick(boolean tick) {
        return new PolyBlock(this.clientState, tick, this.modelFunction);
    }
}
