package de.lunarakai.blockfox.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.blockfox.BlockFoxPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InfoCommand extends SubCommand {
    BlockFoxPlugin plugin;

    public InfoCommand(BlockFoxPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public String getRequiredPermission() {
        return BlockFoxPlugin.PERMISSION_USE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String s1, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        sender.sendMessage(Component.text("--- ", NamedTextColor.AQUA)
                .append(Component.text(plugin.getName(), NamedTextColor.GREEN))
                .append(Component.text(" ---", NamedTextColor.AQUA)));

        sender.sendMessage(Component.translatable("blockfox.info.version", NamedTextColor.AQUA)
                .append(Component.text(": ", NamedTextColor.AQUA))
                .append(Component.text(plugin.getPluginMeta().getVersion(), NamedTextColor.GREEN)));

        sender.sendMessage(Component.translatable("blockfox.info.developer", NamedTextColor.AQUA)
                .append(Component.text(": ", NamedTextColor.AQUA))
                .append(Component.text(plugin.getPluginMeta().getAuthors().getFirst(), NamedTextColor.GREEN)));

        sender.sendMessage(Component.translatable("blockfox.info.website", NamedTextColor.AQUA)
                .append(Component.text(": ", NamedTextColor.AQUA))
                .append(Component.text(plugin.getPluginMeta().getWebsite(), NamedTextColor.GREEN)));

        sender.sendMessage(Component.translatable("blockfox.info.license", NamedTextColor.AQUA)
                .append(Component.text(": ", NamedTextColor.AQUA))
                .append(Component.text("GPL-3.0", NamedTextColor.GREEN)));

        return true;
    }
}

