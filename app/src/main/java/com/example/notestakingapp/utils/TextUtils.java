package com.example.notestakingapp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
//    https://stackoverflow.com/questions/5713558/detect-and-extract-url-from-a-string
    public static List<String> linkDetectFromText(String text) {
        List<String> urls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            urls.add(text.substring(urlMatcher.start(0), urlMatcher.end(0)));
        }
        return urls;
    }
}
