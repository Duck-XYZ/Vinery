package net.satisfy.vinery.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.registry.CompRegistry;
import net.satisfy.vinery.platform.PlatformHelper;

public class WineYears {
	public static final int YEARS_START = 0;
	public static final int MAX_LEVEL = PlatformHelper.getWineMaxLevel();
	public static final int START_DURATION = PlatformHelper.getWineStartDuration();
	public static final int DURATION_PER_YEAR = PlatformHelper.getWineDurationPerYear();
	public static final int DAYS_PER_YEAR = PlatformHelper.getWineDaysPerYear();
	public static final int YEARS_PER_EFFECT_LEVEL = PlatformHelper.getWineYearsPerEffectLevel();
	public static final int MAX_DURATION = PlatformHelper.getWineMaxDuration();

	public static int getYear(Level world) {
		return world != null ? YEARS_START + (int) (world.getDayTime() / 24000 / DAYS_PER_YEAR) : YEARS_START;
	}

	public static int getEffectLevel(ItemStack wine, Level world) {
		return Math.max(0, Math.min(MAX_LEVEL, getWineAge(wine, world) / YEARS_PER_EFFECT_LEVEL));
	}

	public static int getWineAge(ItemStack wine, Level world) {
		if (hasWineYear(wine)) {
			return 0;
		}
		return getYear(world) - getWineYear(wine);
	}

	public static void setWineYear(ItemStack wine, Level world) {
		if (world != null) {
			wine.set(CompRegistry.WINE_YEARS.get(), getYear(world));
		} else {
			wine.set(CompRegistry.WINE_YEARS.get(), YEARS_START);
		}
	}

	public static int getWineYear(ItemStack wine) {
		return wine.getOrDefault(CompRegistry.WINE_YEARS.get(), 0);
	}

	public static int getEffectDuration(ItemStack wine, Level world) {
		int age = getWineAge(wine, world);
		int duration = START_DURATION + (DURATION_PER_YEAR * age);
		return Math.min(duration, MAX_DURATION);
	}


	public static boolean hasWineYear(ItemStack wine) {
		return wine.has(CompRegistry.WINE_YEARS.get());
	}
}

