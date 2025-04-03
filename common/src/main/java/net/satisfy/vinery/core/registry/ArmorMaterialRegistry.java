package net.satisfy.vinery.core.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.satisfy.vinery.core.util.VineryIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ArmorMaterialRegistry {
    public static final Holder<ArmorMaterial> WINEMAKER_ARMOR = register("winemaker_armor", (EnumMap) Util.make(new EnumMap(ArmorItem.Type.class), (enumMap) -> {
        enumMap.put(ArmorItem.Type.BOOTS, 1);
        enumMap.put(ArmorItem.Type.LEGGINGS, 2);
        enumMap.put(ArmorItem.Type.CHESTPLATE, 3);
        enumMap.put(ArmorItem.Type.HELMET, 1);
        enumMap.put(ArmorItem.Type.BODY, 3);
    }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> {
        return Ingredient.of(ItemTags.WOOL);
    }, List.of(new ArmorMaterial.Layer(new VineryIdentifier("winemaker"), "", true), new ArmorMaterial.Layer(new VineryIdentifier("winemaker"), "_overlay", false)));

//    public static final ArmorMaterial WINEMAKER_ARMOR = new ArmorMaterial() {
//
//        @Override
//        public int getDefense(ArmorItem.Type type) {
//            return ArmorMaterials.LEATHER.value().getDefense(type);
//        }
//
//        @Override
//        public int enchantmentValue() {
//            return ArmorMaterials.LEATHER.value().enchantmentValue();
//        }
//
//        @Override
//        public Holder<SoundEvent> equipSound() {
//            return ArmorMaterials.LEATHER.value().equipSound();
//        }
//
//        @Override
//        public Supplier<Ingredient> repairIngredient() {
//            return () -> Ingredient.of(ItemTags.WOOL);
//        }
//
//        @Override
//        public float toughness() {
//            return ArmorMaterials.LEATHER.value().toughness();
//        }
//
//        @Override
//        public float knockbackResistance() {
//            return ArmorMaterials.LEATHER.value().knockbackResistance();
//        }
//    };

    private static Holder<ArmorMaterial> register(String string, EnumMap<ArmorItem.Type, Integer> enumMap, int i, Holder<SoundEvent> holder, float f, float g, Supplier<Ingredient> supplier, List<ArmorMaterial.Layer> list) {
        EnumMap<ArmorItem.Type, Integer> enumMap2 = new EnumMap(ArmorItem.Type.class);
        ArmorItem.Type[] var9 = ArmorItem.Type.values();
        int var10 = var9.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            ArmorItem.Type type = var9[var11];
            enumMap2.put(type, (Integer)enumMap.get(type));
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.withDefaultNamespace(string), new ArmorMaterial(enumMap2, i, holder, supplier, list, f, g));
    }
}
