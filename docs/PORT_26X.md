# Порт на Minecraft 26.x (ветки `mc26.1` → `mc26.2` → `mc26.3`)

Цель: портировать ProjectE (с уже влитым ProjectExtended) на новые версии
Minecraft 26.x / NeoForge. Целевые версии:

| Ветка | Minecraft | NeoForge | Java | Статус |
|---|---|---|---|---|
| `mc26.1` | 26.1.2 | 26.1.2.109 | 25 | ✅ `build` зелёный, запушен |
| `mc26.2` | 26.2 | 26.2.0.88 | 25 | ⏳ |
| `mc26.3` | 26.3 | 26.3.0.8-beta | 25 | ⏳ план |

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

## Порядок дальнейших портов

1. `mc26.2`: создать ветку от `mc26.1`, поднять NeoForge до 26.2.0.88 (MC 26.2),
   пересобрать, разобрать дельту API (26.2 — небольшой шаг от 26.1), перенести
   TOP/Bookshelf/WTHIT.
2. `mc26.3`: NeoForge 26.3.0.8-beta, удалить TOP/Bookshelf/WTHIT, поправить
   beta-API.
3. Каждую ветку пушить в `origin`, когда `./gradlew build` зелёный.
