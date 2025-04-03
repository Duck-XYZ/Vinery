package net.satisfy.vinery.core.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.core.block.entity.FermentationBarrelBlockEntity;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.registry.RecipeTypesRegistry;
import org.jetbrains.annotations.NotNull;

public class FermentationBarrelRecipe implements Recipe<FermentationBarrelBlockEntity> {
    NonNullList<Ingredient> inputs;
    Juice juice;
    String juiceType;
    int juiceAmount;
    ItemStack output;
    boolean wineBottleRequired;

    public FermentationBarrelRecipe(NonNullList<Ingredient> inputs, Juice juice, ItemStack output, boolean wineBottleRequired) {
        this.inputs = inputs;
        this.juice = juice;
        this.output = output;

        this.wineBottleRequired = wineBottleRequired;
    }

    public String getJuiceType() {
        return juice.type();
    }

    public int getJuiceAmount() {
        return juice.amount();
    }

    public boolean isWineBottleRequired() {
        return wineBottleRequired;
    }

    @Override
    public boolean matches(FermentationBarrelBlockEntity blockEntity, Level world) {
        if (this.juice.amount() > 0) {
            if (blockEntity.getFluidLevel() < this.juice.amount()) {
                return false;
            }

            if (!this.juiceType.equals(blockEntity.getJuiceType())) {
                return false;
            }
        }

        if (this.wineBottleRequired) {
            ItemStack wineBottle = blockEntity.getItem(FermentationBarrelBlockEntity.WINE_BOTTLE_SLOT);
            if (wineBottle.isEmpty() || !wineBottle.is(ObjectRegistry.WINE_BOTTLE.get())) {
                return false;
            }
        }

        StackedContents recipeMatcher = new StackedContents();
        int matchingStacks = 0;

        for (int i = 1; i < 4; ++i) {
            ItemStack itemStack = blockEntity.getItem(i);
            if (!itemStack.isEmpty()) {
                ++matchingStacks;
                recipeMatcher.accountStack(itemStack, 1);
            }
        }

        return matchingStacks == this.inputs.size() && recipeMatcher.canCraft(this, null);
    }

    @Override
    public ItemStack assemble(FermentationBarrelBlockEntity recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
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
        return RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<FermentationBarrelRecipe> {
        private static final MapCodec<FermentationBarrelRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(
                    Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap((list) -> {
                        Ingredient[] ingredients = (Ingredient[])list.stream().filter((ingredient) -> !ingredient.isEmpty())
                                .toArray(Ingredient[]::new);

                        if (ingredients.length == 0) {
                            return DataResult.error(() -> "No ingredients for shapeless recipe");
                        } else {
                            return ingredients.length > 4 ? DataResult.error(() -> "Too many ingredients for shapeless recipe")
                                    : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }
                    }, DataResult::success).forGetter(recipe -> recipe.inputs),
                    Juice.CODEC.fieldOf("juice").forGetter(recipe -> recipe.juice),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                    Codec.BOOL.optionalFieldOf("wine_bottle_required", false).forGetter(recipe -> recipe.wineBottleRequired)
            ).apply(instance, FermentationBarrelRecipe::new);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentationBarrelRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public MapCodec<FermentationBarrelRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FermentationBarrelRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static @NotNull FermentationBarrelRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            int ingredientCount = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            ingredients.replaceAll(ingredient -> (Ingredient) Ingredient.CONTENTS_STREAM_CODEC.decode(buf));

            String juiceType = buf.readUtf();
            int juiceAmount = buf.readVarInt();
            boolean wineBottleRequired = buf.readBoolean();
            ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
            return new FermentationBarrelRecipe(ingredients, new Juice(juiceType, juiceAmount), result, wineBottleRequired);
        }

        public static void toNetwork(RegistryFriendlyByteBuf buf, FermentationBarrelRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());
            for (Ingredient ingredient : recipe.inputs) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
            }
            buf.writeUtf(recipe.juiceType);
            buf.writeVarInt(recipe.juiceAmount);
            buf.writeBoolean(recipe.wineBottleRequired);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
        }

    }

    public record Juice(String type, int amount) {

        public static final Codec<Juice> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.STRING.fieldOf("type").forGetter(Juice::type),
                            Codec.INT.optionalFieldOf("amount", 10).forGetter(Juice::amount))
                    .apply(instance, Juice::new);
        });
    }
}