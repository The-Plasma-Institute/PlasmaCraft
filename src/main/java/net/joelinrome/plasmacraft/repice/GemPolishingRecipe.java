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
import org.jetbrains.annotations.Nullable;

public class GemPolishingRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems; // Items read in from recipes
    private final ItemStack output;
    private final ResourceLocation id;

    public GemPolishingRecipe(NonNullList<Ingredient> inputItems, ItemStack output, ResourceLocation id) {
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
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<GemPolishingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "gem_polishing";
    }

    public static class Serializer implements RecipeSerializer<GemPolishingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(PlasmaCraft.MOD_ID, "gem_polishing");

        /**
         * Reads in a JSON file array of recipes and generates a GemPolishingRecipe
         * @param resourceLocation file location
         * @param jsonObject incoming json
         * @return GemPolishingRecipe
         */
        @Override
        public GemPolishingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY); // Defines at least 1 recipe should exist

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));

            }

            return new GemPolishingRecipe(inputs, output, resourceLocation);
        }

        // Below methods sync server to client and vice versa
        // THEY BOTH NEED TO FOLLOW THE SAME ORDER OF OPERATIONS

        @Override
        public @Nullable GemPolishingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack output = buffer.readItem();
            return new GemPolishingRecipe(inputs, output, resourceLocation);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, GemPolishingRecipe recipe) {
            buffer.writeInt(recipe.inputItems.size());

            for (Ingredient ingredient: recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.getResultItem(null), false);
        }
    }
}
