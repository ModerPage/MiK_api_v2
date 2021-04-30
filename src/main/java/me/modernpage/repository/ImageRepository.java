package me.modernpage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import me.modernpage.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long>{
}
