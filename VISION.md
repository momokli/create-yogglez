# VISION.md — create:yogglez

> **Status: PROPOSED** — Entwurf zur Diskussion, noch kein abgestimmtes Projektziel.
> Stand: 2026-09-01 · Quellen: `README.md` (main, Stand 01.09.2026), `mod-design/`, POC-Erfahrung (`feature/poc`, PR #1).

---

## 1. Was yogglez ist (TL;DR)

**create:yogglez** (Arbeitstitel, README: „create?:yogglez" — Namensfrage offen) ist ein **zentrales, modulares Brillen-Framework** für Minecraft-Modpacks. Statt für jeden Mod einen eigenen Hand-Analysator (Multimeter, Network Tool, Scanner, Tricorder) mitzuschleppen, rüstet der Spieler **eine** Brille mit **physikalischen, wechselbaren Linsen** auf. Die Brille übernimmt ausschließlich die Anzeige — physische Werkzeuge bleiben für Eingriffe in der Hand.

Der Rahmen ist als **Standalone-Framework** konzipiert: eigene Progression, eigene Rezepte, eigener Look. Die Integration in die Ästhetik der Create-Ingenieursbrille ist ein **optionaler Brücken-Baustein**, kein Fundament (Richtungswechsel gegenüber dem POC, siehe §8).

## 2. Warum yogglez existiert

1. **Analysator-Müll.** Große Modpacks zwingen Spieler, Dutzende Einzweck-Werkzeuge mitzuführen („du musst diesen Analyser in der Hand halten, um die Prozesse zu sehen"). Das widerspricht der Create-Mentalität: ein Werkzeug, klare Mechanik, wenig Inventar-Chaos.
2. **Progression als Belohnung.** Eine Linse ist ein **hart erarbeitetes, physisches Upgrade** — sie schaltet Wissenszugriff erst frei, nachdem der Spieler die zugrunde liegende Mechanik manuell verstanden hat („Pedagogical Progression").
3. **Offene Plattform statt Warteschleife.** Pack-Autoren sollen Linsen selbst definieren können (JSON/KubeJS), ohne auf offizielle Mod-Updates zu warten. Analyse-Unterstützung wird zu einem dezentralen Ökosystem.
4. **Hero-Story-Potenzial.** Weil die Brille ein physisches, aufrüstbares Item ist (craftbar, handelbar, tragbar, server-seitig), kann sie Progression und Erzählung tragen — das kann ein Client-Overlay nie.

## 3. Zielbild

> **Eine Brille. Alle Analysen. Server-seitig. Pack-Autoren-definiert.**

- Ein Spieler trägt die Yogglez-Brille; beim Blick auf eine Maschine erscheint automatisch die passende Analyse (kontextuelle Linsen-Auflösung, kein Werkzeug-Swapping).
- Pack-Autoren legen Linsen als **Daten** ab (JSON/KubeJS): „Zeige auf Block X die Werte Y". Kein Java nötig.
- Die Brille funktioniert in **jedem Pack** (auch ohne Create) und **brückt optional** in Create-Packs (Overlay, Rezepte, Ästhetik).
- Multiplayer: **server-autoritative Daten**, Client rendert nur. Kein Spieler kann sich Information „cheaten", die er nicht erarbeitet hat.
- Langfristig: yogglez ist der Standard-Analyse-Hub für NeoForge-Modpacks — mit einer aktiven Community geteilter Linsen-Definitionen.

## 4. Kern-Prinzipien

| # | Prinzip | Konsequenz |
|---|---------|------------|
| P1 | **Physisches Item, server-seitige Logik** | Brille + Linsen sind echte Items mit Zustand im Item-Data (Server). Kein Client-Overlay, kein Config-Toggle. Für alle Spieler identisch sichtbar. |
| P2 | **Reines Analyse-Upgrade** | Die Brille zeigt nur Information. Interaktion (Wrench, Schraubenzieher) bleibt beim physischen Werkzeug in der Hand. |
| P3 | **Datengetriebene Erweiterbarkeit** | Linsen-Definitionen via JSON + KubeJS (Datapack-/Script-Mechanik, `/reload`-fähig). Java-Adapter nur dort, wo eine Mod-API zwingend nötig ist. |
| P4 | **Soft-Dependency-Architektur** | Create und alle Linsen-Ziel-Mods sind optional. Fehlender Mod → kein Crash, sondern schlicht keine Linse. Kein Mixin in fremde Mods; nur öffentliche APIs oder BlockEntity-/NBT-Daten. |
| P5 | **Server-Autorität** | Linsen-Daten entstehen serverseitig; der Client rendert ausschließlich. Netz-Payloads klein und generisch (Sicherheit + Performance). |
| P6 | **Kontextuelle Auflösung statt Overlay-Rauschen** | Kein WAILA-Klon: Anzeige nur, wenn eine aktive Linse zum Zielblock passt; bei Mehrfach-Treffern Prioritäts-/Sub-Cycling-Regeln statt UI-Mehrdeutigkeit. |
| P7 | **Physisches Socketing** | Linse installieren = Brille in die Offhand, Linse rechtsklicken. Kein Menü-Slot-Grind. |

## 5. Was yogglez nicht ist

| Verwechslungskandidat | Abgrenzung |
|---|---|
| **Jade / WAILA-Overlays** | Jade zeigt *immer* alles (auch ohne Progression). yogglez zeigt *nur* das, was eine **erarbeitete, aktivierte Linse** freischaltet. Koexistenz-Regeln für Tooltip-Schichten sind Teil der Alpha. |
| **Create: Cyber Goggles (client-side)** | Reines Client-Overlay, keine Welt-Physik. Verfehlt die Vision („dadurch das es client side ist, geht es an der vision vorbei" — yizzl). yogglez: Item + Server-State. |
| **Einzel-Analysatoren (AE2 Network Tool, PnC-…, JEMM, AE2 Network Analyzer)** | yogglez ersetzt deren *Anzeige-Funktion* durch Linsen — die Items bleiben existieren, werden aber obsolet im Inventar. |
| **Jade-/TheOneProbe-Ersatz generell** | yogglez ist kein „alles anzeigen"-Mod, sondern ein **Progression-gekoppelter Analyse-Hub**. |

## 6. Wer es nutzt

- **Spieler:** ein Inventar-Slot statt sechs Diagnose-Werkzeuge; Wissen als Belohnung.
- **Pack-Autoren:** Linsen per JSON/KubeJS definieren und teilen; kein Warten auf yogglez-Updates.
- **Server-Betreiber:** server-autoritative Daten, keine Client-Pflicht-Installation für die Kernlogik; Nutzung als Test-Environment (siehe ftb-skies-2-aero, §9).

## 7. Langfristige Erfolgskriterien (Richtwerte, nicht Zielzahlen)

- Eine neue Linse ist **ohne Java-Änderung** erstellbar (Doku-Test: Pack-Autor folgt einem Tutorial).
- Der Katalog „ersetzbarer Analysatoren" wächst pro Release (gemessen an der Integrations-Matrix in `ROADMAP.md`).
- yogglez läuft **konfliktfrei** auf dem ftb-skies-2-aero-Server (452 Mods, §9) — der Härtetest für Soft-Dependency-Anspruch.
- Multiplayer-konsistent: zwei Spieler sehen dieselbe Analyse, keine Client-Manipulation möglich.

## 8. Offene Fragen (bewusst markiert, nicht beantwortet)

Diese Fragen sind aus dem README („To Be Discussed") und der POC-Analyse übernommen. Sie sind **Entscheidungsgegenstand der concept-Phasen** (Phase 2 bzw. Phase 4 der Roadmap) — nicht hier vorentschieden:

1. **Create-Bindung:** Bleibt Create für Release 1 eine harte Dependency oder ist der Standalone-Pfad (eigene Rezepte/Progression) das MVP-Ziel? (POC ist fest an Create 6.0.9 gebunden — Refactoring-Aufwand offen.)
2. **Soft-Dependency-Mechanik:** Wie werden Wear-Predicates (`addIsWearingPredicate`) und Overlay-Integration in eine Plugin-Architektur ohne statische Importe refaktoriert (Reflection/ServiceLoader vs. Mixin-Plugin)?
3. **JSON- vs. KubeJS-Schema:** Welches Format ist kanonisch? Wie sieht das Sicherheitsmodell für `/reload` aus (Script-Fehler dürfen den Server nicht fällen)?
4. **Netz-Sync:** Wie kommen JSON/KubeJS-Linsen-Daten auf den Client, ohne dass der Client fremden Code ausführt (generische Payloads, Cache-Invalidierung)?
5. **Linsen-Auflösung:** Prioritäts-Gewichte, kontextuelles Sub-Cycling oder Focus-Lock bei Mehrfach-Treffern?
6. **API-Stabilität:** Wie schützt das JSON-System Pack-Autoren vor API-Brüchen fremder Mods (defensive Adapter, Versionierungs-Metadaten)?
7. **Namensgebung & Marke:** „create:yogglez" vs. „yogglez" — der Namespace ist `yogglez`, der Titel im README offen.
8. **Versions-Strategie:** Nur 1.21.1/NeoForge (wie POC und Aero-Server) oder Multi-Version?
9. **Assets:** Ein neutraler Standalone-Look plus optionaler Create-Variante — Scope für Texturen/Modelle?
10. **Curios/Slots:** Nur eine aktive Linse im Item-Data, oder zusätzliche Slot-Quellen?

## 9. Bezug zur Test-Umgebung (ftb-skies-2-aero)

Der Server **github.com/momokli/ftb-skies-2-aero** (FTB Skies 2: Aero, MC 1.21.1, NeoForge 21.1.248, produktiv auf `planet` / aero.projectmellon.de) ist die **Referenz-Test-Umgebung der Alpha**:

- **452 Mods** (435 Stock + 15 Addon + 2 Server-Addons) bilden die Integrationsfläche — kein anderes Test-Setup deckt mehr ab.
- Gleiche MC-/Loader-Basis wie der POC (1.21.1, NeoForge, Java 21) → maximale Übertragbarkeit der POC-Erfahrung.
- Der Server erzwingt die **Soft-Dependency-Härte**: yogglez muss neben 451 anderen Mods crashfrei laufen und gezielt deren öffentliche APIs anzapfen.

Die vollständige Ableitung der Alpha-Integrationsfläche aus der Server-Mod-Liste steht in `ROADMAP.md`, Phase 5 (§8.3–8.10).

---

*Dieses Dokument ist ein Vorschlag. Widersprüche zum README (insb. die „To Be Discussed"-Sektion) sind beabsichtigt markiert und werden in Phase 2 der Roadmap entschieden.*
