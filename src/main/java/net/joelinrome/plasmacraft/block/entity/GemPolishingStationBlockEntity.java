package net.joelinrome.plasmacraft.block.entity;

import net.joelinrome.plasmacraft.recipe.GemPolishingRecipe;
import net.joelinrome.plasmacraft.screen.GemPolishingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GemPolishingStationBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            // Simply triggers the block to get updated for all users
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    // Describes the two slots available in the gem polisher
    // Imagine them like an array in python and here we're just defining which one is which
    private static final int INPUT_SLOT = 0; // This is the top slot index
    private static final int OUTPUT_SLOT = 1; // This is the bottom slot index

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // Used to retain progress within the block
    protected final ContainerData data;
    private int progress = 0; // Starting progress
    private int maxProgress = 78; // max progress

    public GemPolishingStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GEM_POLISHING_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> GemPolishingStationBlockEntity.this.progress;
                    case 1 -> GemPolishingStationBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> GemPolishingStationBlockEntity.this.progress = value;
                    case 1 -> GemPolishingStationBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2; // Should equal the count for the number cases defined above
            }
        };
    }

    public ItemStack getRenderStack() {
        if (itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
            return itemHandler.getStackInSlot(INPUT_SLOT);
        } else {
            return itemHandler.getStackInSlot(OUTPUT_SLOT);
        }
    }

    /**
     * Used so that when multiple mods are loaded we know what the block is capable of handling
     */
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Forge specific method that allows for items to be input from other sources and work flawlessly
     */
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.plasmacraft.gem_polishing_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory inventory, Player player) {
        return new GemPolishingStationMenu(pContainerId, inventory, this, this.data);
    }

    /**
     * Saves the data when the game is saved
     */
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("gem_polishing_inventory", itemHandler.serializeNBT()); // Saves all items within the block to the key "inventory"
        pTag.putInt("gem_polishing_station.progress", progress); // Saves the current block progress
        super.saveAdditional(pTag);
    }

    /**
     * Loads the data when the game is saved
     */
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("gem_polishing_inventory"));
        progress = pTag.getInt("gem_polishing_station.progress");
    }

    /**
     * This performs the block logic every tick
     */
    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        Optional<GemPolishingRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);

        this.itemHandler.extractItem(INPUT_SLOT, 1, false); // Remove 1 item from INPUT_SLOT

        // Simply adds the new SAPPHIRE item to the output slot in addition to existing items
        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    /**
     * @return true if progress is higher than maxProgress, otherwise false
     */
    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress ++;
    }

    private boolean hasRecipe() {
        Optional<GemPolishingRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack result = recipe.get().getResultItem(null);

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    /**
     * Gets all available recipes for GemPolishingRecipe
     */
    private Optional<GemPolishingRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots()); // Sets the container size to that of the max items in entity
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(GemPolishingRecipe.Type.INSTANCE, inventory, level);
    }

    /**
     * @param item input
     * @return true if slot is either empty or contains the same item
     */
    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    /**
     * @param count number of items in slot
     * @return true if there is space left to add items to slot
     */
    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
