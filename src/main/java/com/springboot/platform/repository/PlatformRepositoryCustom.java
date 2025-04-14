package com.springboot.platform.repository;

import com.springboot.platform.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformRepositoryCustom {
    Page<Platform> findAllByCategory(Long categoryId, Pageable pageable);
}
