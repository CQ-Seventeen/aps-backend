CREATE SCHEMA IF NOT EXISTS APS;
USE APS;
DROP TABLE Machine IF EXISTS;
CREATE TABLE Machine (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    device_id VARCHAR(255) NOT NULL comment '机台号',
    code VARCHAR(255) NOT NULL comment '编号',
    institute_id BIGINT NOT NULL comment '机构id',
    factory_id BIGINT NOT NULL comment '工厂id',
    workshop_id BIGINT NOT NULL comment '车间id',
    machine_group_id BIGINT NOT NULL comment '机组id',
    cylinder_diameter INTEGER NOT NULL comment '筒径',
    needle_spacing INTEGER NOT NULL comment '针距',
    needle_number INTEGER NOT NULL comment '针数',
    machine_type VARCHAR(128) DEFAULT NULL comment '机器型号',
    bare_spandex_type VARCHAR(128) DEFAULT NULL comment '裸氨设备类型',
    high_speed TINYINT DEFAULT 0 comment '是否高速机',
    features VARCHAR DEFAULT NULL comment '机器特征',
    status TINYINT NOT NULL comment '机器状态',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    creator_id BIGINT NOT NULL comment '创建人',
    operator_id BIGINT NOT NULL comment '修改人',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_device(`device_id`,`deleted_at`)
);

DROP TABLE Produce_Order IF EXISTS;
CREATE TABLE Produce_Order (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    code VARCHAR(255) NOT NULL comment '生产订单编号',
    institute_id BIGINT NOT NULL comment '机构id',
    customer_code VARCHAR(255) NOT NULL comment '客户编号',
    customer_name VARCHAR(255) NOT NULL comment '客户名称',
    delivery_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '交货时间',
    status TINYINT NOT NULL comment '订单状态',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    creator_id BIGINT NOT NULL comment '创建人',
    operator_id BIGINT NOT NULL comment '修改人',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_produce_order_code(`code`,`deleted_at`)
);

DROP TABLE Produce_Order_Demand IF EXISTS;
CREATE TABLE Produce_Order_Demand (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    produce_order_id BIGINT NOT NULL comment '生产订单id',
    style_code VARCHAR(255) NOT NULL comment '款式编号',
    color VARCHAR(255) NOT NULL comment '颜色',
    order_quantity INTEGER NOT NULL comment '需求数量',
    weave_quantity INTEGER NOT NULL comment '织造数量',
    sample_quantity INTEGER NOT NULL comment '打样数量',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_produce_order_id(`produce_order_id`,`deleted_at`)
);

DROP TABLE Planned_Task IF EXISTS;
CREATE TABLE Planned_Task (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    factory_id BIGINT NOT NULL comment '工厂id',
    produce_order_code VARCHAR(255) NOT NULL comment '生产订单编号',
    weaving_order_id BIGINT NOT NULL comment '织造单id',
    machine_id BIGINT NOT NULL comment '机器id',
    style_code VARCHAR(255) NOT NULL comment '款式编号',
    part VARCHAR(128) NOT NULL DEFAULT '' comment '部位',
    planned_quantity INTEGER NOT NULL comment '计划织造数量',
    plan_start_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '计划开始时间',
    plan_end_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '计划结束时间',
    status TINYINT NOT NULL comment '任务状态',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    creator_id BIGINT NOT NULL comment '创建人',
    operator_id BIGINT NOT NULL comment '修改人',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_task_machine_id(`machine_id`, `deleted_at`)
);

DROP TABLE Style IF EXISTS;
CREATE TABLE Style (
    id            bigint PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    institute_id  bigint   default 1                 not null comment '企业id',
    style_id      varchar(255)                       null comment '外部系统id',
    code          varchar(255)                       not null comment '款式编号',
    name          varchar(128)                       null comment '名称',
    description   varchar(1024)                      null comment '描述',
    images        text                               null comment '图片',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_time datetime default CURRENT_TIMESTAMP not null comment '修改时间',
    deleted_at    bigint   default 0                 not null comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_style_code(`code`,`deleted_at`)
);

DROP TABLE Style_Sku IF EXISTS;
CREATE TABLE Style_Sku (
    id                    bigint PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    institute_id          bigint   default 1                 not null comment '企业id',
    style_code            varchar(255)                       not null comment '款式编号',
    code                  varchar(255)                       not null comment 'sku编号',
    size_id               varchar(255)                       null comment '外部尺码id',
    size                  varchar(128)                       not null comment '尺码',
    expected_produce_time decimal(10, 2)                     null comment '预计下机时间',
    create_time           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_time         datetime default CURRENT_TIMESTAMP not null comment '修改时间',
    deleted_at            bigint   default 0                 not null comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_style_sku_code(`code`,`deleted_at`)
);

DROP TABLE Style_Component IF EXISTS;
CREATE TABLE Style_Component (
    id                    bigint PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    institute_id          bigint   default 1                 not null comment '企业id',
    sku_code              varchar(255)                       not null comment 'sku编号',
    part_id               varchar(255)                       null comment '外部部件id',
    part                  varchar(128)                       not null comment '部位',
    color_id              varchar(255)                       null comment '外部颜色id',
    color                 varchar(128)                       null comment '颜色',
    type                  tinyint                            null,
    program_file          text                               null,
    program_file_url      text                               null,
    number                tinyint  default 1                 not null comment '个数',
    ratio                 tinyint  default 1                 not null comment '比例',
    cylinder_diameter     int                                not null comment '筒径',
    needle_spacing        int                                not null comment '针数',
    description           varchar(1024)                      null comment '描述',
    expected_produce_time decimal(10, 2)                     null comment '预计下机时间',
    expected_weight       decimal(10, 2)                     null,
    standard_number       int                                null,
    machine_requirement   text                               null comment '机器条件',
    default_efficiency    decimal(8, 4)                      null,
    actual_efficiency     decimal(8, 4)                      null,
    yarn_usage      text                               null,
    create_time           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    modified_time         datetime default CURRENT_TIMESTAMP not null comment '修改时间',
    deleted_at            bigint   default 0                 not null comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_style_sku_part_code(`sku_code`,`part`,`deleted_at`)
);

DROP TABLE Task_Segment IF EXISTS;
CREATE TABLE Task_Segment (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    task_id BIGINT NOT NULL comment '任务编号',
    planned_quantity INTEGER NOT NULL comment '计划织造数量',
    plan_start_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '计划开始时间',
    plan_end_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '计划结束时间',
    status TINYINT NOT NULL comment '任务状态',
    sort_index INTEGER NOT NULL default 0 comment '排序号',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    creator_id BIGINT NOT NULL comment '创建人',
    operator_id BIGINT NOT NULL comment '修改人',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_task_id(`task_id`, `deleted_at`)
);

DROP TABLE Weaving_Order IF EXISTS;
CREATE TABLE Weaving_Order (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    institute_id BIGINT NOT NULL comment '企业id',
    factory_id BIGINT NOT NULL comment '工厂id',
    code VARCHAR(255) NOT NULL comment '织造订单编号',
    produce_order_id BIGINT NOT NULL comment '生产订单编号',
    produce_order_code VARCHAR(255) NOT NULL comment '生产订单编号',
    style_code VARCHAR(255) NOT NULL comment '款式编号',
    color TEXT DEFAULT NULL comment '颜色',
    quantity INTEGER NOT NULL comment '需求数量',
    planned_quantity INTEGER NOT NULL comment '已排产数量',
    finish_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '交货时间',
    status TINYINT NOT NULL comment '订单状态',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    creator_id BIGINT NOT NULL comment '创建人',
    operator_id BIGINT NOT NULL comment '修改人',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_weaving_order_code(`code`,`deleted_at`),
    INDEX idx_weaving_produce_order_id(`produce_order_id`,`deleted_at`)
);

DROP TABLE Code_Generate IF EXISTS;
CREATE TABLE Code_Generate (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    type VARCHAR(255) NOT NULL comment '编号类型',
    current_number BIGINT NOT NULL comment '当前值',
    INDEX idx_code_type(`type`)
);

DROP TABLE Organization IF EXISTS;
CREATE TABLE Organization (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    code VARCHAR(255) NOT NULL comment '编号',
    parent_id BIGINT NOT NULL comment '父结点id',
    level INTEGER NOT NULL comment '层级',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_parent_id(`parent_id`,`deleted_at`)
);

DROP TABLE Customer IF EXISTS;
CREATE TABLE Customer (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    institute_id BIGINT NOT NULL comment '企业id',
    code VARCHAR(255) NOT NULL comment '客户编号',
    name VARCHAR(255) DEFAULT NULL comment '客户名',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_customer_code(`code`,`deleted_at`)
);

DROP TABLE Style_Daily_Production IF EXISTS;
CREATE TABLE Style_Daily_Production (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    style_code VARCHAR(255) NOT NULL comment '款式编号',
    date VARCHAR(64) NOT NULL comment '日期',
    quantity INTEGER NOT NULL comment '产量',
    td_quantity INTEGER NOT NULL comment '截止今日产量',
    actual_quantity INTEGER NULL comment '实际产量',
    actual_td_quantity INTEGER NOT NULL comment '实际总产量',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳',
    INDEX idx_style_date(`style_code`,`date`,`deleted_at`)
);

DROP TABLE Schedule_Task IF EXISTS;
CREATE TABLE Schedule_Task (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    machine_ids TEXT NOT NULL comment '选中的机器id列表',
    style_demands TEXT NOT NULL comment '款式需求',
    start_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '开始时间',
    end_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '结束时间',
    status TINYINT NOT NULL comment '任务状态',
    schedule_result MEDIUMTEXT DEFAULT NULL comment '计算结果',
    begin_process_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '开始执行时间',
    finish_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '完成时间',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    creator_id BIGINT NOT NULL comment '创建人',
    operator_id BIGINT NOT NULL comment '修改人',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳'
);

DROP TABLE Machine_Attr_Config IF EXISTS;
CREATE TABLE Machine_Attr_Config (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT comment 'primary key',
    institute_id BIGINT NOT NULL comment '企业id',
    attr_code VARCHAR(256) NOT NULL comment '属性名',
    optional_values TEXT NOT NULL comment '可选值',
    option_type TINYINT NOT NULL DEFAULT 0 comment '选项类型',
    use_to_filter TINYINT NOT NULL DEFAULT 0 comment '是否用于筛选',
    create_time DATETIME NOT NULL default CURRENT_TIMESTAMP comment '创建时间',
    modified_time DATETIME NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    creator_id BIGINT NOT NULL comment '创建人',
    operator_id BIGINT NOT NULL comment '修改人',
    deleted_at BIGINT NOT NULL default 0 comment '是否删除，0-未删除，否则为删除时间戳'
);

DROP TABLE Weaving_Part_Order IF EXISTS;
create table Weaving_Part_Order
(
    id                 bigint auto_increment comment 'primary key'
        primary key,
    institute_id       bigint                             null comment '企业id',
    factory_id         bigint                             null comment '工厂id',
    weaving_order_id   bigint                             not null comment '织造订单编号',
    produce_order_id   bigint                             null comment '生产单id',
    produce_order_code varchar(255)                       null comment '生产单编号',
    style_code         varchar(255)                       not null comment '款式编号',
    part               varchar(255)                       null comment '部位',
    color              text                               null comment '颜色',
    quantity           int                                not null comment '需求数量',
    planned_quantity   int      not null default 0                  comment '已排产数量',
    finish_time        datetime not null default CURRENT_TIMESTAMP  comment '交货时间',
    status             tinyint                            not null comment '订单状态',
    create_time        datetime not null default CURRENT_TIMESTAMP  comment '创建时间',
    modified_time      datetime not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '修改时间',
    deleted_at         bigint   default 0                 not null comment '是否删除，0-未删除，否则为删除时间戳'
);