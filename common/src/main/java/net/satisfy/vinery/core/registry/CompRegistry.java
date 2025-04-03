package net.satisfy.vinery.core.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.satisfy.vinery.core.Vinery;

public class CompRegistry {
    public static final DeferredRegister<DataComponentType<?>> COMPS = DeferredRegister.create(Vinery.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<Integer>> WINE_YEARS = COMPS.register("wine_years", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static void init() {
        COMPS.register();
    }
}
