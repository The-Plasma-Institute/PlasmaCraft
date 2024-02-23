package net.joelinrome.plasmacraft.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joelinrome.plasmacraft.PlasmaCraft;
import net.joelinrome.plasmacraft.block.ModBlocks;
import net.joelinrome.plasmacraft.repice.GemPolishingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GemPolishingCategory implements IRecipeCategory<GemPolishingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(PlasmaCraft.MOD_ID, "gem_polishing");
    public static final ResourceLocation TEXTURE = new ResourceLocation(PlasmaCraft.MOD_ID, "textures/gui/gem_polishing_station_gui.png");

    public static final RecipeType<GemPolishingRecipe> GEM_POLISHING_RECIPE_TYPE = new RecipeType<>(UID, GemPolishingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated animatedArrow; // This doesn't work yet :(

    public GemPolishingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0,176, 80); // Draws just the top half of the GUI texture
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.GEM_POLISHING_STATION.get())); // Icon displayed in JEI
        this.animatedArrow = helper.drawableBuilder(TEXTURE, 176, 0, 14, 26)
                .buildAnimated(200, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public RecipeType<GemPolishingRecipe> getRecipeType() {
        return GEM_POLISHING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.plasmacraft.gem_polishing_station");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, GemPolishingRecipe recipe, IFocusGroup iFocusGroup) {
        // i and i1 are the locations of the input slot
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 80, 11).addIngredients(recipe.getIngredients().get(0));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 80, 59).addItemStack(recipe.getResultItem(null));
    }
}
