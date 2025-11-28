package com.korit.security_practice.repository;

import com.korit.security_practice.entity.Verify;
import com.korit.security_practice.mapper.VerifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VerifyRepository {
    @Autowired
    private VerifyMapper verifyMapper;

    public int addVerify(Verify verify) {
        try {
            verifyMapper.addVerify(verify);
        } catch (DuplicateKeyException e) {
            return 0;
        }
        return 1;
    }

    public int deleteVerifyByUserId(Integer userId) {
        return verifyMapper.deleteVerifyByUserId(userId);
    }

    public Optional<Verify> getVerifyByUserId(Integer userId) {
        return verifyMapper.getVerifyByUserId(userId);
    }
}
