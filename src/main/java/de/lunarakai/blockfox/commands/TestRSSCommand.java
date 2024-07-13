package de.lunarakai.blockfox.commands;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import de.iani.cubesideutils.NamedChatColor;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.blockfox.BlockFoxPlugin;
import de.lunarakai.blockfox.utils.HTMLUtils;
import de.lunarakai.lunarutils.StringUtils;
import de.lunarakai.lunarutils.chat.MessageUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestRSSCommand extends SubCommand {

    private final BlockFoxPlugin plugin;

    public TestRSSCommand(BlockFoxPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "<url>";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        if(args.remaining() != 1) {
            sender.sendMessage(NamedChatColor.DARK_RED + commandString + getUsage());
            return true;
        }
        String url = args.getNext().trim();

        if(!StringUtils.isValidURL(url)) {
            MessageUtils.sendSimpleWarningMessage(player, "please enter a valid URL");
            return true;
        }

        RssReader rssReader = new RssReader();
        try {
            List<Item> items = rssReader.addItemExtension("content:encoded", Item::setComments).read(url).toList();

            Optional<String> title = items.getFirst().getTitle();
            title.ifPresent(string -> sender.sendMessage(NamedChatColor.BLUE + string));

            Optional<String> optionalAuthor = items.getFirst().getAuthor();
            optionalAuthor.ifPresent(string -> sender.sendMessage(NamedChatColor.AQUA + string));

            Optional<String> optionaltest = items.getFirst().getComments();
            if(optionaltest.isPresent()) {
                Document parsedTest = Jsoup.parse(optionaltest.get());
                List<String> list = HTMLUtils.formatHTMLList(parsedTest);
                for(String string: list) {
                    MessageUtils.sendSimpleSuccessMessage(player, string);
                }
            } else {
                Optional<String> optionalDescription = items.getFirst().getDescription();
                optionalDescription.ifPresent(string -> sender.sendMessage(NamedChatColor.GOLD + string));
            }

            Optional<String> optionalLink = items.getFirst().getGuid();
            optionalLink.ifPresent(string -> sender.sendMessage(NamedChatColor.AQUA + string));

            Optional<String> optionalDate = items.getFirst().getPubDate();
            optionalDate.ifPresent(string -> sender.sendMessage(NamedChatColor.GRAY + string));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
