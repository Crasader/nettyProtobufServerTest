CREATE TABLE `t_demo` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_name` varchar(10) NOT NULL COMMENT '用户名',
  `title` varchar(3000) DEFAULT '',
  `create_time` bigint(20) unsigned NOT NULL COMMENT '创建时间,yyyymmddhhmmss',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

