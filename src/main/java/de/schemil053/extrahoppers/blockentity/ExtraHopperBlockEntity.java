package de.schemil053.extrahoppers.blockentity;

import de.schemil053.extrahoppers.Extrahoppers;
import de.schemil053.extrahoppers.block.ExtraHopperBlock;
import de.schemil053.extrahoppers.util.HopperSpeed;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExtraHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
    public static final int HOPPER_CONTAINER_SIZE = 5;
    private NonNullList<ItemStack> items = NonNullList.withSize(HOPPER_CONTAINER_SIZE, ItemStack.EMPTY);
    private int cooldownTime = -1;
    private long tickedGameTime;
    private int moveItemSpeed = 8;

    private HopperSpeed itemsPerUpdate = HopperSpeed.NORMAL;
    private String inventoryTranslation = "container.hopper";

    public ExtraHopperBlockEntity(BlockPos p_155550_, BlockState p_155551_) {
        super(Extrahoppers.HOPPER_BLOCK_ENTITY.get(), p_155550_, p_155551_);
        if(p_155551_.getBlock() instanceof ExtraHopperBlock) {
            this.moveItemSpeed = ((ExtraHopperBlock) p_155551_.getBlock()).getCooldownInTicks();
            this.itemsPerUpdate = ((ExtraHopperBlock) p_155551_.getBlock()).getItemsPerUpdate();
            this.inventoryTranslation = ((ExtraHopperBlock) p_155551_.getBlock()).getInventoryTranslation();
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag)) {
            ContainerHelper.loadAllItems(compoundTag, this.items);
        }

        this.cooldownTime = compoundTag.getInt("TransferCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items);
        }

        compoundTag.putInt("TransferCooldown", this.cooldownTime);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.getItems(), slot, amount);
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        this.unpackLootTable(null);
        this.getItems().set(slot, item);
        if (item.getCount() > this.getMaxStackSize()) {
            item.setCount(this.getMaxStackSize());
        }

    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(inventoryTranslation);
    }

    public static void pushItemsTick(Level level, BlockPos blockPos, BlockState state, ExtraHopperBlockEntity hopperBlock) {
        --hopperBlock.cooldownTime;
        hopperBlock.tickedGameTime = level.getGameTime();
        if (!hopperBlock.isOnCooldown()) {
            hopperBlock.setCooldown(0);

            tryMoveItems(level, blockPos, state, hopperBlock, () -> suckInItems(level, hopperBlock));
        }

    }

    private static boolean tryMoveItems(Level level, BlockPos blockPos, BlockState blockState, ExtraHopperBlockEntity be, BooleanSupplier p_155583_) {
        if (level.isClientSide) {
            return false;
        }
        if (!be.isOnCooldown() && blockState.getValue(ExtraHopperBlock.ENABLED)) {
            boolean changed = false;
            if (!be.isEmpty()) {
                changed = ejectItems(level, blockPos, blockState, be);
            }

            if (!be.inventoryFull()) {
                changed |= p_155583_.getAsBoolean();
            }

            if (changed) {
                be.setCooldown(be.moveItemSpeed);
                setChanged(level, blockPos, blockState);
                return true;
            }
        }

        return false;
    }

    private boolean inventoryFull() {
        for (ItemStack itemstack : this.items) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private static boolean ejectItems(Level level, BlockPos blockPos, BlockState blockState, ExtraHopperBlockEntity hopper) {
        Container target = getAttachedContainer(level, blockPos, blockState);
        if (target == null) {
            return false;
        }
        Direction direction = blockState.getValue(ExtraHopperBlock.FACING).getOpposite();
        if (isFullContainer(target, direction)) {
            return false;
        }
        for (int i = 0; i < hopper.getContainerSize(); ++i) {
            if (!hopper.getItem(i).isEmpty()) {
                ItemStack removed = hopper.removeItem(i, hopper.itemsPerUpdate.get());
                ItemStack remaining = addItem(hopper, target, removed, direction);

                if (remaining.isEmpty()) {
                    target.setChanged();
                    return true;
                }


                ItemStack current = hopper.getItem(i);

                if (current.isEmpty()) {
                    hopper.setItem(i, remaining);
                } else {
                    current.grow(remaining.getCount());
                }

                hopper.setChanged();
            }
        }

        return false;
    }

    private static IntStream getSlots(Container container, Direction direction) {
        return container instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer) container).getSlotsForFace(direction)) : IntStream.range(0, container.getContainerSize());
    }

    private static boolean isFullContainer(Container container, Direction direction) {
        return getSlots(container, direction).allMatch((slot) -> {
            ItemStack itemstack = container.getItem(slot);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private static boolean isEmptyContainer(Container container, Direction direction) {
        return getSlots(container, direction).allMatch((p_59319_) -> {
            return container.getItem(p_59319_).isEmpty();
        });
    }

    public static boolean suckInItems(Level level, ExtraHopperBlockEntity blockEntity) {
//        Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(level, blockEntity);
//        if (ret != null) {
//            return ret;
//        }
        Container container = getSourceContainer(level, blockEntity);
        if (container != null) {
            Direction direction = Direction.DOWN;
            if(isEmptyContainer(container, direction)) {
                return false;
            }
            return getSlots(container, direction).anyMatch((slot) -> tryTakeInItemFromSlot(blockEntity, container, slot, direction));
        } else {
            for (ItemEntity itementity : getItemsAtAndAbove(level, blockEntity)) {
                if (addItem(blockEntity, itementity)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static boolean tryTakeInItemFromSlot(ExtraHopperBlockEntity block, Container source, int slot, Direction dir) {
        ItemStack itemstack = source.getItem(slot);
        if (!itemstack.isEmpty() && canTakeItemFromContainer(block, source, itemstack, slot, dir)) {
            ItemStack removed = source.removeItem(slot, block.itemsPerUpdate.get());
            ItemStack remaining = addItem(source, block, removed, null);

            if (remaining.isEmpty()) {
                source.setChanged();
                return true;
            }

            ItemStack current = source.getItem(slot);

            if (current.isEmpty()) {
                source.setItem(slot, remaining);
            } else {
                current.grow(remaining.getCount());
            }

            source.setChanged();
        }

        return false;
    }

    public static boolean addItem(Container container, ItemEntity droppedItem) {
        boolean flag = false;
        ItemStack itemstack = droppedItem.getItem().copy();
        ItemStack itemstack1 = addItem(null, container, itemstack, null);
        if (itemstack1.isEmpty()) {
            flag = true;
            droppedItem.discard();
        } else {
            droppedItem.setItem(itemstack1);
        }

        return flag;
    }

    public static ItemStack addItem(@Nullable Container to, Container from, ItemStack item, @Nullable Direction direction) {
        if (from instanceof WorldlyContainer worldlycontainer) {
            if (direction != null) {
                int[] aint = worldlycontainer.getSlotsForFace(direction);

                for (int k = 0; k < aint.length && !item.isEmpty(); ++k) {
                    item = tryMoveInItem(to, from, item, aint[k], direction);
                }

                return item;
            }
        }

        int i = from.getContainerSize();

        for (int j = 0; j < i && !item.isEmpty(); ++j) {
            item = tryMoveInItem(to, from, item, j, direction);
        }

        return item;
    }

    private static boolean canPlaceItemInContainer(Container container, ItemStack itemStack, int p_59337_, @Nullable Direction p_59338_) {
        if (!container.canPlaceItem(p_59337_, itemStack)) {
            return false;
        }
        if (container instanceof WorldlyContainer worldlycontainer) {
            return worldlycontainer.canPlaceItemThroughFace(p_59337_, itemStack, p_59338_);
        }

        return true;
    }

    private static boolean canTakeItemFromContainer(Container p_273433_, Container p_273542_, ItemStack p_273400_, int p_273519_, Direction p_273088_) {
        if (!p_273542_.canTakeItem(p_273433_, p_273519_, p_273400_)) {
            return false;
        }
        if (p_273542_ instanceof WorldlyContainer worldlycontainer) {
            return worldlycontainer.canTakeItemThroughFace(p_273519_, p_273400_, p_273088_);
        }

        return true;
    }

    private static ItemStack tryMoveInItem(@Nullable Container to, Container from, ItemStack item, int slot, @Nullable Direction direction) {
        ItemStack itemstack = from.getItem(slot);
        if (canPlaceItemInContainer(from, item, slot, direction)) {
            boolean flag = false;
            boolean flag1 = from.isEmpty();
            if (itemstack.isEmpty()) {
                from.setItem(slot, item);
                item = ItemStack.EMPTY;
                flag = true;
            } else if (canMergeItems(itemstack, item)) {
                int i = item.getMaxStackSize() - itemstack.getCount();
                int j = Math.min(item.getCount(), i);
                item.shrink(j);
                itemstack.grow(j);
                flag = j > 0;
            }

            if (flag) {
                if (flag1 && from instanceof ExtraHopperBlockEntity) {
                    ExtraHopperBlockEntity hopperblockentity1 = (ExtraHopperBlockEntity) from;
                    if (!hopperblockentity1.isOnCustomCooldown()) {
                        int k = 0;
                        if (to instanceof ExtraHopperBlockEntity) {
                            ExtraHopperBlockEntity hopperblockentity = (ExtraHopperBlockEntity) to;
                            if (hopperblockentity1.tickedGameTime >= hopperblockentity.tickedGameTime) {
                                k = 1;
                            }
                        }

                        hopperblockentity1.setCooldown(hopperblockentity1.moveItemSpeed - k);
                    }
                }

                from.setChanged();
            }
        }

        return item;
    }

    @Nullable
    private static Container getAttachedContainer(Level p_155593_, BlockPos p_155594_, BlockState p_155595_) {
        Direction direction = p_155595_.getValue(ExtraHopperBlock.FACING);
        return getContainerAt(p_155593_, p_155594_.relative(direction));
    }

    @Nullable
    private static Container getSourceContainer(Level p_155597_, Hopper p_155598_) {
        return getContainerAt(p_155597_, p_155598_.getLevelX(), p_155598_.getLevelY() + 1.0D, p_155598_.getLevelZ());
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level p_155590_, Hopper p_155591_) {
        return p_155591_.getSuckShape().toAabbs().stream().flatMap((p_155558_) -> {
            return p_155590_.getEntitiesOfClass(ItemEntity.class, p_155558_.move(p_155591_.getLevelX() - 0.5D, p_155591_.getLevelY() - 0.5D, p_155591_.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
    }

    @Nullable
    public static Container getContainerAt(Level p_59391_, BlockPos p_59392_) {
        return getContainerAt(p_59391_, (double) p_59392_.getX() + 0.5D, (double) p_59392_.getY() + 0.5D, (double) p_59392_.getZ() + 0.5D);
    }

    @Nullable
    private static Container getContainerAt(Level level, double p_59349_, double p_59350_, double p_59351_) {
        Container container = null;
        BlockPos blockpos = BlockPos.containing(p_59349_, p_59350_, p_59351_);
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (block instanceof WorldlyContainerHolder) {
            container = ((WorldlyContainerHolder) block).getContainer(blockstate, level, blockpos);
        } else if (blockstate.hasBlockEntity()) {
            BlockEntity blockentity = level.getBlockEntity(blockpos);
            if (blockentity instanceof Container) {
                container = (Container) blockentity;
                if (container instanceof ChestBlockEntity && block instanceof ChestBlock) {
                    container = ChestBlock.getContainer((ChestBlock) block, blockstate, level, blockpos, true);
                }
            }
        }

        if (container == null) {
            List<Entity> list = level.getEntities((Entity) null, new AABB(p_59349_ - 0.5D, p_59350_ - 0.5D, p_59351_ - 0.5D, p_59349_ + 0.5D, p_59350_ + 0.5D, p_59351_ + 0.5D), EntitySelector.CONTAINER_ENTITY_SELECTOR);
            if (!list.isEmpty()) {
                container = (Container) list.get(level.random.nextInt(list.size()));
            }
        }

        return container;
    }

    private static boolean canMergeItems(ItemStack p_59345_, ItemStack p_59346_) {
        return p_59345_.getCount() <= p_59345_.getMaxStackSize() && ItemStack.isSameItemSameTags(p_59345_, p_59346_);
    }

    @Override
    public double getLevelX() {
        return (double) this.worldPosition.getX() + 0.5D;
    }

    @Override
    public double getLevelY() {
        return (double) this.worldPosition.getY() + 0.5D;
    }

    @Override
    public double getLevelZ() {
        return (double) this.worldPosition.getZ() + 0.5D;
    }

    public void setCooldown(int cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    public boolean isOnCustomCooldown() {
        return this.cooldownTime > 8;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public static void entityInside(Level level, BlockPos blockPos, BlockState blockState, Entity entity, ExtraHopperBlockEntity blockEntity) {
        if (entity instanceof ItemEntity && Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ())), blockEntity.getSuckShape(), BooleanOp.AND)) {
            tryMoveItems(level, blockPos, blockState, blockEntity, () -> addItem(blockEntity, (ItemEntity) entity));
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_59312_, Inventory p_59313_) {
        return new HopperMenu(p_59312_, p_59313_, this);
    }

    public long getLastUpdateTime() {
        return this.tickedGameTime;
    }
}
