package com.korit.security_practice.repository;

import com.korit.security_practice.entity.UserRole;
import com.korit.security_practice.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
public class UserRoleRepository {
    @Autowired
    private UserRoleMapper userRoleMapper;

    public int addUserRole(UserRole userRole) {
        try {
            userRoleMapper.addUserRole(userRole);
        } catch (DuplicateKeyException e) {
            return 0;
        }
        return 1;
    }

    public int updateUserRole(UserRole userRole) {
        return userRoleMapper.updateUserRole(userRole);
    }
}
