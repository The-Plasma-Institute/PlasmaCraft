package net.joelinrome.plasmacraft.repice;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.joelinrome.plasmacraft.PlasmaCraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class DeuteriumExtractorRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;

    public DeuteriumExtractorRecipe(ResourceLocation id, NonNullList<Ingredient> inputItems, ItemStack output) {
        this.inputItems = inputItems;
        this.output = output;
        this.id = id;
    }

    /**
     * Checks that the item from the simpleContainer matches the available recipe item at index 0
     * @param simpleContainer item in slot
     * @param level world
     * @return true if simpleContainer item at index 0 matches recipe item  at index 0
     */
    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        // Needed otherwise the server crashes
        if (level.isClientSide()) {
            return false;
        }

        return inputItems.get(0).test(simpleContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<DeuteriumExtractorRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "deuterium_extractor";
    }

    public static class Serializer implements RecipeSerializer<DeuteriumExtractorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(PlasmaCraft.MOD_ID,"gem_empowering");

        /**
         * Reads in a JSON file array of recipes and generates a GemPolishingRecipe
         * @param id file location
         * @param json incoming json
         * @return GemPolishingRecipe
         */
        @Override
        public DeuteriumExtractorRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY); // Needs to match the number of ingredients for the recipe

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new DeuteriumExtractorRecipe(id, inputs, output);
        }

        // Below methods sync server to client and vice versa
        // THEY BOTH NEED TO FOLLOW THE SAME ORDER OF OPERATIONS

        @Override
        public DeuteriumExtractorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new DeuteriumExtractorRecipe(id, inputs, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, DeuteriumExtractorRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }

            buf.writeItemStack(recipe.getResultItem(null), false);
        }
    }
}
