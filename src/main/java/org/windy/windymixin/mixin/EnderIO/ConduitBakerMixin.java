package org.windy.windymixin.mixin.EnderIO;

import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBaker;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.operation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.concurrent.ConcurrentModificationException;
import java.util.function.Function;

/**
 * 修复EnderIO ConduitBaker与Sodium多线程的冲突。
 */
@Mixin(value = ConduitBaker.class, priority = 1100)
public class ConduitBakerMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/HashMap;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"
            )
    )
    private Object windymixin$safeComputeIfAbsent(
            HashMap<Object, Object> map,
            Object key,
            Function<Object, Object> mappingFunction,
            Operation<Object> original
    ) {
        synchronized (map) {
            try {
                return original.call(map, key, mappingFunction);
            } catch (ConcurrentModificationException e) {
                return null;
            }
        }
    }
}
