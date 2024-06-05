create table test_db_info
(
    id                int auto_increment primary key not null comment '主键Id',
    url               varchar(255)                   not null comment '数据库URL',
    username          varchar(255)                   not null comment '用户名',
    password          varchar(255)                   not null comment '密码',
    driver_class_name varchar(255)                   not null comment '数据库驱动'
        name varchar (255) not null comment '数据库名称'
)