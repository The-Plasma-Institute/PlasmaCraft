package net.joelinrome.plasmacraft.item;

import net.joelinrome.plasmacraft.PlasmaCraft;
import net.joelinrome.plasmacraft.util.ModTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class ModToolTiers {
    public static final Tier SAPPHIRE = TierSortingRegistry.registerTier(
            // Netherite = lvl 4
            new ForgeTier(5, 1500, 5, 4f, 25,
                    ModTags.Blocks.NEEDS_SAPPHIRE_TOOL, () -> Ingredient.of(ModItems.SAPPHIRE.get())),
            new ResourceLocation(PlasmaCraft.MOD_ID, "sapphire"),
            // The new tier SAPPHIRE is considered higher than the tier specified below
            List.of(Tiers.NETHERITE), List.of()
    );
}
