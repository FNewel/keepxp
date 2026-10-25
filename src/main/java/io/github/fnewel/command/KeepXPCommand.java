package io.github.fnewel.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.fnewel.data.KeepXPSavedData;
import io.github.fnewel.permission.KeepXPPermissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * /keepxp help
 * /keepxp status
 * /keepxp turn [on|off]       - only without a permission mod
 * /keepxp penalty [0-100]     - only without a permission mod
 */
public final class KeepXPCommand {
    private static final String KEEP_NODE = KeepXPPermissions.nodeName(KeepXPPermissions.KEEP);
    private static final String PENALTY_NODE = KeepXPPermissions.nodeName(KeepXPPermissions.PENALTY, "<0-100>");
    private static final String COMMAND_NODE = KeepXPPermissions.nodeName(KeepXPPermissions.COMMAND);

    private KeepXPCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Evaluated on every check, because permission mods are detected only at server start
        Predicate<CommandSourceStack> admin = KeepXPPermissions::canUseAdminCommands;

        dispatcher.register(literal("keepxp")
                .executes(KeepXPCommand::status)
                .then(literal("help")
                        .requires(admin)
                        .executes(KeepXPCommand::help))
                .then(literal("status")
                        .executes(KeepXPCommand::status))
                .then(literal("turn")
                        .requires(admin)
                        .then(literal("on").executes(ctx -> turn(ctx, true)))
                        .then(literal("off").executes(ctx -> turn(ctx, false))))
                .then(literal("penalty")
                        .requires(admin)
                        .executes(KeepXPCommand::showPenalty)
                        .then(argument("percent", IntegerArgumentType.integer(
                                KeepXPSavedData.MIN_PENALTY, KeepXPSavedData.MAX_PENALTY))
                                .executes(ctx -> setPenalty(ctx, IntegerArgumentType.getInteger(ctx, "percent")))))
        );
    }

    // ---------- help ----------

    private static int help(CommandContext<CommandSourceStack> ctx) {
        MutableComponent text = Component.literal("---- KeepXP ----\n").withStyle(ChatFormatting.GOLD)
                .append(line("/keepxp help", "Show this help"))
                .append(line("/keepxp status", "Show current status of KeepXP"))
                .append(line("/keepxp turn [on | off]", "Turn KeepXP on/off for all players"))
                .append(line("/keepxp penalty [0-100]", "Show/set % of levels removed on death"));

        if (KeepXPPermissions.isActive()) {
            text.append(Component.literal("Permission mod detected - turn/penalty are disabled, use:\n")
                            .withStyle(ChatFormatting.YELLOW))
                    .append(line(KEEP_NODE, "Player keeps XP on death"))
                    .append(line(PENALTY_NODE, "Penalty in %, lowest granted wins"))
                    .append(line(COMMAND_NODE, "Access to admin commands"));
        }

        text.append(Component.literal("----------------").withStyle(ChatFormatting.GOLD));
        ctx.getSource().sendSuccess(() -> text, false);
        return 1;
    }

    private static Component line(String command, String description) {
        return Component.literal(command).withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" - " + description + "\n").withStyle(ChatFormatting.GRAY));
    }

    // ---------- status ----------

    private static int status(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();

        if (KeepXPPermissions.isActive()) {
            source.sendSuccess(() -> Component.literal("KeepXP is controlled by permissions (")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(KEEP_NODE).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(", ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(PENALTY_NODE).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(").").withStyle(ChatFormatting.GOLD)), false);

            ServerPlayer player = source.getPlayer();
            if (player != null) {
                boolean keeps = KeepXPPermissions.canKeepXp(player);
                int penalty = keeps ? KeepXPPermissions.getPenalty(player) : 0;
                source.sendSuccess(() -> Component.literal("You: ").withStyle(ChatFormatting.GOLD)
                        .append(onOff(keeps))
                        .append(keeps ? penaltyText(penalty) : Component.empty()), false);
            }
            return 1;
        }

        KeepXPSavedData data = KeepXPSavedData.get(source.getServer());
        source.sendSuccess(() -> Component.literal("KeepXP is turned ").withStyle(ChatFormatting.GOLD)
                        .append(onOff(data.isEnabled()))
                        .append(Component.literal(" for all players").withStyle(ChatFormatting.GOLD))
                        .append(data.isEnabled() ? penaltyText(data.getPenalty()) : Component.literal(".").withStyle(ChatFormatting.GOLD)),
                false);
        return 1;
    }

    // ---------- turn ----------

    private static int turn(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        CommandSourceStack source = ctx.getSource();
        if (KeepXPPermissions.isActive()) {
            source.sendFailure(permissionWarning(
                    "Grant or remove " + KEEP_NODE + " for players/groups in your permission mod instead."));
            return 0;
        }

        KeepXPSavedData.get(source.getServer()).setEnabled(enabled);
        source.sendSuccess(() -> Component.literal("KeepXP is turned ").withStyle(ChatFormatting.GOLD)
                .append(onOff(enabled))
                .append(Component.literal(" for all players.").withStyle(ChatFormatting.GOLD)), true);
        return 1;
    }

    // ---------- penalty ----------

    private static int showPenalty(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        if (KeepXPPermissions.isActive()) {
            source.sendFailure(permissionWarning(
                    "Penalty is set per player/group with " + PENALTY_NODE + "."));
            return 0;
        }

        KeepXPSavedData data = KeepXPSavedData.get(source.getServer());
        int penalty = KeepXPSavedData.get(source.getServer()).getPenalty();

        MutableComponent text = Component.literal("KeepXP penalty is ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(penalty + "%").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" of levels.").withStyle(ChatFormatting.GOLD));

        if (!data.isEnabled()) {
            text.append(Component.literal(" (No effect until KeepXP is turned on)").withStyle(ChatFormatting.GRAY));
        }

        source.sendSuccess(() -> text, false);
        return penalty;
    }

    private static int setPenalty(CommandContext<CommandSourceStack> ctx, int penalty) {
        CommandSourceStack source = ctx.getSource();
        if (KeepXPPermissions.isActive()) {
            source.sendFailure(permissionWarning(
                    "Set it per player/group with " + PENALTY_NODE + " in your permission mod instead."));
            return 0;
        }

        KeepXPSavedData data = KeepXPSavedData.get(source.getServer());
        KeepXPSavedData.get(source.getServer()).setPenalty(penalty);

        MutableComponent text = Component.literal("KeepXP penalty set to ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(penalty + "%").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" - players keep " + (100 - penalty) + "% of their levels on death.")
                        .withStyle(ChatFormatting.GOLD));

        if (!data.isEnabled()) {
            text.append(Component.literal(" (No effect until KeepXP is turned on)").withStyle(ChatFormatting.GRAY));
        }

        source.sendSuccess(() -> text, false);
        return 1;
    }

    // ---------- shared texts ----------

    private static Component permissionWarning(String hint) {
        return Component.literal("A permission mod is installed, so this command has no effect. ")
                .withStyle(ChatFormatting.RED)
                .append(Component.literal(hint).withStyle(ChatFormatting.YELLOW));
    }

    private static Component onOff(boolean on) {
        return on
                ? Component.literal("on").withStyle(ChatFormatting.GREEN)
                : Component.literal("off").withStyle(ChatFormatting.RED);
    }

    private static Component penaltyText(int penalty) {
        return Component.literal(", penalty ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(penalty + "%").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(".").withStyle(ChatFormatting.GOLD));
    }
}
