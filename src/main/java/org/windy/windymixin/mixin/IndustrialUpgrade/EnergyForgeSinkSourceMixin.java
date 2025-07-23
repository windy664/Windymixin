package org.windy.windymixin.mixin.IndustrialUpgrade;

import com.denfop.api.energy.EnergyForgeSinkSource;
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

/**
 * 防NPE + Forge Energy 双向兼容，兼容反射获取 storages
 */
@Mixin(EnergyForgeSinkSource.class)
public abstract class EnergyForgeSinkSourceMixin implements IEnergyStorage {

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

    // ==== 防NPE Overwrite（IC2/EF接口安全） ====

    @Overwrite(remap = false)
    public double canExtractEnergy(Direction direction) {
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(direction);
        if (storage == null || !storage.canExtract()) return 0.0D;
        return (double) storage.extractEnergy(Integer.MAX_VALUE, true) / 4.0D;
    }

    @Overwrite(remap = false)
    public double canExtractEnergy() {
        int amount = 0;
        if (windymixin$storages != null) {
            for (IEnergyStorage storage : windymixin$storages.values()) {
                if (storage != null && storage.canExtract()) {
                    amount += storage.extractEnergy(Integer.MAX_VALUE, true) / 4;
                }
            }
        }
        return (double) amount;
    }

    @Overwrite(remap = false)
    public int getSourceTier(Direction direction) {
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(direction);
        if (storage == null) return 0;
        try {
            double energy = storage.extractEnergy(Integer.MAX_VALUE, true);
            return EnergyNetGlobal.instance.getTierFromPower(energy / 4.0F);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Overwrite(remap = false)
    public void extractEnergy(Direction direction, double var1) {
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(direction);
        if (storage != null) {
            storage.extractEnergy((int) (var1 * 4.0D), false);
        }
    }

    @Overwrite(remap = false)
    public void extractEnergy(double var1) {
        // 保持空实现
    }

    @Overwrite(remap = false)
    public int getSourceTier() {
        return 1;
    }

    @Overwrite(remap = false)
    public boolean emitsEnergyTo(com.denfop.api.energy.IEnergyAcceptor var1, Direction var2) {
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(var2);
        return storage != null && storage.canExtract();
    }

    @Overwrite(remap = false)
    public boolean acceptsEnergyFrom(com.denfop.api.energy.IEnergyEmitter var1, Direction var2) {
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(var2.getOpposite());
        return storage != null && storage.canReceive();
    }

    @Overwrite(remap = false)
    public double getDemandedEnergy(Direction direction) {
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(direction);
        if (storage == null || !storage.canReceive()) return 0.0D;
        return (double) storage.receiveEnergy(Integer.MAX_VALUE, true) / 4.0D;
    }

    @Overwrite(remap = false)
    public int getSinkTier(Direction direction) {
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(direction);
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
        IEnergyStorage storage = windymixin$storages == null ? null : windymixin$storages.get(direction);
        if (storage != null) {
            storage.receiveEnergy((int) (var2 * 4.0D), false);
        }
    }

    @Overwrite(remap = false)
    public void receiveEnergy(double var2) {
        // 保持空实现
    }

    @Overwrite(remap = false)
    public int getSinkTier() {
        return 1;
    }

    // ==== Forge Energy IEnergyStorage接口 双向实现 ====

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int total = 0;
        if (windymixin$storages != null) {
            for (IEnergyStorage storage : windymixin$storages.values()) {
                if (storage != null && storage.canReceive()) {
                    total += storage.receiveEnergy(maxReceive, simulate);
                }
            }
        }
        return total;
    }

    @Override
    public boolean canReceive() {
        if (windymixin$storages != null) {
            for (IEnergyStorage storage : windymixin$storages.values()) {
                if (storage != null && storage.canReceive()) return true;
            }
        }
        return false;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int total = 0;
        if (windymixin$storages != null) {
            for (IEnergyStorage storage : windymixin$storages.values()) {
                if (storage != null && storage.canExtract()) {
                    total += storage.extractEnergy(maxExtract, simulate);
                }
            }
        }
        return total;
    }

    @Override
    public boolean canExtract() {
        if (windymixin$storages != null) {
            for (IEnergyStorage storage : windymixin$storages.values()) {
                if (storage != null && storage.canExtract()) return true;
            }
        }
        return false;
    }

    @Override
    public int getEnergyStored() {
        int sum = 0;
        if (windymixin$storages != null) {
            for (IEnergyStorage storage : windymixin$storages.values()) {
                if (storage != null) sum += storage.getEnergyStored();
            }
        }
        return sum;
    }

    @Override
    public int getMaxEnergyStored() {
        int sum = 0;
        if (windymixin$storages != null) {
            for (IEnergyStorage storage : windymixin$storages.values()) {
                if (storage != null) sum += storage.getMaxEnergyStored();
            }
        }
        return sum;
    }
}