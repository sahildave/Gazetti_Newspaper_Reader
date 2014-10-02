package com.example.try_masterdetail.util;

import com.parse.ParseConfig;

/**
 * Created by sahil on 3/10/14.
 */
public class ConfigService {

    public ConfigService() {
        if(isConfigAvailable()){
            ParseConfig config = ParseConfig.getCurrentConfig();
            setConfigVersion(config.getNumber("version"));

            setTheHinduElementsFromConfig(config);
        } else {
            setTheHinduElementsFromConstants();
        }
    }

    private boolean isConfigAvailable() {
        return (ParseConfig.getCurrentConfig()!=null);
    }

    private static Number configVersion;

    public static Number getConfigVersion() {
        return configVersion;
    }

    public static void setConfigVersion(Number configVersion) {
        ConfigService.configVersion = configVersion;
    }

    /*
        THE HINDU
     */

    private void setTheHinduElementsFromConstants() {
        setTheHinduBody(Constants.th_body);
        setTheHinduHead(Constants.th_head);
        setTheHinduImageFirst(Constants.th_image_1);
        setTheHinduImageSecond(Constants.th_image_2);
    }

    private void setTheHinduElementsFromConfig(ParseConfig config) {
        setTheHinduBody(config.getString("th_body"));
        setTheHinduHead(config.getString("th_head"));
        setTheHinduImageFirst(config.getString("th_image_1"));
        setTheHinduImageSecond(config.getString("th_image_2"));
    }

    private static String theHinduBody;
    private static String theHinduHead;
    private static String theHinduImageFirst;
    private static String theHinduImageSecond;

    public static String getTheHinduBody() {
        return theHinduBody;
    }

    public static void setTheHinduBody(String th_body) {
        ConfigService.theHinduBody = th_body;
    }

    public static String getTheHinduHead() {
        return theHinduHead;
    }

    public static void setTheHinduHead(String theHinduHead) {
        ConfigService.theHinduHead = theHinduHead;
    }

    public static String getTheHinduImageFirst() {
        return theHinduImageFirst;
    }

    public static void setTheHinduImageFirst(String theHinduImageFirst) {
        ConfigService.theHinduImageFirst = theHinduImageFirst;
    }

    public static String getTheHinduImageSecond() {
        return theHinduImageSecond;
    }

    public static void setTheHinduImageSecond(String theHinduImageSecond) {
        ConfigService.theHinduImageSecond = theHinduImageSecond;
    }
}
