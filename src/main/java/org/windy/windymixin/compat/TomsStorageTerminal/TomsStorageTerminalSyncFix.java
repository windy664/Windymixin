package org.windy.windymixin.compat.TomsStorageTerminal;

import com.tom.storagemod.network.DataPacket;
import com.tom.storagemod.util.IDataReceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.windy.windymixin.Windymixin;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public final class TomsStorageTerminalSyncFix {
    private static final int MAX_PENDING_TICKS = 40;
    private static final Queue<PendingPacket> PENDING_PACKETS = new ArrayDeque<>();

    private TomsStorageTerminalSyncFix() {
    }

    public static void handleClientData(DataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();

            // 1. 如果屏幕已经准备好（IDataReceiver），直接投递
            if (minecraft.gui.screen() instanceof IDataReceiver receiver) {
                receiver.receive(TagValueInput.create(ProblemReporter.DISCARDING, context.player().registryAccess(), packet.tag()));
                return;
            }

            // 2. 治本关键：只要屏幕没准备好，无条件全部缓存！不再判断 containerMenu
            PENDING_PACKETS.add(new PendingPacket(packet.tag().copy(), 0));
            Windymixin.LOGGER.debug("[Tom's Storage] 屏幕未就绪，强制缓存终端包。当前队列积压: {}", PENDING_PACKETS.size());
        });
    }

    public static void clientTick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || PENDING_PACKETS.isEmpty()) {
            return;
        }

        Iterator<PendingPacket> iterator = PENDING_PACKETS.iterator();
        while (iterator.hasNext()) {
            PendingPacket pending = iterator.next();

            // 只要一检测到终端屏幕被打开，立刻按顺序重放所有缓存包
            if (minecraft.gui.screen() instanceof IDataReceiver receiver) {
                receiver.receive(TagValueInput.create(ProblemReporter.DISCARDING, minecraft.player.registryAccess(), pending.tag));
                iterator.remove();
                Windymixin.LOGGER.debug("[Tom's Storage] 屏幕已就绪，成功重放全量/差量同步包。");
            }
            // 超过 2 秒还没打开屏幕，说明是脏数据，直接丢弃防内存泄漏
            else if (pending.ticks >= MAX_PENDING_TICKS) {
                iterator.remove();
                Windymixin.LOGGER.warn("[Tom's Storage] 终端界面超时未创建，缓存包已清理。");
            } else {
                pending.ticks++;
            }
        }
    }

    private static final class PendingPacket {
        private final CompoundTag tag;
        private int ticks;

        private PendingPacket(CompoundTag tag, int ticks) {
            this.tag = tag;
            this.ticks = ticks;
        }
    }
}