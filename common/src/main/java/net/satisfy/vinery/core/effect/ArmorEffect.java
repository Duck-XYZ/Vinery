package net.satisfy.vinery.core.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.vinery.core.util.VineryIdentifier;

import java.util.UUID;

public class ArmorEffect extends MobEffect {
    private static final ResourceLocation ARMOR_LOCATION = new VineryIdentifier("armor_effect");
    private static final ResourceLocation ARMOR_TOUGHNESS_LOCATION = new VineryIdentifier("armor_toughness_effect");


    public ArmorEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x56CBFD);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int i) {
        this.attributeModifiers.put(Attributes.ARMOR, new AttributeTemplate(
                ARMOR_LOCATION, (i+1) * 4.0f, AttributeModifier.Operation.ADD_VALUE));
        this.attributeModifiers.put(Attributes.ARMOR_TOUGHNESS, new AttributeTemplate(
                ARMOR_TOUGHNESS_LOCATION, (i + 1) * 2.0f, AttributeModifier.Operation.ADD_VALUE));

        super.addAttributeModifiers(attributeMap, i);
    }
}