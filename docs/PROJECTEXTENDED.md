# ProjectExtended — импорт в форк и разбор Issues

Дата: 2026-09-21.
Upstream: https://github.com/pupnewfster/ProjectExtended (автор pupnewfster, MIT).
Лицензия сохранена (`ProjectExtended/LICENSE`).

ProjectExtended — аддон к ProjectE, добавляющий «то, что EE2 имел бы в наши дни»:
dark/red matter триденты, щиты, Interdiction Lantern (уличный фонарь-талисман,
как interdiction torch), Alchemical Barrel, датапак-blacklist (+ интеграция
GameStages на 1.19+). Мод самостоятельный (NeoForge/Forge), собирается отдельным
Gradle-проектом внутри `ProjectExtended/`, зависит от ProjectE (`moze_intel.projecte`).

---

## Что импортировано и куда

Каждая ветка аддона перенесена в одноимённую ветку нашего форка (полное дерево
через `git archive`, коммитами без истории исходника, с хэшем upstream в
сообщении). Дерево размещено в каталоге `ProjectExtended/` — не пересекается
с файлами ProjectE и не влияет на его корневой Gradle-сборку.

| Ветка форка | Ветка аддона | Upstream SHA | Коммит импорта |
|-------------|--------------|--------------|----------------|
| `mc1.21.1` (активная) | 1.21.x | `7b76abd` | `17aad084` |
| `mc1.20.x` | 1.20.x | `c6f508d` | `d835f60c` |
| `mc1.19.x` | 1.19.x | `cea05fc` | `113809b9` |
| `mc1.18.x` | 1.18.x | `6fa7073` | `863c91a8` |
| `mc1.16.x` | 1.16.x | `490deb1` | `41b58d2c` |
| `mc1.15.x` | 1.15.x | `9560cc9` | `5d7f40b7` |
| `mc1.14.x` | 1.14.x | `e0232ea` | `874e8694` |

Версия активной ветки аддона: **1.6.2** (NeoForge 1.21.1, `mod_version=1.6.2`).

Зависимости (1.21.x): `minecraft` 1.21.1, `neoforge` ≥21.1.118, `projecte`
mandatory ≥1.0.0 (после `AFTER`), опционально JEI/Bookshelf/GameStages.

Сборка: `cd ProjectExtended && ./gradlew build` (нужна сеть до Maven/CurseForge;
в песочнице сборка не выполнялась — зависимостей аддона нет в оффлайн-кэше).

---

## Разбор открытых issues (8)

| # | Год | Заголовок | Вердикт |
|---|-----|-----------|---------|
| [12](https://github.com/pupnewfster/ProjectExtended/issues/12) | 2025 | Suggestion: Dark and red matter mace | **ENH** — новый предмет (булава с режимами explosion/shockwave + рецепты); большая фича, не применялось |
| [11](https://github.com/pupnewfster/ProjectExtended/issues/11) | 2025 | Red Matter Trident Should Return from Void | **FIXED** — применено в форке (см. ниже, `4ed8a523`) |
| [9](https://github.com/pupnewfster/ProjectExtended/issues/9) | 2024 | Suggestion magic item that grants nightvision | **ENH** — фича (аналог эффекта gem helmet), не применялось |
| [8](https://github.com/pupnewfster/ProjectExtended/issues/8) | 2022 | Interdiction Lanterns | **ALREADY IMPLEMENTED** — блок и рецепт существуют в 1.21.x (`InterdictionLantern`, рецепт `interdiction_lantern.json`, иконка фонаря); жалобу можно закрывать |
| [7](https://github.com/pupnewfster/ProjectExtended/issues/7) | 2021 | Suggestions for the Shield (99% block, thorns) | **ENH** — балансовая фича, не применялось |
| [6](https://github.com/pupnewfster/ProjectExtended/issues/6) | 2021 | Trident disappeared | **LEGACY/FIXED** — автор закрыл в 1.1.0 (1.15.2); в 1.21.x обработка подбора тридента аккуратная: `pickup=ALLOWED`, `playerTouch` только для владельца, `tickDespawn` при невозможности возврата роняет предмет |
| [4](https://github.com/pupnewfster/ProjectExtended/issues/4) | 2020 | [1.15.2] Shield Textures Switched? | **LEGACY/FIXED** — в 1.21.x текстуры корректны: dark shield — бирюзовый (`#1AAAA7`/`#20C5B5`), red shield — красный (`#860000`); цвета проверены по PNG |
| [3](https://github.com/pupnewfster/ProjectExtended/issues/3) | 2020 | EMC mappers (Powah, Silent's Mechanics) | **OUTDATED** — современный ProjectE маппит EMC автоматически из рецептов/тагов (`values.after`, конверсии); отдельных «мод-мапперов» в аддоне нет, добавлять нечего |

### Применённый фикс: #11 — возврат Red Matter тридента из пустоты

Ванильный `ThrownTrident` возвращается только когда `Loyalty > 0` И
(`dealtDamage` или `noPhysics`), причём `dealtDamage` ставится после попадания
или `inGroundTime > 4`. Тридент, улетевший в пустоту, никогда не «садится»,
поэтому возврат не запускается, а на `y < minBuildHeight − 64`
(`Entity.checkBelowWorld`) его уничтожает сервер.

Изменение `PETridentEntity.tick()` (только сервер, только red matter tier,
только если есть loyalty и владелец): при падении ниже `minBuildHeight − 32`
тридент телепортируется на 2 блока выше владельца, скорость обнуляется и
выставляется `noPhysics` — штатный loyalty-возврат поднимает его и отдаёт
в руку со звуком возврата, без гравитации и коллизий. Тёмно-материевый тридент
ведёт себя как ванильный (разрушается в пустоте — как и просил автор issue).

Коммиты: импорт `17aad084`, фикс `4ed8a523` (ветка `mc1.21.1`).