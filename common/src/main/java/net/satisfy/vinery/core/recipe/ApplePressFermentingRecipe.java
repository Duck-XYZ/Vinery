package net.satisfy.vinery.core.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.block.entity.ApplePressBlockEntity;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class ApplePressFermentingRecipe implements Recipe<ApplePressBlockEntity> {
    final Ingredient input;
    ItemStack output;
    boolean requiresBottle;

    public ApplePressFermentingRecipe(Ingredient input, ItemStack output, boolean requiresBottle) {
        this.input = input;
        this.output = output;
        this.requiresBottle = requiresBottle;
    }

    public boolean requiresBottle() {
        return requiresBottle;
    }

    @Override
    public boolean matches(ApplePressBlockEntity inventory, Level world) {
        return input.test(inventory.getItem(1));
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
        return RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypesRegistry.APPLE_PRESS_FERMENTING_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ApplePressFermentingRecipe> {
        public static final MapCodec<ApplePressFermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(isntance -> {
            return isntance.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
                            ItemStack.STRICT_CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                            Codec.BOOL.optionalFieldOf("wine_bottle_required", false).forGetter(recipe -> recipe.requiresBottle))
                    .apply(isntance, ApplePressFermentingRecipe::new);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, ApplePressFermentingRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<ApplePressFermentingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ApplePressFermentingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static ApplePressFermentingRecipe fromNetwork(RegistryFriendlyByteBuf byteBuf) {
            Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(byteBuf);
            ItemStack output = ItemStack.STREAM_CODEC.decode(byteBuf);
            boolean requiresBottle = byteBuf.readBoolean();
            return new ApplePressFermentingRecipe(input, output, requiresBottle);
        }

        private static void toNetwork(RegistryFriendlyByteBuf byteBuf, ApplePressFermentingRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(byteBuf, recipe.input);
            ItemStack.STREAM_CODEC.encode(byteBuf, recipe.output);
            byteBuf.writeBoolean(recipe.requiresBottle);
        }
    }
}
