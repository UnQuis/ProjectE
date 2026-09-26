# План слияния ProjectExpansion → ProjectE (фаза 1: mc1.21.1)

## Контекст

- **Источник (READ-ONLY!):** `/home/alexquasar/Проекты/ProjectExpansion` — ветка `1.21.1`, mod_id `projectexpansion`,
  пакет `cool.furry.mc.neoforge.projectexpansion`. Лицензия MIT (совместима с ProjectE, конфликтов нет).
  194 файла / 15131 строка. **Никогда не изменять файлы в этом репозитории.**
- **Цель:** `/home/alexquasar/Проекты/PE-1.21.1` — worktree ProjectE на ветке `mc1.21.1` (Minecraft 1.21.1,
  NeoForge 21.1.148). Тот же API, что и у источника, поэтому перенос почти механический.
- Версии: mod_id остаётся `projecte`. **Все id переименовываются `projectexpansion:*` → `projecte:*`.**

## Решения (менять нельзя)

1. Корневой пакет: `moze_intel.projecte.expansion`.
   Подпакеты повторяют структуру источника: `util`, `registries`, `item`, `block`, `block.entity`,
   `capability`, `events`, `gui`, `gui.container`, `gui.container.inventory`, `gui.container.slots`,
   `net`, `commands`, `integrations`, `client`, `rendering`, `config`.
2. **Второго `@Mod` не будет.** `cool.furry.mc.neoforge.projectexpansion.Main` → `moze_intel.projecte.expansion.ExpansionCore`
   (обычный класс со статическими `register*`/инициализацией, вызывается из `moze_intel.projecte.PECore`).
3. Регистрация — через классы ProjectE: `ItemDeferredRegister`/`BlockDeferredRegister`/`PEDeferredRegister`
   (`moze_intel.projecte.gameObjs.registration.*`, `moze_intel.projecte.gameObjs.registries.*`) и холдеры
   `ItemRegistryObject`/`BlockRegistryObject`. Создавать их с `PECore.MODID`, тогда id автоматически станут `projecte:*`.
4. Переименования классов-регистров (чтобы не пересекаться с классами ProjectE):
   `registries.Items`→`ExpansionItems`, `registries.Blocks`→`ExpansionBlocks`,
   `registries.BlockEntityTypes`→`ExpansionBlockEntityTypes`, `registries.Menus`→`ExpansionMenus`,
   `registries.Capabilities`→`ExpansionCapabilities`, прочие `registries.*`→`Expansion*`
   (Attachments, DataComponents, Sounds, DamageTypes, Attributes, Enchantments, CreativeTab…),
   `net.PacketHandler`→`net.ExpansionPacketHandler`, `util.*` и `item/block/capability/events/gui/…` — имена классов сохраняются.
5. **Миксины не переносятся** (19 шт. из `mixin/`). Их заменяет нативный код — этим занимается отдельный агент.
   Просто не переноси каталог `mixin/`.
6. Конфиг (`config/Config.java`, 7 client + 22 server опции) **не переносится** — агент главного цикла встроит
   опции в секцию `expansion` конфигов ProjectE. Не создавай свои конфиги.
7. Ресурсы: **только статикой** в `src/main/resources/**` и `src/datagen/java/**`.
   Каталог `src/datagen/generated` генерируется и будет перезаписан — туда ничего не класть.
8. Стиль кода — как в остальном ProjectE: табы, без `var`, комментарии на английском.

## Жёсткие правила для всех агентов

- **Никаких git-команд** (никаких `git add/commit/checkout/status` и т.п.).
- **Не запускать gradle** (`./gradlew ...`) — сборкой занимается главный цикл. Проверяй свою работу только чтением/grep.
- **Не трогать** эти файлы (они в зоне ответственности главного цикла):
  `build.gradle`, `src/main/java/moze_intel/projecte/PECore.java`, `src/main/java/moze_intel/projecte/network/PacketHandler.java`,
  `src/main/java/moze_intel/projecte/config/**`, `src/main/java/moze_intel/projecte/api/**` (публичный API-модуль),
  `src/main/java/moze_intel/projecte/gameObjs/registries/PEItems.java`, `.../PEBlocks.java`, `.../PEBlockEntityTypes.java`,
  `.../PECreativeTabs.java`, `.../PECapabilities`/`PEAttachmentTypes`, а также любые файлы вне своего поддерева.
  Если тебе кажется, что нужен правый чужого файла — напиши это в отчёте, не правь.
- Каждый агент пишет **только в свой поддерев** каталогов. Конфликты запрещены.
- Пространство имён: `projectexpansion:` → `projecte:` во всех строках, ресурсах, тегах, рецептах, лангах.

## Порядок работы (фаза 1 → потом 2)

- Фаза 1 (эта, `mc1.21.1`): нативное слияние. Цель — `./gradlew build` зелёный и рабочая игра.
- Фаза 2 (`mc26.3`): порт результата на API 26.3 (известные дельты: `IItemHandler`→`ResourceHandler<ItemResource>`+
  `Transaction`, `loadAdditional/saveAdditional`→`ValueInput/ValueOutput`, `GuiGraphics`→`GuiGraphicsExtractor`,
  `BlockEntityRenderer.render`→`createRenderState`/`extractRenderState`/`submit`, `Item#isFoil`, `LayerYue`).
- Фаза 3: бэкпорт на `mc26.2`, `mc26.1`.
- Фаза 4+: `mc1.20.4`, а для `1.10–1.12` донора нет (Expansion начинается с 1.14) — писать с нуля.

## Карта контента (итоги инвентаризации)

158 предметов, 106 блоков, 7 типов БЭ, 243 рецепта, 106 blockstate, ~180 моделей блоков, 177 моделей предметов,
106 лут-таблиц, 80 авансов, ~175 текстур, 592 ключа en_us, 8 подкоманд команд (база `px`),
3 capability, 14 сетевых пакетов, 5 GUI-экранов, 1 рендерер, 6 интеграций (JEI, EMI, Curios, Jade, TOP, WTHIT).
**EMC-значений в Expansion нет вообще** — их придётся задавать отдельно (вопрос открыт).

Серии на 16 значений параметризованы enum `util.Matter` (basic, dark, red, magenta, pink, purple, violet, blue,
cyan, green, lime, yellow, orange, white, fading, final): matter-блоки, collector, compressed_collector,
power_flower, relay, emc_link, advanced_alchemical_chest (16 цветов), fuel (11), звёзды (18).
