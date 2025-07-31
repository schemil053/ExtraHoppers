package de.schemil053.extrahoppers.block;

import de.schemil053.extrahoppers.Extrahoppers;
import de.schemil053.extrahoppers.blockentity.ExtraHopperBlockEntity;
import de.schemil053.extrahoppers.util.HopperSpeed;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ExtraHopperBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING_HOPPER;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    private static final VoxelShape TOP = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape FUNNEL = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
    private static final VoxelShape CONVEX_BASE = Shapes.or(FUNNEL, TOP);
    private static final VoxelShape BASE = Shapes.join(CONVEX_BASE, Hopper.INSIDE, BooleanOp.ONLY_FIRST);
    private static final VoxelShape DOWN_SHAPE = Shapes.or(BASE, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
    private static final VoxelShape EAST_SHAPE = Shapes.or(BASE, Block.box(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
    private static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
    private static final VoxelShape WEST_SHAPE = Shapes.or(BASE, Block.box(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
    private static final VoxelShape DOWN_INTERACTION_SHAPE = Hopper.INSIDE;
    private static final VoxelShape EAST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
    private static final VoxelShape NORTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
    private static final VoxelShape SOUTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
    private static final VoxelShape WEST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

    private final int cooldownInTicks;
    private final HopperSpeed itemsPerUpdate;
    private final String inventoryTranslation;

    public ExtraHopperBlock(int cooldownInTicks, HopperSpeed itemsPerUpdate, String inventoryTranslation) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 4.8F).sound(SoundType.METAL).noOcclusion());
        this.cooldownInTicks = cooldownInTicks;
        this.itemsPerUpdate = itemsPerUpdate;
        this.inventoryTranslation = inventoryTranslation;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(ENABLED, Boolean.valueOf(true)));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState p_54105_, @NotNull BlockGetter p_54106_, @NotNull BlockPos p_54107_, @NotNull CollisionContext p_54108_) {
        switch (p_54105_.getValue(FACING)) {
            case DOWN:
                return DOWN_SHAPE;
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case EAST:
                return EAST_SHAPE;
            default:
                return BASE;
        }
    }

    @Override
    public @NotNull VoxelShape getInteractionShape(BlockState p_54099_, @NotNull BlockGetter p_54100_, @NotNull BlockPos p_54101_) {
        switch (p_54099_.getValue(FACING)) {
            case DOWN:
                return DOWN_INTERACTION_SHAPE;
            case NORTH:
                return NORTH_INTERACTION_SHAPE;
            case SOUTH:
                return SOUTH_INTERACTION_SHAPE;
            case WEST:
                return WEST_INTERACTION_SHAPE;
            case EAST:
                return EAST_INTERACTION_SHAPE;
            default:
                return Hopper.INSIDE;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_54041_) {
        Direction direction = p_54041_.getClickedFace().getOpposite();
        return this.defaultBlockState().setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction).setValue(ENABLED, Boolean.valueOf(true));
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos p_153382_, @NotNull BlockState p_153383_) {
        return new ExtraHopperBlockEntity(p_153382_, p_153383_);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153378_, @NotNull BlockState p_153379_, @NotNull BlockEntityType<T> p_153380_) {
        return p_153378_.isClientSide ? null : createTickerHelper(p_153380_, Extrahoppers.HOPPER_BLOCK_ENTITY.get(), ExtraHopperBlockEntity::pushItemsTick);
    }

    @Override
    public void setPlacedBy(@NotNull Level p_54049_, @NotNull BlockPos p_54050_, @NotNull BlockState p_54051_, LivingEntity p_54052_, ItemStack p_54053_) {
        if (p_54053_.hasCustomHoverName()) {
            BlockEntity blockentity = p_54049_.getBlockEntity(p_54050_);
            if (blockentity instanceof ExtraHopperBlockEntity) {
                ((ExtraHopperBlockEntity)blockentity).setCustomName(p_54053_.getHoverName());
            }
        }

    }

    @Override
    public void onPlace(BlockState blockState, @NotNull Level level, @NotNull BlockPos pos, BlockState p_54113_, boolean p_54114_) {
        if (!p_54113_.is(blockState.getBlock())) {
            this.checkPoweredState(level, pos, blockState, 2);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof ExtraHopperBlockEntity) {
                player.openMenu((ExtraHopperBlockEntity)blockentity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState p_54078_, @NotNull Level p_54079_, @NotNull BlockPos p_54080_, @NotNull Block p_54081_, @NotNull BlockPos p_54082_, boolean p_54083_) {
        this.checkPoweredState(p_54079_, p_54080_, p_54078_, 4);
    }

    private void checkPoweredState(Level p_275499_, BlockPos p_275298_, BlockState p_275611_, int p_275625_) {
        boolean flag = !p_275499_.hasNeighborSignal(p_275298_);
        if (flag != p_275611_.getValue(ENABLED)) {
            p_275499_.setBlock(p_275298_, p_275611_.setValue(ENABLED, Boolean.valueOf(flag)), p_275625_);
        }

    }

    @Override
    public void onRemove(BlockState p_54085_, @NotNull Level p_54086_, @NotNull BlockPos p_54087_, BlockState p_54088_, boolean p_54089_) {
        if (!p_54085_.is(p_54088_.getBlock())) {
            BlockEntity blockentity = p_54086_.getBlockEntity(p_54087_);
            if (blockentity instanceof ExtraHopperBlockEntity) {
                Containers.dropContents(p_54086_, p_54087_, (ExtraHopperBlockEntity)blockentity);
                p_54086_.updateNeighbourForOutputSignal(p_54087_, this);
            }

            super.onRemove(p_54085_, p_54086_, p_54087_, p_54088_, p_54089_);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_54103_) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState p_54055_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState p_54062_, Level p_54063_, @NotNull BlockPos p_54064_) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(p_54063_.getBlockEntity(p_54064_));
    }

    @Override
    public @NotNull BlockState rotate(BlockState p_54094_, Rotation p_54095_) {
        return p_54094_.setValue(FACING, p_54095_.rotate(p_54094_.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState p_54091_, Mirror p_54092_) {
        return p_54091_.rotate(p_54092_.getRotation(p_54091_.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54097_) {
        p_54097_.add(FACING, ENABLED);
    }

    @Override
    public void entityInside(@NotNull BlockState p_54066_, Level p_54067_, @NotNull BlockPos p_54068_, @NotNull Entity p_54069_) {
        BlockEntity blockentity = p_54067_.getBlockEntity(p_54068_);
        if (blockentity instanceof ExtraHopperBlockEntity) {
            ExtraHopperBlockEntity.entityInside(p_54067_, p_54068_, p_54066_, p_54069_, (ExtraHopperBlockEntity)blockentity);
        }

    }

    @Override
    public boolean isPathfindable(@NotNull BlockState p_54057_, @NotNull BlockGetter p_54058_, @NotNull BlockPos p_54059_, @NotNull PathComputationType p_54060_) {
        return false;
    }

    public int getCooldownInTicks() {
        return cooldownInTicks;
    }

    public HopperSpeed getItemsPerUpdate() {
        return itemsPerUpdate;
    }

    public String getInventoryTranslation() {
        return inventoryTranslation;
    }
}
