package net.joelinrome.plasmacraft.block.entity;

import net.joelinrome.plasmacraft.item.ModItems;
import net.joelinrome.plasmacraft.repice.DeuteriumExtractorRecipe;
import net.joelinrome.plasmacraft.screen.DeuteriumExtractorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

// Extends BlockEntity for display and management of systems whereas MenuProvider links it to a gui menu
public class DeuteriumExtractorBlockEntity extends BlockEntity implements MenuProvider {
    // Defines which slot is which
    private static final int INPUT_SLOT = 0;
    private static final int FLUID_INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int ENERGY_ITEM_SLOT = 3;

    private final ItemStackHandler itemHandler = new ItemStackHandler(4){ // Defines the number of slots in the block
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        /**
         * Checks that the item being added to a particular slot is appropriate
         * @param slot the id of the slot
         * @param stack the item being added
         * @return
         */
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0, 1 -> true;
                case 2 -> false;
                case 3 -> stack.getItem() == Items.COAL;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty(); // Stores current items in block

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78; // Change this to however long you want it to take to output item


    public DeuteriumExtractorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.DEUTERIUM_EXTRACTOR_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> DeuteriumExtractorBlockEntity.this.progress;
                    case 1 -> DeuteriumExtractorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int iValue) {
                switch (i) {
                    case 0 -> DeuteriumExtractorBlockEntity.this.progress = iValue;
                    case 1 -> DeuteriumExtractorBlockEntity.this.maxProgress = iValue;
                };
            }

            @Override
            public int getCount() {
                return 2; // Needs to match the required variables, here it's progress and maxProgress
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.plasmacraft.deuterium_extractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory inventory, Player player) {
        return new DeuteriumExtractorMenu(pContainerId, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler); // Makes sure the items get loaded in correctly
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate(); // Makes sure the items get removed
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("deuterium_extractor_inventory", itemHandler.serializeNBT()); // Saves the inventory to the pKey
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        itemHandler.deserializeNBT(pTag.getCompound("deuterium_extractor_inventory")); // Loads the inventory from the pKey
        super.load(pTag);
    }

    /**
     * This method gets called 20 times every second and runs our blocks logic
     */
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (isOutputSlotEmptyOrReceivable() && hasRecipe()) {
            increaseCraftingProcess();
            setChanged(level, blockPos, blockState);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void craftItem() {
        Optional<DeuteriumExtractorRecipe> recipe = getCurrentRecipe();
        ItemStack resultItem = recipe.get().getResultItem(getLevel().registryAccess());

        // Takes 1 item out of the input slot
        this.itemHandler.extractItem(INPUT_SLOT, 1, false);

        // Adds 1 item into the output slot of type SAPPHIRE
        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(resultItem.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + resultItem.getCount()));
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean hasProgressFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProcess() {
        this.progress++;
    }

    private boolean hasRecipe() {
        Optional<DeuteriumExtractorRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }
        ItemStack resultItem = recipe.get().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(resultItem.getCount())
                && canInsertItemIntoOutputSlot(resultItem.getItem());
    }

    /**
     * Gets all available recipes for GemPolishingRecipe and runs them against the DeuteriumExtractorRecipe.matches()
     */
    private Optional<DeuteriumExtractorRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(DeuteriumExtractorRecipe.Type.INSTANCE, inventory, level);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    /**
     * Checks that you can insert the wanted amount into the output slot. e.g. has 63 items in slot,
     * and you add 1 then it's 64 which is smaller than or equal to the max stack size
     */
    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize() >=
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count;
    }

    /**
     * Checks if output slot is empty or if it is below the max count of items allowed in that slot
     */
    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}
