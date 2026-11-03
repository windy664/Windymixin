<div align="center">

<img src="assets/banner.svg" alt="Windymixin" width="100%">

# Windymixin

**风吟的通用 BUG 修复** —— NeoForge Mixin 注入合集：修各种 Mod 在高版本混合环境下的崩溃与行为异常，附少量配方调整。

![Platform](https://img.shields.io/badge/Platform-NeoForge-62bda8?style=flat-square&logo=minecraft&logoColor=white)
![Minecraft](https://img.shields.io/badge/MC-26.2-8a9a5b?style=flat-square)
![Java](https://img.shields.io/badge/Java-21%2B-f89820?style=flat-square&logo=openjdk&logoColor=white)

[修复列表](#-修复列表) · [构建](#-构建)

</div>

## ✨ 修复列表

| 目标 Mod | Mixin | 修复内容 |
|----------|-------|----------|
| EnderIO | `ConduitBakerMixin` | 导管烘焙行为修正 |
| Integrated Dynamics | `FixChunkUnloadCrashMixin` | 区块卸载崩溃修复 |
| Iris | `TextDisplayRendererMixin` | 文本显示实体渲染异常 |
| JEG | `BallisticMixin` | 弹道行为修正 |
| JEI | `PacketGiveItemStackMixin` 等 | 物品刷取/作弊权限包校验 |
| Mekanism | `BlockMekanismSecurityMixin`、`BlockTransmitterMixin`、`FixObjGeometryMixin` | 安全方块、传输器与模型几何修复 |
| Tom's Storage | `TomsStorageTerminalSyncFix` | 终端同步修复 |

**配方与物品调整**：`JegRecipeFilter`（JEG 配方过滤）、`MaRecipeNerf` / `MaStealthNerf`（潜行削弱）、`GunPartItems` / `ThaumonItems`（物品注册调整），支持 YAML 配置（`Config` + `ConfigYaml`/`LangYaml`）。

## 🔨 构建

```bash
./gradlew build
```

产物为 NeoForge Mod jar（MC 26.2），放进 `mods/` 即可，无需额外配置。
