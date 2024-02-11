package net.joelinrome.realisticfusionreactors.event;

import net.joelinrome.realisticfusionreactors.RealisticFusionReactors;
import net.joelinrome.realisticfusionreactors.block.entity.ModBlockEntities;
import net.joelinrome.realisticfusionreactors.block.entity.renderer.GemPolishingBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealisticFusionReactors.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.GEM_POLISHING_BE.get(), GemPolishingBlockEntityRenderer::new);
    }
}
