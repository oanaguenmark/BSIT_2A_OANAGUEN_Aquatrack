package com.aquatrack.model;

/**
 * A reusable 5-gallon container owned by the station.
 * Status cycle: Ready -> Out for Delivery -> With Customer
 *               -> Needs Refill -> Sanitizing -> Ready
 */
public class Gallon {

    private String gallonId;
    private String type;            // Round or Slim
    private String currentLocation;
    private String status;

    public Gallon(
            String gallonId,
            String type,
            String currentLocation,
            String status) {

        this.gallonId = gallonId;
        this.type = type;
        this.currentLocation = currentLocation;
        this.status = status;
    }

    public String getGallonId() {
        return gallonId;
    }

    public String getType() {
        return type;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void displayInfo() {

        System.out.println("Gallon ID : " + gallonId);
        System.out.println("Type      : " + type + " (5 gal)");
        System.out.println("Location  : " + currentLocation);
        System.out.println("Status    : " + status);
    }
}
