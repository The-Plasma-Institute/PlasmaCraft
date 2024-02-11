package net.joelinrome.realisticfusionreactors.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.joelinrome.realisticfusionreactors.RealisticFusionReactors;
import net.joelinrome.realisticfusionreactors.repice.GemPolishingRecipe;
import net.joelinrome.realisticfusionreactors.screen.GemPolishingStationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIRealisticFusionReactorsPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(RealisticFusionReactors.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new GemPolishingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<GemPolishingRecipe> polishingRecipes = recipeManager.getAllRecipesFor(GemPolishingRecipe.Type.INSTANCE); // Gets all recipes that can be found
        registration.addRecipes(GemPolishingCategory.GEM_POLISHING_RECIPE_TYPE, polishingRecipes); // Creates links between categories and recipes
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Indicates the position on the screen to click to display all recipes, in this case in the middle of the arrow
        registration.addRecipeClickArea(GemPolishingStationScreen.class, 80, 30, 20, 30, GemPolishingCategory.GEM_POLISHING_RECIPE_TYPE);
    }
}
