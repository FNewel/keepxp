# Keep your xp after death (with permission)

This project is based on [Keep XP on Death](https://modrinth.com/mod/keep-xp-on-death-fabric), updated to a new version (1.21) and added the option to preserve XP by permission.

- Mod works client-side (singleplayer) or server-side only.

Allows 2 methods of use:
- Mod can be used as is (**without permisions**), then it preserves XP for all players.
- With permissions, then XP is only preserved if the `keep.xp` permission is set (for example by using LuckPerms).

Automatically detects the Permissions API if permissions are used (no need to install it in case of LuckPerms, it already includes it).

Ideal for servers where certain players (roles) can be set to keep xp.