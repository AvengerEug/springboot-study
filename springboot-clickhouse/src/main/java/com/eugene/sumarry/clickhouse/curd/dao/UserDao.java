package com.eugene.sumarry.clickhouse.curd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eugene.sumarry.clickhouse.curd.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {


    void createTable();

    List<User> listUser();

    List<String> showTables();

    void updateByIdCH(User user);
}
