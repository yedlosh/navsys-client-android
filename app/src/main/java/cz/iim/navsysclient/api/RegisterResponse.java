package cz.iim.navsysclient.api;

import cz.iim.navsysclient.entities.Location;

public class RegisterResponse {

    private final Location location;
    private final String assignedColor;

    public RegisterResponse(Location location, String assignedColor) {
        this.location = location;
        this.assignedColor = assignedColor;
    }

    public Location getLocation() {
        return location;
    }

    public String getAssignedColor() {
        return assignedColor;
    }
}
