package com.springboot.platform.repository;

import com.springboot.platform.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long>, PlatformRepositoryCustom {
}
