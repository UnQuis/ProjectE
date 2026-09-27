# Слияние ProjectExpansion на Minecraft 1.20.4 (ветка mc1.20.4, worktree /home/alexquasar/Проекты/PE-1.20.4)

## Контекст

- Содержимое мода ProjectExpansion уже перенесено в ProjectE на 1.21.1 (`/home/alexquasar/Проекты/PE-1.21.1`,
  ветка `mc1.21.1`, там всё зелёное). Код оттуда скопирован в этот репозиторий как
  `src/main/java/moze_intel/projecte/expansion/**` и написан под API 1.21.1.
- Задача: перевести этот код на API 1.20.4 (NeoForge 20.4.239). Ветка `mc1.20.4` сама по себе работает —
  эталон любого API ProjectE 1.20.4 это код в этом же репозитории:
  `src/main/java/moze_intel/projecte/**`, `src/api/java/**`, `src/datagen/java/**`.
- Оригинал кода (1.21.1) лежит в `/home/alexquasar/Проекты/PE-1.21.1/src/main/java/moze_intel/projecte/expansion/`.
  Это эталон **поведения**: если сомневаешься, что делал код, — посмотри туда. Но API там 1.21.1.
- Логику аддона улучшать нельзя. Найденные баги — в отчёт, не чинить.

## Дельты 1.21.1 → 1.20.4 (проверены по исходникам)

1. **`Identifier` → `ResourceLocation`**: `net.minecraft.resources.Identifier` не существует, нужно
   `net.minecraft.resources.ResourceLocation` (`PECore.rl(...)`, `ResourceLocation.fromNamespaceAndPath(...)`).
   У `ResourceKey` в 1.20.4 метод `location()`, а не `identifier()`.
2. **Компоненты предметов → NBT** (главная дельта). В 1.20.4 нет `DataComponentPatch`, `DataComponents`,
   `ItemStackTemplate`, `CustomPacketPayload`-компонент, `stack.getComponentsPatch()`/`setComponentsPatch()`,
   `stack.applyComponentsPatch()`. Всё состояние предмета хранится в `CompoundTag`:
   `stack.getTag()`, `stack.getOrCreateTag()`, `stack.setTag(...)`, `stack.getOrCreateTagElement("key", ...)`.
   Эталон: `moze_intel.projecte.util.NBTProcessor`, `moze_intel.projecte.gameObjs.registries.PEDataComponentTypes`
   (в 1.20.4 это NBT-сериализаторы, не компоненты), `moze_intel.projecte.gameObjs.items.AlchemicalBag`,
   `moze_intel.projecte.impl.capability.TransmutationInventory`, `moze_intel.projecte.api.nss.NSSItem`
   (метод `createItem(ItemStack)` в 1.20.4 берёт NBT из стака).
3. **Сеть**: API совпадает с 1.21.1 (`CustomPacketPayload`, `StreamCodec`, `IPayloadHandler`,
   `RegisterPayloadHandlerEvent`). Регистрация — как в `moze_intel.projecte.network.PacketHandler`.
4. **GUI**: `GuiGraphics` есть, но это 1.20.4: `blit(ResourceLocation, int, int, int, int, int, int, int, float)`,
   `drawString(Font, Component, int, int, int)`, `fill(int,int,int,int,int)`, `renderTooltip(GuiGraphics,int,int)`.
   Экран: `Screen#render(GuiGraphics, int, int, float)`, открытие экрана — `Minecraft#setScreen(Screen)`,
   текущий экран — **поле** `Minecraft#screen` (в 1.21.1 это `minecraft.gui.screen()` и `minecraft.gui.setScreen`).
   Виджеты аддона (`gui/BaseWidget`, `gui/EMCDisplay`) написаны под 1.21.1 — сверяйся с
   `moze_intel.projecte.gameObjs.gui.*` этого репозитория.
5. **Реестры**: `moze_intel.projecte.gameObjs.registration.impl.*` в 1.20.4 (имена и сигнатуры отличаются,
   обязательно сверяйся). `BlockBehaviour.Properties` в 1.20.4 не требует `setId`.
6. **Curios 7.3.2**: эталон — `moze_intel.projecte.integration.curios.CurioItemCapability` этого репозитория
   и `expansion/integrations/curios` версии 1.21.1.
7. **Jade / WTHIT / TOP / JEI / EMI**: эталон регистрации — `moze_intel.projecte.integration.*` этого
   репозитория. Для WTHIT плагины аддона должны быть вписаны в `src/main/resources/wthit_plugins.json` —
   это делает главный цикл, перечисли нужные id в отчёте. То же для JEI (`@JeiPlugin` / регистрация категорий)
   и Jade (id провайдера должен быть `projecte:expansion_provider`, иначе Jade валит игру).
8. **BlockEntity**: 1.20.4 — `saveAdditional(CompoundTag, HolderLookup.Provider)`, `loadAdditional(CompoundTag)`,
   `getUpdateTag(Getter<Nbt>)`, `getUpdatePacket()`, `setChanged()`. Никаких `ValueInput`/`ValueOutput`.
9. **Топливо**: в 1.20.4 `Item#getBurnTime(ItemStack)` — без `RecipeType` и `FuelValues`
   (в 1.21.1 был `getBurnTime(ItemStack, RecipeType, FuelValues)`).
10. **Инвентари**: в 1.20.4 только `net.neoforged.neoforge.items.IItemHandler`/`IItemHandlerModifiable`,
    ничего нового (`ResourceHandler`, `Transaction`, `ItemHandlerHelper`) нет. Ветка 1.21.1 уже на `IItemHandler`,
    поэтому этот слой переносится почти без правок.
11. **Команды**: 1.20.4 `Commands`/`CommandSourceStack` API отличается (`getSource()` → 1.20.4 уже так же,
    но `sendSuccess`/`sendFailure` в 1.21.1 возвращают `void`, а в 1.20.4 — `void`; проверяй по
    `moze_intel.projecte.network.commands.*` этого репозитория).
12. **Тултип/атрибуты/зачарования**: сверяйся с `moze_intel.projecte.gameObjs.items.*` и
    `moze_intel.projecte.config.value.*` этого репозитория.

## Правила

- **Никаких git-команд. Не запускать gradle.** Проверять себя чтением и сравнением с кодом 1.20.4 в этом
  же репозитории.
- Писать **только** в своё поддерево (оно указано в задании). Всё остальное — главный цикл.
- Стиль ProjectE: табы, без `var`, комментарии по-английски, никаких миксинов.
- Классы, которые аддон регистрирует сам (реестры), не должны требовать `setId` и прочего из 26.x.

## Отчёт (по-русски, до 400 слов)

1. Что переведено, по поддеревьям.
2. Что не удалось / что осталось сломанным.
3. Какие файлы **вне своего поддерева** надо поправить: точный путь, что именно нужно (метод, импорт, значение).
4. Найденные баги (не чинить).
