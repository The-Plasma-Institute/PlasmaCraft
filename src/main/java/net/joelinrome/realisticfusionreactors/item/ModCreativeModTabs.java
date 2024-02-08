package net.joelinrome.realisticfusionreactors.item;

import net.joelinrome.realisticfusionreactors.RealisticFusionReactors;
import net.joelinrome.realisticfusionreactors.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RealisticFusionReactors.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TUTORIAL_TAB = CREATIVE_MODE_TABS
            .register("tutorial_tab", () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SAPPHIRE.get()))
            .title(Component.translatable("creativetab.tutorial_tab"))
            .displayItems(((itemDisplayParameters, output) -> {
                output.accept(ModItems.RAW_SAPPHIRE.get());
                output.accept(ModItems.SAPPHIRE.get());

                output.accept(ModItems.METAL_DETECTOR.get());

                output.accept(ModItems.STRAWBERRY.get());
                output.accept(ModItems.PINE_CONE.get());
                output.accept(ModItems.SAPPHIRE_STAFF.get());

                output.accept(ModItems.SAPPHIRE_SWORD.get());
                output.accept(ModItems.SAPPHIRE_PICKAXE.get());
                output.accept(ModItems.SAPPHIRE_AXE.get());
                output.accept(ModItems.SAPPHIRE_SHOVEL.get());
                output.accept(ModItems.SAPPHIRE_HOE.get());

                output.accept(ModBlocks.SOUND_BLOCK.get());

                output.accept(ModBlocks.RAW_SAPPHIRE_BLOCK.get());
                output.accept(ModBlocks.SAPPHIRE_BLOCK.get());

                output.accept(ModBlocks.SAPPHIRE_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
                output.accept(ModBlocks.NETHER_SAPPHIRE_ORE.get());
                output.accept(ModBlocks.END_STONE_SAPPHIRE_ORE.get());

                output.accept(ModBlocks.SAPPHIRE_STAIRS.get());
                output.accept(ModBlocks.SAPPHIRE_SLAB.get());
                output.accept(ModBlocks.SAPPHIRE_BUTTON.get());
                output.accept(ModBlocks.SAPPHIRE_PRESSURE_PLATE.get());

                output.accept(ModBlocks.SAPPHIRE_FENCE.get());
                output.accept(ModBlocks.SAPPHIRE_FENCE_GATE.get());
                output.accept(ModBlocks.SAPPHIRE_WALL.get());

                output.accept(ModBlocks.SAPPHIRE_DOOR.get());
                output.accept(ModBlocks.SAPPHIRE_TRAPDOOR.get());

            }))
            .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
