package net.satisfy.vinery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.vinery.core.registry.EntityTypeRegistry;
import net.satisfy.vinery.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.html.Option;
import java.util.Optional;

public class FlowerPotBlockEntity extends BlockEntity {
    private Item flower;

    public FlowerPotBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FLOWER_POT_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        this.writeFlower(compoundTag, this.flower, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        if (compoundTag.contains("flower")) {
            CompoundTag nbtCompound = compoundTag.getCompound("flower");
            if (!nbtCompound.isEmpty()) {
                Optional<ItemStack> stack = ItemStack.parse(provider, nbtCompound);
                this.flower = stack.map(ItemStack::getItem).orElse(null);
            }
        } else this.flower = null;
    }

    public void writeFlower(CompoundTag nbt, Item flower, HolderLookup.Provider provider) {
        CompoundTag nbtCompound = new CompoundTag();
        if (flower != null) {
            nbt.put("flower", flower.getDefaultInstance().save(provider));
        } else nbt.put("flower", null);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    public void setChanged() {
        if (this.level != null && !this.level.isClientSide()) {
            Packet<ClientGamePacketListener> updatePacket = this.getUpdatePacket();

            for (ServerPlayer player : GeneralUtil.tracking((ServerLevel) this.level, this.getBlockPos())) {
                player.connection.send(updatePacket);
            }
        }

        super.setChanged();
    }
}

