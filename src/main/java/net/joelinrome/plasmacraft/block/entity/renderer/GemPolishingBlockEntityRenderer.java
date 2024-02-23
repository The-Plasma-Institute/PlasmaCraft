package net.joelinrome.plasmacraft.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joelinrome.plasmacraft.block.entity.GemPolishingStationBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class GemPolishingBlockEntityRenderer implements BlockEntityRenderer<GemPolishingStationBlockEntity> {
    public GemPolishingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(GemPolishingStationBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = blockEntity.getRenderStack();

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.75f, 0.5f); // The item is going to be rendered in the middle of the block in X and Z and 75% up the block in Y
        poseStack.scale(0.35f, 0.35f, 0.35f);
        poseStack.mulPose(Axis.XP.rotationDegrees(270));

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, getLightLevel(blockEntity.getLevel(), blockEntity.getBlockPos()),
                OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, blockEntity.getLevel(), 1);

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
