package com.portable.server.mapper;

import com.portable.server.model.user.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountMapper {

    User selectAccountById(Long id);

    User selectAccountByHandle(String handle);

    Integer insertAccount(User user);

    Integer updateHandle(@Param("id") Long id, @Param("handle") String handle);

    Integer updatePassword(@Param("id") Long id, @Param("password") String password);
}
