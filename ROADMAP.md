# ROADMAP.md — create:yogglez

> **Status: PROPOSED** — Entwurf zur Diskussion, noch kein abgestimmter Plan.
> Stand: 2026-09-01 · Quellen: `README.md` (main), `VISION.md`, POC (`feature/poc`, PR #1), ftb-skies-2-aero (`docs/stock-pack.md`, `mods-addon/MANIFEST-1.1.md`).

---

## 1. Zweck & Spielregeln

- **Strikte Phasenfolge:** `concept → PoC → concept → MVP → concept → alpha`. Die concept-Phasen haben je einen eigenen Zweck (Produktkonzept / Technische Architektur / Alpha-Definition) und sind keine Wiederholung.
- Jede Phase hat **Goal, Scope, Deliverables, Exit-Criteria (messbar), Risiken**. Eine Phase ist erst abgeschlossen, wenn ihre Exit-Criteria erfüllt sind.
- **Proposed-Charakter:** Phasen ab MVP sind Zielbilder, keine Zusagen. Verschiebungen werden im Changelog dokumentiert.
- Abgeschlossene Phasen (2 teilweise, 0 vollständig) sind als Referenz markiert, nicht zur Wiederholung gedacht.

## 2. Phasenübersicht

| Phase | Typ | Ziel | Status |
|---|---|---|---|
| 0 | **concept** | Produktkonzept, Abgrenzung, Prinzipien | teilweise abgeschlossen (Docs vorhanden, VISION.md neu) |
| 1 | **PoC** | Machbarkeit des Linsen-Kerns beweisen | in Arbeit — Dev-Machbarkeit bewiesen (PR #1); offen: Auslieferung im ftb-skies-2-aero-Pack + Client-Verifikation |
| 2 | **concept** | Architektur: Standalone-Soft-Dependency, Lens-API, JSON/KubeJS-Schema | offen — nächster Schritt |
| 3 | **MVP** | Datengetriebenes, standalone-fähiges Release (Modrinth) | offen |
| 4 | **concept** | Alpha-Definition aus der Server-Mod-Liste (dieses Dokument §8) | Entwurf in diesem PR |
| 5 | **alpha** | Vollständige Integrationsfläche live auf ftb-skies-2-aero | offen |

---

## 3. Phase 0 — concept: Produktkonzept (Fundament)

**Goal:** Klarheit über *was* yogglez ist und *was nicht* — bevor Code entsteht.

**Scope:**
- Ursprungsidee (`mod-design/yizzl_initial_thoughts.md`, `linsenbrille.txt`)
- Abgrenzung zu Client-Overlays (`mod-design/abgrenzung_cyber_goggles.md`)
- Richtungswechsel 2026-09: Standalone-Framework statt Create-Fundament (README „To Be Discussed")

**Deliverables:**
- `mod-design/`-Dokumente (vorhanden)
- `VISION.md` (dieser PR) — Zielbild, Prinzipien P1–P7, offene Fragen

**Exit-Criteria:**
- [ ] `VISION.md` von yizzl reviewt; Prinzipien P1–P7 explizit bestätigt oder korrigiert
- [ ] ≤ 5 offene Kernfragen aus §8 des VISION-Dokuments nach Phase 0 (Rest wandert in Phase 2)
- [ ] Namensfrage „create:yogglez" vs. „yogglez" zumindest vorläufig entschieden

**Risiken:**
- Konzept-Overengineering ohne Nutzer-Feedback (Gegengewicht: POC existiert, Aero-Server als Realtest)
- Verzetteln an der Create-Bindungsfrage (wird in Phase 2 entschieden, nicht hier)

## 4. Phase 1 — PoC: Machbarkeit (in Arbeit — Auslieferungs-Gates offen)

**Goal:** Beweisen, dass ein modulares Linsensystem auf Create-Overlay + Fremd-API machbar ist — **und es im ftb-skies-2-aero-Modpack auszuliefern sowie client-seitig zu verifizieren**. Erst wenn diese Auslieferungs-Gates erfüllt sind, gilt die Phase als abgeschlossen.

**Scope:** M0–M3 — eigene `yogglez:yogglez_goggles`, Lens-Item mit Item-Data-Speicherung, Cycle-Keybind (`H`), Create-Overlay via `IHaveGoggleInformation`/`GogglesItem.addIsWearingPredicate`, Client-Provider-Registry (ohne Mixins), AE2-Network-Lens strikt über `appeng.api`, HUD-Indikator, I18n de/en.

**Deliverables:**
- Branch `feature/poc`, PR #1 (gemerged)
- 3/3 Headless-Gametests, CI-Build + Xvfb-Client-Smoke-Test
- POC-Erfahrungsbericht: `progress-feature-poc.md` (Repo-intern), README-POC-Sektion
- **Auslieferung im ftb-skies-2-aero-Pack:** yogglez-Mod ist im Pack enthalten (Mod-Datei + Manifest-/Mods-Listeneintrag), Server startet damit (Start-Test auf der Dev-Instanz auf `planet`)
- **Client-Verifikation:** manueller Live-Test auf einer echten Client-Instanz mit yizzl/Momo — Brille aufsetzbar, Linse installier-/aktivierbar, Overlay im Spiel sichtbar (nicht nur Xvfb-Smoke-Test)

**Exit-Criteria — Dev-Machbarkeit (erfüllt, Stand Sep 2026):**
- [x] `./gradlew build` grün (auch clean)
- [x] 3/3 Gametests: Goggle-Tooltip, Lens-Lifecycle (install/activate/cycle/NBT), Lens-Registry
- [x] AE2-Daten (Grid, Kanäle, Energie, Puffer) via öffentlicher API nachgewiesen
- [x] CI: Linux headless + Xvfb-Client-Smoke-Test

**Exit-Criteria — finale Auslieferungs-Gates (noch offen — erst damit ist die Phase abgeschlossen):**
- [ ] **Auslieferung im ftb-skies-2-aero-Modpack:** PoC ist als Mod im Pack enthalten (Mod-Datei + Manifest-Eintrag) und der Server startet damit (Start-Test auf der Dev-Instanz, nicht nur lokale Dev-Umgebung)
- [ ] **Client-seitig verifiziert funktionsfähig:** Verifikation auf einer echten Client-Instanz im Live-Spiel (manueller Testlauf mit yizzl/Momo) — nicht nur serverseitige/dev-getestete Funktionalität oder Xvfb-Smoke-Test

**Risiken (aus dem POC gelernt, für Phase 2 relevant):**
- `LangBuilder.forGoggles()` ist client-only → Tooltip-Inhalt muss server-safe erzeugt werden (`collectGoggleContent()`)
- Catnip nicht separat dependency-en (JPMS split-package, bündelt Ponder)
- `CustomData.of(...)` kopiert — read-mutate-write-Reihenfolge bei Lens-NBT beachten
- GameTestServer-Shutdown kann bei schweren Mods hängen → CI prüft „tests passed"-Marker im Log

## 5. Phase 2 — concept: Architektur & Standalone-Entscheidung

**Goal:** Die „To Be Discussed"-Fragen des README beantworten und die Architektur für die Standalone-/Soft-Dependency-Richtung festlegen — **Entscheidungen, kein Implementierungsschub**.

**Scope:**
- **Create-Entscheidung:** Standalone-Basis vs. Create-harte-Dependency für Release 1 (VISION §8.1) — inkl. Refactoring-Aufwand des POC (Build-Source-Sets, entkoppelte Event-Listener)
- **Soft-Dependency-Plugin-Architektur:** dynamische Klassen-Checks/ServiceLoader statt statischer Importe; Wear-Predicate-Ersatz
- **Lens-API-Design:** Provider-Vertrag (`BlockEntityType → LensInfoProvider`), Lebenszyklus, Caching (Kosten mehrerer Provider pro BE)
- **JSON/KubeJS-Schema:** kanonisches Format, Sicherheitsmodell für `/reload`, Cache-Invalidierung, Fehler-Isolation
- **Netz-Protokoll:** generische Payloads, Server-Autorität, Sync-Strategie
- **Linsen-Auflösung:** Prioritäts-Gewichte / kontextuelles Sub-Cycling / Focus-Lock
- **Test-Strategie:** Gametest-Muster pro Linse, Mocking fehlender Mods

**Deliverables:**
- `ARCHITECTURE.md` (Entwurf)
- Lens-API-v1-Spezifikation (Provider-Vertrag)
- JSON/KubeJS-Schema-Draft inkl. Beispiel-Linse
- ADRs (Architecture Decision Records) zu Create-Bindung, Soft-Dependency, Sync, Auflösung
- Vorbereitete Integrations-Matrix (Grundlage = §8 dieses Dokuments)

**Exit-Criteria (messbar):**
- [ ] Alle „To Be Discussed"-Fragen des README haben eine ADR-Antwort oder explizites „defer"
- [ ] API-Spezifikation reviewt; JSON-Schema kann eine Beispiel-Linse für einen Aero-Mod (z. B. Mekanism) vollständig ausdrücken
- [ ] ≥ 80 % der 452 Matrix-Mods sind einer Interaktionsklasse zugeordnet (Linse-API / Linse-BE-NBT / Linse-JSON / Framework / Koexistenz)
- [ ] Kein neuer Feature-Code außer Prototypen zur Validierung der Soft-Dependency-Mechanik

**Risiken:**
- API-Overdesign (YAGNI) — Gegengewicht: Schema-Draft muss genau eine echte Linse ausdrücken
- Fremd-APIs ändern sich (Version-Pinning in der Matrix erforderlich)
- Refactoring des Create-gebundenen POC unterschätzt

## 6. Phase 3 — MVP: Datengetriebenes, standalone-fähiges Release

**Goal:** Ein Release, mit dem Pack-Autoren **ohne Java** Linsen definieren und nutzen können — auch in Packs ohne Create.

**Scope:**
- Implementierung der Phase-2-Entscheidungen (Soft-Dependency-Kern, eigene Rezepte/Progression für den Standalone-Pfad)
- JSON-/KubeJS-Linsen-Loader inkl. `/reload`-Sicherheitsmodell
- Radialmenü + kontextuelles Cycling, HUD-Feed
- 5–8 Kern-Linsen aus der Alpha-Matrix-Priorität **M** (Kandidaten: AE2, Create, Mekanism, Modern Industrialization, Refined Storage, PneumaticCraft, Flux Networks, Functional Storage)
- Rezepte (Brille + Linsen), Config-Screen, I18n de/en, Ponder-Doku
- Publikation: Modrinth (Lizenz MIT)

**Deliverables:**
- Release 0.1.x auf Modrinth + GitHub
- Pack-Autor-Tutorial („erste eigene Linse in 10 Minuten")
- Beispiel-KubeJS-/JSON-Definitionen

**Exit-Criteria (messbar):**
- [ ] Doku-Test: Pack-Autor definiert eine neue Linse ohne Java-Änderung und sie funktioniert (Testprotokoll)
- [ ] ≥ 5 Kern-Linsen im Release; CI grün; ≥ 10 Gametests
- [ ] Dauerlauf 1 Woche auf einer Test-Instanz (aero-Dev, 2 Spieler) ohne Crash; Client-Smoke-Test weiterhin grün
- [ ] Mindestens ein Pack ohne Create demonstriert (Standalone-Pfad)

**Risiken:**
- KubeJS-/Rhino-API-Fit unklar (Anker: `kubejs` 2101.7.2 im Aero-Pack vorhanden)
- Balancing von Lens-Kosten (Rezepte/Progression) — früh mit yizzl testen
- Release-Prozess (Modrinth-Metadaten, Versionierung) unterschätzt

## 7. Phase 4 — concept: Alpha-Definition

**Goal:** Die Integrationsfläche des ftb-skies-2-aero-Servers **vollständig** erfassen, priorisieren und den Alpha-Testplan fixieren.

**Scope:**
- Review der Integrations-Matrix (§8) gegen den **tatsächlichen** Server-Stand (Version-Pinning! siehe §8.1)
- Priorisierung M/S/C pro Linse; Koexistenz-Smoke-Test für alle restlichen Mods
- Testplan: Gametest pro Linse + Live-Check-Liste auf dem Server; Rollout-Gate (produktiver Server darf nicht leiden → Dev-Instanz parallel)
- Risiko-Bewertung pro Mod (öffentliche API vorhanden? Version stabil?)

**Deliverables:**
- Alpha-Scope (finale M/S/C-Liste)
- Testplan + Live-Checkliste
- „Server-Laufzeitkompatibilität"-Checkliste (alle Koexistenz-Mods)

**Exit-Criteria (messbar):**
- [ ] Jede der 452 Mods ist genau einer Interaktionsklasse zugeordnet (Review abgeschlossen)
- [ ] Must-Liste (M) definiert und mit API-Verfügbarkeit begründet
- [ ] Dev-Instanz auf `planet` steht (parallel zu prod), Rollout-Gate dokumentiert
- [ ] Kein offener P1 aus Phase-3-Dauerlauf

**Risiken:**
- Scope-Inflation bei 452 Mods → M/S/C-Disziplin, Koexistenz als Standard-Antwort
- API-Fehlannahmen → pro M-Linse ein Spiking-Auftrag vor Implementierung
- Version-Drift des Packs (1.7.0 vs. 1.7.3, siehe §8.1)

## 8. Phase 5 — alpha: ftb-skies-2-aero als Test-Environment

**Goal:** Das Linsen-Set **live** auf dem produktiven Aero-Server betreiben und damit den Server als Referenz-Test-Umgebung validieren.

### 8.1 Test-Environment (Fakten, Stand Aug/Sep 2026)

| Fakt | Wert |
|---|---|
| Repo | github.com/momokli/ftb-skies-2-aero |
| Pack | FTB Skies 2: Aero (Pack 134) — Stock **1.7.0** (100471), Addon-Manifest **1.7.3** (100475) |
| Mods | **452** = 435 Server-Mods (Stock) + 15 Addon-Mods (MANIFEST-1.1) + 2 Server-Addons (Chunky, Prometheus-Exporter) |
| MC / Loader | 1.21.1 / NeoForge 21.1.248 / Java 21 |
| Host | `planet` (Hetzner, 65.21.27.234), aero.projectmellon.de, produktiv |
| Monitoring | Prometheus-Exporter → Prometheus → Grafana (TPS, MSPT, Spieler, Heap) |
| ⚠️ Version-Drift | Stock-Inventur (1.7.0, `docs/stock-pack.md`) vs. Addon-Manifest (1.7.3) — **vor Alpha-Start exakt pinnen** (offene Frage Q1) |

### 8.2 Interaktionsmodi (Legende)

| Kürzel | Bedeutung |
|---|---|
| **Linse (API)** | yogglez-Adapter liest Daten über die **öffentliche API** des Mods (compileOnly, Provider-Registry). Kein Mixin. |
| **Linse (BE/NBT)** | generischer Provider liest BlockEntity-/NBT-Zustand; keine Mod-API nötig (Insight-Prinzip, POC M3). |
| **Linse (JSON/KubeJS)** | datengetriebene Definition durch Pack-Autoren, kein Java (MVP-Mechanik; in der Alpha für ausgewählte Fälle). |
| **Framework** | yogglez nutzt den Mod als Erweiterungs-/Content-Mechanismus (KubeJS, Datapacks, Quest-Gating). |
| **Koexistenz** | keine Daten-Interaktion; Anforderung = konfliktfreier Parallelbetrieb (Laufzeit-Smoke-Test). |
| **Ops** | Server-Betriebsmodus; für yogglez nur als Umgebung relevant. |

Prioritäten: **M** = Must (Alpha-Pflicht, demonstriert die Vision), **S** = Should (wichtig, nach M), **C** = Could (nice-to-have), **–** = keine Linsen-Priorität (Koexistenz).

> Hinweis: Die README-Beispiellinsen Immersive Engineering und Industrial Craft 2 sind **nicht** im Aero-Pack — die Alpha-Liste wird ausschließlich aus den tatsächlich installierten Mods abgeleitet.

### 8.3 Integrations-Matrix — Kern-Plattform: Create & Aeronautics (36 Stock + 8 Addon)

| Mod | P | yogglez-Interaktion |
|---|---|---|
| Create | M | **Linse (API)** — Fundament der Overlay-Integration: `IHaveGoggleInformation` (POC M2), kinetisches Netz, Contraption-Status |
| Create: Aeronautics (bundled) | M | **Linse (API)** — Schiffszustand: Assemblierung, Anker, Bewegung, Kraftstoff; Aviator-Goggles-Kompatibilität |
| Create Aeronautics: Automated Logistics | S | **Linse (API)** — Schiffs-Logistik-Zustand |
| Create Aeronautics: Throwable Rope Connector | – | Koexistenz |
| Create Aeronautics FTB Chunks | – | Koexistenz (Chunk-Bridge) |
| Create Aeronautics Curios | – | Koexistenz (Curios-Slots) |
| Aeroworks | M | **Linse (API)** — Verarbeitungsmaschinen an Bord (Fortschritt) |
| Aero Copycats | – | Koexistenz (Bauteile) |
| Copycats | – | Koexistenz (Bauteile) |
| Aeronautics Covers | – | Koexistenz |
| Aeronaut's Compass | S | Koexistenz; Kandidat: Kompass-Funktion als Linse (offen, Q2) |
| Aero Portals | – | Koexistenz |
| Create: Big Cannons | S | **Linse (API)** — Kanonen-/Projektil-Daten |
| Create: Nuclear | M | **Linse (API)** — Reaktor-Daten (Temperatur, Brennstoff) |
| Create: Sifter | S | **Linse (BE/NBT)** — Sieb-Fortschritt |
| Create: Rail Grinding | – | Koexistenz |
| Create: Stock Bridge | – | Koexistenz |
| Create: Thrusters (bundled) | S | **Linse (API)** — Schub-/Antriebsdaten |
| Create: Propulsion | S | **Linse (API)** — Antriebsdaten |
| Create: Liquid Fuel | S | **Linse (API)** — Treibstoff-Daten |
| Portable Engine: Liquid Fuel | – | Koexistenz |
| Drive By Wire | – | Koexistenz |
| Linear Bearing | – | Koexistenz |
| Mechanicals | – | Koexistenz |
| Struts | – | Koexistenz |
| Absolute Kinematics | – | Koexistenz |
| Create: Connected | S | **Linse (BE/NBT)** — verbundene Netze (Scope-Detail in Phase 2) |
| Create: Hypertube | – | Koexistenz |
| Create: New Age | S | **Linse (API)** — elektrisches Netz (Spannung, Leistung) |
| Create: Mekanism Compat | – | Koexistenz (Bridge) |
| Create: Chipped | – | Koexistenz |
| Create Ultimine | – | Koexistenz |
| Create: Transmission | – | Koexistenz |
| Railways | S | **Linse (API)** — Zug-Daten (Stationen, Geschwindigkeit) |
| Hang Glider | – | Koexistenz (Flug-Loop) |
| Sophisticated Backpacks: Create Integration | – | Koexistenz (Bridge) |
| Sophisticated Storage: Create Integration | – | Koexistenz (Bridge) |
| *Addon:* Create: Applied Create | S | **Linse (API)** — AE2↔Create-Bridge (Prozess-/Auftragsdaten) |
| *Addon:* Create: Slice & Dice | S | **Linse (BE/NBT)** — Koch-Fortschritt |
| *Addon:* Create: Enchantment Industry | S | **Linse (BE/NBT)** — Verzauberungs-Daten |
| *Addon:* Create: Metallurgy | S | **Linse (BE/NBT)** — Schmelz-/Legierungsdaten |
| *Addon:* Create: Ultimate Factory | S | **Linse (BE/NBT)** — Produktions-Daten |
| *Addon:* Create Stuff & Additions | – | Koexistenz |
| *Addon:* Create: Dragons Plus | – | Koexistenz (Pflicht-Dep von Enchantment Industry) |
| *Addon:* Create: Colony Logistics | S | Koexistenz (Bridge; Daten via Minecolonies, §8.9) |

### 8.4 Integrations-Matrix — Tech & Energie (51 Stock)

| Mod | P | yogglez-Interaktion |
|---|---|---|
| Mekanism | M | **Linse (API)** — Maschinen, Energie, Gase, Multiblocks (Fusionsreaktor, SPS) |
| Mekanism: Generators | M | **Linse (API)** — Generatoren-/Reaktor-Daten |
| Mekanism: Additions | – | Koexistenz |
| Mekanism: Tools | – | Koexistenz |
| Mekanism Unleashed | – | Koexistenz |
| Mekanism Weaponry | – | Koexistenz |
| KubeJS Mekanism | – | Framework (KubeJS-Bridge) |
| Just Enough Mekanism Multiblocks | S | Koexistenz; Referenz: Multiblock-Vollständigkeits-Anzeige wird als Linse abgebildet |
| Modern Industrialization | M | **Linse (API)** — EU, Maschinen, Multiblocks |
| Extended Industrialization | S | **Linse (API)** — via MI |
| MI Tweaks | – | Koexistenz |
| MI Sound Addon | – | Koexistenz |
| EnderIO | M | **Linse (API)** — Conduit-Netzwerke, Maschinen, Energiespeicher |
| PneumaticCraft: Repressurized | M | **Linse (API)** — Druck, Maschinen, Drohnen |
| Applied Pneumatics | S | Koexistenz (AE2-Bridge) |
| Powah | M | **Linse (API)** — Energie, Reaktoren, Ender-Zellen |
| Flux Networks | M | **Linse (API)** — Netzwerk-Transfer, Energiefluss |
| RFTools Base | S | **Linse (API)** — Basis (McJtyLib) |
| RFTools Builder | S | **Linse (API)** — Builder-/Shield-Daten |
| RFTools Power | S | **Linse (API)** — Power-Zellen |
| RFTools Storage | S | **Linse (API)** — Storage-Scanner-Daten |
| RFTools Utility | S | **Linse (API)** — Utility-Daten (Teleporter etc.) |
| Actually Additions | S | **Linse (API)** — Maschinen, Laser |
| Actually Additions: Tiers | – | Koexistenz |
| Oritech | S | **Linse (API)** — Maschinen-Daten |
| Oritech Things | – | Koexistenz |
| Draconic Evolution | S | **Linse (API)** — Reactor-/Energy-Core-Daten |
| Hostile Neural Networks | S | **Linse (API)** — Data-Model-Fortschritt, Loot-Fabricator |
| Replication | S | **Linse (API)** — Replikations-Fortschritt |
| Replication: RS2 Bridge | – | Koexistenz |
| Just Dire Things | S | **Linse (BE/NBT)** — Maschinen-Daten |
| Just Dire Fuels | – | Koexistenz |
| Just Dyna Things | – | Koexistenz |
| Dyson Sphere Project (dysoncubeproject) | C | **Linse (BE/NBT)** — Sphären-Daten |
| Xycraft Core · Xycraft Machines · Xycraft World | – | Koexistenz |
| Shield Generators | S | **Linse (BE/NBT)** — Schild-Status |
| Mob Grinding Utils | S | **Linse (BE/NBT)** — XP-/Mob-Daten |
| Iron Furnaces | – | Koexistenz |
| Jumbo Furnace | – | Koexistenz |
| Excessive Utilities | – | Koexistenz |
| Productive Metalworks | – | Koexistenz |
| Energymeter | S | **Linse (API)** — FE-Durchsatz-Messung |
| Pipez (+ Lag Fix) | – | Koexistenz |
| LaserIO | S | **Linse (BE/NBT)** — Netzwerk-/Kanal-Daten |
| Laser Bridges | – | Koexistenz |
| Modular Routers | S | **Linse (BE/NBT)** — Router-/Modul-Daten |
| Super Factory Manager (SFM) | S | **Linse (BE/NBT)** — Programm-/IO-Daten |
| Logistics Networks | S | **Linse (BE/NBT)** — Netzwerk-Daten |
| Item Collectors | – | Koexistenz |
| Interdimensional Wireless Transmitter | – | Koexistenz |
| Entangled | – | Koexistenz |
| Compact Machines | S | Koexistenz; offen: Innenwelt von außen lesbar? (Q3) |

### 8.5 Integrations-Matrix — Storage & Logistik (44 Stock)

| Mod | P | yogglez-Interaktion |
|---|---|---|
| Applied Energistics 2 | M | **Linse (API)** — Network Lens (POC M3): Netzwerk, Kanäle, Energie, Puffer |
| AE2 Network Analyzer | S | Koexistenz; Analyse-Funktion wird durch die Network-Linse abgedeckt (Replacement-Kandidat) |
| Advanced AE | S | **Linse (API)** — Advanced-Daten (Scope-Detail Phase 2) |
| Extended AE | S | Koexistenz; Auftrags-Daten via AE2-API |
| Extra Storage | – | Koexistenz |
| ME Cells (MEGA Cells) | S | **Linse (API)** — Zellen-/Typ-Füllstände |
| AE2 Things | – | Koexistenz |
| AE2 Wireless Terminals (ae2wtlib) | – | Koexistenz |
| Ender Drives | – | Koexistenz |
| Better P2P | – | Koexistenz |
| Cable Tiers | – | Koexistenz |
| Cable Facades | – | Koexistenz |
| Applied Mekanistics | – | Koexistenz (Bridge) |
| Applied Flux | – | Koexistenz |
| Applied Replicatics | – | Koexistenz |
| AE2 Crafting Table (ae2ct) | – | Koexistenz |
| AE2 Helpers | – | Koexistenz |
| AE2 JEI Integration | – | Koexistenz |
| AE2 Labeled Patterns | – | Koexistenz |
| AE2 Lightning (ae2lt) | – | Koexistenz |
| Applied Schematicannon | – | Koexistenz |
| Applied Sticks | – | Koexistenz |
| Packaged Auto | S | **Linse (BE/NBT)** — Verpackungs-Aufträge |
| AE2 Draconic Fusion Autocrafter | – | Koexistenz |
| ME Requester | S | Koexistenz; Aufträge via AE2-API (offen, Q4) |
| Retro Factory Manager | – | Koexistenz |
| Refined Sticks | – | Koexistenz |
| Refined Types | – | Koexistenz |
| RS Infinity Booster | – | Koexistenz |
| Pocket Storage | – | Koexistenz |
| Refined Storage | M | **Linse (API)** — Netzwerk-/Crafting-Daten, Speicher |
| Refined Storage: Curios Integration | – | Koexistenz |
| Refined Storage: JEI Integration | – | Koexistenz |
| Refined Storage: Mekanism Integration | – | Koexistenz |
| Refined Storage: Quartz Arsenal | – | Koexistenz |
| Functional Storage | S | **Linse (API/BE)** — Füllstände, External Storage |
| Sophisticated Backpacks | S | **Linse (BE/NBT)** — Füllstände, Upgrades |
| Sophisticated Core | – | Koexistenz (Basis) |
| Sophisticated Item Actions | – | Koexistenz |
| Sophisticated Storage | S | **Linse (BE/NBT)** — Füllstände, Upgrades |
| Sophisticated Storage in Motion | S | **Linse (BE/NBT)** — Speicher auf Contraptions (Demo-Szenario) |
| Ender Storage | – | Koexistenz |
| Integrated Dynamics | S | **Linse (API)** — Variablen-/Netzwerk-Daten (CyclopsCore) |
| Integrated Tunnels | S | Koexistenz; Tunnel-Daten via ID |
| Integrated Crafting | S | Koexistenz; Crafting-Daten via ID |
| Integrated Terminals | – | Koexistenz |

### 8.6 Integrations-Matrix — Magic & Player (20 + 15 Stock)

| Mod | P | yogglez-Interaktion |
|---|---|---|
| Ars Nouveau | M | **Linse (API)** — Source/Mana, Rituale, Glyphen |
| Ars Elemental | S | Koexistenz; Daten via Ars-API |
| Ars Additions | – | Koexistenz |
| Ars Caelum | – | Koexistenz |
| Ars Sable | – | Koexistenz |
| Ars Unification | – | Koexistenz |
| Ars Delight | – | Koexistenz |
| ArsEng (AE2-Bridge) | – | Koexistenz |
| Starbunclemania | – | Koexistenz |
| Not Enough Glyphs | – | Koexistenz |
| Iron's Spells 'n Spellbooks (irons_spellbooks) | M | **Linse (API)** — Mana, Spell-Daten |
| Iron's Jewelry | – | Koexistenz (Curios) |
| Irons Sable Compat | – | Koexistenz |
| Malum | S | **Linse (API)** — Spirit-/Runen-Daten |
| NeoVitae | S | **Linse (API)** |
| Enchanted | S | **Linse (API)** |
| Relics | – | Koexistenz (Curios) |
| Reliquary | – | Koexistenz |
| Summoning Rituals | S | **Linse (BE/NBT)** — Ritual-Status |
| Apotheosis | S | **Linse (API)** — Affix-Daten |
| Apothic Attributes | – | Koexistenz |
| Apothic Spawners | S | **Linse (BE/NBT)** — Spawner-Daten |
| Apothic Compat | – | Koexistenz |
| Apothic Tooltip Cleanup | – | Koexistenz |
| Apothic Enchanting | S | **Linse (BE/NBT)** — Verzauberungs-/Bibliotheks-Daten |
| Puffish Skills | S | **Linse (API)** — Skill-Daten (Player-Linse) |
| Puffish Attributes | S | **Linse (API)** — Attribut-Daten |
| FTB Ranks | S | Framework: Ränge als Datenquelle (offen, Q5) |
| AttributeFix | – | Koexistenz |
| Overloaded Armor Bar | – | Koexistenz |
| Trim Effects (trimeffects) | – | Koexistenz |
| Sable | – | Koexistenz |
| CC Sable | – | Koexistenz |
| Sable Cleanup | – | Koexistenz |
| Sable Jade | – | Koexistenz |
| Sable Mass View | – | Koexistenz |
| Dummy's (dummmmmmy) | – | Koexistenz |
| Fargo's Talismans | – | Koexistenz (Curios) |
| Baubley Heart Canisters | – | Koexistenz (Curios) |
| Charm of Undying | – | Koexistenz |
| Inventory Totem | – | Koexistenz |
| Soulbound | – | Koexistenz |
| Pylons | S | **Linse (BE/NBT)** — Wirkungsbereich |
| Torchmaster | S | **Linse (BE/NBT)** — Spawn-Suppression |

### 8.7 Integrations-Matrix — Farming & Exploration (14 + 25 Stock)

| Mod | P | yogglez-Interaktion |
|---|---|---|
| Mystical Agriculture | M | **Linse (API/BE)** — Essenz, Wachstums-Status |
| Mystical Agradditions | – | Koexistenz |
| Mystical Automation | – | Koexistenz |
| Mystical Customization | – | Koexistenz (datengetrieben) |
| Productive Bees | S | **Linse (API/BE)** — Bienen-Daten |
| Mystian Apiary | – | Koexistenz |
| Farmer's Delight | S | **Linse (BE/NBT)** — Koch-Fortschritt |
| Bonsai Trees | S | **Linse (BE/NBT)** — Wachstums-/Ernte-Daten |
| Squat Grow | – | Koexistenz |
| Growth Accelerator Tiers | – | Koexistenz |
| Random Bonemeal Flowers | – | Koexistenz |
| Fishing Overhaul | – | Koexistenz |
| Spice of Life: Carrot (solcarrot) | – | Koexistenz |
| Farming for Blockheads | – | Koexistenz (Market) |
| Cooking for Blockheads | – | Koexistenz |
| Beer | – | Koexistenz |
| Sky Archipelago | S | Framework: Weltgen (Umgebung, keine Linse) |
| FTB Team Bases | S | Framework: Start-Inseln (Umgebung) |
| Forgiving Void | – | Koexistenz (Void-Regeln) |
| Waystones | S | **Linse (API)** — Waystone-Netzwerk, Aktivierung |
| Waystones Sable | – | Koexistenz |
| TelePastries | – | Koexistenz |
| Simple Teleporters: Reforged | – | Koexistenz |
| Tempad | – | Koexistenz |
| Explorer's Compass | – | Koexistenz (eigenes GUI-Werkzeug) |
| Nature's Compass | – | Koexistenz |
| Gateways to Eternity | S | **Linse (BE/NBT)** — Gateway-Fortschritt |
| Lootr | S | Koexistenz; Loot-Kompatibilität (kein Analysator) |
| Chance Cubes | – | Koexistenz |
| Dragon Fight | – | Koexistenz |
| Easy Villagers | S | **Linse (BE/NBT)** — Villager-/Trade-Daten |
| Supplementaries | – | Koexistenz |
| YUNG's Better Nether Fortresses | – | Koexistenz |
| Lithostitched | – | Koexistenz (Weltgen) |
| Structurify | – | Koexistenz |
| Moog's Structures | – | Koexistenz |
| Moog's Soaring Structures | – | Koexistenz |
| L_Ender's Cataclysm | – | Koexistenz |
| Tropicraft | – | Koexistenz |
| Llama Palooza | – | Koexistenz |
| Colorful Allays | – | Koexistenz |
| Simple Tombs | – | Koexistenz |
| GeOre | S | **Linse (BE/NBT)** — Erz-/Geologie-Daten |
| GeOre Nouveau | – | Koexistenz |

### 8.8 Integrations-Matrix — Computer & FTB-Suite (3 + 22 Stock)

| Mod | P | yogglez-Interaktion |
|---|---|---|
| CC: Tweaked | S | **Linse (API)** — Computer-/Peripheral-Status; offen: Lua als Linsen-Quelle (Q6) |
| Advanced Peripherals | S | **Linse (API)** |
| CC:P:E (ccpe) | – | Koexistenz |
| MoreRed (+ CCT-Compat) | – | Koexistenz |

**Content-/Framework-Mods (17) — yogglez nutzt sie als Erweiterungs- bzw. Doku-Mechanik oder läuft koexistent:**

| Mod | Interaktion |
|---|---|
| KubeJS (core) | **Framework** — Linsen-Definitionen per KubeJS-Skript (MVP-Mechanik) |
| Rhino · LootJS · RenderJS · Kube Utils | Framework — KubeJS-Subsysteme (Loot-Modifikation, Rendering) |
| JEI | Koexistenz — Rezept-Anzeige; Linsen-Rezepte erscheinen dort (Integration offen, Q11) |
| Jade (+ Jade Addons) | Koexistenz — Tooltip-Schicht; Overlay-Reihenfolge/Abgrenzung definieren (VISION §5) |
| Curios | Framework — optionale Slot-Quellen für Linsen (offen, VISION §8.10) |
| Configured Defaults | Koexistenz — Config-Sync-Mechanik des Packs |
| Patchouli · Modonomicon · GuideME · Modopedia | Framework — Doku: yogglez-Guides in Guidebooks (GuideME ist bereits POC-Dependency) |
| GeckoLib · AzureLib · Player Animation Lib | Framework — Animation/Rendering von Brille & Linsen |

| FTB Quests | S | Framework: Quest-Daten als Linsen-Quelle (Quest-Linse, offen Q7) |
| FTB Teams | – | Framework (Team-Daten) |
| FTB Chunks | – | Framework: Claim-Status (offen Q8) |
| FTB Essentials | – | Framework |
| FTB Ultimine | – | Framework |
| FTB Backups | – | Ops |
| FTB Library | – | Framework (Basis) |
| FTB Materials | – | Framework (Unification-Basis) |
| FTB Pack Companion | – | Framework |
| FTB Promoter | – | Framework |
| FTB XMod Compat | – | Framework |
| FTB Filter System | – | Framework |
| FTB JEI Extras | – | Koexistenz |
| FTB Echoes | – | Koexistenz |
| FTB EZ Crystals | – | Koexistenz |
| FTB Obsidian | – | Koexistenz |
| FTB Pause Menu API | – | Koexistenz |
| FTB Stuff & Things | – | Framework (Pack-Content) |
| FTB Skies 2: Aero Companion (ftb-skies-2-aero-companion) | – | Koexistenz (Pack-eigene Mod: Aero Scoop) |

### 8.9 Integrations-Matrix — Addon-Mods (MANIFEST-1.1) & Server-Addons

| Mod | P | yogglez-Interaktion |
|---|---|---|
| Minecolonies | M | **Linse (API)** — Kolonie-Daten: Bürger, Baufortschritt, Ressourcen (Alpha-Highlight) |
| Structurize | – | Koexistenz (Dependency) |
| Domum Ornamentum | – | Koexistenz (Dependency) |
| BlockUI | – | Koexistenz (Dependency) |
| Multi-Piston | – | Koexistenz (Dependency) |
| LDLib2 | – | Koexistenz (Dependency) |
| Carry On | – | Koexistenz; Edge-Case: getragene Maschinen weiterhin lensbar? (Test) |
| Chunky (Server-Addon) | – | Ops: Welt-Vorgenerierung |
| Prometheus Exporter (Server-Addon) | – | Ops: Metriken; offen: yogglez-Nutzungsmetriken (Q9) |
| KubeJS-Skripte (aero_gamerules, aero_collision_fixes) | – | Koexistenz (Server-Skripte) |

*(Die 8 Create-Addon-Mods aus MANIFEST-1.1 sind in §8.3 gelistet; die 15 Addon-Mods sind damit vollständig abgedeckt.)*

### 8.10 Alpha-Exit-Kriterien (messbar)

- [ ] **100 % der M-Linsen** funktionieren auf dem produktiven Aero-Server (Live-Checkliste abgehakt)
- [ ] **Koexistenz-Smoke-Test** für alle 452 Mods bestanden: 2 Wochen Dauerbetrieb ohne kritischen Konflikt, ohne yogglez-bedingten Crash
- [ ] Gametest-Suite grün (≥ 15 Tests, mindestens einer pro M-Linse)
- [ ] ≤ 3 offene P1-Bugs; keine P0
- [ ] Coverage-Checkliste (Anhang A) vollständig abgehakt — jede der 452 Mods ist zugeordnet
- [ ] Version-Pinning dokumentiert (Pack-Version + Mod-Versionen zum Alpha-Zeitpunkt, Q1)
- [ ] Playtest mit yizzl auf einer echten Insel (Luftschiff) inkl. Flug-Szenario (bewegte Contraption + Linse)

### 8.11 Alpha-Risiken

| Risiko | Gegenmaßnahme |
|---|---|
| Produktiver Server leidet unter Tests | Dev-Instanz parallel auf `planet`; Rollout-Gate; nur M-Linsen zuerst |
| Mod-Updates driften (Pack 1.7.x) | Version-Pinning in der Matrix; je Mod-Update Koexistenz-Smoke-Test |
| API-Fehlannahmen bei „Linse (API)"-Mods | Spiking-Auftrag pro M-Linse vor Implementierung |
| Rendering-Bugs erst live sichtbar | Xvfb-Client-Smoke-Test (CI) + manuelle Client-Checks mit yizzl |
| Performance (mehrere Provider pro BE) | Provider-Caching, Tick-freie Abfragen (nur bei Blickkontakt), Metriken via Prometheus |
| 452 Mods → Scope-Inflation | M/S/C-Disziplin; Koexistenz ist die Standard-Antwort, nicht die Ausnahme |

---

## 9. Offene Fragen (Roadmap-Ebene)

- **Q1:** Welche Pack-Version ist die Alpha-Basis — 1.7.0 (Stock-Inventur) oder 1.7.3 (Addon-Manifest)? Exaktes Pinning vor Phase-5-Start.
- **Q2:** Wird der Aeronaut's Compass durch eine Linse ersetzt oder bleibt er Koexistenz?
- **Q3:** Compact Machines: Innenwelt-Status von außen lesbar machen — gewollt?
- **Q4:** ME Requester / Advanced AE: reicht die AE2-API oder brauchen die Addons eigene Adapter?
- **Q5:** FTB Ranks als Datenquelle für eine „Rang-Linse" (Sichtbarkeit von Berechtigungen)?
- **Q6:** CC:Tweaked: Linsen-Daten per Lua-Programm erweiterbar (Community-Linsen in Lua)?
- **Q7:** Quest-Linse: FTB-Quests-Fortschritt im Goggle-Overlay — Scope?
- **Q8:** FTB Chunks: Claim-Grenzen als Linsen-Overlay?
- **Q9:** yogglez-Nutzungsmetriken (aktive Linse, Häufigkeit) via Prometheus-Exporter?
- **Q10:** Wer reviewt VISION/ROADMAP (yizzl/Momo)? Merge-Kriterium für diesen PR.
- **Q11:** JEI-Integration: Linsen-Rezepte/-Doku in JEI anzeigen (Rezept-Tab)?

## Anhang A — Koexistenz-Mods (keine direkte Interaktion)

> Interaktion für alle folgenden Mods: **Koexistenz** — konfliktfreier Parallelbetrieb, abgedeckt durch den Alpha-Koexistenz-Smoke-Test (§8.10). Keine Linse, keine Daten-Interaktion geplant. Vollständige explizite Liste des Stock-Koexistenz-Anteils; die 15 Addon-Mods stehen in §8.3/§8.9, die Server-Addons (Chunky, Prometheus-Exporter) ebenfalls in §8.9.

**Performance / Server-Betrieb (24):** Clumps · FastSuite · Measurements · NaNny · Accelerated Decay · AllTheLeaks · Async Locator (Refined) · Async Logger · Better Compatability Checker · CMPreviewFixer · Crash Utilities · Cupboard · Default Server Properties · Derender Patcher · Fast Async World Save · FerriteCore · ImFast · Leaderboards · ModernFix · ServerCore · SG Economy · Spark · State Observer · WITS

**Libraries / APIs (Basis, teils Framework):** Aaron · AnimusNv · Architectury · Ash API · Athena · Atlas API · Auroral · Azimuth · BaguetteLib · Balm · Belts · Bookshelf · BrandonsCore · Caelus · Carbon Config · Cloth Config · CodeChickenLib · Collective · CorgiLib · CoroUtil · CristelLib · CTM · Cucumber · CyclopsCore · Deimos · EdivadLib · EventsLib · Flourish · Fusion · Fzzy Config · Gag · Gaze · Glodium · iChunUtil · Irons Lib · JAGS · Jamd · JamLib · Kiwi · Kotlin for Forge (kotlinforforge) · LibrarianLib · LionfishAPI · Lodestone · Lychee · McJtyLib · Moonlight · MRU · NEP · OctoLib · Oracle Index · owo-lib · Placebo · Platform · PolyLib · POP · Prickle · Puzzles Lib · Refined Types (s. §8.5) · Resourceful Config · Resourceful Lib · Ritchies Projectile Lib · Scalable Cats Force · Searchables · Shtreimel · SmartBrainLib · Structure Expansion · SuperMartijn642 Config Lib · SuperMartijn642 Core Lib · Tesseract API · Titanium · Trenzalore · Valhelsia Core · Watut · Weathergate · YACL (Yet Another Config Lib) · YUNG's API

**QoL / Building / Deko (Rest der 54):** AppleSkin · Armor Poser · Better Blockz · Bits 'n Bobs · Bridging Mod · Bucket Lib · Building Gadgets 2 · Ceramic Bucket · Ceramic Shears · Chipped · Chipped Express · Chisel · Clean Swing · Climbable Ropes · Cloud Glass · Connected Glass · Construction Sticks · Cooking for Blockheads (s. §8.7) · Cosmetic Armor Reworked · Crafting Station JEI · Crafting Tweaks · Eccentric Tome · Elevator ID · Emojiful · Fast Item Frames · Framed Blocks · Glassential · Hooked · Inventory Essentials · Inventory Sorter · Just Enough Breeding · Just Enough Professions · Just Enough Spirits · MCW Furniture · MCW Lights · MoreRed (s. §8.8) · Pipe Connector · Redstone Pen · Showcase Item · Shrink · Simple Magnets · Simply Light · StepCrafter · ToolBelt · Trash Cans · Trash Slot · Traveler's Titles · What the Bucket · Wooden Shears

**Sonstiges (Rest der 43):** Advanced Loot Info · Almanac · Ambient Environment · All Arrows Infinity Fix (allarrowsinfinityfix) · BetterBlockz (s. o.) · Cognition · Common Capabilities · Compact Crafting · Entity Guardian · Feature Recycler · GMAU (Gravitational Modulating Additional Unit) · Irregular Implements · Loot Integrations · Meed · NeoSync · Not Enough Recipe Book · Rain Shield · RS Gauges · Simulated Coasters · StepCrafter (s. o.) · Thunderbolt · ToolKit · Utilitarian · Vanilla Backport

**Client-only-Mods (39, im Server-Pack nicht enthalten):** nicht Teil der Alpha-Integrationsfläche — Relevanz nur für den Client-Smoke-Test (z. B. Sodium/Iris-Kompatibilität des Overlay-Renderings, checked in CI).

---

*Dieses Dokument ist ein Vorschlag. Die Integrations-Matrix basiert auf `docs/stock-pack.md` (1.7.0) und `mods-addon/MANIFEST-1.1.md` (1.7.3) des Repos momokli/ftb-skies-2-aero; Änderungen am Server-Pack verschieben die Matrix (Q1).*
