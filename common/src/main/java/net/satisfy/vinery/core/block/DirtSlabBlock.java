package net.satisfy.vinery.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class DirtSlabBlock extends SlabBlock {
    public DirtSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (heldItem.is(ItemTags.SHOVELS)) {
            if (!level.isClientSide) {
                BlockState pathState = ObjectRegistry.DIRT_PATH_SLAB.get().defaultBlockState()
                        .setValue(TYPE, blockState.getValue(TYPE))
                        .setValue(WATERLOGGED, blockState.getValue(WATERLOGGED));

                level.setBlock(blockPos, pathState, Block.UPDATE_ALL);
                level.playSound(null, blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (!player.isCreative()) {
                    heldItem.hurtAndBreak(1, player,
                            interactionHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
