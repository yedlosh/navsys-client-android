package entities;

/**
 * Created by yedlosh on 10/05/2017.
 */

public class AccessPoint {

    public AccessPoint(String mac, Integer rssi) {
        this.mac = mac;
        this.rssi = rssi;
    }

    public final String mac;
    public final Integer rssi;

    public String getMac() {
        return mac;
    }

    public Integer getRssi() {
        return rssi;
    }
}
