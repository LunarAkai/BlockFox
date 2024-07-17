package de.lunarakai.blockfox.common;

import com.google.common.base.Preconditions;
import de.lunarakai.blockfox.BlockFoxPlugin;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

public class BlockFoxDisplay {
    private final BlockFoxPlugin plugin;
    private final String name;
    private final Location location;
    private final Location centerLocation;
    private final BlockFace orientation;
    private DisplayStatus displayStatus = DisplayStatus.INACTIVE;
    private BlockFoxDisplayMode blockFoxDisplayMode = BlockFoxDisplayMode.RSS_FEED;
    private ArrayList<URI> savedLinks;
    private Boolean isInFixedMode;
    private List<UUID> displays;
    private Player currentPlayer;

    // Width = 5
    // Height = 3

    public BlockFoxDisplay(BlockFoxPlugin plugin, ConfigurationSection displaySection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(displaySection.getString("name"));
        this.location = Preconditions.checkNotNull(displaySection.getLocation("location"));
        this.displays = new ArrayList<>(); // TODO
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
                displays.set(i, UUID.fromString(textDisplay));
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

    public BlockFoxDisplay(BlockFoxPlugin plugin, String name, Location location, BlockFace orientation, boolean isInFixedMode) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(name, "name is null");
        this.location = Preconditions.checkNotNull(location, "location is null");
        this.displays = new ArrayList<>(); // TODO
        this.isInFixedMode = isInFixedMode;

        Preconditions.checkArgument(Math.abs(orientation.getModX()) + Math.abs(orientation.getModZ()) == 1, "no cardinal direction");
        this.orientation = orientation;
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;
        this.centerLocation = location.clone().add(0.5, 0, 0.5);
    }

    public void generateBackgroundBlocks() {
        World world = location.getWorld();
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;
        Location loc = location.clone();

        BlockData block0 = Material.SMOOTH_QUARTZ.createBlockData();

        for (int fx = -1 - 5 - 3; fx < 2; fx++) {
            for (int fy = -1; fy < 3 - 1; fy++) {
                loc.set(location.getX() + d1x * fx, location.getY() + fy, location.getZ() + d1z * fx);
                world.setBlockData(loc, block0);
            }
        }
    }

    /*
        Mitte = Block(1,1)
     */
    public void generateDisplays() {
        // Todo:
        //  2 Text Displays
        //  5 Item Displays + weitere als Buttons für Fediverse Navigation
        //  s. https://imgur.com/9XJ4XWw
        int sizeWidth = 5;
        int sizeHeight = 3;

        World world = location.getWorld();
        for(UUID uuid : displays) {
            if(uuid != null) {
                Entity display = world.getEntity(uuid);
                if(display instanceof Display) {
                    display.remove();
                }
            }
        }
        Collections.fill(displays, null);

        float rotation0 = 0;

        rotation0 = getRotationYaw();
        float rotation = rotation0;

        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;

        Location loc = location.clone();

        TextDisplay textDisplayTitle = world.spawn(loc, TextDisplay.class, textDisplay -> {

        });



    }

    public void save(ConfigurationSection displaySection) {
        displaySection.set("name", name);
        displaySection.set("location", location);
        displaySection.set("orientation", orientation.name());
        displaySection.set("mode", blockFoxDisplayMode.getModeName());
        List<String> textDisplays = new ArrayList<>();
        for(UUID uuid : this.displays) {
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

    public List<UUID> getDisplays() {
        return displays;
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
