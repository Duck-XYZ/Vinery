package net.satisfy.vinery.core.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.vinery.core.util.VineryIdentifier;

import java.util.UUID;

public class LuckEffect extends MobEffect {
    private static final ResourceLocation LUCK_MOD = new VineryIdentifier("luck_effect_mod");

    public LuckEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x56CBFD);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int i) {
        this.attributeModifiers.put(Attributes.LUCK, new AttributeTemplate(
                LUCK_MOD, (i+1) * 2.0f, AttributeModifier.Operation.ADD_VALUE));

        super.addAttributeModifiers(attributeMap, i);
    }
}