package com.phoen1x.impl.block;

import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

public class SDModel extends BlockModel {
    private final ItemDisplayElement drawer;
    private final ServerWorld world;
    private final BlockPos pos;
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public SDModel(BlockState state, ServerWorld world, BlockPos pos, String modelPath) {
        this.world = world;
        this.pos = pos;

        var stack = ItemDisplayElementUtil.getModel(Identifier.of("storagedelight", modelPath));
        this.drawer = ItemDisplayElementUtil.createSimple(stack);
        this.drawer.setTranslation(new Vector3f(0f, 0f, 0f));
        this.drawer.setScale(new Vector3f(2f));
        this.drawer.setDisplaySize(1f, 1f);

        this.addElement(this.drawer);
        this.updateState(state);
    }

    public void updateState(BlockState state) {
        this.drawer.setYaw(state.get(FACING).getPositiveHorizontalDegrees());
    }
}
