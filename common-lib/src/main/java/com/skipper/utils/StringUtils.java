package com.skipper.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
    public static String removeSpecialCharactersAndPlural(String string) {
        List<String> splitString = Arrays.asList(string.split(" "));
        splitString = splitString.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .map(subString-> (subString.endsWith("s") ? subString.substring(0, subString.length() - 1) : subString)) //Remove plural
                .map(subString -> subString.replaceAll("[^a-zA-Z0-9_]", "")) //Only allow numbers, letters and underscore
                .collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        for (String str : splitString) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    public static String removeHtmlTags(String str) {
        // Regular expression to match HTML tags and everything between them
        String htmlTagsRegex = "<[^>]*>";

        // Regular expression to match consecutive newline characters (\n\n)
        String newlinesRegex = "\\n\\n";

        // Regular expression to match all newline characters (\n)
        String allNewlinesRegex = "\\n";

        // Remove HTML tags, consecutive newlines, and all newlines
        return str.replaceAll(htmlTagsRegex, "")
                .replaceAll(newlinesRegex, "")
                .replaceAll(allNewlinesRegex, "");
    }
}
