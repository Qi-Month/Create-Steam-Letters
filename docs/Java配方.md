# Java 配方

用 Java 代码登记本模组的燃料配方: 不写配方 JSON, 也不经过数据生成器, 配方对象在运行期构造并叠加在服务端解析出的配方表上.

方法清单以 `api/recipe` 下的源码为准, 本文只写机制与不可见约束.

## 1. 入口

`SlRecipeBuilders` 按配方类型给出工厂方法, 各自返回一个链式 builder:

| 工厂方法         | 配方类型                   | 输入           |
| ---------------- | -------------------------- | -------------- |
| `solidFuel(...)` | `steam_letters:solid_fuel` | 物品或物品标签 |
| `fluidFuel(...)` | `steam_letters:fluid_fuel` | 流体或流体标签 |

字符串名字按本模组命名空间解析; 需要完整自定义的 id 用接受 `ResourceLocation` 的同名重载.

builder 的链式方法与 datagen 侧的 `SolidFuelRecipeGen` 与 `FluidFuelRecipeGen` 同名同义: 输入, 燃烧时长, 过热标记, 条件. 不给时长时取 builder 的默认值; 流体输入的量固定为 `FluidFuelRecipe.FUEL_AMOUNT`, 两者都不读配方里的数值.

## 2. 示例

```java
SlRecipeBuilders.solidFuel("coal_block")
	.item(Items.COAL_BLOCK)
	.timeItem(8)
	.register();

SlRecipeBuilders.fluidFuel("oil")
	.fluidTag(FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "oil")))
	.timeSeconds(30)
	.superheated()
	.whenModLoaded("immersivepetroleum")
	.register();
```

调用点与 `SlRecipeBuilders.init()` 放在一起即可: 构造期登记的配方先排队, 等第一次资源重载时注入.

## 3. id

名字按 `<类型 id>/<名字>` 解析, 类型 id 取自 `SlRecipeTypes`, 因此与 datagen 产物落在同一套路径: `steam_letters:solid_fuel/<名字>`.

名字里带命名空间时该命名空间优先, 类型路径不变. 完全自定义时用接受 `ResourceLocation` 的重载, 此时不再补类型路径.

## 4. 生效时机

`register()` 是唯一生效点; `build()` 只构造配方对象并自检, 不注入.

- 模组构造期登记: 服务器尚未启动, 配方先排队, 等第一次资源重载
- 服务器已在运行: 立刻重新注入, 当前不在服务端线程时排到服务端线程执行
- `remove(id)`: 从队列里撤掉, 服务器在跑时同样立刻重新注入

## 5. 注入点与覆盖

注入挂在 `AddReloadListenerEvent` 上, 即服务端资源重载的末尾. 该次重载的监听器顺序固定为标签, 配方, 函数, 进度, 之后才是模组添加的监听器, 因此注入时配方表已经解析完数据包, 代码配方直接叠加在它上面.

注入前先剔掉上一次注入的条目, 再叠上当前条件全部通过的配方, 最后整表替换. 这保证两件事: 重载后不会留下上次注入的残留; 与数据包同 id 时, 代码配方掩盖数据包里的那一份.

`RecipeManager` 按 id 建索引, 重复 id 会抛异常, 但剔除与叠加的顺序已经把它挡住, 调用方不需要自己判断 id 是否冲突.

## 6. 条件

`whenCondition`, `whenModLoaded`, `whenModMissing` 都是"全部通过才注入". 条件在每次重载时重新求值, 因此传进去的判断必须是纯函数: 不要读会变的状态, 也不要带副作用.

`whenCondition` 接受配方条件对象, 用当次重载的条件上下文求值, 与数据包 `conditions` 字段走同一套判定. 重载尚未发生时上下文为空, 依赖标签的条件会判为不通过.

## 7. 自检

`build()` 收集配方自身的错误 (输入数量超出类型限制, 缺少输入等) 并写日志:

```
in-code recipe <id> is invalid: <错误>
```

自检只写日志, 不抛异常, 也不阻止注入. 出现这条 warn 说明配方进了配方表却不会按预期匹配, 应清到日志干净.

## 8. 与数据生成器的关系

两条路径的 id 完全一样, 但互不依赖: 数据生成器写出 JSON, 代码配方只在运行期存在, 不进 jar 的 `data` 目录.

同一 id 同时存在于两处时以代码配方为准. 想改一条内置燃料不必复制整个生成器, 覆盖同 id 即可.

## 9. 边界

- 只有本模组的两种燃料配方有代码写法, 机械动力的加工配方仍走数据包或 KubeJS
- 条件只在注入时求值, 不会写进任何 JSON
- 配方表由服务端决定, 客户端只接收结果, 因此两端不需要相同的条件判断

---

相关页面: [燃料配方](https://github.com/Qi-Month/Create-Steam-Letters/wiki/recipes/FuelRecipes), [Java 集成](https://github.com/Qi-Month/Create-Steam-Letters/wiki/java/Java).