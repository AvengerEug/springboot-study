# 相关总结

## 使用jdbc链接clickhouse时，报Code: 81, e.displayText() = DB::Exception: Database tmp_my_database doesn't exist (version 20.11.4.13 (official build))的错误
* 解决方案：需要先创建tmp_my_database数据库，再使用jdbc链接。

* 在clickhouse命令行中执行如下命令创建数据库语句：
    ```shell script
      CREATE DATABASE IF NOT EXISTS tmp_my_database;
    ```
    
* 同时，可以使用如下语句删除数据库：

    ```shell
    DROP DATABASE IF EXISTS tmp_my_database;
    ```

    

## 建表语句解析
* 建表语句：
    ```shell script
        CREATE TABLE tmp_my_database.user
        (
            `id` String,
            `userName` String,
            `aliasName` String,
            `createTime` DateTime
        )
        ENGINE = MergeTree()
        PARTITION BY toYYYYMMDD(createTime)
        ORDER BY (userName)
        PRIMARY KEY (id)
    ```
    
    将在tmp_my_database数据库中创建user表，其中包含id、userName、aliasName、createTime四个字段，其中使用到的引擎为**合并树（最常用的一个）**，按照createTime进行分区，分区内的数据排序规则默认使用userName进行排序（**ps：后续如果可能有多个排序规则的话，可以都填写到order by里面，这样可以提高排序的查询效率。假设修改成 ORDER BY (userName, aliasName)，那么最终在每个分区中先按照userName排序，如果userName相同，再使用aliasName排序**）。指定了id为主键，最终会根据id字段生成一级索引，用于加速表查询。
    
## 关于使用jdbc操作clickhouse的insert操作的建议
* 在clickhouse中，不建议每次直接插入一条数据。在每次插入数据时，clickhouse内部为了让查询变得更快，内部都会做一些额外处理（比如：MergeTree引擎的表需要合并数据），而这些额外处理是非常耗时的，clickhouse的insert操作的qps只能达到千级。因此，不建议直接insert数据

## 外部存储引擎 - kafka引擎

* 外部存储引擎：直接从其他的存储系统读取数据，比如从HDFS\MySQL\Kafka等等。这种表引擎只负责元数据管理和数据查询，而它们自身通常并不负责数据的写入，数据文件直接由外部系统提供。

* kafka外部引擎：

  ```txt
  默认情况下，kafka表引擎每间隔500毫秒会拉取一次数据，时间由stream_poll_timeout_ms参数控制，默认是500毫秒。
  数据首先会被放入缓存，在时机成熟的时候，缓存数据会被刷新到数据表。
  触发kafka表引擎刷新缓存的条件有两个，当满足其中的任意一个时，便会触发刷新动作：
  1、当一个数据块完成写入的时候（一个数据块的大小有kafka_max_block_size参数控制），默认情况下kafka_max_block_size=max_block_size=65536
  2、等待间隔超过7500毫秒，由stream_flush_interval_ms参数控制，默认为7500ms。
  ```

* 使用kafka外部引擎

  ```txt
  可以定义这么一张表：
  CREATE TABLE kafka_test(
    id UInt32,
    code String,
    name String
  ) ENGINE = Kafka()
  SETTINGS
    kafka_broker_list = 'localhost:9092',
    kafka_topic_lise = 'sales-queue',
    kafka_groupt_name = 'chgroup',
    kafka_format = 'JSONEachRow'
    
  这表示，只要kafka的sales-queue的topic有数据接入，那么就会clickhouse就会把这个数据保存到kafka_test表中。
  只要我们直接查询kafka_test表数据就能看到kafka推送的数据了。但是，当你再次查询数据时，你会发现kafka_test表的数据
  不存在了。这是因为kafka表引擎在执行查询之后就会删除表内的数据。因此，我们可以使用如下正确的方式来使用kafka引擎。
  ```

* 正确打开方式：

  > 1. 首先，我们需要创建kafka数据表A，它充当的角色是一条数据管道，负责拉取kafka中的数据
  >
  >    ```shell
  >    ## 创建一张Kafka引擎的表，充当数据管道
  >    CREATE TABLE kafka_test(
  >      id UInt32,
  >      code String,
  >      name String
  >    ) ENGINE = Kafka()
  >    SETTINGS
  >      kafka_broker_list = 'localhost:9092',
  >      kafka_topic_lise = 'sales-queue',
  >      kafka_groupt_name = 'chgroup',
  >      kafka_format = 'JSONEachRow'
  >    ```
  >
  > 2. 其次，我们要创建一张任意引擎（通常是MergeTree）的数据表B，它从当的角色是面向终端用户的查询库
  >
  >    ```shell
  >    ## 创建一张面向用户的终端表，这里使用MergeTree表引擎：
  >    CREATE TABLE kafka_table(
  >      id UInt32,
  >      code String,
  >      name String
  >    ) ENGINE = MergeTree()
  >    ORDER BY id
  >    ```
  >
  > 3. 最后，我们要使用一张物化视图C，它负责将表A的数据实时同步到表B\
  >
  >    ```shell
  >    # 创建一张物化视图，用于将数据从kafka_queue同步到kafka_table:
  >    CREATE MATERIALIZED VIEW consumer TO kafka_table AS SELECT id, code, name FROM kafka_queue
  >    ```

  完成上述3个操作后，最终我们可以将kafka的数据保存到kafka_table表中，也不会存在查询一次数据就丢失的情况了。

## MergeTree引擎原理

* 创建方式语法：

  ```shell
  CREATE TABLE [IS NOT EXISTS] [db_name].table_name(
      name1 [type] [DEFAULT|MATERIALIZED|ALIAS express],
      name2 [type] [DEFAULT|MATERIALIZED|ALIAS express],
      ...
  ) ENGINE = MergeTree()
  [PARTITION BY express]
  [ORDER BY express]
  [PRIMARY KEY express]
  [SAMPLE BY express]
  [SETTINGS name=value, .....]
  ```

* 参数解析：

  > 1. PARTITION BY [选填]：分区键，用于指定表数据以何种标准进行分区。分区的字段可以为多个列字段。如果没有填写，clickhouse将会生成一个名为all的分区。合理的使用数据分区，可以减少查询时数据文件的扫描范围。
  >
  > 2. ORDER BY [必填]：排序键，用于指定在一个数据片段内，数据以何种标准排序。默认情况下主键(PRIMARY KEY)与排序键相同。排序键也可以为多个列字段。在多个列字段的情况下， 优先使用第一个列字段排序，如果第一个列字段相同再使用第二个列字段，以此类推。
  >
  > 3. PAIMARY KEY [选填]：主键，声明后会依照主键字段生成一级索引，用于加速表查询。默认情况下，主键与排序键相同，所以通常直接使用ORDER BY 代为指定主键，无需刻意通过PRIMARY KEY 声明。所以在一般情况下，在单个数据片段内，数据与一级索引以相同的规则升序排序。与其他数据库不同，**MergeTee主键允许存在重复数据**
  >
  > 4. SAMPLE BY [选填]：抽样表达式，用于生命数据以何种标准进行采样。如果使用了此配置项，那么在主键的配置中也需要声明同样的表达式，例如：
  >
  >    ```shell
  >    .....
  >    ) ENGINE = MergeTree()
  >    ORDER BY (ConterId, EventDate, intHash32(UserID))
  >    SAMPLE BY intHash32(UserID)
  >    ```
  >
  >    抽样表达式需要配置SAMPLE子查询使用，这项功能对于选取抽样数据十分有用。
  >
  > 5. SETTINGS index_granularity [选填]：index_granularity对于mergeTree而言是一项非常重要的参数，它表示索引的粒度，默认值为8192。也就是说，MergeTree的索引在默认情况下，每间隔8192行数据才生产一条索引，其具体声明方式如下所示：
  >
  >    ```shell
  >    ...
  >    ) Engine = MergeTree()
  >    SETTINGS index_granularity = 8192;
  >    ```
  >
  >    通常情况下不会修改此参数