package de.lunarakai.blockfox.common;

import com.google.common.base.Preconditions;
import de.lunarakai.blockfox.BlockFoxPlugin;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.jetbrains.annotations.NotNull;

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
    private Map<String, UUID> namedDisplay;
    private Player currentPlayer;

    // Width = 5
    // Height = 3

    public BlockFoxDisplay(BlockFoxPlugin plugin, ConfigurationSection displaySection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(displaySection.getString("name"));
        this.location = Preconditions.checkNotNull(displaySection.getLocation("location"));
        this.displays = new ArrayList<>(); // TODO
        this.namedDisplay = new HashMap<>();
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

//        List<String> list = displaySection.getStringList("textdisplays");
//        for(int i = 0; i < list.size(); i++) {
//            String textDisplay = list.get(i);
//            if(textDisplay != null) {
//                displays.set(i, UUID.fromString(textDisplay));
//            }
//        }

        @NotNull List<Map<?, ?>> map = displaySection.getMapList("nameddisplays");
        this.namedDisplay = (Map<String, UUID>) map.getFirst();

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
        this.namedDisplay = new HashMap<>();
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

        for (int fx = - 3; fx < 2; fx++) {
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
        //  - 2 Text Displays
        //  - 2 Buttons für Hoch und runter scrollen innerhalb eines Posts
        //  - 2 Buttons für Navigation zwischen verschiedenen Posts
        //  - 1 Button um den Link des Posts zu kopieren/öffnen
        //  - 1 Button um zwischen den Modus bzw RSS Feeds zu wechseln
        //  - 4 Buttons für (Fediverse) Neuen Post schreiben, Antworten, Fav und Retoot
        //  ==> Insgesamt 12 Display Entities
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

        loc.set(
                location.getX() + -0.5,
                location.getY() + 1.25,
                location.getZ() + -0.05
        );
        TextDisplay textDisplayTitle = world.spawn(loc, TextDisplay.class, textDisplay -> {
            textDisplay.text(
                    Component.text("Titel eines RSS Posts", NamedTextColor.BLUE)
                            .appendNewline()
                            .append(Component.text("LunarAkai", NamedTextColor.GOLD))
                            .append(Component.text(" | ", NamedTextColor.WHITE))
                            .append(Component.text("12:00", NamedTextColor.GOLD))
                            .append(Component.text(" | ", NamedTextColor.WHITE))
                            .append(Component.text("01.01.2024", NamedTextColor.GOLD))
                            .appendNewline()
                            .append(Component.text("@testuser@mastodon.text", NamedTextColor.GRAY)));

            textDisplay.setRotation(rotation, 0);
            textDisplay.setBrightness(new Display.Brightness(15, 15));
        });
        namedDisplay.put("titlebar", textDisplayTitle.getUniqueId());


        loc.set(
                location.getX() + -0.25,
                location.getY() + -0.55,
                location.getZ() + -0.05
        );
        TextDisplay textDisplayText = world.spawn(loc, TextDisplay.class, textDisplay -> {
            textDisplay.text(Component.text("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum."));
            textDisplay.setRotation(rotation, 0);
            textDisplay.setLineWidth(180);
            textDisplay.setBrightness(new Display.Brightness(15, 15));
        });
        namedDisplay.put("text", textDisplayText.getUniqueId());
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
//        displaySection.set("textdisplays", textDisplays);

        displaySection.set("nameddisplays", List.of(this.namedDisplay));

        List<String> savedLinks = new ArrayList<>();
        if(this.savedLinks != null) {
            for(URI url : this.savedLinks) {
                savedLinks.add(url == null ? null : url.toString());
            }
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
            case EAST -> 270;
            case SOUTH -> 0;
            case WEST -> 90;
            default -> 180;
        };
    }
}
