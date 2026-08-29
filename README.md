# Yogglez

Ein zentrales, modulares Brillen-Framework für Minecraft-Modpacks — auf Basis der
Create **Engineer's Goggles**.

Statt für jede Mod einen eigenen Hand-Analysator mitzuschleppen, rüstest du die
Ingenieursbrille mit aufrüstbaren **Linsen** auf. Die Brille bleibt dabei ein
physisches Werkzeug in der Hand, übernimmt aber ausschließlich die UI- und
Informationsanzeige.

## Kernidee

- **Create-Integration als Basis** — die Engineer's Goggles werden um ein
  modulares Linsensystem erweitert.
- **Reines Analyse-Upgrade** — die Brille zeigt nur Informationen an. Physische
  Werkzeuge (z. B. Schraubenschlüssel) bleiben für die manuelle Interaktion in
  der Hand.
- **Entwickler-API für Modpacks** — Pack-Autoren können fremde Analyse-Tools
  (z. B. via JSON/KubeJS) als Brillen-Linsen integrieren.

## Beispielhafte Linsen (Hand-Analysator-Ersatz)

| Mod | Ersetzt | Zeigt |
| --- | --- | --- |
| Applied Energistics 2 | Network Tool | Netzwerke & Kanäle |
| Immersive Engineering | Engineer's Multimeter | Leitungen & Maschinenwerte |
| Industrial Craft 2 | Scanner / Tricorder | Maschinenstatus & Energie |

## Steuerung

- **Schnelldurchlauf** — per Tastendruck sequenziell durch die aktiven Linsen
  schalten.
- **Radialmenü** — Taste gedrückt halten öffnet ein visuelles Zahnrad-Menü im
  Create-Stil zur direkten Linsenauswahl.
- **Visuelles Feedback** — dezente HUD-Indikatoren oder Farb-Tints am
  Bildschirmrand zeigen die aktive Ansicht an.

## Status

⚠️ **Konzeptphase** — aktuell existieren nur Design-Notizen und das initiale
Konzept (siehe [`mod-design/`](mod-design/)). Es gibt noch keinen Code.

## Design-Notizen

- [`mod-design/yizzl_initial_thoughts.md`](mod-design/yizzl_initial_thoughts.md) — ursprüngliche Idee
- [`mod-design/linsenbrille.txt`](mod-design/linsenbrille.txt) — ausgearbeitetes Konzept
