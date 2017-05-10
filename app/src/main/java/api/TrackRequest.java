package api;

import java.util.List;

import entities.AccessPoint;

/**
 * Created by yedlosh on 10/05/2017.
 */

public class TrackRequest {

    public TrackRequest(String username, Long time, List<AccessPoint> wifiFingerprint) {
        this.username = username;
        this.time = time;
        this.wifiFingerprint = wifiFingerprint;
    }

    public final String username;
    public final Long time;
    public final List<AccessPoint> wifiFingerprint;

    @Override
    public String toString() {
        return "TrackRequest{" +
                "username='" + username + '\'' +
                ", time=" + time +
                ", wifiFingerprint=" + wifiFingerprint +
                '}';
    }
}
