package org.windy.windymixin.mixin.IndustrialUpgrade;

import com.denfop.api.energy.EnergyForgeSink;
import com.denfop.api.energy.EnergyNetGlobal;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Map;

@Mixin(EnergyForgeSink.class)
public abstract class EnergyForgeSinkMixin implements IEnergyStorage {

    @Unique
    private Map<Direction, IEnergyStorage> windymixin$storages;

    @Inject(method = "<init>", at = @org.spongepowered.asm.mixin.injection.At("RETURN"))
    private void windymixin$init(CallbackInfo ci) {
        try {
            // 注意：这里建议用 getSuperclass()，保证拿到父类的成员而不是当前mixin类
            Field field = this.getClass().getSuperclass().getDeclaredField("storages");
            field.setAccessible(true);
            windymixin$storages = (Map<Direction, IEnergyStorage>) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access storages field", e);
        }
    }

    // --- 防NPE ---

    @Overwrite(remap = false)
    public boolean acceptsEnergyFrom(com.denfop.api.energy.IEnergyEmitter var1, Direction var2) {
        IEnergyStorage storage = windymixin$storages.get(var2);
        return storage != null && storage.canReceive();
    }

    @Overwrite(remap = false)
    public double getDemandedEnergy(Direction direction) {
        IEnergyStorage storage = windymixin$storages.get(direction);
        if (storage == null || !storage.canReceive()) return 0.0D;
        return (double) storage.receiveEnergy(Integer.MAX_VALUE, true) / 4.0D;
    }

    @Overwrite(remap = false)
    public int getSinkTier(Direction direction) {
        IEnergyStorage storage = windymixin$storages.get(direction);
        if (storage == null) return 0;
        try {
            double energy = storage.receiveEnergy(Integer.MAX_VALUE, true);
            return EnergyNetGlobal.instance.getTierFromPower(energy / 4.0F);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Overwrite(remap = false)
    public void receiveEnergy(Direction direction, double var2) {
        IEnergyStorage storage = windymixin$storages.get(direction);
        if (storage != null) {
            storage.receiveEnergy((int) (var2 * 4.0D), false);
        }
    }

    @Overwrite(remap = false)
    public void receiveEnergy(double var2) {
        // 保持空实现
    }

    // --- Forge Energy 单向实现 ---
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int total = 0;
        for (IEnergyStorage storage : windymixin$storages.values()) {
            if (storage != null && storage.canReceive()) {
                total += storage.receiveEnergy(maxReceive, simulate);
            }
        }
        return total;
    }

    @Override
    public boolean canReceive() {
        for (IEnergyStorage storage : windymixin$storages.values()) {
            if (storage != null && storage.canReceive()) return true;
        }
        return false;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public int getEnergyStored() {
        int sum = 0;
        for (IEnergyStorage storage : windymixin$storages.values()) {
            if (storage != null) sum += storage.getEnergyStored();
        }
        return sum;
    }

    @Override
    public int getMaxEnergyStored() {
        int sum = 0;
        for (IEnergyStorage storage : windymixin$storages.values()) {
            if (storage != null) sum += storage.getMaxEnergyStored();
        }
        return sum;
    }
}