package in.sahildave.gazetti.util;

import android.content.Context;

import com.parse.ParseConfig;

import in.sahildave.gazetti.R;

/**
 * Created by sahil on 3/10/14.
 */
public class ConfigService {

    private static ConfigService _instance;
    private Context mContext;

    private ConfigService() {
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
    public static synchronized ConfigService getInstance(){
        if (_instance == null) {
            _instance = new ConfigService();
        }
        return _instance;
    }

    public void destroyConfigService(){
        _instance = null;
    }
    
    private boolean isConfigAvailable() {
        return (ParseConfig.getCurrentConfig() != null);
    }

    private Number configVersion;

    public Number getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(Number configVersion) {
        this.configVersion = configVersion;
    }

    /*
        THE HINDU
     */

    private void setTheHinduElementsFromConstants() {
        setTheHinduBody(Constants.getConstant(mContext, R.string.th_body));
        setTheHinduHead(Constants.getConstant(mContext, R.string.th_head));
        setTheHinduImageFirst(Constants.getConstant(mContext, R.string.th_image_1));
        setTheHinduImageSecond(Constants.getConstant(mContext, R.string.th_image_2));
    }

    private void setTheHinduElementsFromConfig(ParseConfig config) {
        setTheHinduBody(config.getString("th_body"));
        setTheHinduHead(config.getString("th_head"));
        setTheHinduImageFirst(config.getString("th_image_1"));
        setTheHinduImageSecond(config.getString("th_image_2"));
    }

    private String theHinduBody;
    private String theHinduHead;
    private String theHinduImageFirst;
    private String theHinduImageSecond;

    public String getTheHinduBody() {
        return theHinduBody;
    }

    public void setTheHinduBody(String theHinduBody) {
        this.theHinduBody = theHinduBody;
    }

    public String getTheHinduHead() {
        return theHinduHead;
    }

    public void setTheHinduHead(String theHinduHead) {
        this.theHinduHead = theHinduHead;
    }

    public String getTheHinduImageFirst() {
        return theHinduImageFirst;
    }

    public void setTheHinduImageFirst(String theHinduImageFirst) {
        this.theHinduImageFirst = theHinduImageFirst;
    }

    public String getTheHinduImageSecond() {
        return theHinduImageSecond;
    }

    public void setTheHinduImageSecond(String theHinduImageSecond) {
        this.theHinduImageSecond = theHinduImageSecond;
    }

     /*
        THE TIMES OF INDIA
     */

    private void setTOIElementsFromConstants() {
        setTOIBody(Constants.getConstant(mContext, R.string.toi_body));
        setTOIHead(Constants.getConstant(mContext, R.string.toi_head));
        setTOIImageFirst(Constants.getConstant(mContext, R.string.toi_image_1));
        setTOIImageSecond(Constants.getConstant(mContext, R.string.toi_image_2));
    }

    private void setTOIElementsFromConfig(ParseConfig config) {
        setTOIBody(config.getString("toi_body"));
        setTOIHead(config.getString("toi_head"));
        setTOIImageFirst(config.getString("toi_image_1"));
        setTOIImageSecond(config.getString("toi_image_2"));
    }

    private String toiBody;
    private String toiHead;
    private String toiImageFirst;
    private String toiImageSecond;

    public String getTOIBody() {
        return toiBody;
    }

    public void setTOIBody(String toiBody) {
        this.toiBody = toiBody;
    }

    public String getTOIHead() {
        return toiHead;
    }

    public void setTOIHead(String toiHead) {
        this.toiHead = toiHead;
    }

    public String getTOIImageFirst() {
        return toiImageFirst;
    }

    public void setTOIImageFirst(String toiImageFirst) {
        this.toiImageFirst = toiImageFirst;
    }

    public String getTOIImageSecond() {
        return toiImageSecond;
    }

    public void setTOIImageSecond(String toiImageSecond) {
        this.toiImageSecond = toiImageSecond;
    }
    
     /*
        THE INDIAN EXPRESS
     */

    private void setIndianExpressElementsFromConstants() {
        setIndianExpressBody(Constants.getConstant(mContext, R.string.tie_body));
        setIndianExpressHead(Constants.getConstant(mContext, R.string.tie_head));
        setIndianExpressImage(Constants.getConstant(mContext, R.string.tie_image));

        setIndianExpressBusinessBody(Constants.getConstant(mContext, R.string.tie_business_body));
        setIndianExpressBusinessHead(Constants.getConstant(mContext, R.string.tie_business_head));
        setIndianExpressBusinessImage(Constants.getConstant(mContext, R.string.tie_business_image));
    }

    private void setIndianExpressElementsFromConfig(ParseConfig config) {
        setIndianExpressBody(config.getString("tie_body"));
        setIndianExpressHead(config.getString("tie_head"));
        setIndianExpressImage(config.getString("tie_image"));

        setIndianExpressBusinessBody(config.getString("tie_business_body"));
        setIndianExpressBusinessHead(config.getString("tie_business_head"));
        setIndianExpressBusinessImage(config.getString("tie_business_image"));
    }

    private String tieBody;
    private String tieHead;
    private String tieImage;
    private String tieBusinessBody;
    private String tieBusinessHead;
    private String tieBusinessImage;

    public String getIndianExpressBody() {
        return tieBody;
    }

    public void setIndianExpressBody(String tieBody) {
        this.tieBody = tieBody;
    }

    public String getIndianExpressHead() {
        return tieHead;
    }

    public void setIndianExpressHead(String tieHead) {
        this.tieHead = tieHead;
    }

    public String getIndianExpressImage() {
        return tieImage;
    }

    public void setIndianExpressImage(String tieImage) {
        this.tieImage = tieImage;
    }

    public String getIndianExpressBusinessImage() {
        return tieBusinessImage;
    }

    public void setIndianExpressBusinessImage(String tieBusinessImage) {
        this.tieBusinessImage = tieBusinessImage;
    }

    public String getIndianExpressBusinessBody() {
        return tieBusinessBody;
    }

    public void setIndianExpressBusinessBody(String tieBusinessBody) {
        this.tieBusinessBody = tieBusinessBody;
    }

    public String getIndianExpressBusinessHead() {
        return tieBusinessHead;
    }

    public void setIndianExpressBusinessHead(String tieBusinessHead) {
        this.tieBusinessHead = tieBusinessHead;
    }

    /*
        FIRSTPOST
    */

    private void setFirstPostElementsFromConstants() {
        setFirstPostBody(Constants.getConstant(mContext, R.string.fp_body));
        setFirstPostHead(Constants.getConstant(mContext, R.string.fp_head));
        setFirstPostImage(Constants.getConstant(mContext, R.string.fp_image));
    }

    private void setFirstPostElementsFromConfig(ParseConfig config) {
        setFirstPostBody(config.getString("fp_body"));
        setFirstPostHead(config.getString("fp_head"));
        setFirstPostImage(config.getString("fp_image"));
    }

    private String fpBody;
    private String fpHead;
    private String fpImage;

    public String getFirstPostBody() {
        return fpBody;
    }

    public void setFirstPostBody(String fpBody) {
        this.fpBody = fpBody;
    }

    public String getFirstPostHead() {
        return fpHead;
    }

    public void setFirstPostHead(String fpHead) {
        this.fpHead = fpHead;
    }

    public String getFirstPostImage() {
        return fpImage;
    }

    public void setFirstPostImage(String fpImage) {
        this.fpImage = fpImage;
    }

    public void setInstance(Context context) {
        this.mContext = context;
    }
}
