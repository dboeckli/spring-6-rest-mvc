# Claude Code — Cheat Sheet

## Skills (`/skillname`)

|            Skill            |                                Wann benutzen                                |
|-----------------------------|-----------------------------------------------------------------------------|
| `/find-docs`                | Aktuelle Doku für Libraries, Frameworks, SDKs, CLIs (via ctx7)              |
| `/dataviz`                  | Charts, Graphen, Dashboards designen (Farben, Layout, Accessibility)        |
| `/update-config`            | settings.json anpassen: hooks, permissions, env vars                        |
| `/keybindings-help`         | Keyboard Shortcuts anpassen (`~/.claude/keybindings.json`)                  |
| `/simplify`                 | Geänderten Code auf Vereinfachung, Effizienz, Altitude reviewen + fixen     |
| `/fewer-permission-prompts` | Häufige read-only Bash-Calls in Allowlist aufnehmen → weniger Prompts       |
| `/loop [interval] /cmd`     | Wiederkehrenden Task auf Intervall starten (z.B. `/loop 5m /babysit-prs`)   |
| `/claude-api`               | Referenz für Claude API / Anthropic SDK (Modelle, Preise, Streaming, Tools) |
| `/run`                      | App starten, im Browser testen, Änderung live verifizieren                  |
| `/init`                     | Neue CLAUDE.md mit Codebase-Dokumentation initialisieren                    |
| `/review`                   | GitHub Pull Request reviewen                                                |
| `/security-review`          | Security Review der aktuellen Branch-Änderungen                             |

## Nützliche Slash-Commands (built-in)

|    Command     |                            Bedeutung                             |
|----------------|------------------------------------------------------------------|
| `/help`        | Hilfe anzeigen                                                   |
| `/clear`       | Konversation zurücksetzen                                        |
| `/config`      | Einstellungen (Modell, Theme, …) interaktiv ändern               |
| `/fast`        | Fast Mode umschalten (Opus mit schnellerem Output)               |
| `/sandbox`     | Sandbox-Modus verwalten (Dateisystem- & Netzwerk-Beschränkungen) |
| `/memory`      | Persistente Erinnerungen anzeigen / verwalten                    |
| `/workflows`   | Laufende Workflow-Agenten beobachten                             |
| `/code-review` | Working Diff reviewen (nicht GitHub PR)                          |

## Agent-Typen (für komplexe Tasks)

|        Agent        |                             Wofür                             |
|---------------------|---------------------------------------------------------------|
| `claude`            | Catch-all, voller Tool-Zugriff                                |
| `claude-code-guide` | Fragen zu Claude Code CLI, SDK, API, Hooks, MCP               |
| `Explore`           | Schnelle read-only Code-Suche (Dateien, Symbole, Referenzen)  |
| `general-purpose`   | Komplexe Recherche, Multi-Step Tasks                          |
| `Plan`              | Implementierungsplan entwerfen, Architektur-Tradeoffs abwägen |
| `statusline-setup`  | Claude Code Statusleiste konfigurieren                        |

## Prompt-Tricks

|                Trick                |                                 Effekt                                 |
|-------------------------------------|------------------------------------------------------------------------|
| `! <command>`                       | Shell-Befehl direkt im Prompt ausführen (Output landet im Chat)        |
| `+500k`                             | Token-Budget für Workflow setzen                                       |
| `ultracode`                         | Multi-Agent Workflow session-weit aktivieren (internes Opt-in-Keyword) |
| `"use a workflow"`                  | Workflow-Tool für diesen Task aktivieren (internes Opt-in-Keyword)     |
| `"run a workflow"`                  | Wie oben — Claude spawnt dann einen deterministischen JS-Orchestrator  |
| `"fan out agents"`                  | Wie oben — explizite Formulierung für parallele Agent-Ausführung       |
| `"orchestrate this with subagents"` | Wie oben                                                               |

### Wie funktioniert das Workflow-Tool technisch?

Das Workflow-Tool ist ein deterministischer JavaScript-Orchestrator. Was passiert wenn es aufgerufen wird:

1. **Claude schreibt ein Script** — plain JavaScript mit `agent()`, `parallel()`, `pipeline()` Aufrufen
2. **Das Script wird im Hintergrund gestartet** — Claude bekommt sofort eine `runId` zurück
3. **Subagents werden gespawnt** — bis zu 16 gleichzeitig, bis 1000 insgesamt pro Workflow
4. **Ergebnis kommt als Notification** — als User-Message in einem späteren Turn

> **Hinweis:** `"use a workflow"` und `ultracode` sind **keine öffentlichen Slash-Commands**,
> sondern interne Verhaltensregeln aus Claudes Tool-Instruktionen. Claude darf das `Workflow`-Tool
> nur aufrufen, wenn eine dieser Phrasen im Prompt steht (Kostenschutz: Workflows können
> Dutzende Agents spawnen). Ohne explizites Opt-in wird kein Workflow gestartet.

## Speicherorte

|                  Pfad                  |                  Inhalt                   |
|----------------------------------------|-------------------------------------------|
| `~/.claude/CLAUDE.md`                  | Globale Instruktionen für alle Projekte   |
| `~/.claude/settings.json`              | Globale Einstellungen, Hooks, Permissions |
| `~/.claude/projects/<project>/memory/` | Persistente Erinnerungen pro Projekt      |
| `~/.claude/skills/`                    | Eigene Skills                             |
| `<project>/CLAUDE.md`                  | Projektspezifische Instruktionen          |
| `<project>/.claude/settings.json`      | Projektspezifische Einstellungen          |

## Memory-Typen

|     Typ     |                             Wann                             |
|-------------|--------------------------------------------------------------|
| `user`      | Rolle, Ziele, Wissensstand, Präferenzen des Nutzers          |
| `feedback`  | Korrekturen & bestätigte Ansätze (mit Why + How to apply)    |
| `project`   | Laufende Ziele, Entscheidungen, Deadlines                    |
| `reference` | Zeiger auf externe Systeme (Linear, Grafana, Slack-Channels) |

