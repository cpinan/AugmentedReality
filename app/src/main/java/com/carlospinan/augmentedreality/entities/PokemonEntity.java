package com.carlospinan.augmentedreality.entities;

import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;

import java.io.Serializable;

/**
 * @author Carlos Piñan
 */
public class PokemonEntity implements Serializable {

    private String name;
    private String type;
    private String path;
    private Location location;
    private Bitmap bitmap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(double latitude, double longitude) {
        this.location = new Location(LocationManager.NETWORK_PROVIDER);
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);
    }
}
