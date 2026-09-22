# ProjectExtended — слит в код ProjectE

Дата: 2026-09-22.
Upstream: https://github.com/pupnewfster/ProjectExtended (автор pupnewfster, **MIT**).
Лицензия сохранена: текст перенесён в корневой файл `LICENSE-ProjectExtended`.

ProjectExtended — аддон к ProjectE, добавляющий «то, что EE2 имел бы в наши дни»:
dark/red matter триденты и щиты, Alchemical Barrel, Interdiction Lantern,
датапак-blacklist + интеграция GameStages.

**Статус: аддон больше не существует как отдельный мод/каталог.** Весь его код
перенесён в `src/` самого ProjectE (ветка `mc1.21.1`) под неймспейсом `projecte`.
Каталог `ProjectExtended/` со своим Gradle-проектом удалён — сборка идёт одной
общей командой `./gradlew build`.

---

## Что перенесено и куда

### Новые классы (`src/main/java/moze_intel/projecte/`)

| Область | Файлы |
|---------|-------|
| Предметы | `gameObjs/items/tools/PETrident`, `gameObjs/items/tools/PEShield` |
| Сущность | `gameObjs/entity/PETridentEntity` (с фиксом issue #11) |
| Блоки | `gameObjs/blocks/AlchemicalBarrel`, `gameObjs/blocks/InterdictionLantern` |
| Блочные сущности | `gameObjs/block_entities/AlchemicalBarrelBlockEntity`, `InterdictionLanternBlockEntity` |
| Контейнер/экран | `gameObjs/container/AlchemicalBarrelContainer`, `gameObjs/gui/AlchemicalBarrelScreen` |
| Рецепт | `gameObjs/customRecipes/PEShieldSpecialRecipe` (краска → окрашенный щит) |
| Чёрные списки | `gameObjs/blacklist/BlacklistManager`, `BlacklistType`, `EMCGameStageHelper` (`GameStagesHelper` уже был в ProjectE) |
| Пакет | `network/packets/to_client/PacketSyncBlacklist` |
| Рендер | `client/rendering/PETridentRenderer`, `ShieldISTER`, `TridentISTER` |

### Изменённые файлы

- **Реестры**: `PEItems` (+4), `PEBlocks` (+2), `PEBlockEntityTypes` (+2),
  `PEEntityTypes` (+`PE_TRIDENT`), `PEContainerTypes`, `PEDataComponentTypes`
  (`TRIDENT_MODE`), `PERecipeSerializers` (`SHIELD_DECORATION`), `PETags.Items`
  (`blacklist_condenser`/`blacklist_learning`), `PECreativeTabs`
  (PROJECTE, FUNCTIONAL_BLOCKS, REDSTONE_BLOCKS, COMBAT).
- **Текст**: `PELang` (режимы тридента, предупреждения чёрных списков, элемент
  списка, advancement бочки), `ServerConfig` + `PEConfigTranslations`
  (`showMissingGameStages`).
- **Сеть**: `PacketHandler` — регистрация `PacketSyncBlacklist`.
- **Ядро/клиент**: `PECore` (слушатели reload/sync/попыток использования,
  `serverQuit` очистка), `PEClient` (рендер тридента, свойства
  `projecte:blocking`/`projecte:throwing`, экран бочки, тултипы, переопределения
  клиента, отключение от сервера).
- **Доступ**: `META-INF/accesstransformer.cfg` — `ThrownTrident.ID_LOYALTY`,
  `ThrownTrident.dealtDamage`, `ThrownTrident.isAcceptibleReturnOwner()`.
- **Сборка**: `build.gradle` — `compileOnly("net.darkhax.gamestages:GameStages-Forge-1.20.3:${gamestages_version}")`
  и `filter.includeGroupAndSubgroups('net.darkhax')` в exclusiveRepo blamejared;
  `gradle.properties` — `gamestages_version=17.0.1`.
- **Текстуры**: 13 PNG (+`interdiction_lantern.png.mcmeta`) в
  `src/main/resources/assets/projecte/textures/{block,item,entity}/`.

### Датаген (`src/datagen/`)

Провайдеры дополнены: `PEBlockStateProvider`, `PEItemModelProvider`,
`PESpriteSourceProvider`, `PELangProvider`, `PERecipeProvider`,
`PEBlockLootTable`, `PEAdvancementsGenerator`, `PEItemTagsProvider`,
`PEBlockTagsProvider`, `PEEntityTypeTagsProvider`. Сгенерированный вывод
(blockstate/models/recipes/tags/loot/advancement/lang) лежит в
`src/datagen/generated/` под неймспейсом `projecte`.

Обновление ассетов: `./gradlew runData`.

### Зависимости и защита без GameStages

- GameStages подключён **только на этапе компиляции** (`compileOnly`) — в рантайм
  его нет.
- Рантайм-проверка: `GameStagesHelper.checkModsLoaded()` в конструкторе `PECore`,
  при отсутствии мода `gameStagesLoaded = false`, и все проверки стадий
  (`EMCGameStageHelper`) штатно пропускаются.
- Без GameStages работают: чёрные списки на тегах, синхронизация пакетом,
  предупреждения в тултипах (включая перечень недостающих стадий, если мод есть).

---

## Функционал

1. **Dark/Red Matter Trident** — 4 режима (`normal`, `channeling`, `riptide`,
   `shockwave`) через data component `TRIDENT_MODE`, циклическое переключение
   ПКМ, свойство модели `projecte:throwing`.
2. **Dark/Red Matter Shield** — свойство модели `projecte:blocking`, рецепт
   окрашивания краской (`shield_decoration`), 16 цветных названий, свой рендер
   (`ShieldISTER`).
3. **Alchemical Barrel** — блок с инвентарём (54 слота), открытая/закрытая
   модель (`facing` + `open`), контейнер+экран (255×230), табы
   PROJECTE/FUNCTIONAL_BLOCKS/REDSTONE_BLOCKS, advancement, self-drop лут.
4. **Interdiction Lantern** — как interdiction torch, но фонарь; теги
   `guarded_by_piglins`, `piglin_repellents`, `mineable/pickaxe`, cutout,
   `.commonTicker` у БЕ.
5. **Датапак-blacklist** — теги `projecte:blacklist_condenser` и
   `projecte:blacklist_learning` (условия перечисления `all`/`any`),
   `BlacklistManager` (перезагрузка при change-фильтре), синхронизация клиенту
   `PacketSyncBlacklist`, предупреждения в тултипе (`WARNING_BLACKLIST_*`,
   `LIST_ELEMENT`).
6. **GameStages** — для каждого элемента чёрного списка можно указать стадии;
   без них предмет нельзя использовать в конденсаторе/таблице трансмутации, а
   тултип покажет, каких стадий не хватает (настраивается
   `showMissingGameStages`).

### Применённый фикс: #11 — возврат Red Matter тридента из пустоты

Ванильный `ThrownTrident` возвращается только когда `Loyalty > 0` И
(`dealtDamage` или `noPhysics`), причём `dealtDamage` ставится после попадания
или `inGroundTime > 4`. Тридент, улетевший в пустоту, никогда не «садится»,
поэтому возврат не запускается, а на `y < minBuildHeight − 64`
(`Entity.checkBelowWorld`) его уничтожает сервер.

В `PETridentEntity.tick()` (только сервер, только red matter tier, только при
наличии loyalty и владельца): при падении ниже `minBuildHeight − 32` тридент
телепортируется на 2 блока выше владельца, скорость обнуляется и выставляется
`noPhysics` — штатный loyalty-возврат поднимает его и отдаёт в руку. Тёмно-материевый
тридент ведёт себя как ванильный (разрушается в пустоте — как и просил автор issue).

---

## Сборка

```bash
./gradlew build          # main + api + тесты
./gradlew runData        # генерация ассетов/даты в src/datagen/generated
```

Нужен доступ к maven.blamejared.com (загрузка GameStages/Bookshelf для
`compileOnly`/`localRuntime`).

---

## История: ветки-каталоги (устарело)

Ранее каждая ветка аддона вендорилась отдельным каталогом `ProjectExtended/` в
одноимённой ветке форка. Этот подход **отменён**: каталог удалён, функционал
живёт в коде ProjectE.

| Ветка форка | Ветка аддона | Upstream SHA | Коммит импорта |
|-------------|--------------|--------------|----------------|
| `mc1.21.1` (активная) | 1.21.x | `7b76abd` | `17aad084` |
| `mc1.20.x` | 1.20.x | `c6f508d` | `d835f60c` |
| `mc1.19.x` | 1.19.x | `cea05fc` | `113809b9` |
| `mc1.18.x` | 1.18.x | `6fa7073` | `863c91a8` |
| `mc1.16.x` | 1.16.x | `490deb1` | `41b58d2c` |
| `mc1.15.x` | 1.15.x | `9560cc9` | `5d7f40b7` |
| `mc1.14.x` | 1.14.x | `e0232ea` | `874e8694` |

Версия аддона при слиянии: **1.6.2** (NeoForge 1.21.1).

---

## Разбор открытых issues аддона (8)

| # | Год | Заголовок | Вердикт |
|---|-----|-----------|---------|
| [12](https://github.com/pupnewfster/ProjectExtended/issues/12) | 2025 | Suggestion: Dark and red matter mace | **ENH** — новый предмет (булава с режимами explosion/shockwave + рецепты); большая фича, не применялось |
| [11](https://github.com/pupnewfster/ProjectExtended/issues/11) | 2025 | Red Matter Trident Should Return from Void | **FIXED** — применено (см. выше) |
| [9](https://github.com/pupnewfster/ProjectExtended/issues/9) | 2024 | Suggestion magic item that grants nightvision | **ENH** — фича (аналог эффекта gem helmet), не применялось |
| [8](https://github.com/pupnewfster/ProjectExtended/issues/8) | 2022 | Interdiction Lanterns | **ALREADY IMPLEMENTED** — блок и рецепт существуют; жалобу можно закрывать |
| [7](https://github.com/pupnewfster/ProjectExtended/issues/7) | 2021 | Suggestions for the Shield (99% block, thorns) | **ENH** — балансовая фича, не применялось |
| [6](https://github.com/pupnewfster/ProjectExtended/issues/6) | 2021 | Trident disappeared | **LEGACY/FIXED** — автор закрыл в 1.1.0; в 1.21.x обработка подбора аккуратная: `pickup=ALLOWED`, `playerTouch` только для владельца, `tickDespawn` при невозможности возврата роняет предмет |
| [4](https://github.com/pupnewfster/ProjectExtended/issues/4) | 2020 | [1.15.2] Shield Textures Switched? | **LEGACY/FIXED** — текстуры корректны: dark shield — бирюзовый (`#1AAAA7`/`#20C5B5`), red shield — красный (`#860000`) |
| [3](https://github.com/pupnewfster/ProjectExtended/issues/3) | 2020 | EMC mappers (Powah, Silent's Mechanics) | **OUTDATED** — современный ProjectE маппит EMC автоматически из рецептов/тагов; отдельных «мод-мапперов» не существует |
