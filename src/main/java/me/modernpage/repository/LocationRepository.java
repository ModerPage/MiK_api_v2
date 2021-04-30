package me.modernpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.modernpage.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
	Location findLocationByLongitudeAndLatitude(double longitude, double latitude);
}
