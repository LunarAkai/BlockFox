package de.lunarakai.cuberss;

import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.lunarakai.cuberss.commands.TestRSSCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class CubeRSS extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandRouter commandRouter = new CommandRouter(getCommand("cuberss"));
        commandRouter.addCommandMapping(new TestRSSCommand(this), "test");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
