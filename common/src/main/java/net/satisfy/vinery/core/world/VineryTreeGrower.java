package net.satisfy.vinery.core.world;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VineryTreeGrower extends TreeGrower {
    private final ResourceKey<ConfiguredFeature<?, ?>> flower;
    private final ResourceKey<ConfiguredFeature<?, ?>> flower2;

    public VineryTreeGrower(String string, ResourceKey<ConfiguredFeature<?, ?>> flower1, ResourceKey<ConfiguredFeature<?, ?>> flower2) {
        super(string, Optional.empty(), Optional.empty(), Optional.empty());
        this.flower = flower1;
        this.flower2 = flower2;
    }

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomSource, boolean bl) {
        if (randomSource.nextBoolean()) return this.flower;
        return this.flower2;
    }
}
