package de.lunarakai.blockfox.common;

import com.google.common.base.Preconditions;
import de.lunarakai.blockfox.BlockFoxPlugin;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
    private ArrayList<URI> savedLinks;
    private UUID[] textDisplays;
    private Player currentPlayer;

    // Width = 5
    // Height = 3

    public BlockFoxDisplay(BlockFoxPlugin plugin, ConfigurationSection displaySection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(displaySection.getString("name"));
        this.location = Preconditions.checkNotNull(displaySection.getLocation("location"));
        this.textDisplays = new UUID[99]; // TODO
        this.savedLinks = new ArrayList<>();

        BlockFace orientation = BlockFace.NORTH;
        try {
            orientation = BlockFace.valueOf(displaySection.getString("orientation"));
        } catch (IllegalArgumentException ignored) {

        }
        this.orientation = orientation;
        this.centerLocation = location.clone().add(0.5, 0, 0.5);

        String modeValue = displaySection.getString("mode");

        switch(modeValue) {
            case "fixed_rss": this.blockFoxDisplayMode = BlockFoxDisplayMode.FIXED_RSS_FEED; break;
            case "fediverse": this.blockFoxDisplayMode = BlockFoxDisplayMode.FEDIVERSE_CLIENT; break;
            case "fixed_fediverse": this.blockFoxDisplayMode = BlockFoxDisplayMode.FIXED_FEDIVERSE_CLIENT; break;
            default: this.blockFoxDisplayMode = BlockFoxDisplayMode.RSS_FEED;
        }

        List<String> list = displaySection.getStringList("textdisplays");
        for(int i = 0; i < list.size(); i++) {
            String textDisplay = list.get(i);
            if(textDisplay != null) {
                textDisplays[i] = UUID.fromString(textDisplay);
            }
        }

        List<String> savedLinksList = displaySection.getStringList("savedlinks");
        for(int i = 0; i < savedLinksList.size(); i++) {
            String link = savedLinksList.get(i);
            if(link != null) {
                try {
                    URI uri = new URI(savedLinksList.get(i));
                    savedLinks.add(uri);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void save(ConfigurationSection displaySection) {
        displaySection.set("name", name);
        displaySection.set("location", location);
        displaySection.set("orientation", orientation.name());
        displaySection.set("mode", blockFoxDisplayMode.getModeName());
        List<String> textDisplays = new ArrayList<>();
        for(UUID uuid : this.textDisplays) {
            textDisplays.add(uuid == null ? null : uuid.toString());
        }
        displaySection.set("textdisplays", textDisplays);
        List<String> savedLinks = new ArrayList<>();
        for(URI url : this.savedLinks) {
            savedLinks.add(url == null ? null : url.toString());
        }
        displaySection.set("savedlinks", savedLinks);
    }

    public void addJoiningPlayer(Player player) {
        Preconditions.checkNotNull(player);
        Preconditions.checkState(displayStatus == DisplayStatus.INACTIVE);
        this.displayStatus = DisplayStatus.ACTIVE;
        this.currentPlayer = player;
    }

    public String getName() {
        return name;
    }

    public boolean hasPlayer() {
        return currentPlayer != null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Location getLocation() {
        return location;
    }

    public BlockFace getOrientation() {
        return orientation;
    }

    public DisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public UUID[] getTextDisplays() {
        return textDisplays;
    }

    public BlockFoxDisplayMode getBlockFoxDisplayMode() {
        return blockFoxDisplayMode;
    }

    public void setDisplayStatus(DisplayStatus status) {
        this.displayStatus = status;
    }

    public void setBlockFoxDisplayMode(BlockFoxDisplayMode mode) {
        this.blockFoxDisplayMode = mode;
    }

    public ArrayList<URI> getSavedLinks() {
        return savedLinks;
    }

    public void addLink(URI link) {
        savedLinks.add(link);
    }

    private int getRotationYaw() {
        return switch (orientation) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
