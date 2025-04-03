package net.satisfy.vinery.core.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.vinery.core.util.VineryIdentifier;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

public class ImprovedHealthEffect extends MobEffect {
    private static final ResourceLocation MAX_HEALTH_MOD = new VineryIdentifier("improved_health_mod");

    public ImprovedHealthEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x56CBFD);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int i) {
        this.attributeModifiers.put(Attributes.MAX_HEALTH, new AttributeTemplate(
                MAX_HEALTH_MOD, (i + 1) * 2.0f, AttributeModifier.Operation.ADD_VALUE));

        super.addAttributeModifiers(attributeMap, i);
    }
}