package net.joelinrome.realisticfusionreactors.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.joelinrome.realisticfusionreactors.RealisticFusionReactors;
import net.joelinrome.realisticfusionreactors.block.ModBlocks;
import net.joelinrome.realisticfusionreactors.item.ModItems;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = RealisticFusionReactors.MOD_ID)
public class ModEvents {
}
