package net.satisfy.vinery.core.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.vinery.core.util.GrapeType;
import net.satisfy.vinery.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@SuppressWarnings("deprecation")
public class GrapeBush extends BushBlock implements BonemealableBlock {
    public static final MapCodec<GrapeBush> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(propertiesCodec(),
                GrapeType.CODEC.fieldOf("type").forGetter(block -> block.type))
                .apply(inst, GrapeBush::new);
    });
    public static final IntegerProperty AGE;
    private static final VoxelShape SHAPE;

    public final GrapeType type;

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    public GrapeBush(Properties settings, GrapeType type) {
        super(settings);
        this.type = type;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(this.getGrapeType().getSeeds());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        int i = state.getValue(AGE);
        boolean bl = i == 3;
        if (!bl && itemStack.is(Items.BONE_MEAL)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (i > 1) {
            int x = level.random.nextInt(2);
            popResource(level, blockPos, new ItemStack(getGrape().getItem(), x + (bl ? 1 : 0)));
            level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            level.setBlock(blockPos, state.setValue(AGE, 1), 2);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        double growthChance = PlatformHelper.getGrapeGrowthChance();
        if (age < 3 && random.nextDouble() < growthChance && canGrowPlace(world, pos, state)) {
            BlockState newState = state.setValue(AGE, age + 1);
            world.setBlock(pos, newState, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrowPlace(LevelReader world, BlockPos blockPos, BlockState blockState) {
        return world.getRawBrightness(blockPos, 0) > 9;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader world, BlockPos blockPos) {
        return canGrowPlace(world, blockPos, blockState) && this.mayPlaceOn(world.getBlockState(blockPos.below()), world, blockPos);
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.isSolidRender(world, pos);
    }

    public GrapeType getGrapeType() {
        return this.type;
    }

    @Override
    public Type getType() {
        return Type.GROWER;
    }

    public ItemStack getGrape() {
        return new ItemStack(this.getGrapeType().getFruit());
    }


    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.getValue(AGE) + 1);
        world.setBlock(pos, state.setValue(AGE, i), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    static {
        AGE = BlockStateProperties.AGE_3;
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }

    public static class SavannaGrapeBush extends GrapeBush {

        public SavannaGrapeBush(Properties settings, GrapeType type) {
            super(settings, type);
        }

        @Override
        public boolean canGrowPlace(LevelReader world, BlockPos blockPos, BlockState blockState) {
            return world.getRawBrightness(blockPos, 0) >= 14;
        }
    }

    public static class TaigaGrapeBush extends GrapeBush {

        public TaigaGrapeBush(Properties settings, GrapeType type) {
            super(settings, type);
        }

        @Override
        public boolean canGrowPlace(LevelReader world, BlockPos blockPos, BlockState blockState) {
            if (world.getRawBrightness(blockPos, 0) <= 4) {
                return false;
            }
            int size = 4;
            Iterator<BlockPos> var2 = BlockPos.betweenClosed(blockPos.offset(-size, -2, -size), blockPos.offset(size, 1, size)).iterator();

            BlockPos pos;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                pos = var2.next();
            } while (!(world.getBlockState(pos).getBlock() == Blocks.PODZOL || world.getBlockState(pos).getBlock() == Blocks.COARSE_DIRT || world.getBlockState(pos).getBlock() == Blocks.GRASS_BLOCK));

            return true;
        }

        @Override
        protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
            return false;
        }
    }
}
