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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class CreateCommand extends SubCommand {
    private final BlockFoxPlugin plugin;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");

    public CreateCommand(BlockFoxPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "<name> [fixed] [noblocks]";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public String getRequiredPermission() {
        return BlockFoxPlugin.PERMISSION_ADMIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        if(args.remaining() < 1 || args.remaining() > 3) {
            MessageUtils.sendSimpleWarningMessage(player, commandString + getUsage());
            return true;
        }

        String name = args.getNext().toLowerCase().trim();
        if(!VALID_NAME_PATTERN.matcher(name).matches()) {
            MessageUtils.sendSimpleWarningMessage(player, "display.name.invalid");
            return true;
        }

        if(plugin.getDisplayList().getBlockFoxDisplay(name) != null) {
            MessageUtils.sendSimpleWarningMessage(player, "display.name.exists");
            return true;
        }

        boolean fixed_mode = false;
        boolean noblocks = false;
        while(args.hasNext()) {
            String arg = args.next().toLowerCase().trim();
            if(arg.equals("fixed")) {
                fixed_mode = true;
            } else if(arg.equals("noblocks")) {
                noblocks = true;
            } else {
                MessageUtils.sendSimpleWarningMessage(player, commandString + getUsage());
            }
        }

        BlockFace orientation = null;
        Location location = null;

        @Nullable
        RayTraceResult target = player.rayTraceBlocks(6);
        if(target == null || target.getHitBlock() == null) {
            MessageUtils.sendSimpleWarningMessage(player, "display.create.lookAtCenter");
            return true;
        }
        BlockFace face = target.getHitBlockFace();
        if(face != BlockFace.NORTH && face != BlockFace.WEST && face != BlockFace.EAST && face != BlockFace.SOUTH) {
            MessageUtils.sendSimpleWarningMessage(player, "display.create.lookAtSide");
            return true;
        }
        location = target.getHitBlock().getLocation();
        orientation = face;

        BlockFoxDisplay newDisplay = new BlockFoxDisplay(plugin, name, location, orientation, fixed_mode);
        if(plugin.getDisplayList().collidesWithBlockFoxDisplay(newDisplay)) {
            MessageUtils.sendSimpleWarningMessage(player, "display.create.collidesWithOther");
            return true;
        }
        newDisplay.generateDisplays();
        if(!noblocks) {
            newDisplay.generateBackgroundBlocks();
        }
        plugin.getDisplayList().addBlockFoxDisplay(newDisplay);
        MessageUtils.sendSimpleSuccessMessage(player, "display.create.success");
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        if(args.remaining() == 2 || args.remaining() == 3) {
            args.getNext();

            boolean fixed_mode = false;
            boolean noblocks = false;

            while(args.remaining() > 1) {
                String arg = args.getNext().toLowerCase().trim();
                if(arg.equals("fixed")) {
                    fixed_mode = true;
                } else if(arg.equals("noblocks")) {
                    noblocks = true;
                } else {
                    return List.of();
                }
            }
            ArrayList<String> result = new ArrayList<>();
            result.add("");
            if(!fixed_mode) {
                result.add("fixed");
            }
            if(!noblocks) {
                result.add("noblocks");
            }
            return result;
        }
        return List.of();
    }
}
