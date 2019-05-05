package com.hanista.mobogram.messenger;

public class BuildVars {
    public static String APP_HASH;
    public static int APP_ID;
    public static String BING_SEARCH_KEY;
    public static int BUILD_VERSION;
    public static String BUILD_VERSION_STRING;
    public static boolean DEBUG_VERSION;
    public static String FOURSQUARE_API_ID;
    public static String FOURSQUARE_API_KEY;
    public static String FOURSQUARE_API_VERSION;
    public static String GCM_SENDER_ID;
    public static String HOCKEY_APP_HASH;
    public static String HOCKEY_APP_HASH_DEBUG;
    public static String SEND_LOGS_EMAIL;

    static {
        DEBUG_VERSION = false;
        BUILD_VERSION = 851;
        BUILD_VERSION_STRING = "3.13.3";
        APP_ID = 38662;
        APP_HASH = "34bca7ca1abba565a002aece3b6c0439";
        HOCKEY_APP_HASH = "your-hockeyapp-api-key-here";
        HOCKEY_APP_HASH_DEBUG = "your-hockeyapp-api-key-here";
        GCM_SENDER_ID = "760348033672";
        SEND_LOGS_EMAIL = "info@hanista.com";
        BING_SEARCH_KEY = "dKYt6BjhkmFnJABZI/nWs++mx7owYEKZLcdA3DTOO1s";
        FOURSQUARE_API_KEY = "Y3YVQTXZSBBGOHW0IUNRZO1BYUNKU5LF3JZEPIKU0SH4N2RX";
        FOURSQUARE_API_ID = "PBE0FWMJB0L2AKPEZ4AANTE03TMS2YZWVTXM10BR5VH5OTDB";
        FOURSQUARE_API_VERSION = "20150326";
    }
}
