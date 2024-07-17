package de.lunarakai.blockfox.common;

import de.lunarakai.blockfox.BlockFoxPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class DisplayList {
    private static final String DISPLAY_LIST_FILENAME = "blockfox_displays.yml";
    private final BlockFoxPlugin plugin;
    private File displayListFile;

    private final HashMap<String, BlockFoxDisplay> bfDisplays;
    private final HashMap<UUID, BlockFoxDisplay> playerUsingBFDisplay;
    private final HashMap<Location, BlockFoxDisplay> bfdisplayBlocks;
    private final HashMap<UUID, BlockFoxDisplay> bfdisplayItemDisplays;
    private final HashMap<BlockFoxDisplayMode, BlockFoxDisplay> bfDisplayMode;

    public DisplayList(BlockFoxPlugin plugin) {
        this.plugin = plugin;
        this.bfDisplays = new HashMap<>();
        this.bfdisplayBlocks = new HashMap<>();
        this.playerUsingBFDisplay = new HashMap<>();
        this.bfdisplayItemDisplays = new HashMap<>();
        this.bfDisplayMode = new HashMap<>();
        this.displayListFile = new File(plugin.getDataFolder(), DISPLAY_LIST_FILENAME);
    }

    public void load() {
        bfDisplays.clear();
        if(!this.displayListFile.exists()) {
            return;
        }

        YamlConfiguration conf = new YamlConfiguration();
        try {
            conf.load(this.displayListFile);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "could not load display list file ", e);
        }
        ConfigurationSection displaysSection = conf.getConfigurationSection("displays");
        if(displaysSection != null) {
            for(String displayName : displaysSection.getKeys(false)) {
                ConfigurationSection displaySection = displaysSection.getConfigurationSection(displayName);
                if(displaySection != null) {
                    BlockFoxDisplay blockFoxDisplay = new BlockFoxDisplay(plugin, displaySection);
                    this.bfDisplays.put(blockFoxDisplay.getName(), blockFoxDisplay);
                    setDisplayBlocks(blockFoxDisplay);
                }
            }
        }
    }

    public void save() {
        YamlConfiguration conf = new YamlConfiguration();
        ConfigurationSection displaysSection = conf.createSection("displays");
        int i = 0;
        for(BlockFoxDisplay display : bfDisplays.values()) {
            display.save(displaysSection.createSection(Integer.toString(i++)));
        }
        this.displayListFile.getParentFile().mkdirs();
        try {
            conf.save(this.displayListFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "could not save display list file ", e);
        }
    }

    private void setDisplayBlocks(BlockFoxDisplay blockFoxDisplay) {
//        for(Location loc : blockFoxDisplay.getBlocks()) {
//            bfdisplayBlocks.put(loc.clone(), blockFoxDisplay);
//        }
        for(UUID uuid : blockFoxDisplay.getTextDisplays()) {
            if(uuid != null) {
                bfdisplayItemDisplays.put(uuid, blockFoxDisplay);
            }
        }
    }

    public BlockFoxDisplay getBlockFoxDisplay(String blockFoxDisplayName) {
        return bfDisplays.get(blockFoxDisplayName);
    }

    public Collection<BlockFoxDisplay> getBlockFoxDisplays() {
        return bfDisplays.values();
    }

    public void addBlockFoxDisplay(BlockFoxDisplay blockFoxDisplay) {
        this.bfDisplays.put(blockFoxDisplay.getName(), blockFoxDisplay);
        setDisplayBlocks(blockFoxDisplay);
        save();
    }

    public boolean collidesWithBlockFoxDisplay(BlockFoxDisplay newBlockFoxDisplay) {
//        for(Location location : newBlockFoxDisplay.getBlocks()) {
//            if(bfdisplayBlocks.get(location) != null) {
//                return true;
//            }
//        }
        return false;
    }

    public void setBlockFoxDisplayForPlayer(Player player, BlockFoxDisplay blockFoxDisplay) {
        if(blockFoxDisplay != null) {
            playerUsingBFDisplay.put(player.getUniqueId(), blockFoxDisplay);
        } else {
            playerUsingBFDisplay.remove(player.getUniqueId());
        }
    }

    public BlockFoxDisplay getPlayerDisplay(Player player) {
        return playerUsingBFDisplay.get(player.getUniqueId());
    }

    public BlockFoxDisplay getBFDisplayAtBlock(Block block) {
        return bfdisplayBlocks.get(block.getLocation());
    }

    public BlockFoxDisplay getDisplayForItemDisplay(UUID id) {
        return bfdisplayItemDisplays.get(id);
    }

    public void removeBlockFoxDisplay(BlockFoxDisplay blockFoxDisplay) {
        if(blockFoxDisplay.hasPlayer()) {
            plugin.getManager().leaveDisplay(blockFoxDisplay.getCurrentPlayer(), true);
        }

        for(UUID uuid : blockFoxDisplay.getTextDisplays()) {
            if(uuid != null) {
                bfdisplayItemDisplays.remove(uuid);
            }
        }
//        for(Location block : blockFoxDisplay.getBlocks()) {
//            bfdisplayBlocks.remove(block);
//        }
        //blockFoxDisplay.removeItemDisplay();

        bfDisplays.remove(blockFoxDisplay.getName());
        save();
    }
}
