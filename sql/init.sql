create database `wlinsk-ai-aq`
  character set utf8mb4
  collate utf8mb4_general_ci;

use `wlinsk-ai-aq`;

create table tb_user
(
    `id`            int unsigned not null auto_increment comment '自增id',
    `user_id`       varchar(50)  not null comment '用户id',
    `user_account`  varchar(256) not null comment '账号',
    `user_password` varchar(512) not null comment '密码',
    `union_id`      varchar(256) null comment '微信开放平台id',
    `mp_open_id`    varchar(256) null comment '公众号openId',
    `user_name`     varchar(256) null comment '用户昵称',
    `user_avatar`   varchar(1024) null comment '用户头像',
    `user_profile`  varchar(512) null comment '用户简介',
    `user_role`     varchar(256)          default 'user' not null comment '用户角色：user/admin/ban',
    `ai_point`      int                   default 5 comment 'AI点数',
    `create_time`   datetime              default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time`   datetime comment '更新时间',
    `version`       int          not null default 1 comment '版本号',
    `del_state`     int          not null default 10 comment '删除标识 10:正常 11:删除',
    primary key (`id`),
    unique key `uniq_user_id` (`user_id`),
    index           idx_union_id (`union_id`)
) comment '用户表';

create table tb_app
(
    `id`               int unsigned not null auto_increment comment '自增id',
    `app_id`           varchar(50)                        not null comment '应用id',
    `app_name`         varchar(128)                       not null comment '应用名',
    `app_desc`         varchar(2048) null comment '应用描述',
    `app_icon`         varchar(1024) null comment '应用图标',
    `app_type`         tinyint  default 0                 not null comment '应用类型（0-得分类，1-测评类）',
    `scoring_strategy` tinyint  default 0                 not null comment '评分策略（0-自定义，1-AI）',
    `review_status`    int      default 0                 not null comment '审核状态：0-待审核, 1-通过, 2-拒绝',
    `review_message`   varchar(512) comment '审核信息',
    `reviewer_id`      varchar(50) comment '审核人 id',
    `review_time`      datetime comment '审核时间',
    `user_id`          varchar(50)                        not null comment '用户id',
    `create_time`      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time`      datetime comment '更新时间',
    `version`          int                                not null default 1 comment '版本号',
    `del_state`        int                                not null default 10 comment '删除标识 10:正常 11:删除',
    primary key (`id`),
    unique key `uniq_app_id` (`app_id`),
    index              idx_app_name (`app_name`)
) comment '应用表';

create table tb_question
(
    `id`               int unsigned not null auto_increment comment '自增id',
    `question_id`      varchar(50) not null comment '题目id',
    `question_content` text null comment '题目内容（json格式）',
    `app_id`           varchar(50) not null comment '应用id',
    `user_id`          varchar(50) not null comment '用户id',
    `create_time`      datetime             default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time`      datetime comment '更新时间',
    `version`          int         not null default 1 comment '版本号',
    `del_state`        int         not null default 10 comment '删除标识 10:正常 11:删除',
    primary key (`id`),
    unique key `uniq_question_id` (`question_id`),
    index              idx_app_id (`app_id`)
) comment '题目表';

create table tb_scoring_result
(
    `id`                 int unsigned not null auto_increment comment '自增id',
    `result_id`          varchar(50)  not null comment '结果id',
    `result_name`        varchar(128) not null comment '结果名称，如物流师',
    `result_desc`        text null comment '结果描述',
    `result_picture`     varchar(1024) null comment '结果图片',
    `result_prop`        varchar(128) null comment '结果属性集合 JSON，如 [I,S,T,J]',
    `result_score_range` int null comment '结果得分范围，如 80，表示 80及以上的分数命中此结果',
    `app_id`             varchar(50)  not null comment '应用id',
    `user_id`            varchar(50)  not null comment '用户id',
    `create_time`        datetime              default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time`        datetime comment '更新时间',
    `version`            int          not null default 1 comment '版本号',
    `del_state`          int          not null default 10 comment '删除标识 10:正常 11:删除',
    primary key (`id`),
    unique key `uniq_result_id` (`result_id`),
    index                idx_app_id (`app_id`)
) comment '评分结果表';
create table tb_user_answer_record
(
    `id`               int unsigned not null auto_increment comment '自增id',
    `record_id`        varchar(50)                        not null comment '记录id',
    `app_id`           varchar(50)                        not null comment '应用id',
    `app_type`         tinyint  default 0                 not null comment '应用类型（0-得分类，1-测评类）',
    `scoring_strategy` tinyint  default 0                 not null comment '评分策略（0-自定义，1-AI）',
    `choices`          text null comment '用户答案（JSON 数组）',
    `result_id`        varchar(50) null comment '评分结果id',
    `result_name`      varchar(128) null comment '结果名称，如物流师',
    `result_desc`      text null comment '结果描述',
    `result_picture`   varchar(1024) null comment '结果图标',
    `result_score`     int null comment '得分',
    `user_id`          varchar(50)                        not null comment '用户id',
    `create_time`      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `update_time`      datetime comment '更新时间',
    `version`          int                                not null default 1 comment '版本号',
    `del_state`        int                                not null default 10 comment '删除标识 10:正常 11:删除',
    primary key (`id`),
    unique key `uniq_record_id` (`record_id`),
    index              idx_app_id (`app_id`),
    index              idx_user_id (`user_id`)
) comment '用户答题记录';


CREATE TABLE `tb_chat_session`
(
    `id`          int unsigned not null auto_increment comment '自增id',
    `session_id`  varchar(32) not null comment '会话id',
    `user_id`     varchar(32) not null comment '用户id',
    `title`       varchar(100) comment '会话标题',
    `create_time` DATETIME    not null default current_timestamp comment '创建时间',
    `update_time` DATETIME    not null default current_timestamp on update current_timestamp comment '更新时间',
    `creater`     varchar(32) not null comment '创建人',
    `updater`     varchar(32) not null comment '更新人',
    primary key (`id`),
    index         `session_id_index` (`session_id`),
    index         `user_id_index` (`user_id`),
    index         `update_time_index` (`update_time`)
) COMMENT='用户会话表';