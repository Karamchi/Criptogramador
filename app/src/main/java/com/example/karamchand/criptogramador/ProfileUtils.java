package com.example.karamchand.criptogramador;

import android.app.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileUtils {

    private static int KEY = 0x27;

    public static HashMap<String, String> getProfile() {
        HashMap<String, String> res = new HashMap();
        File f = new File("/profile/profile.txt");
        f.mkdirs();
        for (String s : FileUtils.readFromFile(f.getParent(), f.getName())) {
            res.put(symmetric(s.split(" ")[0]), symmetric(s.split(" ")[1]));
        }
        return res;
    }

    public static void putInProfile(Activity context, String s, String i) {
        HashMap<String, String> h = getProfile();
        h.put(s, i);
        saveProfile(context, h);
    }

    private static void saveProfile(Activity context, HashMap<String, String> profile) {
        ArrayList<String> contents = new ArrayList<>();
        for (String k : profile.keySet()) {
            contents.add(symmetric(k) + " " + symmetric(profile.get(k)));
        }
        FileUtils.save(context, "/profile", "profile", contents);
    }

    private static String symmetric(String s) {
        String res = "";
        for (char c : s.toCharArray()) {
            int w = c;
            w = w ^ KEY;
            res += Character.toString((char) w);
        }
        return res;
    }


}
