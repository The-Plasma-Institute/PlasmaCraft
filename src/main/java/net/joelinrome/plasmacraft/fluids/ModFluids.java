package net.joelinrome.plasmacraft.fluids;

import net.joelinrome.plasmacraft.PlasmaCraft;
import net.joelinrome.plasmacraft.block.ModBlocks;
import net.joelinrome.plasmacraft.item.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, PlasmaCraft.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_DEUTERIUM = FLUIDS.register("deuterium_fluid",
            () -> new ForgeFlowingFluid.Source(ModFluids.DEUTERIUM_FLUID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING_DEUTERIUM = FLUIDS.register("flowing_deuterium",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.DEUTERIUM_FLUID_PROPERTIES));


    public static final ForgeFlowingFluid.Properties DEUTERIUM_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.DEUTERIUM_FLUID_TYPE, SOURCE_DEUTERIUM, FLOWING_DEUTERIUM)
            .slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.DEUTERIUM_BLOCK)
            .bucket(ModItems.DEUTERIUM_BUCKET);


    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}