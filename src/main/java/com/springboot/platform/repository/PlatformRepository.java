package com.springboot.platform.repository;

import com.springboot.platform.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long>, PlatformRepositoryCustom {
    Optional<Platform> findByPlatformName(String name);
}
