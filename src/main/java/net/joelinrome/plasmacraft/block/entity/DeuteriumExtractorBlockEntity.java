package net.joelinrome.plasmacraft.block.entity;

import net.joelinrome.plasmacraft.PlasmaCraft;
import net.joelinrome.plasmacraft.fluids.ModFluids;
import net.joelinrome.plasmacraft.recipe.DeuteriumExtractorRecipe;
import net.joelinrome.plasmacraft.screen.DeuteriumExtractorMenu;
import net.joelinrome.plasmacraft.util.ModEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// Extends BlockEntity for display and management of systems whereas MenuProvider links it to a gui menu
public class DeuteriumExtractorBlockEntity extends BlockEntity implements MenuProvider {
    // Defines which slot is which
    public static final int FLUID_ITEM_INPUT_SLOT = 0;
    public static final int ENERGY_ITEM_INPUT_SLOT = 1;
    public static final int FLUID_ITEM_OUTPUT_SLOT = 2;

    private final ItemStackHandler itemHandler = new ItemStackHandler(3){ // Defines the number of slots in the block
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
                case FLUID_ITEM_INPUT_SLOT, FLUID_ITEM_OUTPUT_SLOT -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
                case ENERGY_ITEM_INPUT_SLOT -> stack.getItem() == Items.COAL || stack.getItem() == Items.COAL_BLOCK;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty(); // Stores current items in block

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyInputFluidHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyOutputFluidHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78; // Change this to however long you want it to take to output item

    private final ModEnergyStorage ENERGY_STORAGE = createEnergyStorage();
    private final FluidTank FLUID_INPUT_TANK = createFluidTank(Fluids.WATER);
    private final FluidTank FLUID_OUTPUT_TANK = createFluidTank(ModFluids.SOURCE_DEUTERIUM.get());

    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(64000, 200) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        };
    }

    private FluidTank createFluidTank(FlowingFluid flowingFluid) {
        return new FluidTank(64000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if(!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid() == flowingFluid;
            }
        };
    }

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

    public IEnergyStorage getEnergyStorage() {
        return this.ENERGY_STORAGE;
    };

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
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        if(cap == ForgeCapabilities.FLUID_HANDLER && side == Direction.UP) {
            return lazyOutputFluidHandler.cast();
        } else if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyInputFluidHandler.cast();
        }

        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler); // Makes sure the items get loaded in correctly
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
        lazyInputFluidHandler = LazyOptional.of(() -> FLUID_INPUT_TANK);
        lazyOutputFluidHandler = LazyOptional.of(() -> FLUID_OUTPUT_TANK);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate(); // Makes sure the items get removed
        lazyEnergyHandler.invalidate();
        lazyInputFluidHandler.invalidate();
        lazyOutputFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("deuterium_extractor_inventory", itemHandler.serializeNBT()); // Saves the inventory to the pKey
        pTag.putInt("deuterium_extractor_energy", ENERGY_STORAGE.getEnergyStored());
        pTag.put("deuterium_extractor_water_tank", FLUID_INPUT_TANK.writeToNBT(new CompoundTag()));
        pTag.put("deuterium_extractor_deuterium_tank", FLUID_OUTPUT_TANK.writeToNBT(new CompoundTag()));

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("deuterium_extractor_inventory")); // Loads the inventory from the pKey

        ENERGY_STORAGE.setEnergy(pTag.getInt("deuterium_extractor_energy"));
        FLUID_INPUT_TANK.readFromNBT(pTag.getCompound("deuterium_extractor_water_tank"));
        FLUID_OUTPUT_TANK.readFromNBT(pTag.getCompound("deuterium_extractor_deuterium_tank"));
    }

    /**
     * This method gets called 20 times every second and runs our blocks logic
     */
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        fillUpOnEnergy();
        fillUpOnFluid();

        if (hasEnoughEnergyToCraft() && hasEnoughWaterToCraft()) {
            increaseCraftingProcess();
            extractEnergy();
            setChanged(level, blockPos, blockState);

            if (hasProgressFinished()) {
                fillTankWithFluid(FLUID_ITEM_OUTPUT_SLOT, new FluidStack(ModFluids.SOURCE_DEUTERIUM.get(), 500));
                extractWater();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void extractWater() {
        this.FLUID_INPUT_TANK.drain(500, IFluidHandler.FluidAction.EXECUTE);
    }

    public FluidTank getFluidTank(int fluidSlot) {
        switch (fluidSlot) {
            case FLUID_ITEM_INPUT_SLOT -> {
                return FLUID_INPUT_TANK;
            }
            case FLUID_ITEM_OUTPUT_SLOT -> {
                return FLUID_OUTPUT_TANK;
            }
            default -> {
                PlasmaCraft.LOGGER.error("unknown fluid tank to fill");
                return null;
            }
        }
    }

    private void fillUpOnFluid() {
        if(hasFluidItemInSlot(FLUID_ITEM_INPUT_SLOT)) {
            transferItemFluidToTank(FLUID_ITEM_INPUT_SLOT);
        }
    }

    private void transferItemFluidToTank(int fluidSlot) {
        this.itemHandler.getStackInSlot(fluidSlot).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(
                iFluidHandlerItem -> {
                    int drainAmount = Math.min(this.FLUID_INPUT_TANK.getSpace(), 1000);

                    FluidStack stack = iFluidHandlerItem.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
                    if(stack.getFluid() == Fluids.WATER) {
                        stack = iFluidHandlerItem.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                        fillTankWithFluid(fluidSlot, stack);
                        // Extracts and returns empty bucket
                        this.itemHandler.extractItem(fluidSlot, 1, false);
                        this.itemHandler.insertItem(fluidSlot, iFluidHandlerItem.getContainer(), false);
                    }
                }
        );
    }

    private void fillTankWithFluid(int fluidSlot, FluidStack stack) {
        getFluidTank(fluidSlot).fill(new FluidStack(stack.getFluid(), stack.getAmount()), IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean hasFluidItemInSlot(int fluidSlot) {
        return this.itemHandler.getStackInSlot(fluidSlot).getCount() > 0 &&
                this.itemHandler.getStackInSlot(fluidSlot).getCapability(
                        ForgeCapabilities.FLUID_HANDLER_ITEM
                ).isPresent();
    }

    private void extractEnergy() {
        this.ENERGY_STORAGE.extractEnergy(100, false);
    }

    private void fillUpOnEnergy() {
        if(hasEnergyItemInSlot(ENERGY_ITEM_INPUT_SLOT)) {
            // TODO: This needs to correctly delete the item and input the correct amount of energy, currently only adds 200 FE
            this.itemHandler.extractItem(ENERGY_ITEM_INPUT_SLOT, 1, false);
            this.ENERGY_STORAGE.receiveEnergy(3200, false);
        }
    }

    private boolean hasEnergyItemInSlot(int energyItemSlot) {
        return !this.itemHandler.getStackInSlot(energyItemSlot).isEmpty() &&
                this.itemHandler.getStackInSlot(energyItemSlot).getItem() == Items.COAL;
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

    private boolean hasEnoughWaterToCraft() {
        return this.FLUID_INPUT_TANK.getFluidAmount() >= 500;
    }

    private boolean hasEnoughEnergyToCraft() {
        return this.ENERGY_STORAGE.getEnergyStored() >= 100 * maxProgress; // Placeholder, feel free to adjust
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
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

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }
}
