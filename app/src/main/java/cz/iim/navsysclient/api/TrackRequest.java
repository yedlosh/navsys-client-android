package cz.iim.navsysclient.api;

import java.util.List;

import cz.iim.navsysclient.entities.AccessPoint;

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
