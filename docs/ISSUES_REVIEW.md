# ProjectE — разбор открытых Issues (форк `UnQuis/ProjectE`)

Дата разбора: 2026-09-21.
Репозиторий-источник: https://github.com/sinkillerj/ProjectE (upstream).
Форк: https://github.com/UnQuis/ProjectE (origin).

На момент разбора в upstream открыто **257 issues** (плюс 26 PR — см.
`PR_REVIEW.md`). Все issues просмотрены и классифицированы; полный индекс
по категориям — в конце документа.

Методология: для каждого issue — чтение заголовка/описания, локализация в коде
той ветки, на которую жалуются, сверка с ванильным поведением (для
mc1.21.1 — по исходникам NeoForge 21.1.148, извлечённым из artifacts),
проверка «воспроизводимо ли это в принципе» и есть ли смысл трогать.
Категории и правила см. ниже.

---

## Резюме: что сделано в форке

### 1. Три issues исправлены напрямую (коммиты `0b00a2f0` / `2fec0daf`)

| Issue | Суть | Исправление |
|-------|------|-------------|
| [#2412](https://github.com/sinkillerj/ProjectE/issues/2412) | Спам DEBUG-лога на каждый пакет синхронизации знаний | Удалена строка `PECore.debugLog` в `KnowledgeSyncEmcPKT.handle` (mc1.21.1) |
| [#2431](https://github.com/sinkillerj/ProjectE/issues/2431) | Ввод в поиск стола работает только после клика; первый ввод теряется | `GUITransmutation.init` → `setFocused(true)` при открытии (mc1.21.1, mc1.20.x) |
| [#2433](https://github.com/sinkillerj/ProjectE/issues/2433) | Клавиша E закрывает стол во время ввода в поиск (нельзя набрать «Emerald») | `keyPressed` поглощает ввод, пока поиск фокусирован; доходи до `super.keyPressed` только иначе (mc1.21.1, mc1.20.x) |

Разбор #2431/#2433 подтверждён по ванильным исходникам 1.21.1:
`EditBox.keyPressed` возвращает false для букв (символы идут через `charTyped`),
а `AbstractContainerMenu.doClick` закрывает контейнер по `Escape`/инвентарь —
поэтому буквы «съедались» игрой, а E закрывала меню. Фикс перенесён и на
`mc1.20.x` (`2fec0daf`), т.к. механика там та же.

### 2. Замкнутые на применённые PR (см. `PR_REVIEW.md`)

| Issue | Решает PR | Статус |
|-------|-----------|--------|
| [#2454](https://github.com/sinkillerj/ProjectE/issues/2454) (поиск по id) | #2479 | закрыто применением |
| [#2461](https://github.com/sinkillerj/ProjectE/issues/2461) (краш DM/RM) | #2462 | закрыто применением |
| [#2471](https://github.com/sinkillerj/ProjectE/issues/2471) (NPE buf null) | #2472 | закрыто применением |
| [#2457](https://github.com/sinkillerj/ProjectE/issues/2457) (EMC не насчитывается) | #2475 | вероятно, закрыто (изоляция ошибок маппинга) — проверить на живой сборке |
| [#2435](https://github.com/sinkillerj/ProjectE/issues/2435) (нет EMC у предметов с компонентами) | #2475 | вероятно, закрыто — проверить |
| [#2478](https://github.com/sinkillerj/ProjectE/issues/2478) (custom EMC игнорируется) | #2475 | на 1.21.1 адресуется; на 1.20.1 не переносился |
| [#2329](https://github.com/sinkillerj/ProjectE/issues/2329), [#2229](https://github.com/sinkillerj/ProjectE/issues/2229) (EMC<1 теряется) | #2485 | opt-in `mapping.recover_missing_items` (default false) |
| [#2438](https://github.com/sinkillerj/ProjectE/issues/2438), [#2287](https://github.com/sinkillerj/ProjectE/issues/2287), [#2318](https://github.com/sinkillerj/ProjectE/issues/2318), [#2366](https://github.com/sinkillerj/ProjectE/issues/2366), [#2373](https://github.com/sinkillerj/ProjectE/issues/2373) (SWRG/полёт) | #2481 + фикс `hasSwrgWithEmc` | закрыто/адресовано на mc1.21.1 (см. «SWRG-кластер») |

### 3. Проверено и классифицировано как «не баг», дизайн или уже закрыто

| Issue | Вердикт |
|-------|---------|
| [#2375](https://github.com/sinkillerj/ProjectE/issues/2375) (дуп курсор+Eclipse) | **Уже митигировано**: `SlotOutput.mayPlace()==false` + `remove()` платит EMC + фикс `onSwapCraft` (#1972). Воспроизвести на 1.21.1 нельзя |
| [#2398](https://github.com/sinkillerj/ProjectE/issues/2398) (Crafter выплёвывает камень) | **Ваниль**: Crafter выбрасывает `craftingRemainingItem`; поведение Minecraft, не ProjectE |
| [#2405](https://github.com/sinkillerj/ProjectE/issues/2405) (листва пропадает) | **Ваниль**: естественное опадание листвы |
| [#2390](https://github.com/sinkillerj/ProjectE/issues/2390) (ботинки меняют FOV) | **Математика верна**: GemFeet даёт ровно +0.1 к скорости, компенсация `-0.5*getFovEffectScale` точна для базового случая в 1.21. Edge-кейс — полёт; тянет на репро |
| [#2038](https://github.com/sinkillerj/ProjectE/issues/2038), [#2210](https://github.com/sinkillerj/ProjectE/issues/2210) (FOV ботинок, легаси/пульсация) | Дизайн/ваниль-сглаживание FOV; на 1.12.x-ветках компенсация грубее |
| [#2407](https://github.com/sinkillerj/ProjectE/issues/2407) (9 наггетов → слиток «бесплатно») | **Округление**: наггет=16, 256/9 → 28 вместо 256, т.е. 9 наггетов (144) выдаются как слиток (256); корневой фикс — дробная EMC (#2145), фича, не баг маппинга |
| [#2339](https://github.com/sinkillerj/ProjectE/issues/2339) (потеря крошечной EMC) | Дисциплина округления того же класса, что #2407 |
| [#2147](https://github.com/sinkillerj/ProjectE/issues/2147) (step assist «монополия») | Дизайн god boots |
| [#2254](https://github.com/sinkillerj/ProjectE/issues/2254) (броня → ~0 урона) | Кумулятивная защита ванильной брони; вопрос баланса |
| [#2227](https://github.com/sinkillerj/ProjectE/issues/2227) (Mind Stone не EE2-баланс) | Намеренный баланс форка; не баг |

### 4. SWRG-кластер (полёт с кольцом)

Причины «SWRG не летает / отключается» из #2287/#2318/#2366/#2373/#2438 и
регресс из #2481 — общая: проверка наличия кольца считает только предмет в
руке/хотбаре. На mc1.21.1 исправлено: `InternalAbilities.hasSwrgWithEmc`
проверяет hotbar → offhand → curios, а полёт дополнительно гейтится наличием
EMC. На ветках mc1.19.2/mc1.20.x фикс не переносился (активная ветка — 1.21.1).

### 5. Прочее без изменений (осознанно)

- **#2364** — «стол зависает при потоке обновлений EMC» (1.20.1): перф-класс;
  на 1.21.1 #2480/#2481 заметно снижают аллокации, нужен репро-профиль.
- **#2436 / #2199 / #1912** — Gem of Eternal Density «не активируется»: код
  активации корректен, upstream поставил `cannot-reproduce` для одной из жалоб;
  нужно воспроизведение на текущей сборке.
- **#2429** — достижения при старте мира: не связано с кодом PE.
- **#2460** — `IEMCProxy NoClassDefFoundError` в отдельной сборке NeoForge
  21.1.220: похоже на артефакт сборки/окружения, нужен точный репро.
- **#2417 / #2419 / #2450** — пропажа/отсутствие EMC в паках (ATM10 и др.):
  окружение/конфиг/сторонние моды, ждут репро.
- **#2439** — краш Oreich (Oritech#596), **#2445** — миксин-конфликт
  ArchitecturyAPI: дефекты сторонних модов, не PE.
- Прочие интероп-жалобы (#2392, #2418, #2441, #2443, #2423, #2320, #2299 и
  легаси-аналоги) — нужен репро с логами/на стороне другого мода.

---

## Категории индекса (как читать таблицу ниже)

| Категория | Значение |
|-----------|----------|
| `FIX` | Исправлено в форке (коммит указан) |
| `DONE_BY_PR` | Закрыто применённым PR (коммит в `PR_REVIEW.md`) |
| `DONE_BY_PR?` | Вероятно закрыто применённым PR — проверить на живой сборке |
| `ADDRESSED` | Адресовано на mc1.21.1 (SWRG-фикс и др.) |
| `MITIGATED` | Уже закрыто/смягчено существующим кодом (обычно фикс #1972) |
| `VANILLA` | Ванильное поведение Minecraft, не баг ProjectE |
| `DESIGN` | Осознанное дизайн-решение / математика округления |
| `CONFIG` | Решено через opt-in конфиг (PR #2485) |
| `CANNOT_REPRO` | Upstream пометил `cannot-reproduce` |
| `INTEROP` | Мод-интероп, нужен репро на стороне другого мода |
| `ENV` | Окружение/сборка/конфиг пака, нужен репро |
| `NEEDS_REPRO` | Нужно воспроизведение на текущей версии |
| `ENH` | Feature request (не баг) |
| `OPEN` | Без достаточной информации |
| `LEGACY` | Легаси-версия (до 1.19), ветки вне активной поддержки |

Распределение: FIX 3, DONE_BY_PR 4, DONE_BY_PR? 3, ADDRESSED 6, MITIGATED 6,
VANILLA 2, DESIGN 8, CONFIG 2, CANNOT_REPRO 4, INTEROP 24, ENV 28,
NEEDS_REPRO 8, ENH 91, OPEN 1, LEGACY 67. Итого 257.

---

## Полный индекс открытых issues

### FIX — Исправлено в форке (3)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2433](https://github.com/sinkillerj/ProjectE/issues/2433) | 2025 | Inventory Button in Search Bar Closes Transmutation Table | Исправлено в форке: клавиша E больше не закрывает стол при вводе в поиск (0b00a2f0 / 2fec0daf) |
| [#2431](https://github.com/sinkillerj/ProjectE/issues/2431) | 2025 | latest version seems to not force focus on the text box for the transmutation tablet, allo | Исправлено в форке: принудительный фокус поиска + поглощение key-ввода (0b00a2f0 / 2fec0daf) |
| [#2412](https://github.com/sinkillerj/ProjectE/issues/2412) | 2025 | Debug log spam | Исправлено в форке: убран спам DEBUG-лога KnowledgeSyncEmcPKT (mc1.21.1, 0b00a2f0) |

### DONE_BY_PR — Закрыто применённым PR (4)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2471](https://github.com/sinkillerj/ProjectE/issues/2471) | 2026 | java.lang.NullPointerException: Cannot invoke "net.minecraft.network.FriendlyByteBuf.readB | Исправлено PR #2472 (NPE buf null в TransmutationContainer.fromNetwork) |
| [#2461](https://github.com/sinkillerj/ProjectE/issues/2461) | 2026 | [1.21.1 PE1.1.0] Server crashes on player tick after crafting DM/RM tools — ItemStack.save | Исправлено PR #2462 (краш сервера после крафта DM/RM) |
| [#2454](https://github.com/sinkillerj/ProjectE/issues/2454) | 2026 | Can we search item by Identifier? | Реализовано PR #2479 «search by identifier», применён на mc1.21.1 |
| [#2317](https://github.com/sinkillerj/ProjectE/issues/2317) | 2023 | Search for items on the tablet, the transmutation table. | Поиск предметов на планшете/столе: реализовано PR #2479 (search by name/identifier), применён на mc1.21.1 |

### DONE_BY_PR? — Вероятно закрыто применённым PR (проверить) (3)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2478](https://github.com/sinkillerj/ProjectE/issues/2478) | 2026 | Custom EMC values (values.after / values.conversion) are silently ignored for a specific s | Подмножество предметов без своих EMC — сценарий компонентов, адресуется PR #2475 (1.21.1; на 1.20.1 не переносился) |
| [#2457](https://github.com/sinkillerj/ProjectE/issues/2457) | 2026 | Fails to join singleplayer world | Похоже, закрыто PR #2475 (изоляция ошибок маппинга; нужна проверка на живой сборке) |
| [#2435](https://github.com/sinkillerj/ProjectE/issues/2435) | 2025 | Missing EMC for Potions | Вероятно, улучшено PR #2475 (точный маппинг предметов с компонентами; проверить) |

### ADDRESSED — Адресовано на mc1.21.1 (6)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2438](https://github.com/sinkillerj/ProjectE/issues/2438) | 2025 | swiftwolf's rending gale doesn't work, at all | SWRG не работает (1.19.2): на 1.21.1 решено PR #2481 + наш фикс hasSwrgWithEmc; 1.19.2 не переносили |
| [#2373](https://github.com/sinkillerj/ProjectE/issues/2373) | 2024 | Swiftwolf's rending gale flight | SWRG flight (1.20.1): аналогично, на 1.21.1 исправлено |
| [#2366](https://github.com/sinkillerj/ProjectE/issues/2366) | 2024 | swift wolf rending gale error | SWRG error (1.20.1): тот же класс, на 1.21.1 исправлено PR #2481 |
| [#2363](https://github.com/sinkillerj/ProjectE/issues/2363) | 2024 | Ring of Arcana flight disables. | Ring of Arcana снимает полёт: класс SWRG, на 1.21.1 исправлено PR #2481 + фиксом hasSwrgWithEmc |
| [#2318](https://github.com/sinkillerj/ProjectE/issues/2318) | 2023 | Swiftwolf's Rending Gale stops working when any item is in hand (also heppens with curios) | SWRG останавливается с предметом в руке: лечится нашим фиксом hasSwrgWithEmc (hotbar->offhand->curios) |
| [#2287](https://github.com/sinkillerj/ProjectE/issues/2287) | 2023 | Swiftwolf rending gale not allowing me to fly  | SWRG не летает (старая ветка): на 1.21.1 решено PR #2481 + фикс EMC-гейта полёта |

### MITIGATED — Уже закрыто/смягчено в коде или легаси-фиксом (6)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2375](https://github.com/sinkillerj/ProjectE/issues/2375) | 2025 | Dupe glitch in conjunction with Inventory Profiles Next buttons | Дуп курсор+клик по кругу трансмутации: уже заблокирован (SlotOutput.mayPlace=false, remove платит EMC, фикс #1972) |
| [#2043](https://github.com/sinkillerj/ProjectE/issues/2043) | 2020 | [1.12.2] Shift Clicking out of Transmutation Table takes EMC but does not give stack | Shift-клик из стола забирает EMC без стека (1.12.2): легаси, фикс #1972 |
| [#1854](https://github.com/sinkillerj/ProjectE/issues/1854) | 2019 | Transmutation table EMC decrease bug | Уменьшение EMC в столе (1.12.2): класс дупа, закрыт фиксом #1972 |
| [#1852](https://github.com/sinkillerj/ProjectE/issues/1852) | 2019 | Mouse Tweaks EMC Duplication Glitch | Дуп Mouse Tweaks (1.12.2): легаси, тот же класс #2375; ветку не трогали |
| [#1737](https://github.com/sinkillerj/ProjectE/issues/1737) | 2018 | Transmutation Table EMC Increase Bug | Увеличение EMC в столе (1.12.2): класс дупа, закрыт фиксом #1972 |
| [#967](https://github.com/sinkillerj/ProjectE/issues/967) | 2015 | Rapid shift-clicking pulls out extra stack | Быстрый shift-клик вытаскивает лишний стек (1.12.2): легаси, покрыт фиксом #1972 (onSwapCraft) |

### VANILLA — Ванильное поведение (не баг) (2)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2405](https://github.com/sinkillerj/ProjectE/issues/2405) | 2025 | All leaves disappearing when placed with Mercurial Eye and then broken | Листва пропадает — обычное ванильное опадание, не баг ProjectE |
| [#2398](https://github.com/sinkillerj/ProjectE/issues/2398) | 2025 | [1.21.1] Crafter Spits Out Philosopher's Stone | Crafter выплёвывает камень — штатное поведение ванильного Crafter для предметов с craftingRemainingItem |

### DESIGN — Дизайн-решение / математика (8)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2407](https://github.com/sinkillerj/ProjectE/issues/2407) | 2025 | Free EMC bug | «Бесплатная» EMC 9 наггетов->слиток: 256/9=28 из-за целочисленного округления; фикс = дробная EMC (#2145) |
| [#2390](https://github.com/sinkillerj/ProjectE/issues/2390) | 2025 | 1.21 gem boots mess with fov | FOV ботинок: компенсация -0.5*fovEffectScale математически точна для базовой скорости в 1.21 (GemFeet даёт ровно +0.1); edge-кейсы — полёт |
| [#2339](https://github.com/sinkillerj/ProjectE/issues/2339) | 2024 | Extracting and re-inserting maximally-valuable items voids relatively tiny amounts of EMC  | Потеря крошечной EMC при извлечении/вставке — округление формата |
| [#2254](https://github.com/sinkillerj/ProjectE/issues/2254) | 2022 | why is one piece of red matter armor enough to make all damage basically 0 | Броня даёт ~0 урона: кумулятивная защита ванильной брони + моды; вопрос баланса, не баг |
| [#2227](https://github.com/sinkillerj/ProjectE/issues/2227) | 2022 | Discrepancy: Mind Stone not giving 1 level per use as in original EE2 | Mind Stone: 1 уровень за использование против «как в оригинальном EE2» — намеренный баланс ветки, не баг |
| [#2210](https://github.com/sinkillerj/ProjectE/issues/2210) | 2022 | Pulsing FOV/Speed from boots | Пульсирующий FOV/скорость ботинок: сглаживание ванильного tickFov + атрибут скорости |
| [#2147](https://github.com/sinkillerj/ProjectE/issues/2147) | 2021 | Step Assist Monopoly | Step Assist Monopoly: god boots поднимают на любой блок |
| [#2038](https://github.com/sinkillerj/ProjectE/issues/2038) | 2020 | [ProjectE-1.12.2-PE1.4.1 - forge-14.23.5.2854] Gem Boots decrease FOV drastically even aft | Gem boots уменьшают FOV (1.12.2): легаси-компенсация была грубее; на 1.21.1 — как #2390 |

### CONFIG — Одобрено через конфиг (PR #2485) (2)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2329](https://github.com/sinkillerj/ProjectE/issues/2329) | 2024 | Items With EMC Values Calculated Under 1 Don't Default To 1 When Config Says To | EMC<1 не доводится до 1: добавлен opt-in mapping.recover_missing_items (PR #2485); дефолт — осознанное усечение |
| [#2229](https://github.com/sinkillerj/ProjectE/issues/2229) | 2022 | Integer EMC causes issues with small items | Целочисленное округление EMC -> opt-in mapping.recover_missing_items (PR #2485) |

### CANNOT_REPRO — Помечено can't-reproduce (4)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2266](https://github.com/sinkillerj/ProjectE/issues/2266) | 2023 | Philosopher stone acting like Destruction catalyst | Метка can't-reproduce |
| [#2199](https://github.com/sinkillerj/ProjectE/issues/2199) | 2022 | [cannot-reproduce]Gem of Eternal Density functioning while out of inventory | Gem of Eternal Density работает извне инвентаря (cannot-reproduce) |
| [#2136](https://github.com/sinkillerj/ProjectE/issues/2136) | 2021 | evertide amulet makes you walk on water, making water uninteractable | Evertide позволяет ходить по воде (can't-reproduce) |
| [#2133](https://github.com/sinkillerj/ProjectE/issues/2133) | 2021 | Incorrect EMC prediction | Неверный EMC-прогноз (can't-reproduce) |

### INTEROP — Мод-интероп (нужен репро на стороне другого мода) (24)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2445](https://github.com/sinkillerj/ProjectE/issues/2445) | 2025 | 1.21.1newForge runs concurrently with the ArchitecturyAPI on the corresponding server vers | ArchitecturyAPI: миксин-конфликт при загрузке — конфликт загрузчика, не код PE |
| [#2443](https://github.com/sinkillerj/ProjectE/issues/2443) | 2025 | Roost Ultimate – Chicken Seeds Do Not Replant When Harvested by ProjectE Pedestal (Goddess | Roost Ultimate не пересаживает семена — поведение Harvest Band со сторонним модом |
| [#2441](https://github.com/sinkillerj/ProjectE/issues/2441) | 2025 | Red Morningstar ability duplicates Powah's Player Transmitters | Red Morningstar дублирует Powah Transmitters — интероп; нужен репро |
| [#2439](https://github.com/sinkillerj/ProjectE/issues/2439) | 2025 | [Bug] Ticking GridNode \| Cannot invoke "rearth.oritech.api.item.ItemApi$InventoryStorage. | Oritech GridNode NPE — краш стороннего мода (Oritech#596) |
| [#2423](https://github.com/sinkillerj/ProjectE/issues/2423) | 2025 | How can i get the harvest band effect on crops of other mod | Harvest Band на чужих культурах — зависит от реализации мода |
| [#2418](https://github.com/sinkillerj/ProjectE/issues/2418) | 2025 | Ghost Item? | Ghost item (GTCEu+AE2): нужен репро |
| [#2414](https://github.com/sinkillerj/ProjectE/issues/2414) | 2025 | Incompatibility Between ProjectE, KubeJS, and Recipe Loading | KubeJS + рецепты: нужен репро с логами |
| [#2392](https://github.com/sinkillerj/ProjectE/issues/2392) | 2025 | There is a bug with sophisticated backpacks and projecte | Sophisticated Backpacks: shift-клик рюкзака в стол — нужен фильтр/фикс на стороне мода |
| [#2320](https://github.com/sinkillerj/ProjectE/issues/2320) | 2023 | Harvest Band breaking whole crop instead of just the top | Harvest band ломает всю культуру — зависит от мода растений |
| [#2299](https://github.com/sinkillerj/ProjectE/issues/2299) | 2023 | You can duplicate items with sophisticated backpack | Дуп с sophisticated backpack (1.16.5) |
| [#2238](https://github.com/sinkillerj/ProjectE/issues/2238) | 2022 | Interaction between Energy Condenser Mk 1 and Inventory Tweaks sorting function destroy co | Energy Condenser + сортировка Inventory Tweaks ломают содержимое (1.12.2) |
| [#2212](https://github.com/sinkillerj/ProjectE/issues/2212) | 2022 | Lucraft energy storage item + ProjectE transumation table crash | Краш стола с Lucraft energy item — хранилище-энергия стороннего мода, нужен репро |
| [#2190](https://github.com/sinkillerj/ProjectE/issues/2190) | 2022 | Incompatibility with fairylights | Несовместимость с fairylights (1.12.2) |
| [#2168](https://github.com/sinkillerj/ProjectE/issues/2168) | 2021 | harvest goddes band not activating | harvest band не активируется (1.12.2) |
| [#2040](https://github.com/sinkillerj/ProjectE/issues/2040) | 2020 | [1.12.2] Compatability bug with BonsaiTree's Hopping Bonsai Pot | Hopping Bonsai Pot (1.12.2) |
| [#1990](https://github.com/sinkillerj/ProjectE/issues/1990) | 2020 | [MC - 1.15.2] mod interaction - Quark | Quark (1.15.2) |
| [#1817](https://github.com/sinkillerj/ProjectE/issues/1817) | 2019 | ProjectE-1.12.2-PE1.4.0 stops JEI showing Advanced Rocketry recipes | ProjectE ломает JEI Advanced Rocketry (1.12.2) |
| [#1700](https://github.com/sinkillerj/ProjectE/issues/1700) | 2018 | Duplication bug with farming station. | Дуп с farming station (1.12.2) |
| [#1677](https://github.com/sinkillerj/ProjectE/issues/1677) | 2018 | Equivalent Energistics problem - recipes disappear shortly after owner logs off | Equivalent Energistics рецепты пропадают |
| [#1578](https://github.com/sinkillerj/ProjectE/issues/1578) | 2017 | relays do not accept items using pipes etc... | Реле не принимают предметы из труб |
| [#1491](https://github.com/sinkillerj/ProjectE/issues/1491) | 2017 | Can't get Harvest Goddess Ring to work on pedestal | Harvest ring на pedestal (1.12.2) |
| [#1360](https://github.com/sinkillerj/ProjectE/issues/1360) | 2016 | Harvest goddess band bug? | Harvest goddess band (1.11.2) |
| [#1215](https://github.com/sinkillerj/ProjectE/issues/1215) | 2016 | Harvest goddess band with Agricraft | Harvest band + Agricraft (1.9.4) |
| [#875](https://github.com/sinkillerj/ProjectE/issues/875) | 2015 | Harvest godess band + Immersive Engineering Crops | Harvest band + Immersive Engineering |

### ENV — Окружение/сборка (нужен репро) (28)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2419](https://github.com/sinkillerj/ProjectE/issues/2419) | 2025 | Not auto assigning EMC values | Не присваиваются EMC крафт-объектам — окружение; есть mclo.gs, ждёт разбора |
| [#2417](https://github.com/sinkillerj/ProjectE/issues/2417) | 2025 | No EMC values | ATM10: нет EMC — окружение пака/конфиг; нужен репро |
| [#2337](https://github.com/sinkillerj/ProjectE/issues/2337) | 2024 | [1.20.1] ProjectE crashed Minecraft after loading the world | Краш после загрузки мира (1.20.1) — окружение, нужен репро с логом |
| [#2323](https://github.com/sinkillerj/ProjectE/issues/2323) | 2024 | [1.20.1] EMC values missing and blank mapping.toml file. | Пустой mapping.toml (1.20.1): конфиг/прегенерация; нужен репро |
| [#2274](https://github.com/sinkillerj/ProjectE/issues/2274) | 2023 | ProjectE crashing with forge | Краш с Forge: окружение, нужен репро с полным логом |
| [#2260](https://github.com/sinkillerj/ProjectE/issues/2260) | 2022 | customemc value not working  | customemc value не работает (1.12.2) — легаси/окружение |
| [#2259](https://github.com/sinkillerj/ProjectE/issues/2259) | 2022 | All my items vanished in my transmutation table | Пропали предметы из стола (1.16.5) |
| [#2258](https://github.com/sinkillerj/ProjectE/issues/2258) | 2022 | Opening transmutation table with too many learned recipes freezes client | Фриз при открытии стола с большим knowledge (1.12.2) — легаси, производительность |
| [#2232](https://github.com/sinkillerj/ProjectE/issues/2232) | 2022 | EMC not showing | EMC не отображается — окружение, нужен репро |
| [#2209](https://github.com/sinkillerj/ProjectE/issues/2209) | 2022 | everything lost the EMC value while it run on the server | Всё потеряло EMC на сервере (1.12.2) — легаси/окружение |
| [#2207](https://github.com/sinkillerj/ProjectE/issues/2207) | 2022 | EMC Deleted After Death | EMC пропадает после смерти (1.12.2) |
| [#2204](https://github.com/sinkillerj/ProjectE/issues/2204) | 2022 | Lost emc values on items that previously had them | Потеряны EMC (1.12.2), затронуты klein stars |
| [#2201](https://github.com/sinkillerj/ProjectE/issues/2201) | 2022 | No emc in "dimensional shard" from rftools dimensions mod | Нет EMC у dimensional shard (1.12.2) — сторонний мод |
| [#2171](https://github.com/sinkillerj/ProjectE/issues/2171) | 2022 | emc and item gone | emc and item gone (1.12.2) |
| [#2156](https://github.com/sinkillerj/ProjectE/issues/2156) | 2021 | all my items from the transmutation table are gone but the EMC continues (I can't sell/buy | Все предметы пропали, EMC остался (1.12.2) |
| [#2067](https://github.com/sinkillerj/ProjectE/issues/2067) | 2020 | Project E dev environment dependency failure | Проблема dev-окружения (1.14+) |
| [#2062](https://github.com/sinkillerj/ProjectE/issues/2062) | 2020 | Calculating EMC values of crafting items after adding EMC to Base ingredients (AE2, i know | E MC после добавления base (AE2, 1.12.2) |
| [#2061](https://github.com/sinkillerj/ProjectE/issues/2061) | 2020 | deleted everything in the tablet | Удалилось всё в планшете (1.12.2) |
| [#2028](https://github.com/sinkillerj/ProjectE/issues/2028) | 2020 | OreDictionary / Unlocalised Name Custom EMC Not Working | OreDictionary custom EMC не работает (1.12.2) |
| [#1950](https://github.com/sinkillerj/ProjectE/issues/1950) | 2020 | No EMC values | Нет EMC-значений (1.12.2) |
| [#1911](https://github.com/sinkillerj/ProjectE/issues/1911) | 2019 | Coal doesn't show an EMC value | Уголь без EMC (1.12.2) |
| [#1885](https://github.com/sinkillerj/ProjectE/issues/1885) | 2019 | ProjectE - Can't get fluid X | Не получить fluid X (1.12.2) |
| [#1879](https://github.com/sinkillerj/ProjectE/issues/1879) | 2019 | Server unable to start due to adding EMC to some AE2 Items | Сервер не стартует из-за AE2 EMC (1.12.2) |
| [#1861](https://github.com/sinkillerj/ProjectE/issues/1861) | 2019 | World Stays Loading, GPU Usage drops to 0% | Мир завис, GPU 0% |
| [#1828](https://github.com/sinkillerj/ProjectE/issues/1828) | 2019 | Stack EMC not working with AE2 | Stack EMC не работает с AE2 (1.12.2) |
| [#1787](https://github.com/sinkillerj/ProjectE/issues/1787) | 2018 | Transmutation table not showing items | Стол не показывает предметы (1.12.2) |
| [#1372](https://github.com/sinkillerj/ProjectE/issues/1372) | 2016 | FATAL: Exception during Mapping Collection from Mapper | FATAL при mapping collection (1.12.2) — тот же класс #2457; в новых версиях изолировано |
| [#962](https://github.com/sinkillerj/ProjectE/issues/962) | 2015 | Issues setting EMC values for some items | Проблемы установки EMC для некоторых предметов |

### NEEDS_REPRO — Нужно воспроизведение (8)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2460](https://github.com/sinkillerj/ProjectE/issues/2460) | 2026 | NoClassDefFoundError / IllegalStateException when initializing IEMCProxy on NeoForge 1.21. | IEMCProxy NoClassDefFoundError: ServiceLoader в конкретной сборке NeoForge (21.1.220); нужен точный репро |
| [#2450](https://github.com/sinkillerj/ProjectE/issues/2450) | 2026 | [1.20.1]  When I log in to the server, the transmutation table is reset. | Сброс стола при входе (1.20.1): синхронизация knowledge/EMC при релогине; нужен репро |
| [#2436](https://github.com/sinkillerj/ProjectE/issues/2436) | 2025 | Gem of Eternal Density can't work !!! | Gem of Eternal Density не активируется (1.20.1): код активации корректен; нужно репро на текущей сборке |
| [#2429](https://github.com/sinkillerj/ProjectE/issues/2429) | 2025 | Achievments bugged on start | Достижения на старте мира: не связано с кодом PE; нужен репро (ванильные advancement/world data) |
| [#2364](https://github.com/sinkillerj/ProjectE/issues/2364) | 2024 | Transmutation Table hanging the client when too many updates to EMC happen | TM зависает при потоке обновлений EMC (1.20.1): перф-класс; на 1.21.1 PR #2480/#2481 снижают аллокации, нужен репро/профиль |
| [#2338](https://github.com/sinkillerj/ProjectE/issues/2338) | 2024 | [1.20.1] Destruction catalyst not working as expected | Destruction catalyst не работает как ожидалось (1.20.1) |
| [#2248](https://github.com/sinkillerj/ProjectE/issues/2248) | 2022 | RM Furnace not auto feeding from extra slots | RM Furnace не самоподаётся из лишних слотов — нужен репро |
| [#1912](https://github.com/sinkillerj/ProjectE/issues/1912) | 2019 | gem of eternal density not activating | gem eternal density не активируется (1.12.2) — легаси |

### ENH — Feature request (91)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2459](https://github.com/sinkillerj/ProjectE/issues/2459) | 2026 | (1.20.1) Improvements to EMC difficulty tuning | Тюнинг сложности EMC (1.20.1) — набор запросов фич, не реализовано |
| [#2427](https://github.com/sinkillerj/ProjectE/issues/2427) | 2025 | Setting emc values with tags? | Задание EMC тегами — дизайн/запрос фичи |
| [#2426](https://github.com/sinkillerj/ProjectE/issues/2426) | 2025 | ［请求 物品 功能］批量定物品EMC | Массовая установка EMC — запрос фичи (批量定物品EMC) |
| [#2411](https://github.com/sinkillerj/ProjectE/issues/2411) | 2025 | Storage of enchanted items | Хранение зачарованных предметов: поведение/решение конфига; в новых версиях опции нет, запрос фичи |
| [#2408](https://github.com/sinkillerj/ProjectE/issues/2408) | 2025 | interdiction torches missing a couple of original features. | Метка enhancement/feature |
| [#2380](https://github.com/sinkillerj/ProjectE/issues/2380) | 2025 | Feature Request/Suggestion: Covalence Loss defaults, but also Covalence gain attribute tha | Feature request: коваленс-потери/прирост per-player; не реализовано |
| [#2372](https://github.com/sinkillerj/ProjectE/issues/2372) | 2024 | 1.20.1 - Unable to add botany pots to blacklist for watch of flowing time | Blacklist ботанических горшков для Watch of Flowing Time (1.20.1): запорос конфига |
| [#2341](https://github.com/sinkillerj/ProjectE/issues/2341) | 2024 | Is there a way to Set EMC Value to a whole mod? | Установка EMC на целый мод — запрос фичи |
| [#2302](https://github.com/sinkillerj/ProjectE/issues/2302) | 2023 | How do I go about adding EMC values through datapacks? (NOT THE IN GAME COMMAND.) | Как задавать EMC датапаками — вопрос/документация, не фича-реализация |
| [#2234](https://github.com/sinkillerj/ProjectE/issues/2234) | 2022 | Void Ring | Void Ring — вопрос/запрос фичи |
| [#2221](https://github.com/sinkillerj/ProjectE/issues/2221) | 2022 | Cannot modify or add Philosopher's Stone World Transmutations via a datapack | Невозможно менять трансмутации камня датапаком |
| [#2213](https://github.com/sinkillerj/ProjectE/issues/2213) | 2022 | [Feature Request] Ability to empty alchemical bags | Опустошение alchemical bags |
| [#2206](https://github.com/sinkillerj/ProjectE/issues/2206) | 2022 | [Feature Request] Configuration options for energy collectors, anti matter relays, and ene | Конфигурация коллекторов/реле: запрос фичи |
| [#2172](https://github.com/sinkillerj/ProjectE/issues/2172) | 2022 | Third Person Item Model unfinished | Модель предмета в третьем лице недоделана |
| [#2145](https://github.com/sinkillerj/ProjectE/issues/2145) | 2021 | [Feature Request] allow fractional EMC with perBlock property | Фича: дробная EMC/per-block; не реализовано |
| [#2085](https://github.com/sinkillerj/ProjectE/issues/2085) | 2021 | Feature request: return of the /projectE reloadEmc command | Вернуть /projectE reloadEmc |
| [#2072](https://github.com/sinkillerj/ProjectE/issues/2072) | 2020 | [1.14+] A Couple of Projectile Suggestions | Предложения по снарядам |
| [#2071](https://github.com/sinkillerj/ProjectE/issues/2071) | 2020 | Fuel textures off by a pixel | Текстуры топлива не пиксель съехали |
| [#2065](https://github.com/sinkillerj/ProjectE/issues/2065) | 2020 | Enhancement | Enhancement |
| [#2046](https://github.com/sinkillerj/ProjectE/issues/2046) | 2020 | [1.12.2 Suggestion] Blacklist Items From The Transmutation Table | Blacklist предметов из стола (1.12.2) |
| [#2036](https://github.com/sinkillerj/ProjectE/issues/2036) | 2020 | [Feature Request] Config to disable ability to take certain items from tablet | Конфиг запрета выноса предметов из планшета |
| [#2032](https://github.com/sinkillerj/ProjectE/issues/2032) | 2020 | "R" key autopopulating items to left side of transmutation tablet | Клавиша R автоподстановка слева от планшета |
| [#2026](https://github.com/sinkillerj/ProjectE/issues/2026) | 2020 | Repair Tailsman should consume EMC to repair items | Repair talisman должен тратить EMC |
| [#2022](https://github.com/sinkillerj/ProjectE/issues/2022) | 2020 | Shulker box contents aren't taken into account when shulker box is burned for EMC | Содержимое шалкеров учитывать при сжигании в EMC |
| [#2012](https://github.com/sinkillerj/ProjectE/issues/2012) | 2020 | [Request] Config Options to Adjust the EMC usage of Items | Конфиг EMC-использования предметов |
| [#1992](https://github.com/sinkillerj/ProjectE/issues/1992) | 2020 | Silly Question - Why is the Energy Condensor Mk1 2,450,655 EMC? | Вопрос про EMC конденсатора Mk1 |
| [#1952](https://github.com/sinkillerj/ProjectE/issues/1952) | 2020 | [1.14.4-PE1.0.0B] [suggestion] Scale EMC/tick production for PE-machines, not their TPS | Масштаб не TPS, а EMC/tick |
| [#1949](https://github.com/sinkillerj/ProjectE/issues/1949) | 2019 | How can i change what i create with Philosopher's Stone? | Как менять создаваемое камнем |
| [#1924](https://github.com/sinkillerj/ProjectE/issues/1924) | 2019 | Projecte Transmutation Table Help please!  | Помощь по столу |
| [#1920](https://github.com/sinkillerj/ProjectE/issues/1920) | 2019 | [Suggestion] Adjustable Rate for Repair Talisman in Inventory | Регулируемая скорость repair talisman |
| [#1906](https://github.com/sinkillerj/ProjectE/issues/1906) | 2019 | Suggestion: Add config option to disable Gravity Greaves's offensive ability | Конфиг отключения оборонительной способности greaves |
| [#1903](https://github.com/sinkillerj/ProjectE/issues/1903) | 2019 | Suggestion of a small feature -- EMC Portable Inventory instead of the item holding other  | Портативный EMC-инвентарь |
| [#1893](https://github.com/sinkillerj/ProjectE/issues/1893) | 2019 | Request for Mapper control | Контроль мапперов |
| [#1890](https://github.com/sinkillerj/ProjectE/issues/1890) | 2019 | [Suggestion] Equivalent Exchange 1 transmutations? | Эквивалентный обмен 1 трансмутации |
| [#1886](https://github.com/sinkillerj/ProjectE/issues/1886) | 2019 | [Suggestion] Option to apply an exponentially increasing penalty when retrieving items | Экспоненциальный штраф при возврате |
| [#1853](https://github.com/sinkillerj/ProjectE/issues/1853) | 2019 | [Suggestion] Calculate Conversions with multiple outputs | Расчёт конверсий с несколькими выходами |
| [#1801](https://github.com/sinkillerj/ProjectE/issues/1801) | 2019 | implement mutable offline transmutation knowledge | Оффлайн mutable transmutation knowledge |
| [#1775](https://github.com/sinkillerj/ProjectE/issues/1775) | 2018 | Gravity Greaves cannot be disabled | Gravity Greaves нельзя отключить |
| [#1767](https://github.com/sinkillerj/ProjectE/issues/1767) | 2018 | [Suggestion] Early game one way EMC trashcan | Односторонний мусоропровод EMC ранней игры |
| [#1762](https://github.com/sinkillerj/ProjectE/issues/1762) | 2018 | [Suggestion] Different choices for difficulty change | Разные варианты сложности |
| [#1741](https://github.com/sinkillerj/ProjectE/issues/1741) | 2018 | [Suggestion] Config option for autofocus | Конфиг autofocus (1.12.2) |
| [#1740](https://github.com/sinkillerj/ProjectE/issues/1740) | 2018 | A question/feature request about setting EMC values | Вопрос/фича об установке EMC |
| [#1711](https://github.com/sinkillerj/ProjectE/issues/1711) | 2018 | [1.12.2] Question Goddess Band with Mystical | Вопрос Goddess Band + Mystical (1.12.2) |
| [#1693](https://github.com/sinkillerj/ProjectE/issues/1693) | 2018 | [Question] Add EMC values for mod integration | EMC для мод-интеграции |
| [#1673](https://github.com/sinkillerj/ProjectE/issues/1673) | 2018 | Disable EMC for items with NBT tag (ex.: non empty Storage Disk from RefinedStorage) | Отключить EMC для NBT-предметов (storage disk) |
| [#1664](https://github.com/sinkillerj/ProjectE/issues/1664) | 2018 | Suggestion: separate API Jar | Отдельный API jar |
| [#1657](https://github.com/sinkillerj/ProjectE/issues/1657) | 2018 | [Suggestion] 'Burnable' NBT Tag | 'Burnable' NBT tag |
| [#1635](https://github.com/sinkillerj/ProjectE/issues/1635) | 2018 | [Request] optimizing of the TileEnities | Оптимизация TileEntities |
| [#1592](https://github.com/sinkillerj/ProjectE/issues/1592) | 2017 | Add handleKnowledge helper to API | handleKnowledge helper в API |
| [#1591](https://github.com/sinkillerj/ProjectE/issues/1591) | 2017 | Cached knowledge provider not sufficient | Cached knowledge provider |
| [#1590](https://github.com/sinkillerj/ProjectE/issues/1590) | 2017 | Is this mod on Gradle? | Мод на Gradle? (вопрос) |
| [#1501](https://github.com/sinkillerj/ProjectE/issues/1501) | 2017 | Removing EMC values of an entire mod | Удаление EMC целого мода |
| [#1472](https://github.com/sinkillerj/ProjectE/issues/1472) | 2017 | [1.10.2][Suggestion] DM and RM damage reduction too high | Урон DM/RM слишком высок (1.10.2) |
| [#1438](https://github.com/sinkillerj/ProjectE/issues/1438) | 2016 | [1.10.2][Suggestion] Move cooldown management to DMPedestal | КД управления в DMPedestal |
| [#1402](https://github.com/sinkillerj/ProjectE/issues/1402) | 2016 | [1.10.2] Matter Block Texture a little off | Текстура matter block чуть съехала (1.10.2) |
| [#1362](https://github.com/sinkillerj/ProjectE/issues/1362) | 2016 | [Suggestion] Fuel Matter and Matter Matter | Топливо/Matter |
| [#1344](https://github.com/sinkillerj/ProjectE/issues/1344) | 2016 | Covalence Dust Exploit | Covalence dust exploit |
| [#1140](https://github.com/sinkillerj/ProjectE/issues/1140) | 2016 | [request] Blacklist/whitelist of entities not to be affected by SWRG pedestal | Black/whitelist сущностей для SWRG pedestal |
| [#1122](https://github.com/sinkillerj/ProjectE/issues/1122) | 2015 | [Request #2] Red Katar + Rubber Wood Integration (MFR) with code | Red Katar + Rubber Wood (MFR) |
| [#1120](https://github.com/sinkillerj/ProjectE/issues/1120) | 2015 | [Request] Red Matter Morning Star toggley thingamabob | Red Matter Morning Star toggle |
| [#1117](https://github.com/sinkillerj/ProjectE/issues/1117) | 2015 | [Request] Gem Armor Chestplate should use EMC | Gem chestplate должен тратить EMC |
| [#1098](https://github.com/sinkillerj/ProjectE/issues/1098) | 2015 | Improvement concept for Repair Talismans | Улучшение repair talisman |
| [#1093](https://github.com/sinkillerj/ProjectE/issues/1093) | 2015 | Upgraded gem of eternal destiny | Улучшенный gem of eternal destiny |
| [#1082](https://github.com/sinkillerj/ProjectE/issues/1082) | 2015 | Enchancement for the mining tool | Апгрейд горного инструмента |
| [#1024](https://github.com/sinkillerj/ProjectE/issues/1024) | 2015 | [Requests] Smoother flight and Disabling AoE mining function | Плавный полёт и отключение AoE |
| [#935](https://github.com/sinkillerj/ProjectE/issues/935) | 2015 | [suggestion] covalent infused tools | covalent infused tools |
| [#891](https://github.com/sinkillerj/ProjectE/issues/891) | 2015 | Item Consumption Enhancements | Item consumption enhancements |
| [#823](https://github.com/sinkillerj/ProjectE/issues/823) | 2015 | In game guide book | Встроенная книга-гайд |
| [#790](https://github.com/sinkillerj/ProjectE/issues/790) | 2015 | Show stored EMC in condensers | Показать накопленную EMC в конденсаторах |
| [#789](https://github.com/sinkillerj/ProjectE/issues/789) | 2015 | [Suggestion] Pedestal Balancing-EMC Usage | Балансировка pedestal по EMC |
| [#784](https://github.com/sinkillerj/ProjectE/issues/784) | 2015 | [Suggestion] Ability to add EMC to items but not able to learn the item | Уметь давать EMC без изучения предмета |
| [#760](https://github.com/sinkillerj/ProjectE/issues/760) | 2015 | [Suggestion] Pickaxe and Morningstar change | Pickaxe and Morningstar changes |
| [#707](https://github.com/sinkillerj/ProjectE/issues/707) | 2015 | [SUGGESTION] Disable bonemeal effect Harvest Goddess Band in DM Pedestal | Отключить bonemeal эффект harvest band на pedestal |
| [#697](https://github.com/sinkillerj/ProjectE/issues/697) | 2015 | Improve gravitateEntityTowards method | Улучшить gravitateEntityTowards |
| [#599](https://github.com/sinkillerj/ProjectE/issues/599) | 2015 | [Feature] Silk-Touch mode for Red Morningstar? | Silk-Touch для Red Morningstar |
| [#596](https://github.com/sinkillerj/ProjectE/issues/596) | 2015 | Non-Pedestal Tooltips for pedestal-Items missing | Tooltips для pedestal-предметов |
| [#594](https://github.com/sinkillerj/ProjectE/issues/594) | 2015 | Tooltips: show durability EMC loss/stored EMC gain | Tooltips: показывать потери/выгоду EMC по долговечности |
| [#555](https://github.com/sinkillerj/ProjectE/issues/555) | 2015 | [Request] Ability to add ores to the dark/red matter pick r.click ability | Добавлять руды для ПКМ-способности кирки |
| [#552](https://github.com/sinkillerj/ProjectE/issues/552) | 2015 | Ideas for PE-Addons | Идеи PE-аддонов |
| [#549](https://github.com/sinkillerj/ProjectE/issues/549) | 2015 | Things about Harvest Goddess Band on Pedestals.  | Про harvest band на pedestal |
| [#534](https://github.com/sinkillerj/ProjectE/issues/534) | 2015 | Talisman of Repair EMC | Talisman of Repair EMC |
| [#525](https://github.com/sinkillerj/ProjectE/issues/525) | 2015 | [Request] Watch of flowing time render | Рендер Watch of Flowing Time |
| [#519](https://github.com/sinkillerj/ProjectE/issues/519) | 2015 | Feature Request - Collector Config | Конфиг коллектора |
| [#472](https://github.com/sinkillerj/ProjectE/issues/472) | 2015 | [SUGGESTION] New features for the fluid amulets | Новые фичи амулетов жидкостей |
| [#471](https://github.com/sinkillerj/ProjectE/issues/471) | 2015 | Request for upcoming pack | Запрос для будущего пака |
| [#428](https://github.com/sinkillerj/ProjectE/issues/428) | 2015 | Idea: Fluid Transmutation | Fluid Transmutation |
| [#424](https://github.com/sinkillerj/ProjectE/issues/424) | 2015 | SUGGESTION: /projecte_addEMC command gui | ГУИ команды /projecte_addEMC |
| [#410](https://github.com/sinkillerj/ProjectE/issues/410) | 2015 | [Suggestion] Bow evolution branch | Эволюция лука |
| [#306](https://github.com/sinkillerj/ProjectE/issues/306) | 2014 | Multiplayer Tables and Stuff | Мультиплеерные столы и что-то ещё |
| [#301](https://github.com/sinkillerj/ProjectE/issues/301) | 2014 | back to ae2 support | Назад к поддержке AE2 |
| [#225](https://github.com/sinkillerj/ProjectE/issues/225) | 2014 | Small change to energy condensers GUI | Изменения ГУИ конденсаторов |

### OPEN — Не разобран детально (1)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2283](https://github.com/sinkillerj/ProjectE/issues/2283) | 2023 | 2 bug | Заголовок «2 bug» без деталей — нет информации |

### LEGACY — Легаси-версия (до 1.19) (67)

| # | Год | Тема | Вердикт |
|---|---|---|---|
| [#2314](https://github.com/sinkillerj/ProjectE/issues/2314) | 2023 | [1.16.5] Adding some mods may break EMC entirely | Некоторые моды ломают EMC (1.16.5): класс изоляции маппинга; на 1.21.1 адресуется PR #2475, ветка не трогалась |
| [#2277](https://github.com/sinkillerj/ProjectE/issues/2277) | 2023 | Dark matter pedestal not working (version ProjectE-1.7.10-PE1.10.1.jar) | Dark matter pedestal (1.7.10): легаси |
| [#2275](https://github.com/sinkillerj/ProjectE/issues/2275) | 2023 | Creative flight and Rending Gale not working (no crash) | Creative flight и Rending Gale (1.16.5): легаси |
| [#2269](https://github.com/sinkillerj/ProjectE/issues/2269) | 2023 | repair talisman does not repair tools and armour from random loot mod despite being added  | Repair talisman не чинит лут модов (1.12.x) — NBT whitelist, легаси |
| [#2202](https://github.com/sinkillerj/ProjectE/issues/2202) | 2022 | Flight doesn't work correctly with the ring | Полёт кольца работает неверно (1.12.2) |
| [#2200](https://github.com/sinkillerj/ProjectE/issues/2200) | 2022 | i cant climb ladders or vines when other mods are active | Не лезет по лестницам с модами (1.12.2) |
| [#2184](https://github.com/sinkillerj/ProjectE/issues/2184) | 2022 | Error happens with klein star | Ошибка с klein star (1.12.2) |
| [#2183](https://github.com/sinkillerj/ProjectE/issues/2183) | 2022 | mod dont load | mod dont load (1.12.2) |
| [#2152](https://github.com/sinkillerj/ProjectE/issues/2152) | 2021 | idk if this is new in version 1.16.5-PE1.0.1B but it wont allow me to use the abyss helmet | Абилки abyss helmet/infernal chestplate (1.16.5) |
| [#2141](https://github.com/sinkillerj/ProjectE/issues/2141) | 2021 | [1.12.2] Game crashed while initializing the game | Краш при инициализации (1.12.2) |
| [#2122](https://github.com/sinkillerj/ProjectE/issues/2122) | 2021 | Infinite EMC (1.16.5) | Infinite EMC (1.16.5) |
| [#2121](https://github.com/sinkillerj/ProjectE/issues/2121) | 2021 | Infernal Armor Explosion Does No Damage | Infernal armor explosion не наносит урон (1.16.5) |
| [#2109](https://github.com/sinkillerj/ProjectE/issues/2109) | 2021 | Right Clicking with Dark or Red Matter Tools Doesn’t Work on Redstone Ore | ПКМ тёмными инструментами по редстоун-руде (1.12.2) |
| [#2090](https://github.com/sinkillerj/ProjectE/issues/2090) | 2021 | Cannot shift click into empty slots in condensor | Shift-клик в пустые слоты конденсатора (1.12.2) |
| [#2089](https://github.com/sinkillerj/ProjectE/issues/2089) | 2021 | Right click item stack in condenser gets full stack | ПКМ стек в конденсатор берёт весь стек (1.12.2) |
| [#2073](https://github.com/sinkillerj/ProjectE/issues/2073) | 2020 | 1.15.2 ProjectE-1.15.2-PE1.0.4  No projecte instruction | Нет projecte instruction (1.15.2) |
| [#2053](https://github.com/sinkillerj/ProjectE/issues/2053) | 2020 | Dark matter furnace glitch | Глюк тёмной духовки (1.12.2) |
| [#2045](https://github.com/sinkillerj/ProjectE/issues/2045) | 2020 | [1.12.2] Archangel's Smite Not Accepting Arrows | Archangel's Smite не берёт стрелы (1.12.2) |
| [#2039](https://github.com/sinkillerj/ProjectE/issues/2039) | 2020 | 1.12.2 Server Block Glitch | Server Block Glitch (1.12.2) |
| [#1958](https://github.com/sinkillerj/ProjectE/issues/1958) | 2020 | [1.12.2-PE1.4.1] Mercurial Eye Does not Work | Mercurial Eye не работает (1.12.2) |
| [#1954](https://github.com/sinkillerj/ProjectE/issues/1954) | 2020 | item containers with meta data (containing items) can be lost if quick-clicking into proje | Контейнеры с предметами теряются при quick-click (1.12.2) |
| [#1946](https://github.com/sinkillerj/ProjectE/issues/1946) | 2019 | [1.12.2-PE1.4.1] Gem Cuirass has no EMC | Gem Cuirass без EMC (1.12.2) |
| [#1937](https://github.com/sinkillerj/ProjectE/issues/1937) | 2019 | EMC not showing on Server when having different language selected | EMC не видно на сервере с другим языком (1.12.2) |
| [#1933](https://github.com/sinkillerj/ProjectE/issues/1933) | 2019 | [1.12.2 - 1.4.1] SimpleChannelHandlerWrapper exception of Fake Player left clicks on Dedic | SimpleChannelHandlerWrapper exception с fake player (1.12.2) |
| [#1930](https://github.com/sinkillerj/ProjectE/issues/1930) | 2019 | Morningstar not working for ONLY ME on private server | Morningstar не работает на сервере |
| [#1925](https://github.com/sinkillerj/ProjectE/issues/1925) | 2019 | Swiftwolf Rending Gale, Lightning At Player Crashes you | SWRG молния у игрока крашит (1.12.2) |
| [#1921](https://github.com/sinkillerj/ProjectE/issues/1921) | 2019 | Mercurial Eye is NOT available | Mercurial Eye недоступен (1.12.2) |
| [#1915](https://github.com/sinkillerj/ProjectE/issues/1915) | 2019 | katar causes crash when rightclicking trees / leaves  | katar крашит при клике по деревьям/листьям (1.12.2) |
| [#1884](https://github.com/sinkillerj/ProjectE/issues/1884) | 2019 | Internal Server crash while generating world (getStoredEMC) | Краш сервера при генерации мира (getStoredEMC, 1.12.2) |
| [#1882](https://github.com/sinkillerj/ProjectE/issues/1882) | 2019 | Dark Matter Armor protects from lava, but not from burning | Тёмная броня защищает от лавы, но не от горения |
| [#1878](https://github.com/sinkillerj/ProjectE/issues/1878) | 2019 | Dark Matter Pedestal Not Accepting Items | Pedestal не принимает предметы (1.12.2) |
| [#1873](https://github.com/sinkillerj/ProjectE/issues/1873) | 2019 | crash while starting up game | Краш при старте (1.12.2) |
| [#1872](https://github.com/sinkillerj/ProjectE/issues/1872) | 2019 | New Update Doesn't Load Worlds | Обновление не грузит миры (1.12.2) |
| [#1869](https://github.com/sinkillerj/ProjectE/issues/1869) | 2019 | (PO3) Missing Gem of Eternal Density Recipe  | Нет рецепта Gem of Eternal Density (PO3) |
| [#1862](https://github.com/sinkillerj/ProjectE/issues/1862) | 2019 | Tome of Knowledge no longer unlocks all transmutation knowledge after unlearning | Tome of Knowledge не разблокирует знание после unlearn |
| [#1796](https://github.com/sinkillerj/ProjectE/issues/1796) | 2018 | condenser no longer collecting energy or keeping item to duplecate | Конденсатор не копит энергию |
| [#1784](https://github.com/sinkillerj/ProjectE/issues/1784) | 2018 | Swiftwolfs ring does not function when in the aggro range of hostile entities | Кольцо не работает у враждебных мобов |
| [#1747](https://github.com/sinkillerj/ProjectE/issues/1747) | 2018 | [1.10.2] Explosives issue | Взрывчатка (1.10.2) |
| [#1732](https://github.com/sinkillerj/ProjectE/issues/1732) | 2018 | When i play for some long time it lags only for.. | Лаги через время (1.12.2) |
| [#1705](https://github.com/sinkillerj/ProjectE/issues/1705) | 2018 | 1.12.2  | 1.12.2 общее |
| [#1701](https://github.com/sinkillerj/ProjectE/issues/1701) | 2018 | Can't place Blaze powder inside Energy Condenser | Blaze powder в конденсатор |
| [#1696](https://github.com/sinkillerj/ProjectE/issues/1696) | 2018 | Item disappears from input slot | Предмет исчезает из input slot |
| [#1681](https://github.com/sinkillerj/ProjectE/issues/1681) | 2018 | Craft Tweaker not removing defualt recipes, however adding custom recipes is no issue. | CraftTweaker не убирает дефолтные рецепты |
| [#1679](https://github.com/sinkillerj/ProjectE/issues/1679) | 2018 | Licence Change | Смена лицензии (help wanted) |
| [#1631](https://github.com/sinkillerj/ProjectE/issues/1631) | 2017 | Alchemical Chest Weirdness | Alchemical Chest Weirdness |
| [#1608](https://github.com/sinkillerj/ProjectE/issues/1608) | 2017 | Covalence Chest stops working at random | Covalence Chest случайно останавливается |
| [#1604](https://github.com/sinkillerj/ProjectE/issues/1604) | 2017 | random crashes with items in dm pedestal  | Краши с предметами в pedestal |
| [#1544](https://github.com/sinkillerj/ProjectE/issues/1544) | 2017 | 1.10.2 energy condenser output side | Выходная сторона конденсатора (1.10.2) |
| [#1534](https://github.com/sinkillerj/ProjectE/issues/1534) | 2017 | [1.10.2] - Some Project E console spam | Спам консоли (1.10.2) |
| [#1527](https://github.com/sinkillerj/ProjectE/issues/1527) | 2017 | (1.10.2) Energy Condenser Interactions | Взаимодействие конденсатора (1.10.2) |
| [#1487](https://github.com/sinkillerj/ProjectE/issues/1487) | 2017 | Dark matter pickaxe not changing or charging | Тёмная кирка не меняет заряд |
| [#1428](https://github.com/sinkillerj/ProjectE/issues/1428) | 2016 | Transmutation Tablet Item Issue | Проблема планшета (1.10.2) |
| [#1386](https://github.com/sinkillerj/ProjectE/issues/1386) | 2016 | [1.10.2-PE1.0.6B] Inventory Tweaks won't sort alchemical chest/bag | Inventory Tweaks не сортирует alchemical (1.10.2) |
| [#1357](https://github.com/sinkillerj/ProjectE/issues/1357) | 2016 | Infinite EMC Bug for 1.7.10 | Infinite EMC 1.7.10 |
| [#1325](https://github.com/sinkillerj/ProjectE/issues/1325) | 2016 | Coal and Coal Blocks | Уголь и угольные блоки |
| [#1305](https://github.com/sinkillerj/ProjectE/issues/1305) | 2016 | [1.9.4-PE1.0.4B] Free EMC bug | Free EMC bug (1.9.4) |
| [#1161](https://github.com/sinkillerj/ProjectE/issues/1161) | 2016 | Hammer/morningstar doesn't dig 3x3x1 | Hammer/morningstar не копает 3x3x1 |
| [#1157](https://github.com/sinkillerj/ProjectE/issues/1157) | 2016 | Problem with alchemical bag and inventory tweaks. | Alchemical bag + inventory tweaks |
| [#1050](https://github.com/sinkillerj/ProjectE/issues/1050) | 2015 | Energy condensers consume infinite emc from relay. | Конденсаторы жрут бесконечную EMC из реле |
| [#1028](https://github.com/sinkillerj/ProjectE/issues/1028) | 2015 | Protection bypass with Nova Catalyst/Cataclysm, Hyperkinetic/Catalytic lens | Protection bypass с Nova Catalyst (старо) |
| [#965](https://github.com/sinkillerj/ProjectE/issues/965) | 2015 | Matter pickaxe 3x modes don't seem to fully work | 3x моды кирки не работают |
| [#934](https://github.com/sinkillerj/ProjectE/issues/934) | 2015 | Armor protection config or buff, and fire protection bug | Настройка брони/огнезащиты |
| [#858](https://github.com/sinkillerj/ProjectE/issues/858) | 2015 | Central License Talk Thread | Центральный тред лицензии |
| [#727](https://github.com/sinkillerj/ProjectE/issues/727) | 2015 | Out of date localisations (Especially: ja_JP) | Устаревшие локализации (ja_JP) |
| [#561](https://github.com/sinkillerj/ProjectE/issues/561) | 2015 | inventory tweaks not working. | inventory tweaks не работает (1.7.10?) |
| [#359](https://github.com/sinkillerj/ProjectE/issues/359) | 2015 | Recipes that require Klein Star Omega do not require it to be full | Рецепты Klein Star Omega не требуют полной звезды |
| [#338](https://github.com/sinkillerj/ProjectE/issues/338) | 2014 | Code/resource contributors please submit your UUID - High Alchemist list | Список контрибьюторов (help wanted) |
