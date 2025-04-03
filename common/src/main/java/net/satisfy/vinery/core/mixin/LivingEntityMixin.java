package net.satisfy.vinery.core.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.vinery.core.registry.MobEffectRegistry;
import net.satisfy.vinery.core.util.WineYears;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow @Final private Map<MobEffect, MobEffectInstance> activeEffects;

	@Shadow protected abstract int increaseAirSupply(int i);

	protected LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Unique
	private boolean hasStatusEffect(MobEffect effect) {
		return activeEffects.containsKey(effect);
	}

	@Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
	private void applyFoodEffects(Level level, ItemStack itemStack, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
		cir.cancel();
		level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), ((LivingEntity)(Object)this).getEatingSound(itemStack), SoundSource.NEUTRAL, 1.0F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.4F);

		//new code
		List<FoodProperties.PossibleEffect> list = foodProperties.effects();
		for (FoodProperties.PossibleEffect effect : list) {
			if (!(((LivingEntity)(Object)this).level().random.nextFloat() < effect.probability())) continue;
			MobEffectInstance statusEffectInstance = new MobEffectInstance(effect.effect());
			statusEffectInstance.amplifier = WineYears.getEffectLevel(itemStack, level);
			if(statusEffectInstance.getEffect().equals(MobEffects.HEAL) || statusEffectInstance.getEffect().equals(MobEffects.HARM)){
				statusEffectInstance.duration = 1;
			}
			((LivingEntity)(Object)this).addEffect(statusEffectInstance);
		}

		itemStack.consume(1, ((LivingEntity)(Object)this));
		this.gameEvent(GameEvent.EAT);
		cir.setReturnValue(itemStack);
	}

//	@Redirect(method = "calculateFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEffect(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"))
//	public MobEffectInstance improvedJumpBoostFall(LivingEntity livingEntity, MobEffect effect) {
//		return livingEntity.hasEffect(MobEffectRegistry.IMPROVED_JUMP_BOOST.get()) ? livingEntity.getEffect(MobEffectRegistry.IMPROVED_JUMP_BOOST.get()) : livingEntity.getEffect(MobEffects.JUMP);
//	}

	@Inject(method = "getJumpBoostPower", at = @At(value = "HEAD"), cancellable = true)
	private void improvedJumpBoost(CallbackInfoReturnable<Float> cir) {
		if (this.hasStatusEffect(MobEffectRegistry.IMPROVED_JUMP_BOOST.get())) {
			cir.setReturnValue((0.1F * (float)(this.activeEffects.get(MobEffectRegistry.IMPROVED_JUMP_BOOST.get()).getAmplifier() + 1)));
		}
	}

	@Inject(method = "onEffectRemoved", at = @At(value = "HEAD"))
	private void onEffectRemoved(MobEffectInstance mobEffectInstance, CallbackInfo ci) {
		if (mobEffectInstance.getEffect().is(MobEffectRegistry.JELLIE)) {
			LivingEntity entity = (LivingEntity)(Object)this;
			entity.setAbsorptionAmount(entity.getAbsorptionAmount() - (float)(4 * (mobEffectInstance.getAmplifier() + 1)));
		}
	}
}