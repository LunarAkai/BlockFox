package de.lunarakai.blockfox.common;

import com.google.common.base.Preconditions;
import de.lunarakai.blockfox.BlockFoxPlugin;
import de.lunarakai.lunarutils.chat.MessageUtils;
import org.bukkit.entity.Player;

public class BlockFoxManager {
    private final BlockFoxPlugin plugin;

    public BlockFoxManager(BlockFoxPlugin plugin) {
        this.plugin = plugin;
    }

    public void joinDisplay(Player player, BlockFoxDisplay display) {
        if(!player.hasPermission(BlockFoxPlugin.PERMISSION_USE)) {
            return;
        }
        Preconditions.checkArgument(plugin.getDisplayList().getPlayerDisplay(player) == null, "player is already using a display");
        Preconditions.checkArgument(display.getDisplayStatus() == DisplayStatus.INACTIVE, "display is already in use");
        display.addJoiningPlayer(player);
        plugin.getDisplayList().setBlockFoxDisplayForPlayer(player, display);
    }
    public void leaveDisplay(Player player, boolean message) {
        BlockFoxDisplay display = plugin.getDisplayList().getPlayerDisplay(player);
        Preconditions.checkArgument(display != null, "player is not using any display");
        plugin.getDisplayList().setBlockFoxDisplayForPlayer(player, null);
        if(message) {
            MessageUtils.sendSimpleInfoMessage(player, "blockfox.display.usagecanceled");
        }
    }
}
