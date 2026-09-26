# Фаза 2: порт слияния ProjectExpansion на Minecraft 26.3

## Контекст

- **Цель:** `/home/alexquasar/Проекты/ProjectE`, ветка `mc26.3` (Minecraft 26.3, NeoForge 26.3.0.16-beta).
  Рабочее дерево уже содержит скопированный порт из фазы 1 (`/home/alexquasar/Проекты/PE-1.21.1`, ветка
  `mc1.21.1`, коммит `6b3255cad`) в пакете `moze_intel.projecte.expansion.*` (172 файла) и ресурсы
  (359 текстур, 248 рецептов, модели, 9 файлов `pe_custom_conversions`). **Это рабочий код для 1.21.1, его нужно
  переписать под API 26.3.** Логику не менять, только API.
- Образец «как надо» — соседние классы самого ProjectE в этом же репозитории: они уже портированы на 26.3.
- Исходники Minecraft 26.3: `build/moddev/artifacts/minecraft-patched-26.3.0.16-beta-sources.jar`
  (читать через `unzip -p`), NeoForge: `build/moddev/artifacts/neoforge-*.jar` / `neoforge-*-sources.jar`.

## Правила

- **Никаких git-команд. Не запускать gradle** (сборку делает главный цикл). Проверять себя чтением и grep.
- Каждый агент пишет **только в свой поддерев** (список ниже). Не трогать:
  `build.gradle`, `PECore.java`, `PEClient.java`, `config/**`, `api/**`, `network/PacketHandler.java`,
  `gameObjs/registries/PE*.java`, `gameObjs/PETags.java`, `src/datagen/**`.
- Стиль ProjectE: табы, без `var`, комментарии по-английски, `PE`-префиксы классов.
- Никаких миксинов. Клиентские классы нельзя упоминать из кода, который грузится на выделенном сервере
  (`Objects#requireNonNull(Minecraft.getInstance().player)` и передача `LocalPlayer` в метод с параметром `Player`
  заставляют верификатор грузить клиентский класс — см. `expansion/client/ExpansionClientHooks` и
  `expansion/client/HitDirectionSource`, это уже сделано правильно, не ломай).

## Ключевые дельты API 1.21.1 → 26.3

1. **Инвентари:** `net.neoforged.neoforge.items.IItemHandler*` **удалён**. Замена:
   `net.neoforged.neoforge.transfer.ResourceHandler<ItemResource>` + `ResourceTransaction`/`Transaction`.
   `insertItem(stack,...)` → `insert(resource, tx, ctx)`, `extractItem` → `extract`, `getStackInSlot` → `getResource`,
   `setStackInSlot` → `setResource`, `getSlots()` → `iterator()`/`size()`, `isItemValid` → `ResourceValidator`.
   Обязательны транзакции: операции без `Transaction` не пройдут. Образцы: `impl/capability/AlchBagImpl`,
   `gameObjs/block_entities/CollectorMK1BlockEntity`, `CollectorMK2BlockEntity`, `gameObjs/container/PEContainer`.
2. **Сериализация БЭ:** `loadAdditional(CompoundTag)` → `loadAdditional(ValueInput)`,
   `saveAdditional(CompoundTag)` → `saveAdditional(ValueOutput)`. Образец: `CollectorMK1BlockEntity`.
3. **GUI:** `GuiGraphics` → `GuiGraphicsExtractor`; `renderBg` → `extractBackground`, `renderLabels` → `extractLabels`,
   `renderTooltip` больше не вызывается (текст тултипа сам попадает в `extractRenderState`), состояние шейдера и
   блендинга ушло в `RenderType`. Образцы: `client/gui/GUITransmutation`, `GUICondenser`, `GUICollector` самого PE.
4. **Рендер блоков:** `BlockEntityRenderer.render(...)` → `createRenderState()` + `extractRenderState(...)` +
   `submit(RenderState, PoseStack, SubmitNodeCollector, CameraRenderState)`; `MultiBufferSource` исчез.
   Образец: `client/render/ChestRenderer` ProjectE в этом репозитории.
5. **Слоты PE** (`ValidatedSlot`, `SlotGhost`, `InventoryContainerSlot`, `SlotPredicates`) принимают
   `ResourceHandler<ItemResource>` — контейнеры аддона надо переводить целиком.
6. **`Item#inventoryTick`** в 26.3 вызывается только на сервере; клиентскую логику предметов выносить в клиентские тики.
7. **`Item#use`** возвращает `InteractionResult`, а не `InteractionResultHolder<ItemStack>`.
8. **Регистрация:** посмотреть, какие классы deferred-регистров использует `PEItems`/`PEBlocks` в 26.3, и привести
   `expansion/registries/*` к тому же API (в 1.21.1 это `ItemDeferredRegister`/`BlockDeferredRegister`).
9. **`Identifier`** вместо `ResourceLocation` — использовать `PECore.rl(...)`, он уже возвращает правильный тип.
10. **Модельные повороты:** в 26.3 ванилла сменила конвенцию `BlockModelGenerators.ROTATION_FACING`
    (для «лежащих» моделей нужен `ROTATIONS_COLUMN_WITH_FACING`) — это уже сделано в датагене PE, у аддона
    ресурсы статикой, проверь, что повороты совпадают с 1.21.1.
11. **События/сеть:** имена NeoForge-типов те же, но сверяйся с кодом PE (например `PacketHandler`).

## Разделение работы

- **Агент A (ядро):** `expansion/util/`, `expansion/registries/`, `expansion/item/`, `expansion/block/`,
  `expansion/block/entity/`, `expansion/capability/`, `expansion/ExpansionCore.java`,
  `expansion/config/Config.java`. Это самый большой кусок: 4 БЭ с инвентарями, блоки, предметы, регистры.
- **Агент B (клиент):** `expansion/gui/`, `expansion/gui/container/**`, `expansion/rendering/`,
  `expansion/client/`, `expansion/integrations/**`.
- **Агент C (сеть/команды/события):** `expansion/net/**`, `expansion/commands/**`, `expansion/events/**`.
- **Главный цикл:** интеграция, нативные замены миксинов в файлах ProjectE, конфиг, теги, датаген, сборка, тесты.

## Отчёт каждого агента (по-русски, до 400 слов)

1. Сколько файлов изменено, что именно переписано (по пунктам: класс → новый API).
2. Что не удалось перевести и почему.
3. Какие выводы/подводные камни нашёл (не чинить, сообщить).
4. Список файлов ProjectE вне своего поддерева, которые нужно поправить (не править самому).
