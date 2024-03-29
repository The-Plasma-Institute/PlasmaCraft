package net.joelinrome.plasmacraft.item.custom;

import net.joelinrome.plasmacraft.sound.ModSounds;
import net.joelinrome.plasmacraft.util.ModTags;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MetalDetectorItem extends Item {
    public MetalDetectorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide()) {
            BlockPos positionClicked = pContext.getClickedPos();
            Player player = pContext.getPlayer();
            boolean foundBlock = false;

            for (int i = 0; i < positionClicked.getY() + 64; i++) {
                BlockState state = pContext.getLevel().getBlockState(positionClicked.below(i));

                if (isValuableBlock(state)) {
                    outputValuableCoordinates(positionClicked.below(i), player, state.getBlock());
                    foundBlock = true;
                    pContext.getLevel().playSeededSound(null, positionClicked.getX(), positionClicked.getY(), positionClicked.getZ(),
                            ModSounds.METAL_DETECTOR_FOUND_ORE.get(), SoundSource.BLOCKS, 1f, 1f, 0);
                    break;
                }
            }
            if (!foundBlock) {
                player.sendSystemMessage(Component.literal("No valuables found"));
            }
        }
        pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), player -> player.broadcastBreakEvent(player.getUsedItemHand()));
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        /* Can use Component.literal here instead to avoid needing a key but this method is better as you can define
        * the language specific translation within en_us.json
        */
        pTooltipComponents.add(Component.translatable("tooltip.plasmacraft.metal_detector.tooltip"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    private void outputValuableCoordinates(BlockPos blockPos, Player player, Block block) {
        player.sendSystemMessage(Component.literal(String.format("Found %s at (%d, %d, %d)",
                I18n.get(block.getDescriptionId()),
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        )));
    }

    private boolean isValuableBlock(BlockState state) {
        return state.is(ModTags.Blocks.METAL_DETECTOR_VALUABLES);
    }
}
