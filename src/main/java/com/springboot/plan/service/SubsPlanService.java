package com.springboot.plan.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.plan.entity.SubsPlan;
import com.springboot.plan.repository.SubsPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class SubsPlanService {
    private final SubsPlanRepository subsPlanRepository;

    public SubsPlanService(SubsPlanRepository subsPlanRepository) {
        this.subsPlanRepository = subsPlanRepository;
    }

    public SubsPlan VerifiedSubsPlan(long subsPlanId) {
        Optional<SubsPlan> optionalSubsPlan = subsPlanRepository.findById(subsPlanId);
        SubsPlan subsPlan = optionalSubsPlan.orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.NOT_FOUND));

        return subsPlan;
    }
}
