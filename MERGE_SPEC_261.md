# Бэкпорт слияния ProjectExpansion на Minecraft 26.1 (ветка mc26.1, worktree /home/alexquasar/Проекты/PE-26.1)

## Контекст

- В `/home/alexquasar/Проекты/PE-26.1` уже перенесено содержимое аддона ProjectExpansion из ветки `mc26.3`
  (код `moze_intel.projecte.expansion.*`, ресурсы, конфиг, теги, датаген-провайдеры). Код написан под API
  **26.3** и не компилируется под **26.1**. Эталон API 26.1 — уже портированный код ProjectE в этом же
  репозитории.
- Этот бэкпорт **почти идентичен** бэкпорту на 26.2, который уже сделан: 26.1 и 26.2 очень близки
  (в частности CookingFuel в них ещё нет, `Item#getBurnTime(stack, recipeType, FuelValues)` есть,
  Curios отдаёт `IItemHandlerModifiable` из API, а capability `curios:item_handler` регистрирует как
  `ResourceHandler<ItemResource>`).
- Образец «как надо» для инвентарей — соседние классы ProjectE в этом репозитории.

## Ключевая дельта 26.3 → 26.1: инвентари

В 26.1 сосуществуют оба API: `net.neoforged.neoforge.transfer.ResourceHandler<ItemResource>`
(есть `ItemStacksResourceHandler`, `CombinedResourceHandler`, `RangedResourceHandler`, `RootCommitJournal`)
и старый `net.neoforged.neoforge.items.IItemHandler` / `IItemHandlerModifiable`. **Слоты ProjectE в 26.1
принимают `IItemHandler`** (эталон: `gameObjs/container/slots/ValidatedSlot.java`).

Что делать:
- Внутри блоок-энтити можно оставить код на `ItemStacksResourceHandler` и транзакциях, но наружу (в слоты,
  в контейнеры, в capability, которые отдаются в GUI) отдавать `IItemHandler`: `IItemHandler.of(обработчик)`
  — такой статический фабричный метод есть в 26.1.
- В контейнерах (`expansion/gui/container/**`) перейти на `IItemHandler` / `IItemHandlerModifiable` и на
  `insertItem(int, ItemStack, boolean)` / `extractItem(int, int, boolean)` / `getStackInSlot` /
  `setStackInSlot` / `getSlotLimit` — как в версии для 1.21.1.
- Образец: версии этих файлов из `/home/alexquasar/Проекты/PE-1.21.1/src/main/java/moze_intel/projecte/expansion/`
  (там инвентари ещё на `IItemHandler` и без транзакций) — это ближе всего к тому, что нужно.
- Транзакции (`Transaction`, `RootCommitJournal`) в 26.1 есть, но для слотов они не нужны.
  `RootCommitJournal` нужен там, где мутации EMC/знаний должны попасть в транзакцию.
- `ItemHandlerHelper` в NeoForge 26.1 есть (в отличие от 26.3, где его убрали).

## Правила

- **Никаких git-команд. Не запускать gradle.** Проверять себя чтением, grep и сравнением с 1.21.1 и 26.1.
- Писать **только** в своё поддерево. Не трогать: `build.gradle`, `PECore.java`, `PEClient.java`, `config/**`,
  `api/**`, `network/**`, `gameObjs/**`, `utils/**`, `emc/**`, `events/**`, `integration/**`, `src/datagen/**`,
  `item/**`, `block/**`, `util/**`, `registries/**`, `client/**`, `integrations/**`.
- Стиль ProjectE: табы, без `var`, комментарии по-английски. Никаких миксинов.

## Поддеревья (только они)

- `src/main/java/moze_intel/projecte/expansion/block/entity/**`
- `src/main/java/moze_intel/projecte/expansion/gui/container/**`
- `src/main/java/moze_intel/projecte/expansion/capability/**`
- `src/main/java/moze_intel/projecte/expansion/rendering/**`

## Известные ошибки компиляции в этом поддереве

1. `ContainerCollector`, `ContainerCondenserMK3Input/Output`, `ContainerArcaneTransmutationTablet`,
   `ContainerAdvancedAlchemicalChest`: `ResourceHandler<ItemResource> cannot be converted to IItemHandler`.
2. `BlockEntityCollector`: `isValid(int, ItemResource)` нет в 26.1, `insertItemStacked`, `CombinedResourceHandler`
   в сигнатурах, `@Override` на отсутствующих методах.
3. `BlockEntityCondenserMK3`: конструктор `(StackHandler, WriteMode)`, `insertItemStacked`.
4. `BlockEntityAdvancedAlchemicalChest`: `IItemHandler cannot be converted to ResourceHandler<ItemResource>`.
5. `BlockEntityEMCLink`: `Prediction` нет в 26.1 → `ItemHandlerHelper#giveItemToPlayer` / 1.21.1-вариант.
6. `BlockEntityBase.StackHandler cannot be converted to IItemHandlerModifiable`.
7. `expansion/rendering/*`: `rotateDegrees(Axis, float)` в 26.3 → на 26.1 используется `mulPose(Axis.YP.rotationDegrees(…))`
   (см. `moze_intel.projecte.rendering.ChestRenderer` в этом репозитории).
8. Сериализация: код уже на `ValueInput`/`ValueOutput` (в 26.1 они есть) — оставить, но проверить вложенные инвентари.

## Отчёт (по-русски, до 350 слов)

1. Что переведено.
2. Что не удалось.
3. Подводные камни и найденные баги (не чинить, сообщить).
4. Какие файлы вне поддерева надо поправить.
