# Design principle: physical item, not a client-side overlay

**Why create:yogglez is not another client-side-only goggle mod.**

There are client-side-only mods that add overlays/HUDs for block information —
e.g. **Create: Cyber Goggles**. On the surface they look similar to this
project. They are not. Yizzl's position on Cyber Goggles (quote):

> „dadurch das es client side ist, finde ich geht es an der vision vorbei"

*"because it is client-side, I think it misses the vision"*

## What client-side-only means for the vision

A client-side-only mod:

- works only for the player who installed it — information is *not* bound to
  anything in the game world;
- cannot be enforced on servers / cannot be a shared gameplay element in
  modpacks;
- has no physicality: the "goggles" are not a thing you craft, hold, wear and
  trade — they are a config toggle;
- cannot be the foundation of a Hero Story, because nothing in the world
  changes when you obtain them.

## The yogglez vision (design principles)

1. **Physical item, server-side logic.** The Yogglez Goggles are a real item:
   crafted, held, worn in the head slot, carried between inventories. The lens
   system lives in the item data (NBT on the server), not in the client's
   renderer. Any player on a server sees the same goggles and the same lenses.
2. **Pure analysis upgrade.** The goggles only *display* information — physical
   tools (wrenches, analyzers) stay in the hand for interaction. But the
   display is driven by server-authoritative data.
3. **Pack-developer API.** Modpack authors integrate third-party analyzers as
   lenses (later via JSON/KubeJS) — a content-side API, not a client patch.
4. **Hero-Story potential (later).** Because the goggles are a physical,
   upgradeable tool, they can carry progression and narrative — something a
   client-side overlay can never do.

## Consequence

Client-side rendering is used *only* as the presentation layer on top of
server-side state (lens inventory, active lens, provider data). If a feature
can only exist client-side, it does not fulfill the vision and is deferred or
rejected.

## Relation to Create: Cyber Goggles

| Aspect                | Create: Cyber Goggles (client-side) | create:yogglez                     |
| --------------------- | ----------------------------------- | ---------------------------------- |
| Installation          | client mod, per-player              | world item, server-side state      |
| Goggles               | visual overlay                      | physical item (craft, hold, wear)  |
| Lens system           | not applicable                      | item data (NBT), swappable lenses  |
| Pack integration      | per-user config                     | developer API for modpacks (M4+)   |
| Multiplayer semantics | cosmetic/individual                 | shared, consistent for all players |
