# Бэкпорт слияния Expansion на Minecraft 26.2 (ветка mc26.2, worktree /home/alexquasar/Проекты/PE-26.2)

## Контекст

- В `/home/alexquasar/Проекты/PE-26.2` уже перенесено содержимое аддона ProjectExpansion из ветки `mc26.3`
  (код в `moze_intel.projecte.expansion.*`, ресурсы, конфиг, теги, датаген-провайдеры, 15 файлов ProjectE
  с нативными заменами миксинов). Оно написано под API **26.3** и не компилируется под **26.2**: 71 ошибка.
- Эталон API 26.2 — уже портированный код ProjectE в этом же репозитории.
- Эталон «как было до порта на 26.3» — ветка `mc1.21.1`: `/home/alexquasar/Проекты/PE-1.21.1/src/main/java/moze_intel/projecte/expansion/`.
  **1.21.1 и 26.2 гораздо ближе друг к другу, чем 26.3** (см. ниже), поэтому для инвентарей правильный
  источник — 1.21.1, а не 26.3.

## Что уже совпадает с 26.3 (не трогать!)

- `GuiGraphicsExtractor`, `extractBackground` / `extractLabels` / `extractTooltip` — в 26.2 есть, код GUI верный.
- `createRenderState` / `extractRenderState` / `submit(...)` у `BlockEntityRenderer` — в 26.2 есть (эталон:
  `moze_intel/projecte/rendering/ChestRenderer.java`).
- `ValueInput` / `ValueOutput` для сериализации блок-энтити — в 26.2 есть (эталон:
  `gameObjs/block_entities/CollectorMK1BlockEntity.java:327`).
- `Identifier` вместо `ResourceLocation`, отсутствие атрибута `bus` у `@EventBusSubscriber`,
  `Item.Properties` с обязательным `setId`, `BlockBehaviour.Properties` с `setId`,
  `assets/<ns>/items/*.json` для предметов, формат ингредиентов рецептов (bare id / `#tag`).

## Ключевая дельта 26.3 → 26.2: инвентари

В 26.2 **сосуществуют оба API**: новый `net.neoforged.neoforge.transfer.ResourceHandler<ItemResource>`
(есть `ItemStacksResourceHandler`, `CombinedResourceHandler`, `RangedResourceHandler`, `RootCommitJournal`)
и старый `net.neoforged.neoforge.items.IItemHandler` / `IItemHandlerModifiable`. При этом **слоты ProjectE
в 26.2 принимают `IItemHandler`** (эталон: `gameObjs/container/slots/ValidatedSlot.java`), а не `ResourceHandler`.

Что делать:
- Внутри бло-энтити можно оставить 26.3-реализацию на `ItemStacksResourceHandler` и транзакциях, но наружу
  (в слоты, в контейнеры, в capability, которые отдаются в GUI) отдавать `IItemHandler`:
  `IItemHandler.of(обработчик)` — такой статический фабричный метод есть в 26.2.
- В контейнерах (`expansion/gui/container/**`) перейти на `IItemHandler` / `IItemHandlerModifiable` и на
  `insertItem(int, ItemStack, boolean)` / `extractItem(int, int, boolean)` / `getStackInSlot` /
  `setStackInSlot` / `getSlotLimit` — как в 1.21.1.
- Взять за образец 1.21.1 версии этих файлов:
  `gui/container/ContainerCollector.java`, `ContainerCondenserMK3Input.java`,
  `ContainerArcaneTransmutationTablet.java`, `ContainerBase.java`, `ContainerKnowledgeCondenser.java` и т.п.
- Транзакции (`Transaction`, `TransactionContext`, `RootCommitJournal`) в 26.2 тоже есть, но для слотов
  они не нужны: `IItemHandler`-путь работает без них. Не выбрасывай `RootCommitJournal` там, где он нужен
  для того, чтобы мутации EMC/знаний попали в транзакцию (см. 1.21.1 → 26.3 изменения в `BlockEntityCollector`).
- `WrappedItemHandler` в NeoForge 26.2 **отсутствует** — использовать то, что есть в ProjectE 26.2
  (`moze_intel.projecte.impl.capability.*`, `gameObjs.block_entities.CollectorMK1BlockEntity`).

## Правила

- **Никаких git-команд. Не запускать gradle.** Проверять себя чтением, grep и сравнением с 1.21.1/26.2.
- Писать **только** в своё поддерево (ниже). Чужие файлы не трогать, о них писать в отчёте.
- Стиль ProjectE: табы, без `var`, комментарии по-английски.
- Никаких миксинов. Клиентские классы нельзя упоминать из кода, который грузится на выделенном сервере.

## Поддеревья (только они)

- **Агент:** `src/main/java/moze_intel/projecte/expansion/block/entity/**`,
  `src/main/java/moze_intel/projecte/expansion/gui/container/**`,
  `src/main/java/moze_intel/projecte/expansion/capability/**`,
  `src/main/java/moze_intel/projecte/expansion/rendering/**`.

## Известные ошибки, которые нужно починить (из лога компиляции)

1. `ContainerCollector`, `ContainerCondenserMK3Input`, `ContainerArcaneTransmutationTablet`:
   `ResourceHandler<ItemResource> cannot be converted to IItemHandler`.
2. `BlockEntityCollector:93` `isValid(int, ItemResource)` — нет такого метода в 26.2 (у `IItemHandler` это
   `isItemValid(int, ItemStack)`), `insertItemStacked`, `CombinedResourceHandler` в сигнатурах.
3. `BlockEntityCondenserMK3:50,324,325` — конструктор `(StackHandler, WriteMode)`, `insertItemStacked`,
   `@Override` на методе, которого в 26.2 нет.
4. `BlockEntityAdvancedAlchemicalChest:65,136` — `IItemHandler cannot be converted to ResourceHandler<ItemResource>`.
5. `BlockEntityEMCLink:248` — `Prediction` нет в 26.2 (в 26.3 `Inventory#placeItemBackInInventory(..., Prediction)`,
   в 26.2 — `ItemHandlerHelper#giveItemToPlayer`).
6. `BlockEntityBase.StackHandler cannot be converted to IItemHandlerModifiable`.
7. `expansion/rendering/*`: в 26.3 используется `rotateDegrees(Axis, float)`, в 26.2 API рендера отличается —
   сверяйся с `moze_intel.projecte.rendering.ChestRenderer` в этом репозитории.
8. Сериализация: код из 26.3 уже на `ValueInput`/`ValueOutput` — оставить, но проверить, что вложенные
   инвентари читаются/пишутся правильно в 26.2 (эталон — `CollectorMK1BlockEntity`).

## Отчёт (по-русски, до 350 слов)

1. Что переведено (по пунктам).
2. Что не удалось и почему.
3. Подводные камни, найденные баги (не чинить, сообщить).
4. Какие файлы вне поддерева надо поправить.
