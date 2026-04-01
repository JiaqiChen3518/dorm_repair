package com.qg.dorm.mapper;

import com.qg.dorm.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    /**
     * 插入用户
     * @param user
     * @return
     */
    @Insert("INSERT INTO user (account, password, role, name, phone, building, room, is_bound, status) " +
            "VALUES (#{account}, #{password}, #{role}, #{name}, #{phone}, #{building}, #{room}, #{isBound}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 根据账号查询用户
     * @param account
     * @return
     */
    @Select("SELECT * FROM user WHERE account = #{account}")
    User selectByAccount(String account);

    /**
     * 根据用户ID查询用户
     * @param id
     * @return
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    /**
     * 更新用户密码
     * @param id
     * @param password
     * @return
     */
    @Update("UPDATE user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 绑定用户宿舍
     * @param id
     * @param building
     * @param room
     * @return
     */
    @Update("UPDATE user SET building = #{building}, room = #{room}, is_bound = 1 WHERE id = #{id}")
    int bindDormitory(@Param("id") Long id, @Param("building") String building, @Param("room") String room);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Update("UPDATE user SET name = #{name}, phone = #{phone} WHERE id = #{id}")
    int updateInfo(User user);
}
