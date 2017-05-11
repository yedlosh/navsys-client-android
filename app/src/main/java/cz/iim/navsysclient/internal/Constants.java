package cz.iim.navsysclient.internal;

public class Constants {

    private Constants() {
    }

    public static final int TRACKING_INTERVAL = 5 * 1000;
    public static final String NAVSYS_API_ADDR = "http://10.100.101.6:3001";

    public static final String NAVSYS_API_TRACK = "/track";
    public static final String NAVSYS_API_REGISTER = "/register";
    public static final String NAVSYS_API_CANCEL = "/cancel";
    public static final String NAVSYS_API_DESTINATIONS = "/destinations";

    public static final String NAVSYS_DESTINATION_ID_KEY = "id";
    public static final String NAVSYS_DESTINATION_ID_NAME = "name";
}
