# create?:yogglez

![create:yogglez lens concept](mod-design/create_brille_lenses.jpg)

A **central, modular goggle framework** for Minecraft modpacks that empowers pack creators and players
with data-driven **JSON and KubeJS extensibility**.

Instead of carrying a separate handheld analyzer for every mod, you equip a unified pair of goggles
and upgrade them with **lenses**. The headgear takes over UI and information display exclusively, operating as a **standalone framework**
that can optionally integrate with popular aesthetics like Create's *Engineer's Goggles*.

## Core idea

- **Universal modular headgear** — a dedicated goggle framework equipped with a modular lens system to analyze machinery and
    networks, functioning standalone or bridging seamlessly into Create's aesthetic.
- **Pure analysis upgrade** — the goggles only display information; physical tools stay in your hand for manual interaction.
- **Modular physical sockets** — lenses are physical items slotted directly into the server-side goggle frame.
- **Data-driven extensibility** — pack authors can integrate third-party analysis tools via JSON and KubeJS.

## To Be Discussed

- **MVP Scope & Architectural Viability:** Is moving away from Create’s native goggle framework as the baseline feasible for an MVP, or should we keep Create as a hard dependency for release one?  
  *Decision Basis:* **Vision** (immediate reach across all pack styles vs. staying purely in the Create ecosystem) vs. **Strategy** (shipping a fast, low-risk MVP vs. committing to a longer foundational build phase) vs. **Development** (tearing out tightly bound event listeners right now vs. inheriting existing POC technical debt).

- **Soft-Dependency Decoupling:** How should equipment checks and wear predicates (such as Create's `addIsWearingPredicate`) be refactored into a soft-dependency plugin architecture to ensure crash-free operation across diverse modpacks?  
  *Decision Basis:* **Vision** (making the mod a universal kitchen-sink staple vs. a dedicated tech-mod companion) vs. **Strategy** (accepting higher initial design overhead to unlock massive future adoption vs. building fast with hard dependencies) vs. **Development** (writing dynamic runtime class checks and mixin plugins vs. relying on compile-time static imports).

- **POC Validation & Build-System Impact:** How will transitioning the active POC codebase (currently tied directly to Create 6.0.9 and its native goggle extension points) safely refactor into a soft-dependency plugin architecture without breaking current gradle builds, gametests, or local client smoke tests?  
  *Decision Basis:* **Vision** (ensuring a completely bulletproof, bug-free player experience from day one) vs. **Strategy** (halting new feature work to clean up tech debt vs. rushing features on top of a shaky prototype) vs. **Development** (restructuring Gradle source sets and rewriting gametests to mock missing mods vs. maintaining a monolithic codebase).

- **Cross-Mod Base Compatibility & Standalone Headgear:** How will the mod handle self-contained progression and vanilla-based crafting recipes for packs that exclude Create entirely, while still supporting optional upgrade paths if Create is present?  
  *Decision Basis:* **Vision** (giving pack authors total creative control over progression vs. sticking to predictable, predefined recipes) vs. **Strategy** (balancing parallel crafting trees for vanilla vs. modded vs. supporting a single standard path) vs. **Development** (architecting dynamic datapack recipe injection systems vs. hardcoding static JSON recipes).

- **Asset Pipeline & Dual-Base Theming:** What is the artistic and technical scope for maintaining a neutral, standalone default goggle asset alongside an optional Create-integrated variant without adding unnecessary overhead?  
  *Decision Basis:* **Vision** (delivering a seamless visual identity whether played in vanilla survival or heavy steampunk packs) vs. **Strategy** (doubling our 3D asset and texture maintenance workload for every update vs. settling for a single universal look) vs. **Development** (building dynamic model loaders and conditional texture overrides vs. using a single static item model).

- **Data-Driven Extensibility & Script Safety:** How can the framework architect KubeJS hooks and datapack JSON loaders to support hot-reloading via `/reload` while maintaining strict server-authoritative boundaries?  
  *Decision Basis:* **Vision** (empowering creators to tweak and test lenses live without restarting the server) vs. **Strategy** (accepting runtime script vulnerability vs. enforcing rigid, compiled-only stability) vs. **Development** (writing safe cache-clearing memory management for `/reload` vs. executing static Java logic).

- **Network and Data Synchronization:** How does the server-authoritative architecture ensure that a lens defined via JSON or KubeJS renders flawlessly on the client and is supplied with data without requiring the client to execute modified code?  
  *Decision Basis:* **Vision** (guaranteeing a buttery-smooth, lag-free multiplayer HUD experience) vs. **Strategy** (hardening multiplayer security against client-side exploitation vs. prioritizing ease of data transfer) vs. **Development** (engineering lightweight, generic packet payloads vs. sending heavy, frequent state updates).

- **Overlapping Lens Resolution:** How should the framework handle blocks compatible with multiple installed lenses—implementing priority weights, contextual sub-cycling, or a focus-lock mechanic to prevent UI ambiguity?  
  *Decision Basis:* **Vision** (ensuring multi-tool blocks feel completely intuitive and frictionless to inspect) vs. **Strategy** (forcing players to learn extra keybinds or modifier rules vs. relying on automatic guessing that might pick the wrong tool) vs. **Development** (building client-side state machines and priority-weight sorting systems vs. hardcoding simple first-match lookups).

- **Maintainability and Longevity:** When mods are updated (e.g., API methods for AE2 or Immersive Engineering change), how does the JSON system protect pack creators from silent failures or game crashes during runtime?  
  *Decision Basis:* **Vision** (protecting modpacks from breaking when major third-party mods update their APIs) vs. **Strategy** (investing heavily in boilerplate architecture now vs. dealing with constant maintenance patches later) vs. **Development** (writing defensive reflection and modular adapter wrappers vs. calling foreign API methods directly).

## Design Philosophy

 - **Inventory Freedom & Constant Readiness:** Consolidating half a dozen niche diagnostic tools (multimeters, network tools, scanners) into a single worn headgear keeps vital inventory space open.
    Once earned and installed, the right analytical tool is always active when looking at a machine, while contextual sub-cycling effortlessly untangles multi-compatible machinery, removing
    tedious tool-swapping and inventory management while preserving the weight of progression.
  - **Pedagogical Progression & Narrative Gating:** Withholding holistic data overlays until a lens is physically crafted and unlocked enforces
    a natural learning curve. Players must first engage with individual machine components manually to understand how systems work at a
    granular level, turning the lens into a hard-earned reward for mastering the underlying mechanics.
  - **Decentralized Open Platform:** Moving beyond a static feature set, the JSON and KubeJS integration architecture empowers pack creators to build
    and share their own lens definitions. This creates an open, decentralized ecosystem where pack authors can bypass waiting for official mod
    updates and seamlessly expand analytical support for any mod.

## Controls & Ergonomics

- **Tactile Socketing** — hold the framework in your offhand and apply a lens directly to integrate it into the physical housing.
- **Sequential & Contextual Cycling** — trigger a rapid-shift input to step through your installed lenses, or use a contextual modifier when looking at multi-compatible machinery to cycle strictly between valid lenses for that target.
- **Radial Selection Wheel** — hold the menu trigger to bring up a contextual selection interface, allowing direct access to any installed lens without breaking movement or vision.
- **Adaptive HUD Feed** — server-side data streams dynamically into the active lens view, providing clear visual feedback at the periphery of your sight line whenever you face compatible machinery.

## Example lenses (handheld-analyzer replacements)
*Data-driven templates defining these foreign mod integrations (via JSON and KubeJS) allow pack authors to configure custom mappings effortlessly:*
| Mod                   | Replaces              | Shows                   |
| --------------------- | --------------------- | ----------------------- |
| Applied Energistics 2 | Network Tool          | Networks & channels     |
| Immersive Engineering | Engineer's Multimeter | Wiring & machine values |
| Industrial Craft 2    | Scanner / Tricorder   | Machine status & energy |

## Status

✅ **POC (branch `feature/poc`)** — a runnable first implementation exists. See the
[POC section](#poc) below for stack, build instructions, milestones and what works.

## POC

A proof-of-concept implementation of the core lens framework on
**NeoForge 1.21.1 + Create 6.0.9** (Java 21, Gradle / NeoGradle userdev).

### Stack

| Component  | Version                                                                                          |
| ---------- | ------------------------------------------------------------------------------------------------ |
| Minecraft  | 1.21.1                                                                                           |
| NeoForge   | 21.1.217                                                                                         |
| Create     | 6.0.9-216 (`com.simibubi.create:create-1.21.1`, slim artifact)                                   |
| Registrate | MC1.21-1.3.0+67 (maven.ithundxr.dev)                                                             |
| Ponder     | 1.0.81+mc1.21.1 (bundles catnip, incl. `LangBuilder` — do **not** add catnip separately)         |
| Flywheel   | 1.0.6 · Vanillin 1.1.3-41                                                                        |
| AE2        | 19.2.17 (optional lens target, `compileOnly` + `localRuntime`, public `appeng.api` only)         |
| GuideME    | 21.1.17 (`localRuntime`, AE2 dependency)                                                         |
| License    | MIT                                                                                              |

### Build

```bash
# JDK 21 required (e.g. Temurin 21); Gradle 9.2.1 wrapper included
./gradlew build          # compiles + runs processResources (green)
./gradlew runGameTestServer   # headless gametests (3/3 pass, see below)
./gradlew runClient      # dev client (needs a display)
```

Dev-run runtime mods (Create, Ponder, Flywheel, Vanillin, Registrate, AE2, GuideME)
are pulled from the configured Maven repositories automatically.

### Milestones

| #   | Goal                                                             | Status |
| --- | ---------------------------------------------------------------- | ------ |
| M0  | Template + Create dependency set up, build green                  | ✅     |
| M1  | `Yogglez Goggles` + `Lens` items, code registry, "cycle lens" keybind (default `H`) | ✅ |
| M2  | Demo `YogglezTestMachineBlockEntity` implements Create's `IHaveGoggleInformation` — custom info in the real Create goggle overlay | ✅ (server-side data verified by gametest; visual overlay = manual smoke test) |
| M3  | Client-side provider registry (`BlockEntityType -> LensInfoProvider`), AE2 Network Lens via `appeng.api`, HUD lens indicator | ✅ (compiles + loads; visual = manual smoke test) |

### What works

- **Own goggles, not Create's**: `yogglez:yogglez_goggles` is a new `GogglesItem`
  subclass. Create recognizes it through the official
  `GogglesItem.addIsWearingPredicate(...)` extension point, so its goggle overlay
  (tooltips, value boxes) turns on when you wear them.
- **Modular lens storage**: lens ids are stored in the item's `custom_data`
  component (`Lenses` list + `ActiveLens` index). Install: hold the goggles in the
  offhand and right-click a `Lens` item. Cycle: `H` key (payload → server-side
  cycle → sync).
- **M2 core proof**: `yogglez:test_machine` is a BlockEntity implementing Create's
  public API `IHaveGoggleInformation`; its `addToGoggleTooltip` shows simulated
  process values (Status/Temperature/Pressure/Throughput) in the **real Create
  goggle overlay**. The overlay icon is replaced with the Yogglez Goggles
  (`IHaveCustomOverlayIcon.getIcon`).
- **M3 foreign blocks**: client-side `LensProviderRegistry` maps `BlockEntityType`
  → `LensInfoProvider` (no mixins). Two example providers:
  - `yogglez:demo` (Insight Lens) — generic block analysis for vanilla BEs
    (furnace/chest/hopper/brewing stand): block name, BE type, NBT key overview.
  - `ae2:network` (Network Lens) — **Network Tool replacement** for AE2
    controllers/drives/energy acceptors etc., read strictly via public
    `appeng.api` (`GridHelper.getExposedNode`, `IGridNode`, `IGrid`,
    `IEnergyService`, `IPathingService`): node status, channels, power, buffer.
- **HUD indicator** (`lens_hud` layer): shows the active lens bottom-right.
- **Foreign-block tooltip overlay** (`lens_info` layer, registered above Create's
  `goggle_info`): renders lens data when a provider matches the looked-at block.
- **Gametests** (`runGameTestServer`, headless): goggle-tooltip content,
  lens install/activate/cycle NBT lifecycle, lens registry. 3/3 pass.
- **I18n**: `en_us` + `de_de`.

### What does NOT work yet / limitations

- The **visual** side (Create overlay rendering for the test machine, lens HUD,
  AE2 network tooltip in-world) is implemented but was **not** verified in a live
  client in this environment (headless build sandbox, no display). Manual smoke
  test: `./gradlew runClient`, give yourself the items via the `create:yogglez`
  creative tab, wear the goggles, look at a test machine / AE2 controller.
- Lens items are a single item with NBT variants (creative tab offers
  pre-configured ones); distinct items + crafting recipes are M4+.
- No radial menu, no KubeJS/JSON lens definitions, no config screen yet.
- AE2 lens shows network data of the *looked-at node's grid*; multi-network
  overlays and power-cell specifics are follow-ups.

### Manual client smoke test

```text
1. ./gradlew runClient
2. Creative tab "create:yogglez": take Yogglez Goggles + Insight Lens + AE2 Network Lens (+ test machine block)
3. Wear the goggles (right-click) -> Create's overlay appears for Create blocks and the test machine
4. Hold a lens + goggles in offhand, right-click -> lens installed (tooltip shows it)
5. Press H -> cycles the active lens; HUD indicator bottom-right updates
6. Look at a furnace (Insight Lens) or an AE2 controller/drive (Network Lens)
   -> foreign-block tooltip with network status/channels
```

### Open points (M4+)

- KubeJS/JSON lens definitions for pack authors, radial lens menu,
  more lenses (Immersive Engineering, Industrial Craft 2), real recipes,
  publishing (Modrinth), config screen.

## Design notes

- [`mod-design/yizzl_initial_thoughts.md`](mod-design/yizzl_initial_thoughts.md) — original idea
- [`mod-design/linsenbrille.txt`](mod-design/linsenbrille.txt) — elaborated concept
- [`mod-design/abgrenzung_cyber_goggles.md`](mod-design/abgrenzung_cyber_goggles.md) — design principle: physical item, not a client-side overlay (Yizzl)
