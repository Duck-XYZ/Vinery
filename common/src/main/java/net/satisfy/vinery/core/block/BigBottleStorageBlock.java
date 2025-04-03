package net.satisfy.vinery.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.vinery.core.registry.StorageTypeRegistry;
import net.satisfy.vinery.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

public class BigBottleStorageBlock extends StorageBlock {

    public BigBottleStorageBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final SoundEvent OPEN_SOUND = SoundEvents.BAMBOO_WOOD_DOOR_OPEN;

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                level.playSound(null, blockPos, OPEN_SOUND, SoundSource.BLOCKS, 0.4f, 0.4f);
                level.setBlock(blockPos, blockState.setValue(OPEN, !blockState.getValue(OPEN)), UPDATE_ALL);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else if (blockState.getValue(OPEN)) {
            return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    public boolean canInsertStack(ItemStack stack) {
        return stack.is(TagRegistry.SMALL_BOTTLE) || stack.is(TagRegistry.LARGE_BOTTLE);
    }

    @Override
    public int size(){
        return 1;
    }

    @Override
    public ResourceLocation type() {
        return StorageTypeRegistry.BIG_BOTTLE;
    }

    @Override
    public Direction[] unAllowedDirections() {
        return new Direction[]{Direction.DOWN, Direction.UP};
    }

    @Override
    public int getSection(Float x, Float y) {
        return 0;
    }
}
