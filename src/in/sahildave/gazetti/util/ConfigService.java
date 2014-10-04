package in.sahildave.gazetti.util;

import com.parse.ParseConfig;

/**
 * Created by sahil on 3/10/14.
 */
public class ConfigService {

    public ConfigService() {
        if (isConfigAvailable()) {
            ParseConfig config = ParseConfig.getCurrentConfig();
            setConfigVersion(config.getNumber("version"));

            setTheHinduElementsFromConfig(config);
            setTOIElementsFromConfig(config);
            setIndianExpressElementsFromConfig(config);
            setFirstPostElementsFromConfig(config);
        } else {
            setTheHinduElementsFromConstants();
            setTOIElementsFromConstants();
            setIndianExpressElementsFromConstants();
            setFirstPostElementsFromConstants();
        }
    }

    private boolean isConfigAvailable() {
        return (ParseConfig.getCurrentConfig() != null);
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

    public static void setTheHinduBody(String theHinduBody) {
        ConfigService.theHinduBody = theHinduBody;
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

     /*
        THE TIMES OF INDIA
     */

    private void setTOIElementsFromConstants() {
        setTOIBody(Constants.toi_body);
        setTOIHead(Constants.toi_head);
        setTOIImageFirst(Constants.toi_image_1);
        setTOIImageSecond(Constants.toi_image_2);
    }

    private void setTOIElementsFromConfig(ParseConfig config) {
        setTOIBody(config.getString("toi_body"));
        setTOIHead(config.getString("toi_head"));
        setTOIImageFirst(config.getString("toi_image_1"));
        setTOIImageSecond(config.getString("toi_image_2"));
    }

    private static String toiBody;
    private static String toiHead;
    private static String toiImageFirst;
    private static String toiImageSecond;

    public static String getTOIBody() {
        return toiBody;
    }

    public static void setTOIBody(String toiBody) {
        ConfigService.toiBody = toiBody;
    }

    public static String getTOIHead() {
        return toiHead;
    }

    public static void setTOIHead(String toiHead) {
        ConfigService.toiHead = toiHead;
    }

    public static String getTOIImageFirst() {
        return toiImageFirst;
    }

    public static void setTOIImageFirst(String toiImageFirst) {
        ConfigService.toiImageFirst = toiImageFirst;
    }

    public static String getTOIImageSecond() {
        return toiImageSecond;
    }

    public static void setTOIImageSecond(String toiImageSecond) {
        ConfigService.toiImageSecond = toiImageSecond;
    }
    
     /*
        THE INDIAN EXPRESS
     */

    private void setIndianExpressElementsFromConstants() {
        setIndianExpressBody(Constants.tie_body);
        setIndianExpressHead(Constants.tie_head);
        setIndianExpressImageFirst(Constants.tie_image_1);
        setIndianExpressImageSecond(Constants.tie_image_2);
        setIndianExpressSkipBodyElement(Constants.tie_skip);
    }

    private void setIndianExpressElementsFromConfig(ParseConfig config) {
        setIndianExpressBody(config.getString("tie_body"));
        setIndianExpressHead(config.getString("tie_head"));
        setIndianExpressImageFirst(config.getString("tie_image_1"));
        setIndianExpressImageSecond(config.getString("tie_image_2"));
        setIndianExpressSkipBodyElement(config.getString("tie_skip"));
    }

    private static String tieBody;
    private static String tieHead;
    private static String tieImageFirst;
    private static String tieImageSecond;
    private static String tieSkipBodyElement;

    public static String getIndianExpressBody() {
        return tieBody;
    }

    public static void setIndianExpressBody(String tieBody) {
        ConfigService.tieBody = tieBody;
    }

    public static String getIndianExpressHead() {
        return tieHead;
    }

    public static void setIndianExpressHead(String tieHead) {
        ConfigService.tieHead = tieHead;
    }

    public static String getIndianExpressImageFirst() {
        return tieImageFirst;
    }

    public static void setIndianExpressImageFirst(String tieImageFirst) {
        ConfigService.tieImageFirst = tieImageFirst;
    }

    public static String getIndianExpressImageSecond() {
        return tieImageSecond;
    }

    public static void setIndianExpressImageSecond(String tieImageSecond) {
        ConfigService.tieImageSecond = tieImageSecond;
    }

    public static String getIndianExpressSkipBodyElement() {
        return tieSkipBodyElement;
    }

    public static void setIndianExpressSkipBodyElement(String tieSkipBodyElement) {
        ConfigService.tieSkipBodyElement = tieSkipBodyElement;
    }

    /*
        FIRSTPOST
    */

    private void setFirstPostElementsFromConstants() {
        setFirstPostBody(Constants.fp_body);
        setFirstPostHead(Constants.fp_head);
        setFirstPostImage(Constants.fp_image);
    }

    private void setFirstPostElementsFromConfig(ParseConfig config) {
        setFirstPostBody(config.getString("fp_body"));
        setFirstPostHead(config.getString("fp_head"));
        setFirstPostImage(config.getString("fp_image"));
    }

    private static String fpBody;
    private static String fpHead;
    private static String fpImage;

    public static String getFirstPostBody() {
        return fpBody;
    }

    public static void setFirstPostBody(String fpBody) {
        ConfigService.fpBody = fpBody;
    }

    public static String getFirstPostHead() {
        return fpHead;
    }

    public static void setFirstPostHead(String fpHead) {
        ConfigService.fpHead = fpHead;
    }

    public static String getFirstPostImage() {
        return fpImage;
    }

    public static void setFirstPostImage(String fpImage) {
        ConfigService.fpImage = fpImage;
    }
}