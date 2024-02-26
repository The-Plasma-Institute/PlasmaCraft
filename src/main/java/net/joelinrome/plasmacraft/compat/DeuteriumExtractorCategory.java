package net.joelinrome.plasmacraft.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.joelinrome.plasmacraft.PlasmaCraft;
import net.joelinrome.plasmacraft.block.ModBlocks;
import net.joelinrome.plasmacraft.recipe.DeuteriumExtractorRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DeuteriumExtractorCategory implements IRecipeCategory<DeuteriumExtractorRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(PlasmaCraft.MOD_ID, "deuterium_extracting");
    public static final ResourceLocation TEXTURE = new ResourceLocation(PlasmaCraft.MOD_ID,
             "textures/gui/deuterium_extractor_gui.png");

    public static final RecipeType<DeuteriumExtractorRecipe> DEUTERIUM_EXTRACTOR_TYPE =
            new RecipeType<>(UID, DeuteriumExtractorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated animatedArrow;

    public DeuteriumExtractorCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DEUTERIUM_EXTRACTOR_BLOCK.get()));
        this.animatedArrow = helper.drawableBuilder(TEXTURE, 176, 0, 14, 26)
                .buildAnimated(200, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public RecipeType<DeuteriumExtractorRecipe> getRecipeType() {
        return DEUTERIUM_EXTRACTOR_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.plasmacraft.deuterium_extractor");
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
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, DeuteriumExtractorRecipe deuteriumExtractorRecipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 80, 11).addIngredients(deuteriumExtractorRecipe.getIngredients().get(0));

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 80, 59).addItemStack(deuteriumExtractorRecipe.getResultItem(null));
    }

    @Override
    public void draw(DeuteriumExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        animatedArrow.draw(guiGraphics, 85, 30);
    }
}
