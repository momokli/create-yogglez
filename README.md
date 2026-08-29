# Yogglez

![Yogglez lens concept](mod-design/create_brille_lenses.jpg)

A central, modular goggle framework for Minecraft modpacks — built on top of the
Create **Engineer's Goggles**.

Instead of carrying a separate handheld analyzer for every mod, you upgrade the
engineer's goggles with swappable **lenses**. The goggles stay a physical tool
in your hand, but take over UI and information display exclusively.

## Core idea

- **Create integration as the foundation** — the Engineer's Goggles are extended
  with a modular lens system.
- **Pure analysis upgrade** — the goggles only display information. Physical
  tools (e.g. wrenches) stay in your hand for manual interaction.
- **Developer API for modpacks** — pack authors can integrate third-party
  analysis tools (e.g. via JSON/KubeJS) as goggle lenses.

## Example lenses (handheld-analyzer replacements)

| Mod                   | Replaces              | Shows                   |
| --------------------- | --------------------- | ----------------------- |
| Applied Energistics 2 | Network Tool          | Networks & channels     |
| Immersive Engineering | Engineer's Multimeter | Wiring & machine values |
| Industrial Craft 2    | Scanner / Tricorder   | Machine status & energy |

## Controls

- **Quick cycle** — cycle through the active lenses sequentially with a key press.
- **Radial menu** — hold a key to open a Create-style gear menu for direct lens
  selection.
- **Visual feedback** — subtle HUD indicators or color tints at the screen edge
  show the currently active view.

## Status

⚠️ **Concept phase** — only design notes and the initial concept exist so far
(see [`mod-design/`](mod-design/)). There is no code yet.

## Design notes

- [`mod-design/yizzl_initial_thoughts.md`](mod-design/yizzl_initial_thoughts.md) — original idea
- [`mod-design/linsenbrille.txt`](mod-design/linsenbrille.txt) — elaborated concept
