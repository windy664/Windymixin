package org.windy.windymixin.mixin.Titanium;

import com.hrznstudio.titanium.reward.Reward;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mixin(Reward.class)
public class RewardMixin {

    /**
     * 禁止联网获取玩家 UUID 列表，直接返回空列表。
     */
    @Overwrite
    private static List<UUID> getPlayers(URL url) {
        System.out.println("[Mixin] 已阻止 Reward 从网络加载 UUID 数据：" + url);
        return Collections.emptyList();  // 你也可以返回默认的UUID列表
    }
}