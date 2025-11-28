package com.korit.security_practice.mapper;

import com.korit.security_practice.entity.Verify;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface VerifyMapper {
    int addVerify(Verify verify);
    int deleteVerifyByUserId(Integer userId);
    Optional<Verify> getVerifyByUserId(Integer userId);
}
