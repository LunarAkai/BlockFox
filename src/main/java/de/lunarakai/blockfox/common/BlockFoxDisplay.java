package de.lunarakai.blockfox.common;

import com.google.common.base.Preconditions;
import de.lunarakai.blockfox.BlockFoxPlugin;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class BlockFoxDisplay {
    private final BlockFoxPlugin plugin;
    private final String name;
    private final Location location;
    private final Location centerLocation;
    private final BlockFace orientation;
    private DisplayStatus displayStatus = DisplayStatus.INACTIVE;
    private BlockFoxDisplayMode blockFoxDisplayMode = BlockFoxDisplayMode.RSS_FEED;
    private UUID[] textDisplays;
    private Player currentPlayer;

    public BlockFoxDisplay(BlockFoxPlugin plugin, ConfigurationSection displaySection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(displaySection.getString("name"));
        this.location = Preconditions.checkNotNull(displaySection.getLocation("location"));
        this.textDisplays = new UUID[99]; // TODO

        BlockFace orientation = BlockFace.NORTH;
        try {
            orientation = BlockFace.valueOf(displaySection.getString("orientation"));
        } catch (IllegalArgumentException ignored) {

        }
        this.orientation = orientation;
        this.centerLocation = location.clone().add(0.5, 0, 0.5);

        String modeValue = displaySection.getString("mode");

        switch(modeValue) {
            case "fediverse": this.blockFoxDisplayMode = BlockFoxDisplayMode.FEDIVERSE_CLIENT; break;
            default: this.blockFoxDisplayMode = BlockFoxDisplayMode.RSS_FEED;
        }

        List<String> list = displaySection.getStringList("textdisplays");
        for(int i = 0; i < list.size(); i++) {
            String textDisplay = list.get(i);
            if(textDisplay != null) {
                textDisplays[i] = UUID.fromString(textDisplay);
            }
        }
    }


}
