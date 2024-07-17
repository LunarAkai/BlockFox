package de.lunarakai.blockfox;

import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.lunarakai.blockfox.commands.InfoCommand;
import de.lunarakai.blockfox.commands.TestRSSCommand;
import de.lunarakai.blockfox.common.BlockFoxManager;
import de.lunarakai.blockfox.common.DisplayList;
import java.util.Locale;
import java.util.ResourceBundle;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockFoxPlugin extends JavaPlugin {
    public static final String PERMISSION_USE = "blockfox.use";
    public static final String PERMISSION_ADMIN = "blockfox.admin";

    private BlockFoxManager blockFoxManager;
    private DisplayList displayList;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        registerLocales();

        getServer().getScheduler().runTask(this, this::onLateEnable);
    }

    public void onLateEnable() {
        displayList = new DisplayList(this);
        displayList.load();

        blockFoxManager = new BlockFoxManager(this);

        // Commands
        CommandRouter commandRouter = new CommandRouter(getCommand("blockfox"));
        // Common
        commandRouter.addCommandMapping(new InfoCommand(this), "info");

        // RSS
        commandRouter.addCommandMapping(new TestRSSCommand(this), "testrss");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DisplayList getDisplayList() {
        return displayList;
    }

    public BlockFoxManager getManager() {
        return blockFoxManager;
    }

    private void registerLocales() {
        TranslationRegistry registry = TranslationRegistry.create(Key.key("blockfox:lang"));

        ResourceBundle bundle_en_US = ResourceBundle.getBundle("lang.en_US", Locale.US, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.US, bundle_en_US, true);

        ResourceBundle bundle_de_DE = ResourceBundle.getBundle("lang.de_DE", Locale.GERMAN, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.GERMAN, bundle_de_DE, true);

        GlobalTranslator.translator().addSource(registry);
    }

}
