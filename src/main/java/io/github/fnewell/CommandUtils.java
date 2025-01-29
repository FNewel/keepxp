package io.github.fnewell;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandUtils {

    /*
    * Register commands for the mod
    * */
    public static void RegisterCommands() {

        // Register "/keepxp help" command
        Text help_text = Text.literal("/keepxp help - Show this help\n").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY))
                .append(Text.literal("/keepxp help - Show this help\n").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY)))
                .append(Text.literal("/keepxp override [on | off] - Turn KeepXP for all players\n").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY)))
                .append(Text.literal("/keepxp status - Show current status of KeepXP\n").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY)))
                .append(Text.literal("----------------").setStyle(Style.EMPTY.withBold(true).withColor(Formatting.GRAY)));

        EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("keepxp")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("help")
                        .executes(context -> {
                            //? if >=1.20 {
                            context.getSource().sendFeedback(() -> help_text, false);
                            //?} else
                            /*context.getSource().sendFeedback(help_text, false);*/
                            return 1;
                        })
                )
        ));


        // Register "/keepxp override [on | off]" command
        Text override_text_on = Text.literal("KeepXP override is turned ").setStyle(Style.EMPTY.withColor(Formatting.GOLD))
                .append(Text.literal("on").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        Text override_text_off = Text.literal("KeepXP override is turned ").setStyle(Style.EMPTY.withColor(Formatting.GOLD))
                .append(Text.literal("off").setStyle(Style.EMPTY.withColor(Formatting.RED)));

        EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("keepxp")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("override")
                        .then(literal("on")
                                .executes(context -> {
                                    DataUtils serverState = DataUtils.getServerState(KeepXP.getServer());
                                    serverState.keepXPoverride = true;
                                    KeepXP.keepXPoverride = true;

                                    //? if >=1.20 {
                                    context.getSource().sendFeedback(() -> override_text_on, false);
                                    //?} else
                                    /*context.getSource().sendFeedback(override_text_on, false);*/
                                    return 1;
                                })
                        )
                        .then(literal("off")
                                .executes(context -> {
                                    DataUtils serverState = DataUtils.getServerState(KeepXP.getServer());
                                    serverState.keepXPoverride = false;
                                    KeepXP.keepXPoverride = false;

                                    //? if >=1.20 {
                                    context.getSource().sendFeedback(() -> override_text_off, false);
                                    //?} else
                                    /*context.getSource().sendFeedback(override_text_off, false);*/
                                    return 1;
                                })
                        )
                )
        ));


        // Register "/keepxp status" command
        Text status_text_override = Text.literal("KeepXP is overridden and turned on for all players.").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
        Text status_text_permissions = Text.literal("KeepXP is turned on based on permissions.").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
        Text status_text_default = Text.literal("KeepXP is turned on for all players.").setStyle(Style.EMPTY.withColor(Formatting.GOLD));

        EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("keepxp")
                .then(literal("status")
                        .executes(context -> {
                            if (KeepXP.keepXPoverride) {
                                //? if >=1.20 {
                                context.getSource().sendFeedback(() -> status_text_override, false);
                                //?} else
                                /*context.getSource().sendFeedback(status_text_override, false);*/
                            } else if (KeepXP.permissionsAPI) {
                                //? if >=1.20 {
                                context.getSource().sendFeedback(() -> status_text_permissions, false);
                                //?} else
                                /*context.getSource().sendFeedback(status_text_permissions, false);*/
                            } else {
                                //? if >=1.20 {
                                context.getSource().sendFeedback(() -> status_text_default, false);
                                //?} else
                                /*context.getSource().sendFeedback(status_text_default, false);*/
                            }
                            return 1;
                        })
                )
        ));
    }
}
