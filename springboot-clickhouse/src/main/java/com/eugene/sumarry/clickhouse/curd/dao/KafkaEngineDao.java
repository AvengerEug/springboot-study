package com.eugene.sumarry.clickhouse.curd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eugene.sumarry.clickhouse.curd.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KafkaEngineDao extends BaseMapper<User> {

    List<String> showTables();

    /**
     * 创建kafka引擎表
     * @param kafkaTable
     * @param topic kafak引擎监听的topic
     */
    void createKafkaEngineTable(@Param("kafkaTable") String kafkaTable, @Param("topic") String topic, @Param("kafkaHost") String kafkaHost);


    /**
     * 创建普通用户终端表，最终物化视图会将kafka引擎表的数据写入到此表中
     * @param table
     */
    void createTable(@Param("table") String table);

    /**
     * 创建视图，将kafkaTable表的数据写入到table中
     * @param table
     * @param kafkaTable
     */
    void createView(@Param("table") String table, @Param("kafkaTable") String kafkaTable);
}
