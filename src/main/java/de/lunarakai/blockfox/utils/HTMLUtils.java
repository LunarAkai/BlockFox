package de.lunarakai.blockfox.utils;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLUtils {
    public static List<String> formatHTMLList(Document document) {
        // todo: format html elements to readable string (for example new line at ul li)
        // todo: with current method ul li inside another ul li are duplicated
        List<String> list = new ArrayList<>();
        Elements elements = document.select("ul");

        for (Element element : elements) {

            list.add("- " + element.html() + "\n");




        }
        return list;
    }

}
