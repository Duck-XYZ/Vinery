package net.satisfy.vinery.core.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.block.entity.ApplePressBlockEntity;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class ApplePressMashingRecipe implements Recipe<ApplePressBlockEntity> {
    Ingredient input;
    ItemStack output;

    public ApplePressMashingRecipe(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(ApplePressBlockEntity recipeInput, Level level) {
        return input.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(ApplePressBlockEntity recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypesRegistry.APPLE_PRESS_MASHING_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ApplePressMashingRecipe> {
        public static final MapCodec<ApplePressMashingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
                            ItemStack.STRICT_CODEC.fieldOf("output").forGetter(recipe -> recipe.output))
                    .apply(inst, ApplePressMashingRecipe::new);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, ApplePressMashingRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ApplePressMashingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplePressMashingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ApplePressMashingRecipe fromNetwork(RegistryFriendlyByteBuf byteBuf) {
            return new ApplePressMashingRecipe(
                    Ingredient.CONTENTS_STREAM_CODEC.decode(byteBuf),
                    ItemStack.STREAM_CODEC.decode(byteBuf)
            );
        }

        private static void toNetwork(RegistryFriendlyByteBuf byteBuf, ApplePressMashingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(byteBuf, recipe.input);
            ItemStack.STREAM_CODEC.encode(byteBuf, recipe.output);
        }
    }
}
