package de.lunarakai.blockfox.utils;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HTMLUtils {
    public static List<String> formatHTMLList(Document document) {
        // Todo: needs a prettier solution but it works for one sub list point at least ^^
        List<String> list = new ArrayList<>();
        Elements elements = document.select("ul");
        Elements li = elements.select("li");

        for (Element element : li) {
            if(element.html().contains("ul")) {
                list.add("- " + element.html().split("<ul>")[0].trim() + "\n");
                continue;
            }
            list.add("- " + element.text() + "\n");
        }
        return list;
    }

}
