package de.lunarakai.blockfox.utils;

import de.lunarakai.blockfox.BlockFoxPlugin;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    public static String convertStringTime(BlockFoxPlugin plugin, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ");
        try {
            Date date = sdf.parse(time);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            outputFormat.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("common.timezone")));

            return outputFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
