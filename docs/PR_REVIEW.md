# ProjectE — разбор открытых Pull Requests (форк `UnQuis/ProjectE`)

Дата разбора: 2026-09-21.
Репозиторий-источник: https://github.com/sinkillerj/ProjectE (upstream).
Форк: https://github.com/UnQuis/ProjectE (origin).
Ветки разбирались по `refs/remotes/pr/NNN` относительно базовых веток форка.
Документ — снимок состояния на дату разбора; все хэши коммитов относятся к форку.

Всего открытых PR на момент разбора: **26**. Применено: **21**, отклонено: **5**.

Проверки слияния выполнялись read-only через `git merge-tree --write-tree`
(без изменения рабочего дерева), компиляция — только на `mc1.21.1`
(`./gradlew --offline compileJava compileTestJava test`: 163 теста зелёные).
Ветка `mc1.20.x` — в песочнице без сети к Maven (ProGuard mappings / JEI),
поэтому правки на ней проверялись статически (`git show`/sed) и код-ревью.

---

## Сводная таблица

| PR | Название | База | Вердикт | Что сделано в форке |
|----|----------|------|---------|---------------------|
| #2442 | Feat: PT_BR localization | mc1.21.1 | ACCEPT | применён (`b3c89850`) |
| #2468 | Rewrite japanese localization | mc1.21.1 | ACCEPT | применён (`c77fd63c`) |
| #2447 | Update fr_fr.json | mc1.21.1 | ACCEPT (фикс опечатки) | применён (`79be86be` + `c094d208`) |
| #2432 | Translate into Classical Chinese | mc1.21.1 | ACCEPT (фиксы) | применён (`6d0928f5` + `c094d208`) |
| #2480 | perf: reduce per-tick allocations in BlockEntities and BlackHoleBand | mc1.21.1 | ACCEPT | применён (`d1272892`) |
| #2483 | perf: optimize numeric computations | mc1.21.1 | ACCEPT | применён (`9785b266`) |
| #2481 | perf: reduce per-tick allocations in player tick handling | mc1.21.1 | ACCEPT + наш фикс регрессии | применён (`16baf5e5` + `e3335715`) |
| #2472 | Fix NPE in TransmutationContainer.fromNetwork (buf null) | mc1.21.1 | ACCEPT | применён (`0b1a256a`) |
| #2449 | Client disconnect in Spectator mode (null packet buffer) | mc1.21.1 | ACCEPT | применён (`a3d5a9df`) |
| #2462 | Fix DM/RM tools and armor crashing server on NeoForge 1.21.1 | mc1.21.1 | ACCEPT (без рекламного коммита) | применён (`e2ba9d31`) |
| #2434 | Philstone crafting recipes hard to find in JEI/EMI | mc1.21.1 | ACCEPT (катализатор сохранён) | применён (`594adaac` + `b9773a9a`) |
| #2475 | Fix EMC mapper correctness and failure isolation | mc1.21.1 | ACCEPT (review-fix) | применён (`ed2702f0`) |
| #2479 | Transmutation table search by identifier | mc1.21.1 | ACCEPT (табы-фикс) | применён (`75fb59a0` + `5aef7445`) |
| #2485 | Add opt-in modpack EMC recovery (depends on #2475) | mc1.21.1 | ACCEPT (draft) | применён (`2a7a85c8`) |
| #2352 | Fallback keypress handling in Transmutation GUI | mc1.20.x | ACCEPT | применён (`47923574`) |
| #2358 | Revert removing Philstone alt Function | mc1.20.x | ACCEPT (только фикс, без бампа версии) | применён (`ee6f4838`) |
| #2253 | schedule bonus ticks (TimeWatch) | mc1.18.x | ACCEPT (+ харднинг NPE) | применён (`9b110a1c` + `343dc881`) |
| #2332 | Fix Dark Matter axe crash on empty ItemStack | mc1.12.x | ACCEPT | применён (`a476e896`) |
| #1982 | Condensers ignore covalence loss | mc1.12.x | ACCEPT | применён (`4b32411e`) |
| #2333 | "#2332 for 1.11.x" | mc1.11.x | ACCEPT | применён (`a7d1c8eb`) |
| #2334 | "#2332 for 1.10.x" (база указана неверно) | mc1.20.x | REJECT как подан → ручной порт на 1.10.x | ручной порт (`3c0fa141`) |
| #2482 | perf: fastutil maps for EMC mapping | mc1.21.1 | REJECT | конфликтует с #2475 |
| #2463 | Exclude tamed animals from interdiction torch | mc1.21.1 | REJECT | неверный предикат |
| #2214 | Create gradle.yml (CI) | mc1.19.x | REJECT | нерабочий пайплайн |
| #2041 | tehnut.info/maven down → CurseForge | mc1.15.x | REJECT | неверное направление фикса |
| #1036 | Split create/destruction EMC (2015, API) | master | REJECT | легаси, конфликт, устарел |

---

## Применённые PR на mc1.21.1 (активная ветка)

### #2442 — Brazilian Portuguese локализация — ACCEPT
Добавлена полная `pt_br.json`. Ключи совпадают с текущей структурой.
Коммиты форка: `b3c89850`.

### #2468 — Переписанная японская локализация — ACCEPT
Заменено содержимое `ja_jp.json` (перевод с заглавных форм, токенов и легенд).
Коммит: `c77fd63c`.

### #2447 — Обновление `fr_fr.json` — ACCEPT (с фиксом опечатки)
Применён как есть; при ревью обнаружена опечатка «Poubre» в новых строках
(«poudre»), исправлено в отдельном коммите: `79be86be` + `c094d208`.

### #2432 — Классический китайский — ACCEPT (с фиксами)
Перевод на древнекитайский (`lzh.json`). При ревью исправлено:
`%.3f`→`%s` в токенах, 緩→速 (перепутаны «slow»/«fast»), добавлена концовка
файла. Коммиты: `6d0928f5` + `c094d208`.

### #2480 — perf: сокращение пер-тик аллокаций в BE и BlackHoleBand — ACCEPT
Заменил `new Fabrication()`-стиль вложенных объектов на переиспользуемые
экраны и убрал лишние аллокации (`BlockEntityHooks`, `BlackHoleBand`).
Поведение не меняет. Коммит: `d1272892`.

### #2483 — perf: числовые оптимизации без float/string парсинга — ACCEPT
Заменил парсинг/округления на целочисленные пути в EMC/топливном коде.
Идентичность результатов проверена по граничным значениям. Коммит: `9785b266`.

### #2481 — perf: player tick handling — ACCEPT + фикс регрессии SWRG
Сокращение пер-тик аллокаций в `PlayerTick`. При ревью найден **регресс полёта
SWRG**: проверка `hasSwrgWithEmc` стала учитывать только хотбар, из-за чего
полёт выключался при кольце в offhand/curios. Добавлен фикс
(горячая проверка hotbar → offhand → curios, `InternalAbilities`):
`16baf5e5` + `e3335715`.

### #2472 — NPE `TransmutationContainer.fromNetwork` при `buf == null` — ACCEPT
Защита от разыменования нулевого буфера. Коммит: `0b1a256a`.

### #2449 — Дисконнект клиента в Spectator-режиме (null packet buffer) — ACCEPT
Решает разрыв соединения при открытии контейнеров блок-энтатити спектатором
(тот же класс NPE, что и #2472, на пустом буфере). Коммит: `a3d5a9df`.

### #2462 — Краш сервера на NeoForge при крафте DM/RM — ACCEPT (без рекламного коммита)
`ItemStack.save()` «Value must be positive: 0» при нулевой прочности —
фикс durability-компонента. В PR входил отдельный коммит с рекламой в README;
он отброшен. Целый фикс выбран через cherry-pick: `e2ba9d31`.

### #2434 — Рецепты Philstone'а трудно найти в JEI/EMI — ACCEPT (катализатор сохранён)
Убраны пустые crafted-рецепты камня из категории crafting. При ревью оставлен
катализатор камня для категории WorldTransmutation (иначе ломается найденное
превращение мира): `594adaac` + `b9773a9a`.

### #2475 — EMC mapper correctness & failure isolation — ACCEPT (review-fix)
Крупнейший PR ветки: корректный вычисляемый граф EMC, изоляция ошибок
(один плохой ингредиент больше не абортит весь маппинг), переработанный
`BigFractionToLongGenerator`, полные юнит-тесты. Проверено алгоритмически;
163 теста зелёные. Коммит: `ed2702f0`. Открытые дизайн-нити (EMC фейерверк-
ракет и т.п.) зафиксированы для апстрима, но не блокируют.

### #2479 — Поиск по идентификатору в столе — ACCEPT (табы-фикс)
`SearchQueryParser`: поиск по id(`#`) и имени. При ревью нормализованы
отступы (смешанные tabs/spaces → tabs): `75fb59a0` + `5aef7445`.

### #2485 — opt-in modpack EMC recovery — ACCEPT (draft, зависит от #2475)
`mapping.recover_missing_items` (по умолчанию **false**) + документация
`docs/emc-recovery.md`. Входит в связку с #2475. Коммит: `2a7a85c8`.

### Дополнительно: фиксы issues (#2412, #2431, #2433) — `0b00a2f0`
См. `ISSUES_REVIEW.md`.

---

## Применённые PR на легаси-ветках

### mc1.20.x — `2fec0daf`
- **#2352** — fallback `keyPressed` в GUITransmutation: первый ввод после открытия
  ГУИ фокусирует поиск (`47923574`). Низкий риск.
- **#2358** — возврат alt-трансмутации Philosopher's Stone: merged только фикс-
  коммит `0ec304b6`, **версионный бамп 1.0.1→1.0.2 отброшен** во избежание
  коллизии с апстрим-тегом (`ee6f4838`).
- Дополнительно: фикс поиска стола #2431/#2433 — `2fec0daf`.

### mc1.18.x — `343dc881`
- **#2253** — Watch of Flowing Time: отложенная очередь bonus-тиков с бюджетом
  `maxTPSLoss` вместо синхронного прожига. Закрывает риск IllegalStateException /
  NPE в `SpedUpBlockEntity.tick()` при освобождении воркера; добавлен харднинг
  (`343dc881`). Коммиты: `9b110a1c` + `343dc881`.

### mc1.12.x — `4b32411e`
- **#2332** — Dark Matter axe: `if (s.isEmpty()) continue;` перед
  `OreDictionary.getOreIDs(s)` в `PEToolBase.clearOdAOE` (`a476e896`).
- **#1982** — конденсаторы игнорируют covalence loss (опция, по умолчанию выкл):
  `EMCHelper.getEmcSellValue(stack, true)` в CondenserTile/MK2 (`4b32411e`).

### mc1.11.x — `a7d1c8eb`
- **#2333** — тот же фикс #2332 для 1.11.x.

### mc1.10.x — `3c0fa141`
- **#2334** — PR подан против mc1.20.x, но база в заголовке ветки — 1.10.x, и
  `PEToolBase.java` в mc1.20.x удалён (рефакторинг). Как подан — конфликт;
  правильное действие — ручной порт охранки в `origin/mc1.10.x`, что и сделано
  (`3c0fa141`). Охранка идентична #2332/#2333.

---

## Отклонённые PR

### #2482 — fastutil maps для EMC-маппера — REJECT
Конфликтует архитектурно с уже принятым #2475 (переработанный маппинг).
Двойное слияние усложнит поддержку без измеренной выгоды. Если позже понадобится
— портировать поверх #2475 отдельно.

### #2463 — исключить приручённых животных из репульсии факела — REJECT
Предикат выбора сущностей затрагивает неверный класс/критерий (проверено по коду
`InterdictionTile`): не отсекает именно приручённых, может сломать репульсию
мобов из целевых групп. Нужна переработка.

### #2214 — CI workflow (gradle.yml) — REJECT
`publish`-джоба нерабочая: в `build.gradle` нет GitHub Packages-репозитория для
`maven-publish`; JDK 11 против toolchain 17 (работает только благодаря foojay-
автозагрузке, случайно); закреплённые `checkout@v3/setup-java@v3` (2022), EOL
`ubuntu-latest`. Для EOL-ветки бесполезно.

### #2041 — перенос HWYLA/CraftTweaker на CurseForge — REJECT
Неверное направление: рабочий фикс — живой `https://maven.tehnut.info`
(одна строка). Старый Cursemaven-плагин депрекейтед, координата
`curse.maven:CraftTweaker-1.15.2:2953559` содержит не-slug id — резолв скорее
всего падает. Рекомендация для mc1.15.x: одна строка `maven.tehnut.info`.

### #1036 — Split create/destruction EMC (2015) — REJECT
API-рефакторинг 2015 года против `master`: 7× modify/delete (файлы удалены в
современном master), за 10 лет не принят апстримом, архитектуру API уже
переработали самостоятельно; `master` почти не поддерживается. Порт 33 файлов —
высокий риск на EOL-ветку без выгоды.

---

## Итоговое состояние веток форка

| Ветка | HEAD | Содержимое |
|-------|------|------------|
| `mc1.21.1` (активная) | `0b00a2f0` | 14 PR + фиксы issues #2412/#2431/#2433 |
| `mc1.20.x` | `2fec0daf` | #2352, #2358, фиксы #2431/#2433 |
| `mc1.18.x` | `343dc881` | #2253 + NPE-харднинг |
| `mc1.12.x` | `4b32411e` | #2332, #1982 |
| `mc1.11.x` | `a7d1c8eb` | #2333 |
| `mc1.10.x` | `3c0fa141` | #2334 (ручной порт) |

Все локальные ветки выровнены с `origin/` (публичный форк), рабочие деревья чисты.