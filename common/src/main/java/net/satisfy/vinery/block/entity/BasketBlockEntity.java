package net.satisfy.vinery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.block.BasketBlock;
import net.satisfy.vinery.client.gui.handler.BasketGuiHandler;
import net.satisfy.vinery.registry.BlockEntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BasketBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private NonNullList<ItemStack> items;
    private final ContainerOpenersCounter openersCounter;
    private final ChestLidController chestLidController = new ChestLidController();

    public BasketBlockEntity( BlockPos blockPos, BlockState blockState) {
        super(BlockEntityTypeRegistry.BASKET_ENTITY.get(), blockPos, blockState);
        this.items = NonNullList.withSize(9, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
                BasketBlockEntity.this.playSound(blockState);
            }

            protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
                BasketBlockEntity.this.playSound(blockState);
            }

            protected void openerCountChanged(Level level, BlockPos blockPos, BlockState blockState, int i, int j) {
                BasketBlockEntity.this.signalOpenCount(level, blockPos, blockState, i, j);
            }

            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof ChestMenu) {
                    Container container = ((ChestMenu)player.containerMenu).getContainer();
                    return container == BasketBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items, provider);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag)) {
            ContainerHelper.loadAllItems(compoundTag, this.items, provider);
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }
    
    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new BasketGuiHandler(i,inventory,this);
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    void playSound(BlockState blockState) {
        Vec3i vec3i = blockState.getValue(BasketBlock.FACING).getNormal();
        double d = (double)this.worldPosition.getX() + 0.5D + (double)vec3i.getX() / 2.0D;
        double e = (double)this.worldPosition.getY() + 0.5D + (double)vec3i.getY() / 2.0D;
        double f = (double)this.worldPosition.getZ() + 0.5D + (double)vec3i.getZ() / 2.0D;
        assert this.level != null;
        this.level.playSound(null, d, e, f, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public float getOpenNess(float f) {
        return this.chestLidController.getOpenness(f);
    }

    public static void lidAnimateTick(Level level, BlockPos blockPos, BlockState blockState, BasketBlockEntity basketBlockEntity) {
        basketBlockEntity.chestLidController.tickLid();
    }

    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            this.chestLidController.shouldBeOpen(j > 0);
            return true;
        } else {
            return super.triggerEvent(i, j);
        }
    }

    protected void signalOpenCount(Level level, BlockPos blockPos, BlockState blockState, int i, int j) {
        Block block = blockState.getBlock();
        level.blockEvent(blockPos, block, 1, j);
    }
}