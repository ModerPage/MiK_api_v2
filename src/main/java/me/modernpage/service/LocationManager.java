package me.modernpage.service;

import me.modernpage.entity.Location;

public interface LocationManager {
	Location saveLocation(Location location);
	Location findLocationByLongitudeAndLatitude(double longitude, double latitude);
}
