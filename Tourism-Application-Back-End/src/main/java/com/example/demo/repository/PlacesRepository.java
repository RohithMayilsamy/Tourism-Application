package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Places;
public interface PlacesRepository extends JpaRepository<Places,Long> {

}
