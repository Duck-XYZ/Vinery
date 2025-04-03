package net.satisfy.vinery.core.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.block.entity.StorageBlockEntity;
import net.satisfy.vinery.core.registry.CompRegistry;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.util.GeneralUtil;
import net.satisfy.vinery.core.util.WineYears;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class DrinkBlockItem extends BlockItem {
    private final int baseDuration;
    private final boolean scaleDurationWithAge;
    private final FoodProperties foodProps;

    public DrinkBlockItem(Block block, Properties settings, int baseDuration, boolean scaleDurationWithAge, FoodProperties foodProps) {
        super(block, settings);
        this.foodProps = foodProps;
        this.baseDuration = baseDuration;
        this.scaleDurationWithAge = scaleDurationWithAge;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        if (!Objects.requireNonNull(context.getPlayer()).isCrouching()) {
            return null;
        }
        BlockState blockState = this.getBlock().getStateForPlacement(context);
        return blockState != null && this.canPlace(context, blockState) ? blockState : null;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        if(level.getBlockEntity(blockPos) instanceof StorageBlockEntity wineEntity){
            wineEntity.setStack(0, itemStack.copyWithCount(1));
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        List<FoodProperties.PossibleEffect> effects = foodProps != null ? foodProps.effects() : Lists.newArrayList();
        if (effects.isEmpty()) {
            list.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            for (FoodProperties.PossibleEffect possibleEffect : effects) {
                MobEffectInstance effectInstance = possibleEffect.effect();
                Holder<MobEffect> effect = effectInstance.getEffect();
                String effectName = effect.value().getDisplayName().getString();
                int amplifier = Math.max(0, WineYears.getEffectLevel(itemStack, Minecraft.getInstance().level));
                String amplifierRoman = amplifier > 0 ? " " + toRoman(amplifier) : "";
                int durationTicks = scaleDurationWithAge ? WineYears.getEffectDuration(itemStack, Minecraft.getInstance().level) : baseDuration;
                durationTicks = Math.max(0, durationTicks);
                String formattedDuration = formatDuration(durationTicks);
                String tooltipText = effectName + amplifierRoman + " (" + formattedDuration + ")";
                list.add(Component.literal(tooltipText).withStyle(effect.value().getCategory().getTooltipFormatting()));
            }
        }
        list.add(Component.empty());

        int age = Math.max(0, WineYears.getWineAge(itemStack, Minecraft.getInstance().level));
        list.add(Component.translatable("tooltip.vinery.age", age).withStyle(ChatFormatting.WHITE));
        list.add(Component.empty());
        int yearsToNextUpgrade = WineYears.YEARS_PER_EFFECT_LEVEL - (age % WineYears.YEARS_PER_EFFECT_LEVEL);
        int daysToNextUpgrade = Math.max(0, yearsToNextUpgrade * WineYears.DAYS_PER_YEAR);
        list.add(Component.translatable("tooltip.vinery.next_upgrade", daysToNextUpgrade)
                .withStyle(style -> style.withColor(TextColor.fromRgb(0x93c47d))));
    }

    @Override
    @SuppressWarnings("unused")
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide) {
            int age = Math.max(0, WineYears.getWineAge(itemStack, level));
            int duration = Math.max(0, scaleDurationWithAge ? WineYears.getEffectDuration(itemStack, level) : baseDuration);
            int amplifier = Math.max(0, WineYears.getEffectLevel(itemStack, level));
            List<FoodProperties.PossibleEffect> effects = foodProps.effects();
            for (FoodProperties.PossibleEffect possibleEffect : effects) {
                Holder<MobEffect> effect = possibleEffect.effect().getEffect();
                livingEntity.addEffect(new MobEffectInstance(effect, duration, amplifier));
            }
        }
        itemStack.shrink(1);
        return GeneralUtil.convertStackAfterFinishUsing(livingEntity, itemStack, ObjectRegistry.WINE_BOTTLE.get(), this);
    }

    private String formatDuration(int ticks) {
        int totalSeconds = Math.max(0, ticks) / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        return ItemUtils.startUsingInstantly(level, player, interactionHand);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedBy(stack, world, player);
        WineYears.setWineYear(stack, world);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world != null && WineYears.hasWineYear(stack)) {
            WineYears.setWineYear(stack, world);
        }
    }

    private String toRoman(int number) {
        return switch (number) {
            case 0 -> "I";
            case 1 -> "II";
            case 2 -> "III";
            case 3 -> "IV";
            case 4 -> "V";
            case 5 -> "VI";
            case 6 -> "VII";
            case 7 -> "VIII";
            case 8 -> "IX";
            case 9 -> "X";
            default -> String.valueOf(number);
        };
    }

//    @PlatformOnly(PlatformOnly.FORGE)
//    public CompoundTag getShareTag(ItemStack stack) {
//        CompoundTag tag = new CompoundTag();
//        if (stack.has(CompRegistry.WINE_YEARS)) {
//            tag.putInt("Year", stack.getTag().getInt("Year"));
//        }
//        return tag;
//    }
//
//    @PlatformOnly(PlatformOnly.FORGE)
//    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
//        if (nbt != null && nbt.contains("Year")) {
//            stack.getOrCreateTag().putInt("Year", nbt.getInt("Year"));
//        }
//    }
}
