package com.eugene.sumarry.clickhouse;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eugene.sumarry.clickhouse.curd.dao.UserDao;
import com.eugene.sumarry.clickhouse.curd.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

    @Autowired
    private UserDao userDao;


    /**
     * 初始化user表
     */
    @Before
    public void init() {
        List<String> strings = userDao.showTables();
        System.out.println(strings);
        if (strings.contains("user")) {
            return;
        }

        userDao.createTable();
    }

    @Test
    public void insert() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUserName("avengerEug");
        user.setAliasName("avengerEug-alias");
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());

        userDao.insert(user);
    }

    @Test
    public void query() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", "avengerEug");

        List<User> users = userDao.selectList(queryWrapper);
        System.out.println(JSON.toJSONString(users));
    }

    /**
     * clickhouse提供了delete和update的操作，但它与我们常用的数据库的update、delete语法不一致
     * 它是alter语句的变种。属于Mutation查询，这类查询是一个很重的操作，更适用于批量数据的修改和删除，
     * 其次，它不支持事务，一旦语句被提交执行，就会立即对数据产生影响，无法回滚。
     * 最后，Mutation语句的执行是一个异步的后台过程，语句被提交之后就会立即返回。所以这并不代表具体逻辑
     * 已经执行完毕。
     *
     * 因此，如果一定要执行update或delete操作的，得自己在xml文件中写对应的sql语句，不能直接使用
     * mybatis-plus提供的update、delete操作。
     *
     * 分区键和主键不能被更新
     *
     * 总而言之就是：最好是使用clickhouse做数据保存和查询使用，DDL操作，太耗性能
     *
     */
    @Test
    public void update() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", "avengerEug");

        User user = userDao.selectOne(queryWrapper);
        if (user != null) {
            System.out.println("db存在的信息：" + JSON.toJSONString(user));
            user.setAliasName("eugene");
            user.setUpdateTime(System.currentTimeMillis());
            userDao.updateByIdCH(user);
        }

        user = userDao.selectOne(queryWrapper);
        System.out.println("更新后的信息：有可能不是最新的数据，因为在clickhouse中mutation操作是一个异步过程" + JSON.toJSONString(user));
    }

}
