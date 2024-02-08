package net.joelinrome.realisticfusionreactors.datagen;

import net.joelinrome.realisticfusionreactors.RealisticFusionReactors;
import net.joelinrome.realisticfusionreactors.block.ModBlocks;
import net.joelinrome.realisticfusionreactors.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RealisticFusionReactors.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.SAPPHIRE);
        simpleItem(ModItems.RAW_SAPPHIRE);

        simpleItem(ModItems.METAL_DETECTOR);
        simpleItem(ModItems.STRAWBERRY);
        simpleItem(ModItems.PINE_CONE);

        simpleBlockItem(ModBlocks.SAPPHIRE_DOOR);

        fenceItem(ModBlocks.SAPPHIRE_FENCE, ModBlocks.SAPPHIRE_BLOCK);
        buttonItem(ModBlocks.SAPPHIRE_BUTTON, ModBlocks.SAPPHIRE_BLOCK);
        wallItem(ModBlocks.SAPPHIRE_WALL, ModBlocks.SAPPHIRE_BLOCK);
        trapdoorItem(ModBlocks.SAPPHIRE_TRAPDOOR);

        evenSimplerBlockItem(ModBlocks.SAPPHIRE_STAIRS);
        evenSimplerBlockItem(ModBlocks.SAPPHIRE_SLAB);
        evenSimplerBlockItem(ModBlocks.SAPPHIRE_PRESSURE_PLATE);
        evenSimplerBlockItem(ModBlocks.SAPPHIRE_FENCE_GATE);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(RealisticFusionReactors.MOD_ID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(RealisticFusionReactors.MOD_ID,"item/" + item.getId().getPath()));
    }

    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(RealisticFusionReactors.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void trapdoorItem(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath() + "_bottom"));
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  new ResourceLocation(RealisticFusionReactors.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  new ResourceLocation(RealisticFusionReactors.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  new ResourceLocation(RealisticFusionReactors.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }
}
