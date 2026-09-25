# Порт на Minecraft 26.x (ветки `mc26.1` → `mc26.2` → `mc26.3`)

Цель: портировать ProjectE (с уже влитым ProjectExtended) на новые версии
Minecraft 26.x / NeoForge. Целевые версии:

| Ветка | Minecraft | NeoForge | Java | Статус |
|---|---|---|---|---|
| `mc26.1` | 26.1.2 | 26.1.2.109 | 25 | ✅ `build` зелёный, запушен |
| `mc26.2` | 26.2 | 26.2.0.88 | 25 | ✅ `build` зелёный (159 тестов), запушен |
| `mc26.3` | 26.3 | 26.3.0.16-beta | 25 | ✅ `build` зелёный (159 тестов), запушен |

Интеграции, удалённые в 26.x: EMI и CraftTweaker убраны; Jade переведён на
Modrinth Maven; Parchment отключён; GameStages остались как legacy `compileOnly`
с рантайм-защитой (когда мода нет — фича выключается). TOP, Bookshelf, WTHIT
переносятся в 26.2 и удаляются в 26.3 (модов для 26.3 нет).

## mc26.1 — что сделано (коммиты `dd4da093` … `7b3ccee5`)

- Полная сборка: `./gradlew build` → **BUILD SUCCESSFUL** (874 → 0 ошибок компиляции).
- Функциональность ProjectExtended сохранена: тёмная/красная материя (триденты и
  щиты), алхимическая бочка, фонарь интердикции, синх датапаков + blacklist'ы
  (теги и GameStages), рецепт декорирования щита, фикс #11 (возврат тридента из
  пустоты).
- Сборка: JUnit 5.13.4 + явный `junit-platform-launcher` (Gradle подсовывает
  свой старый лаунчер, из-за чего падал junit-fml NeoForge), Shadow 9.4.3
  (8.3.6 не умеет Java 25 bytecode).

### Ключевые изменения API 26.1, на которые опирается порт

- `ResourceKey.location()` → `identifier()`, `ResourceLocation` → `Identifier`;
  `Registry.get` возвращает `Optional`, `getHolderOrThrow` заменён на
  `lookupOrThrow(key).get(key.identifier())`.
- `BlockBehaviour.Properties` и `Item.Properties` требуют
  `setId(ResourceKey)` **до** создания объекта (иначе «Block/Item id not set»).
  Инъекция id сделана в `BlockDeferredRegister` / `BlockTypeDeferredRegister` /
  `ItemDeferredRegister`; вызовы в `PEBlocks` переведены на
  `Function<Properties, …>`. Для wall/floor-блоков каждый получает свой
  экземпляр Properties (иначе оба блока разделяли бы id последнего).
- `ToolMaterial`/`ArmorMaterial` — records, `Tier` удалён; enchantment value
  обязан быть положительным: наши «незачаровываемые» материалы получили 1
  (эквивалент старого 0).
- `Explosion` — интерфейс, реализация `ServerExplosion`; `NovaExplosion`
  переписан на её наследование, взрыв собирает дроп в одну кучу.
- Рендер: `BlockEntityRenderer`/`EntityRenderer` перешли на state-модель
  (`createRenderState`/`extractRenderState`/`submit`); ISTER щита/тридента стали
  `SpecialModelRenderer` (`projecte:shield` / `projecte:trident`), свойства
  предметов — `RangeSelectItemModelProperty` (`projecte:active/mode/blocking/
  throwing`); overlay трансмутации — `GuiLayer`, подсветка блоков —
  `ExtractBlockOutlineRenderStateEvent`.
- Transfer-реформа NeoForge: чтение/хранение инвентарей — `ResourceHandler<
  ItemResource>`; `IItemHandler`/`ItemHandlerHelper` остались deprecated внутри
  (адаптер `ItemHandlerResourceAdapter`).
- Данные сущностей — `ValueInput`/`ValueOutput`; `CompoundTag.getCompound`
  возвращает `Optional`; `ItemStack.getTags/getDescription/isRepairable` удалены
  (`typeHolder().tags()`, `getHoverName()`, компонент `REPAIRABLE`);
  `getArmorSlots()` → цикл по `EquipmentSlot.values()` c `isArmor()`;
  `ChunkPos` — record (`x()`/`z()`); `gamerule`-имена без префикса `RULE_`;
  `TNT_EXPLOSION_DROP_DECAY`, `WATER_EVAPORATES` вместо `ultraWarm()`.
- Дата-тримы больше не хранят предметы: `TrimMaterial`/`TrimPattern` потеряли
  ссылки на предметы. `ArmorTrimProcessor` строит EMC материалов по тегу
  `trim_materials` + компоненту `provides_trim_material`, шаблонов — по тегу
  `trim_templates`; фейерверки (`SHAPE_BY_ITEM`, `TRAIL`, `TWINKLE`,
  `GUNPOWDER`) и баннер-по-цвету переписаны на data-driven API.

## Известные риски (нужна проверка в игре)

- `MindStone`: при переполнении XP-orb конвертируется в опыт до максимума,
  остаток теряется (в 1.21.1 возвращался с остатком).
- Злой кролик задаётся через `RABBIT_VARIANT` + `applyComponentsFromItemStack`:
  проверить броню/атаку/цели и рендер EVIL-варианта.
- Дроп содержимого блоков (Pedestal/DMPedestal и др.): `onRemove` больше не
  вызывается — убедиться, что новый механизм 26.1 роняет инвентарь.
- Само-remainder у Arcana/Zero/Philosophers' Stone: эквивалента в 26.1 нет,
  инструмент может не возвращаться сам себе.
- `WorldTransmutationManager` переписан на `ContextAwareReloadListener` +
  `StrictJsonParser`: проверить загрузку датапак-трансмутаций с
  `neoforge:conditions`.
- `GemData` перешёл на полный `ItemStack.CODEC` (старые сейвы читаются через
  `orElse`).
- `key.category.projecte` добавлен в датаген-lang, но в существующих языковых
  файлах перевода может не быть.

## mc26.2 — дельта 26.1 → 26.2 (коммиты `10af9487`, `34917bf6`, `a9ac2191`)

Шаг между 26.1 и 26.2 оказался небольшим: **31 ошибка в 16 файлах** плюс датаген
(344 ошибки) и один тест. Ключевые изменения:

- `BlockPos.getCenter()` удалён → `Vec3.atCenterOf(pos)` (11 мест).
- Встроенные типы сущностей переехали в `net.minecraft.world.entity.EntityTypes`
  (`LIGHTNING_BOLT`, `PLAYER` для capability игрока).
- `CriteriaTriggers` → `net.minecraft.advancements.triggers`; новые пакеты
  `advancements.triggers` / `advancements.predicates` в датагене.
- `Options.hideGui` → `mc.gui.hud.isHidden()`, новый четырёхпараметрический
  custom-renderer в оверлее трансмутации.
- `LivingEntity#knockback` — новый overload с `DamageSource`.
- Цветные баннеры в 26.2 **не** слиты: `Items.BANNER` — это `ColorCollection<Item>`,
  баннер для EMC берётся через `Items.BANNER.pick(baseColor)`.
- Цветные блоки в датагене (`ColorCollection`), `TagAppender<T>` с `ResourceKey`,
  `Potions.LUCK` → `PotionIds.LUCK`, удалён `FurnaceFuel` data map провайдера.

## mc26.3 — дельта 26.2 → 26.3 (коммиты `95315596` … `ec16f187`)

Основная работа: **полная миграция на transfer-API** (71 файл) — NeoForge 26.3
удалил пакет `net.neoforged.neoforge.items` целиком.

- `IItemHandler`/`ItemStackHandler`/`ItemHandlerHelper`/fluid-хендлеры заменены на
  `ResourceHandler<ItemResource>` / `<FluidResource>` + `Transaction`; все
  контейнеры и слоты — через новый транзакционный слот, redstone — через
  `ResourceHandlerUtil.getRedstoneSignalFromResourceHandler`.
- Публичный API (`IAlchBagItem`, `IAlchBagProvider`, `IKnowledgeProvider`)
  переведён на `ResourceHandler<ItemResource>` (breaking, мажорный порт).
- Инструменты: `AxeItem`/`HoeItem`/`ShovelItem` удалены → `Item` с
  `Properties#axe/hoe/shovel`, AOE через `BlockTransformer`.
- `BrewingMapper`: `PotionBrewing` удалён → `RecipeManager`/`BrewingRecipe`.
- Реестр `BLOCK_TYPE` выпилен; `PushReaction.DESTROY` → `POPPED`.
- **Топливо**: data map `furnace_fuels` больше не существует — шесть fuel-предметов
  и блоков получили `minecraft:cooking_fuel` через item-определения ( burn-time
  как в 1.21.1: 6400/25600/102400, блоки ×9). Кодом константу задать нельзя.
- **Датагенерация**: run-тип `data` разделён на `serverData`/`clientData`, причём
  каждый прогон чистит свой выходной каталог — клиентский вывод вынесен в
  `src/datagen/generated-client` (добавлен в resources). Провайдеры больше не
  создают `ItemStack` на этапе бутстрапа (только `ItemStackTemplate` или
  `NSSItem`+`DataComponentPatch`: компоненты в 26.3 ещё не привязаны), `save()`
  не принимает дефолтный id, `PEBlockLootTable` снова ограничивает
  `getKnownBlocks()` своими блоками, damage type определяется файлом в ресурсах.
- Ресурсы advancements перегенерированы: старый формат наград (`{"recipe": ...}`)
  и критерий `has_the_recipe` в 26.3 не парсятся — из-за них реестр advancements
  падал бы и в реальной игре.
- Интеграции **TOP и WTHIT удалены** (на 26.3 не портированы): классы, IMC-хук,
  зависимости; остаётся только Jade.
- Ветка доведена до NeoForge 26.3.0.16-beta; `build` зелёный, 159 тестов.

## Порядок дальнейших портов

Все три ветки (`mc26.1`, `mc26.2`, `mc26.3`) собраны, протестированы и запушены
в `origin`. Дальнейшие шаги — по желанию:

1. Обновить docs при выходе новых версий NeoForge 26.x.
2. Проверка в игре по спискам «известные риски» выше (EMC щитов с баннером,
   сумки/сундуки/бочка, Curios, трансмутации, инструменты, фейерверки).
3. Косметика: ключ `key.category.projecte` есть в датаген-ланге, но не во всех
   переводах; модель `assets/projecte/item/manual.json` — рудимент (самого
   руководства в моде нет).
