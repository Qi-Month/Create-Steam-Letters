# Create: Letter's in Steam

机械动力 (Create) 的蒸汽附属模组. 以水与燃料生产蒸汽, 蒸汽转换为应力与加工能力.

生产链为: 燃料 -> 热量 -> 蒸汽 -> 动力 / 加工. 所有设备均为普通方块, 接入 Create 的应力网络, 传送带, 漏斗, 工作盆,
护目镜信息与 Ponder 教学.

| 项目       | 说明                                        |
|------------|---------------------------------------------|
| 模组 ID    | `steam_letters`                             |
| 环境       | Minecraft 1.20.1 / Forge 47.x / Create 6.0+ |
| 许可证     | GNU LGPL 3.0                                |
| 版本与依赖 | 见 [`gradle.properties`](gradle.properties) |

## 文档

| 页面                                                                                | 内容                                     |
|-------------------------------------------------------------------------------------|------------------------------------------|
| [KubeJS 集成](https://github.com/Qi-Month/Create-Steam-Letters/wiki/KubeJS)         | 以脚本注册蒸汽设备, 贴图, 数值, 燃料配方 |
| [KubeJS API 参考](https://github.com/Qi-Month/Create-Steam-Letters/wiki/KubeJS-API) | 类型, 方法, 参数, 贴图与标签清单         |
| [Java 集成](https://github.com/Qi-Month/Create-Steam-Letters/wiki/Java)             | 以 Java 接入蒸汽存储, 并行加工与热源     |
| [Wiki 首页](https://github.com/Qi-Month/Create-Steam-Letters/wiki)                  | 文档导航与入门顺序                       |

---

## 一. 内容概览

表格按职能分类, 每行对应一组设备. 具体方块见源码列.

| 分类       | 内容                                                                 | 源码                                                                                                                                                                                      |
|------------|----------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 蒸汽生产   | 锅炉, 太阳能锅炉: 水加热为蒸汽, 热量来自下方燃烧室或自然光           | [`SlBlocks`](src/main/java/dev/celestiacraft/steam_letters/common/register/SlBlocks.java)                                                                                                 |
| 燃料       | 固体燃烧室, 流体燃烧室: 为锅炉提供热量                               | [`SlRecipeTypes`](src/main/java/dev/celestiacraft/steam_letters/common/register/SlRecipeTypes.java)                                                                                         |
| 动力       | 蒸汽飞轮与飞轮引擎: 蒸汽转换为转速与应力输出                         | [`common/block/engine/`](src/main/java/dev/celestiacraft/steam_letters/common/block/engine/)                                                                                              |
| 加工       | 蒸汽锻锤, 蒸汽搅拌器, 蒸汽锯, 高级注液器: 蒸汽驱动的 Create 式加工机 | [`common/block/`](src/main/java/dev/celestiacraft/steam_letters/common/block/)                                                                                                            |
| 工作盆系   | 青铜工作盆: 多流体罐与并行处理, 兼容搅拌, 压缩, 注液等盆类配方       | [`common/block/bronze_basin/`](src/main/java/dev/celestiacraft/steam_letters/common/block/bronze_basin/)                                                                                  |
| 流体输运   | 青铜蒸汽泵: 管网中存在蒸汽时加压泵送                                 | [`common/block/steam_pump/`](src/main/java/dev/celestiacraft/steam_letters/common/block/steam_pump/)                                                                                      |
| 储运与材料 | 蒸汽容器 (空 / 封), 蒸汽机构, 青铜与锡材料, 机壳                     | [`SlItems`](src/main/java/dev/celestiacraft/steam_letters/common/register/SlItems.java) / [`SlMaterials`](src/main/java/dev/celestiacraft/steam_letters/common/register/SlMaterials.java) |
| 流体       | 蒸汽与过热蒸汽两种流体, 含桶与流体方块                               | [`SlFluids`](src/main/java/dev/celestiacraft/steam_letters/common/register/SlFluids.java)                                                                                                 |

新增同类设备时, 注册到对应注册类即可, 不涉及本文修改.

---

## 二. 蒸汽体系

### 2.1 链路

```
燃料 --> 燃烧室(热量) --> 锅炉(水 + 热量) --> 蒸汽
自然光 --> 太阳能锅炉(水) -----------> 蒸汽

蒸汽 --> 飞轮引擎(SU / RPM) --> 应力输出
     --> 加工机器 / 蒸汽泵
```

### 2.2 通用约定

- 流体容量以 `mB` 计, 生产与消耗速率以 `mB/tick` 计; `20 tick = 1 秒`.
- 水与蒸汽按 `1:1` 换算, 即消耗 `1 mB` 水产出 `1 mB` 蒸汽.
- 燃料消耗, 流体消耗与产出计算只在服务端执行; 客户端只负责渲染与提示.
- 需要跨区块卸载, 重载, 重启保留的状态均持久化.
- 锅炉默认产出本模组的蒸汽流体, 产出流体 ID 可由配置替换为任意流体. 修改配置不会转换, 清空或销毁世界中已有的流体.

### 2.3 跨设备机制

| 机制     | 说明                                                                                                                           | 实现                                                                                                                                                                                                                |
|----------|--------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 热量来源 | 锅炉不维护自身热量数据, 只读取正下方燃烧室的状态; 位移仅一格, 因此不走 Capability                                              | [`IHeatSource`](src/main/java/dev/celestiacraft/steam_letters/api/machine/heat/IHeatSource.java)                                                                                                                    |
| 过热蒸汽 | 使用过热蒸汽的机器按全局倍率减少蒸汽消耗; 引擎额外在应力输出上乘倍率                                                           | [`SteamConfig`](src/main/java/dev/celestiacraft/steam_letters/config/common/SteamConfig.java)                                                                                                                       |
| 并行     | 工作盆系机器按并行数连续应用配方: 原料不足或产物放不下即停止, 蒸汽按实际操作扣减                                               | [`ISteamBasinMachine`](src/main/java/dev/celestiacraft/steam_letters/api/machine/ISteamBasinMachine.java)                                                                                                           |
| 预热     | 引擎在预热进度完成前不写入应力, 飞轮不转; 预热只判断有无蒸汽, 与档位无关                                                       | [`SteamEngineBlockEntity`](src/main/java/dev/celestiacraft/steam_letters/common/block/engine/SteamEngineBlockEntity.java)                                                                                           |
| 注册参数 | 容量, 产出, 热量上限等参数由注册时写入的 spec 承载, 使用 `IntSupplier` / `DoubleSupplier` 而非裸数值 -- 注册发生时配置尚未加载 | [`SteamEngineSpec`](src/main/java/dev/celestiacraft/steam_letters/api/machine/SteamEngineSpec.java) / [`SteamEngineDriveSpec`](src/main/java/dev/celestiacraft/steam_letters/api/machine/SteamEngineDriveSpec.java) |

具体数值以配置与注册类为准, 本文不重复.

---

## 三. 配置

| 文件                               | 类型   | 内容                                            |
|------------------------------------|--------|-------------------------------------------------|
| `nebula/steam_letters/common.toml` | COMMON | 蒸汽与各设备的机制参数                          |
| `nebula/steam_letters/server.toml` | SERVER | 各设备的应力影响 (impact) 与应力容量 (capacity) |

- 配置实现为模块化: 每个设备一个 `ConfigModule` 子类, 在 [
  `CommonConfigs`](src/main/java/dev/celestiacraft/steam_letters/config/CommonConfigs.java) 中挂载一次即生效.
  新增模块不涉及其他改动.
- 每个配置项均带 `.comment()` 与界面翻译键, 形如 `steam_letters.config.<模块>.<键>`. toml 中的注释保持英文原文, 界面上由
  Configured 读取 `<界面翻译键>.comment` 显示译文.
- 配置项的界面名与注释随语言数据生成: 英文在 [
  `English.java`](src/main/java/dev/celestiacraft/steam_letters/datagen/language/English.java), 中文在 [
  `Chinese.java`](src/main/java/dev/celestiacraft/steam_letters/datagen/language/Chinese.java), 运行 `runData` 后分别写入
  `assets/steam_letters/lang/en_us.json` 与 `zh_cn.json`.
- 改动需重启服务端或客户端后生效, 文件头部已有说明.
- 应力值通过 Create 的 `BlockStressValues` 提供器读取, 因此护目镜提示与 Create 自身配置体系保持一致.

---

## 四. 数据驱动

| 层             | 内容                                                                                                          | 源码                                                                                              |
|----------------|---------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| 物品标签       | `steam_letters:*` 描述设备族 (蒸汽飞轮, 引擎, 锅炉, 各类燃烧室, 蒸汽容器等), `forge:*` 描述青铜, 锡等通用材料 | [`SlItemTags`](src/main/java/dev/celestiacraft/steam_letters/tags/SlItemTags.java)                |
| 流体标签       | `forge:steam`, `forge:superheated_steam`. 判断是否为蒸汽一律查询标签, 不查询具体流体 ID                       | [`SlFluidTags`](src/main/java/dev/celestiacraft/steam_letters/tags/SlFluidTags.java)              |
| 自定义配方类型 | `steam_letters:solid_fuel`, `steam_letters:fluid_fuel`: 声明燃料种类, 燃烧时间与供热量                        | [`SlRecipeTypes`](src/main/java/dev/celestiacraft/steam_letters/common/recipe/SlRecipeTypes.java) |
| Create 配方    | 压片, 搅拌, 粉碎, 注液, 序列组装等走 Create 原生配方类型                                                      | [`datagen/recipe/`](src/main/java/dev/celestiacraft/steam_letters/datagen/recipe/)                |
| 数据生成       | 方块状态, 模型, 标签, 配方, 语言均由 `runData` 生成, 手写 JSON 属例外                                         | [`DataGenerators`](src/main/java/dev/celestiacraft/steam_letters/datagen/DataGenerators.java)     |
| JEI            | 燃料配方的自定义类别                                                                                          | [`compat/jei/`](src/main/java/dev/celestiacraft/steam_letters/compat/jei/)                        |

---

## 五. 兼容

- **Create 6** (必需): 应力网络, Ponder, Flywheel, Registrate, 护目镜信息沿用 Create 的实现.
- **JEI**: 燃料配方类别.
- **KubeJS**: 以脚本注册自定义设备与燃料配方,
  见 [KubeJS 集成](https://github.com/Qi-Month/Create-Steam-Letters/wiki/KubeJS).
- **Create: Diesel Generators**: 青铜工作盆接入其注液铸造行为 (软依赖, 运行时检测).
- **Jade**: 方块信息.
- **Oculus / Embeddium**: 渲染.
- **Ponder**: 自带场景与自定义 Ponder 标签.

---

## 六. 安装

1. 安装 Minecraft 1.20.1 与对应 Forge (47.x).
2. 安装 Create (6.0+) 及其依赖.
3. 将构建产物 `Create-Steam-Letters-<版本>-1.20.1.jar` 放入 `mods/`.

可选联动 (JEI, KubeJS 等) 缺失不影响启动, 相关逻辑自动跳过.

---

## 七. 开发

### 7.1 目录结构

源码根为 `src/main/java/dev/celestiacraft/steam_letters/`.

| 目录                   | 职责                                                                                                                             |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `api/`                 | 公共契约: Registrate 入口, 应力, 翻译键, 机器接口, 粒子基类                                                                      |
| `common/block/<设备>/` | 每个设备一个包: Block, BlockEntity, 渲染器, ValueBox / 值行为                                                                    |
| `common/register/`     | 注册入口: `SlBlocks`, `SlItems`, `SlBlockEntities`, `SlFluids`, `SlMaterials`, `SlParticles`, `SlCreativeTabs`, `SlSpriteShifts` |
| `common/recipe/`       | 配方类型, 序列化器与示例生成器                                                                                                   |
| `config/`              | `ConfigModule` 基类与每设备一个模块                                                                                              |
| `client/`              | 客户端专用: 模型与方块状态生成, Ponder, 粒子, 流体渲染, 语言键                                                                   |
| `compat/`              | JEI, KubeJS, CDG, 着色器联动                                                                                                     |
| `datagen/`             | 数据生成提供器                                                                                                                   |
| `tags/`                | 物品与流体标签                                                                                                                   |
| `utils/`               | 流体交互, 插值跟随值, 按面 Capability 缓存等工具                                                                                 |

### 7.2 扩展点

| 目标                              | 扩展点                                                                                                                                                                                                     |
|-----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 新的蒸汽机器 (储汽 / 抽汽 / 耗汽) | 实现 [`ISteamStorage`](src/main/java/dev/celestiacraft/steam_letters/api/machine/ISteamStorage.java)                                                                                                       |
| 应用 Create 盆类配方的机器        | 实现 [`ISteamBasinMachine`](src/main/java/dev/celestiacraft/steam_letters/api/machine/ISteamBasinMachine.java): 提供并行数, 放宽配方匹配                                                                   |
| 按压床 / 传送带逻辑处理的机器     | 实现 [`ISteamPressMachine`](src/main/java/dev/celestiacraft/steam_letters/api/machine/ISteamPressMachine.java)                                                                                             |
| 新的热源                          | 实现 [`IHeatSource`](src/main/java/dev/celestiacraft/steam_letters/api/machine/heat/IHeatSource.java)                                                                                                      |
| 方块携带自身的机制参数            | 实现 [`ISteamEngineBlock`](src/main/java/dev/celestiacraft/steam_letters/api/machine/ISteamEngineBlock.java). 参数放在方块而非 BE 上: 同一 BE 类型会服务多个方块, 放在方块上可避免相互覆盖, 也无需额外同步 |
| 新的配置分组                      | 继承 [`ConfigModule`](src/main/java/dev/celestiacraft/steam_letters/config/api/ConfigModule.java), 在 `CommonConfigs` 中添加一行                                                                           |
| 资源 ID 与 Registrate 短写        | [`SlApis`](src/main/java/dev/celestiacraft/steam_letters/api/SlApis.java): `loadResource` / `loadCreate` / `loadForge` / `loadCDG`                                                                         |
| 整合包注册自定义设备              | [`compat/kubejs/builder/`](src/main/java/dev/celestiacraft/steam_letters/compat/kubejs/builder/); 用法见 [KubeJS 集成](https://github.com/Qi-Month/Create-Steam-Letters/wiki/KubeJS)                       |

### 7.3 新增一台设备

1. 在 `common/block/<名字>/` 下创建 Block 与 BlockEntity, 必要时添加渲染器与值行为.
2. 实现所需接口, 多数机器只需 `ISteamStorage`.
3. 在 `SlBlocks` / `SlBlockEntities` 注册, 标签通过 `.tag(...)` 添加.
4. 需要配置时新增 `ConfigModule` 子类, 在 `CommonConfigs` 挂载.
5. 在 `datagen` 中补充模型, 配方, 中英语言文件, 或复用 `ItemModelGen`.
6. 需要教学时添加 Ponder 场景.

该流程不涉及本文的修改.

---

## 八. 许可

许可证为 GNU LGPL 3.0, 见 [`gradle.properties`](gradle.properties) 与 `META-INF/mods.toml`.

---

## 附: 维护约定

为使文档无需随代码变动, 本文遵循三条约定:

1. 只写机制与结构, 不写清单: 方块指向注册类, 配置项指向 toml.
2. 不写数值: 默认值只出现在配置与代码中, 本文不重复.
3. 分类而非枚举: 表格每行对应一类长期存在的职能, 新增同类内容不会使表格过期.

需要展开的细节写入 [wiki](https://github.com/Qi-Month/Create-Steam-Letters/wiki)