/**	本质就是执行计算，依赖于HDFS存储数据，把SQL转换成MR程序
 * Hive是基于hadoop的一个数据仓库工具,可以将结构化的数据文件映射成一张表，并提供类SQL查询功能;
 * 使用HQL作为查询接口；使用HDFS存储；使用MapReduce计算；
 * 本质是:将HQL转化成MapReduce程序。
 * 适合离线数据处理。
 * schema(模式，元信息存放到数据库中)
 * 数据库和表都是路径。
 * hive在写操作是不校验，读时校验。


 *return code 2 from org.apache.hadoop.hive.ql.exec.mr.MapRedTask (state=08S01,code=2);
 *namenode处于safemode状态，然后进入namenode所在节点通过使用
  hdfs dfsadmin -safemode leave命令退出安全模式，就可以解决问题。

  set yarn.resourcemanager.zk-address=zkMaster:2181,zkSecond:2181,zkSlave:2181;

Exit code is 143 Container exited with a non-zero exit code 143
hive (default)> SET mapreduce.map.memory.mb;
		mapreduce.map.memory.mb=512
hive (default)> SET mapreduce.reduce.memory.mb;
		mapreduce.reduce.memory.mb=512
hive (default)> SET yarn.nodemanager.vmem-pmem-ratio;
		yarn.nodemanager.vmem-pmem-ratio=4.2
因此，单个map和reduce分配物理内存512M；虚拟内存限制512*4.2=2.1G；
单个reduce处理数据量超过内存1G的限制导致；设置 mapreduce.reduce.memory.mb=1024 解决
这就是说当资源不够是，AppMaster会kill掉reduce释放资源给map。
解决办法是调整mapreduce.job.reduce.slowstart.completedmaps参数，默认为0.05，即map完成0.05后reduce就开始copy，如果集群资源不够，有可能导致reduce把资源全抢光，可以把这个参数调整到0.8，map完成80%后才开始reduce copy。
SET mapreduce.job.reduce.slowstart.completedmaps=0.8

schematool -dbType mysql -initSchema

 *开启metastore: hive --service metastore
 *开启hiveserver2: hive --service hiveserver2
 *客户端连接hive：beeline -u jdbc:hive2://Master:10000 -n root
 *set hive.execution.engine=spark;
 *
 * 检查hive server2是否启动：   netstat -anp |grep 10000
 * 后台启动hiveserver2
 * 		nohup hiveserver2 1>/dev/null 2>&1 &
 * 		nohup hive --service hiveserver2 >/dev/null 2>&1 &
 *
 * hive命令模型
 * hive>dfs -lsr /                         	//显示dfs下文件：路径/库/表/文件
 * hive>dfs -rmr /目录                 				//dfs命令，删除目录
 * hive>!clear ;                          	//hive中执行shell命令
 * 
 * set hive.cli.print.current.db=true;		#让提示符显示当前库
 * set hive.cli.print.header=true; 			#显示查询结果时显示字段名称
 * set hive.exec.mode.local.auto=true;		#可以让hive将mrjob提交给本地运行器运行
 * set hive.enforce.bucketing = true;		#指定开启分桶
 * set mapreduce.job.reduces=3;
 * 
 * 元数据都储存在mysql：use hive用户库; 
 * SELECT * FROM version v;       	#查看hive版本
 * SELECT * FROM tbls  ;     		#查看有哪些表,易区分各表。
 * select * from SDS ;   			#查看表对应的hdfs目录的metedata
 * SELECT * FROM partitions p WHERE p.TBL_ID=1;     #查看某个表的partitions：
 * SELECT * FROM columns_v2 cv;    		#查看某个表的列：
 * select * from PARTITION_KEYS;      	#查看某个表的partition
 * select * from DBS;     				#查看数据仓库信息
 * 调优：explain———解释执行计划
 * explain select sum(*) from test2 ;
 * 
 * 
 * hive数据类型
     tinyint
     smallint
     int
     bigint
     String                       //''|""
     varchar                   	//1-65535
     char                       //255
     timestamp             		//format "YYYY-MM-DD HH:MM:SS.fffffffff"
     Date                      //form{{YYYY-MM-DD}}
     decimal
     UNIONTYPE           		//联合类型
     NULL
 * 
 * show databases;
 * use dbName;
 * show tables;
 * 
 * create database if not exists dbname; #创建库
 * 
 * alter database dbname set dbproperties('edited-by'='gyb333'); #修改库(不能删除或“重置”数据库属性)
 * 
 * describe database extended dbname; 	#查询库
 * 
 * drop database [if exists] dbname; 	#删除库
 * 
 * desc database extended dbname; 		#显示库的扩展信息
 * 
 * CREATE  DATABASE IF NOT EXISTS  HiveDB
   COMMENT '注释'
   LOCATION 'HiveDB';	#默认在hdfs://ns/user/hive/warehouse/HiveDB.db
   #LOCATION 'hdfs://ns/hive3/';	#指定在hdfs文件路径
 * 
 * #DROP  DATABASE IF EXISTS  HiveDB  CASCADE;	#级联删除
 * 
 * show functions 				#显示hive中所有的内置函数
 * 
 * desc 表;                             #表的描述
 * 
 * desc formatted 表;               		#查询表的结构
 * 
 * desc extended 表;                		#显示表的扩展信息
 * 
 * select * from 表;                 	#查询表的信息
 * 
 * show partitions 表;                #查看表的分区
 * 
 * show tables like '*name*'; 			#模糊搜索表
 * 
 * show create table 表					#显示建表语句
 *
 * 插入模式里输出^A先按一下 ctrl-v 再按 ctrl-a代表'\001'
 * 
 * CREATE [EXTERNAL] TABLE if not exists tbeHive (id INT, name STRING)
	row format delimited 
 	fields terminated by '\001'  
	collection items terminated by '\002' 
	map keys terminated by '\003'
	lines terminated by '\n' 
	stored as textfile  	#HIVE的存储文件格式:SEQUENCE FILE | TEXT FILE | PARQUET FILE | RC FILE
	[location] '/hive/pokes';
 * 区别： 内部表的目录由hive创建在默认的仓库目录下：/user/hive/warehouse/....
 * 	   	 外部表EXTERNAL的目录由用户建表时自己指定： location '/位置/'
 * 
 * 添加列：ALTER TABLE tbeHive add COLUMNS (address string,age int);
 * 全部替换：ALTER TABLE tbeHive REPLACE COLUMNS (id int,name string,address string,age int);
 * 修改已存在的列定义：alter table tbeHive change id uid string;
 * 
 * #创建分区表:分区标识不能存在于表字段中
 * CREATE TABLE tbpHive (id INT, name STRING) PARTITIONED BY (province STRING,city STRING);
 * 
 * alter table tbpHive [add|drop] partition(province='hebei',city='baoding') #添加分区
 *
 *	插入单条数据： insert into table tbpHive values(10,'test','hunan','changsha');
 *
 * 导入插入数据(加载到HDFS)
 * load data local inpath 'path/filename' [overwrite] into table 表名;	#复制：从本地数据导入Hive表
 * load data inpath 'path/filename' into table 表名;				#剪切：HDFS上移动数据到Hive表
 * 
 *导入数据到不同的分区目录：
 * load data local inpath 'path/filename' into table 表名 partition(day='2017-04-08');
 * load data local inpath 'path/filename' into table 表名 partition(day='2017-04-09');
 *
 * create table 库名1.表名1 like 库名2.表名2;            #复制表(表结构)
 * 
 * insert overwrite table test2 partition(provice='hebei') 
  		select id , name , age from test1;      #增加数据
 * 
 * create table tbData as select id,name from tbeHive;					#复制表(表结构和数据)
 * 
 * 同一个源表多重插入
 *  from tbSource
	insert overwrite table tbTarget partition(day='lt2')
		select ip,url,staylong where staylong<200
	insert overwrite table tbTarget partition(day='gt2')
		select ip,url,staylong where staylong>200;
 * 
 * 导出数据到本地和HDFS文件系统
 * insert overwrite [local] directory "hodoop目录"  
  		select user, login_time from user_login;     #将查询数据输出hdfs目录
 * hive -e "hql语句" > /tmp/out.txt                 #保存sql语句查询信息到本地文件 
 *
 * DROP TABLE IF EXISTS tbeHive; 
 *  drop一个内部表时，表的元信息和表数据目录都会被删除；
 *  drop一个外部表时，只删除表的元信息，表的数据目录不会删除； 
 *

 *
 */