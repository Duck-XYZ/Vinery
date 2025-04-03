package net.satisfy.vinery.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class GrapeType implements Comparable<GrapeType>, StringRepresentable {
    private final String id;
    private final boolean lattice;
    private Holder<Item> fruit;
    private Holder<Item> seeds;
    private Holder<Item> bottle;

    public static final MapCodec<GrapeType> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(Codec.STRING.fieldOf("id").forGetter(grape -> grape.id),
                        ItemStack.ITEM_NON_AIR_CODEC.fieldOf("fruit").forGetter(grape -> grape.fruit),
                        ItemStack.ITEM_NON_AIR_CODEC.fieldOf("seeds").forGetter(grape -> grape.seeds),
                        ItemStack.ITEM_NON_AIR_CODEC.fieldOf("bottle").forGetter(grape -> grape.bottle),
                        Codec.BOOL.fieldOf("lattice").forGetter(grape -> grape.lattice))
                .apply(inst, GrapeType::new);
    });

    public GrapeType(String id) {
        this(id, false);
    }

    public GrapeType(String id, boolean lattice) {
        this(id, Holder.direct(Items.AIR), Holder.direct(Items.AIR), Holder.direct(Items.AIR), lattice);
    }

    private GrapeType(String id, Holder<Item> fruit, Holder<Item> seeds, Holder<Item> bottle, boolean lattice) {
        this.id = id;
        this.fruit = fruit;
        this.seeds = seeds;
        this.bottle = bottle;
        this.lattice = lattice;
    }

    @Override
    public @NotNull String getSerializedName() {
        return id;
    }

    public Item getFruit() {
        return this.fruit.value();
    }

    public Item getSeeds() {
        return this.seeds.value();
    }

    public Item getBottle() {
        return bottle.value();
    }

    public boolean isLattice() {
        return lattice;
    }

    public void setItems(Holder<Item> fruit, Holder<Item> seeds, Holder<Item> bottle) {
        this.fruit = fruit;
        this.seeds = seeds;
        this.bottle = bottle;
    }

    @Override
    public int compareTo(@NotNull GrapeType grapeType) {
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrapeType grapeType)) return false;
        return Objects.equals(id, grapeType.id);
    }
}