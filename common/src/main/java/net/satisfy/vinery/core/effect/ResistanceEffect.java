package net.satisfy.vinery.core.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.vinery.core.util.VineryIdentifier;

import java.util.UUID;

public class ResistanceEffect extends MobEffect {
    private static final ResourceLocation KNOCKBACK_MOD = new VineryIdentifier("resistance_knockback_mod");
    private static final ResourceLocation TOUGHNESS_MOD = new VineryIdentifier("resistance_toughness_mod");


    public ResistanceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x56CBFD);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int i) {
        this.attributeModifiers.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeTemplate(
                KNOCKBACK_MOD, (i+1) * 2.0f, AttributeModifier.Operation.ADD_VALUE));
        this.attributeModifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeTemplate(
                TOUGHNESS_MOD, (i+1) * 2.0f, AttributeModifier.Operation.ADD_VALUE));

        super.addAttributeModifiers(attributeMap, i);
    }
}