package cz.iim.navsysclient.api;

import cz.iim.navsysclient.entities.Location;

public class TrackResponse {

    private final Location location;
    private final boolean finished;

    public TrackResponse(Location location, boolean finished) {
        this.location = location;
        this.finished = finished;
    }

    public Location getLocation() {
        return location;
    }

    public boolean getFinished() {
        return finished;
    }
}