package de.lunarakai.blockfox;

import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.lunarakai.blockfox.commands.TestRSSCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockFoxPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        CommandRouter commandRouter = new CommandRouter(getCommand("blockfox"));
        commandRouter.addCommandMapping(new TestRSSCommand(this), "testrss");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
