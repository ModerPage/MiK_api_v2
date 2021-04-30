package me.modernpage.service.impl;

import me.modernpage.service.LocationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.modernpage.entity.Location;
import me.modernpage.repository.LocationRepository;

@Service
@Transactional
public class LocationManagerImpl implements LocationManager {
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Override
	public Location findLocationByLongitudeAndLatitude(double longitude, double latitude) {
		// TODO Auto-generated method stub
		return locationRepository.findLocationByLongitudeAndLatitude(longitude, latitude);
	}

	@Override
	public Location saveLocation(Location location) {
		// TODO Auto-generated method stub
		return locationRepository.save(location);
	}
}
