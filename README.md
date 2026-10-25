
# Keep XP

Mod is available on [Modrinth](https://modrinth.com/mod/keep-xp-on-death-new).

A lightweight ***server-side*** Fabric mod that lets players keep their experience levels after death, with an optional ***penalty*** that takes a percentage of levels instead of everything. Works on dedicated servers (vanilla clients can join, no client install needed) and in singleplayer. Configure it with simple commands, or per player and per group through any permission mod such as LuckPerms.

No more worrying about losing levels and hours spent farming!

## Key Features

- **Keep XP on Death:** Players keep their levels after death instead of losing them.
- **Adjustable Penalty (0–100%):** Optionally remove a percentage of levels on death.
  - `0%`: penalty is off, levels and XP bar stay untouched.
  - `>0%`: that percentage of levels is removed (rounded down) and the XP bar is reset to 0.
  - Removed levels drop as XP orbs using the vanilla formula (7 points per level, max 100).
- **Server-Side Only:** Install it only on the server. Vanilla clients can join without the mod.
- **Singleplayer & LAN:** Install it on the client, it runs on the game's built-in server.
- **Two Modes (detected automatically):**
  - **Default Mode:** KeepXP and the penalty are set globally for all players with commands.
  - **Permissions Mode:** KeepXP and the penalty are set per player or group with permissions (see the ***Permissions*** section).

## Commands

| Command | Description | Who can use it |
|---|---|---|
| `/keepxp` or `/keepxp status` | Show the current KeepXP status | Everyone |
| `/keepxp help` | Show available commands | Op level 2 or `keepxp.command` |
| `/keepxp turn [on \| off]` | Turn KeepXP on/off for all players *(Default Mode only)* | Op level 2 or `keepxp.command` |
| `/keepxp penalty [0-100]` | Show or set the % of levels removed on death *(Default Mode only)* | Op level 2 or `keepxp.command` |

> In Permissions Mode, `turn` and `penalty` don't change anything and show a warning telling you which permission to use instead.

## Permissions

> If a permission mod is installed (such as LuckPerms), KeepXP detects it automatically and switches to Permissions Mode.
> Players can check their own KeepXP setting in game with `/keepxp` or `/keepxp status`.

| Permission | Description |
|---|---|
| `keepxp.keep` | Player keeps XP on death. Players without it lose XP the vanilla way. |
| `keepxp.penalty.<0-100>` | Penalty in % for the player or group, e.g. `keepxp.penalty.30`. Only applies together with `keepxp.keep`. If none is set, there is no penalty. If a player has several, the lowest one wins. |
| `keepxp.command` | Access to admin commands (`help`, `turn`, `penalty`). Without a permission mod, op level 2 is required. |

## Usage

- **Default Mode:** After installing the mod, XP is kept for all players with no penalty. Use `/keepxp turn` and `/keepxp penalty` to change this.
- **Permissions Mode:** Give `keepxp.keep` to players or groups, and optionally `keepxp.penalty.<0-100>`.

## Frequently Asked Questions (FAQ)

- **Q: Does the mod work in singleplayer?**
  - **A:** Yes. Install it on your client. It works in singleplayer and LAN worlds.
- **Q: Do players need to install the mod to join my server?**
  - **A:** No. It is server-side only, so vanilla clients can join.
- **Q: How exactly does the penalty work?**
  - **A:** With a 30% penalty, a player at level 20 loses 6 levels (rounded down). They respawn at level 14 with an empty XP bar, and 42 XP points (7 × 6) drop as orbs where they died. With 0%, nothing is lost or dropped.
- **Q: What happens at low levels?**
  - **A:** Removed levels are rounded down, so 30% of level 3 removes 0 levels. Only the XP bar is reset.
- **Q: What if the `keepInventory` gamerule is on?**
  - **A:** Vanilla behaviour takes priority: players keep everything and no penalty is applied.
- **Q: Which permission mods are supported?**
  - **A:** Any mod that uses the Fabric Permission API or the fabric-permissions-api (LuckPerms supports both). Mods that only bundle the API library without providing permissions are ignored.
- **Q: I installed a permission mod and nobody keeps XP anymore. Why?**
  - **A:** In Permissions Mode, XP is kept only for players with `keepxp.keep`. Give it to your default group to restore the old behaviour.
- **Q: Can players with permissions use `/keepxp turn` or `/keepxp penalty`?**
  - **A:** These commands have no effect while a permission mod is installed. Everything is set through permissions instead.

## Support

If you'd like to support the continued development of Keep XP, consider contributing on my **[☕ Ko-fi page](https://ko-fi.com/fnewell)**.
Any amount is greatly appreciated and helps me dedicate more time to creating awesome mods.
⭐ _Supporters also receive a special Supporter role on my Discord server!_

Have questions, encountered a bug, or just want to chat about the mod?
Join our community on [Discord](https://discord.gg/EBtW6HG4AH)!

For bug reports and technical issues, you can also open an issue on [GitHub](https://github.com/FNewel/keepxp/issues).