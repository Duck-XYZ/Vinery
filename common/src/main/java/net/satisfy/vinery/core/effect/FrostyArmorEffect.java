package net.satisfy.vinery.core.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.vinery.core.util.VineryIdentifier;
import org.spongepowered.asm.mixin.injection.At;

public class FrostyArmorEffect extends MobEffect {
    public static final ResourceLocation ARMOR_MOD = new VineryIdentifier("frosty_armor_mod");
    public static final ResourceLocation DAMAGE_MOD = new VineryIdentifier("frosty_damage_mod");
    public static final ResourceLocation MOVEMENT_SPEED_MOD = new VineryIdentifier("frosty_speed_mod");

    public static final double FROST_MULTIPLIER = -0.05D;

    public FrostyArmorEffect() {
        super(MobEffectCategory.NEUTRAL, 0x56CBFD);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int i) {
        this.attributeModifiers.put(Attributes.MOVEMENT_SPEED, new MobEffect.AttributeTemplate(
                FrostyArmorEffect.MOVEMENT_SPEED_MOD, FROST_MULTIPLIER, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        this.attributeModifiers.put(Attributes.ATTACK_DAMAGE, new AttributeTemplate(
                FrostyArmorEffect.DAMAGE_MOD, (i+1) * 2.0f, AttributeModifier.Operation.ADD_VALUE));
        this.attributeModifiers.put(Attributes.ARMOR, new AttributeTemplate(
                ARMOR_MOD, (i+1) * 4.0f, AttributeModifier.Operation.ADD_VALUE));

        super.addAttributeModifiers(attributeMap, i);
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        living.setIsInPowderSnow(true);
        if (amplifier > 0 && living.canFreeze()) {
            living.setTicksFrozen(Math.min(living.getTicksRequiredToFreeze(), living.getTicksFrozen() + amplifier));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}