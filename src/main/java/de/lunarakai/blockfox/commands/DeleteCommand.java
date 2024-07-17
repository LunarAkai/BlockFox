package de.lunarakai.blockfox.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.blockfox.BlockFoxPlugin;
import de.lunarakai.blockfox.common.BlockFoxDisplay;
import de.lunarakai.lunarutils.chat.MessageUtils;
import java.util.Collection;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand extends SubCommand {
    private final BlockFoxPlugin plugin;

    public DeleteCommand(BlockFoxPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "[name]";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public String getRequiredPermission() {
        return BlockFoxPlugin.PERMISSION_ADMIN;
    }

    // doesnt work??
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        BlockFoxDisplay display = plugin.getDisplayList().getBFDisplayAtBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN));
        if(display == null) {
            Block target = player.getTargetBlockExact(6);
            if(target != null) {
                display = plugin.getDisplayList().getBFDisplayAtBlock(target);
            }
        }
        if(display != null) {
            plugin.getDisplayList().removeBlockFoxDisplay(display);
            MessageUtils.sendSimpleWarningMessage(player, "display.delete.success");
        }
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
