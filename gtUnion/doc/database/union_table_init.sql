DROP TABLE IF EXISTS `t_union_member`;
CREATE TABLE `t_union_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `bus_id` int(11) DEFAULT NULL COMMENT '商家id',
  `is_union_owner` int(2) DEFAULT NULL COMMENT '是否盟主(0:否 1:是)',
  `status` int(2) DEFAULT NULL COMMENT '盟员状态(1:申请入盟 2:已入盟 3:申请退盟 4:退盟过渡期)',
  `enterprise_name` varchar(200) DEFAULT NULL COMMENT '企业名称',
  `enterprise_address` varchar(255) DEFAULT NULL COMMENT '企业地址',
  `director_name` varchar(50) DEFAULT NULL COMMENT '负责人名称',
  `director_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `director_email` varchar(50) DEFAULT NULL COMMENT '负责人邮箱',
  `address_longitude` varchar(50) DEFAULT NULL COMMENT '地址经度',
  `address_latitude` varchar(50) DEFAULT NULL COMMENT '地址维度',
  `address_province_code` varchar(50) DEFAULT NULL COMMENT '地址省份code',
  `address_city_code` varchar(50) DEFAULT NULL COMMENT '地址城市code',
  `address_district_code` varchar(50) DEFAULT NULL COMMENT '地址区code',
  `is_member_out_notify` int(2) DEFAULT '1' COMMENT '盟员退出是否短信通知(0:否 1:是)',
  `notify_phone` varchar(20) DEFAULT NULL COMMENT '短信通知手机号',
  `integral_exchange_ratio` double(8,2) DEFAULT NULL COMMENT '积分兑换率(百分比)',
  `discount` double(8,2) DEFAULT NULL COMMENT '统一折扣(折)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='盟员';

DROP TABLE IF EXISTS `t_union_member_join`;
CREATE TABLE `t_union_member_join` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(2) DEFAULT NULL COMMENT '入盟类型(1:申请 2:推荐)',
  `apply_member_id` int(11) DEFAULT NULL COMMENT '入盟盟员id',
  `recommend_member_id` int(11) DEFAULT NULL COMMENT '推荐盟员id',
  `is_recommend_agree` int(2) DEFAULT NULL COMMENT '是否同意推荐(0：不同意 1：同意)',
  `reason` varchar(200) DEFAULT NULL COMMENT '加入或推荐理由',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='入盟申请';

DROP TABLE IF EXISTS `t_union_member_out`;
CREATE TABLE `t_union_member_out` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(2) DEFAULT NULL COMMENT '退盟类型(1:自己申请 2:盟主移出)',
  `apply_member_id` int(11) DEFAULT NULL COMMENT '退盟盟员id',
  `apply_out_reason` varchar(200) DEFAULT NULL COMMENT '退盟理由',
  `confirm_out_time` datetime DEFAULT NULL COMMENT '盟主审核退盟时间',
  `actual_out_time` datetime DEFAULT NULL COMMENT '实际退盟时间',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='退盟申请';

DROP TABLE IF EXISTS `t_union_main`;
CREATE TABLE `t_union_main` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主表',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `img` varchar(300) DEFAULT NULL COMMENT '图标',
  `join_type` int(2) DEFAULT NULL COMMENT '加盟方式(1:推荐 2:申请、推荐)',
  `illustration` varchar(800) DEFAULT NULL COMMENT '说明',
  `member_limit` int(8) DEFAULT NULL COMMENT '成员总数上限',
  `is_integral` int(2) DEFAULT '0' COMMENT '是否开启积分(0:否 1:是)',
  `validity` datetime DEFAULT NULL COMMENT '有效期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟';

DROP TABLE IF EXISTS `t_union_main_create`;
CREATE TABLE `t_union_main_create` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `bus_id` int(11) DEFAULT NULL COMMENT '商家id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `permit_id` int(11) DEFAULT NULL COMMENT '联盟许可id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟创建';

DROP TABLE IF EXISTS `t_union_main_permit`;
CREATE TABLE `t_union_main_permit` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `bus_id` int(11) DEFAULT NULL COMMENT '商家id',
  `package_id` int(11) DEFAULT NULL COMMENT '盟主服务套餐id',
  `validity` datetime DEFAULT NULL COMMENT '许可有效期',
  `order_money` double(10,2) DEFAULT NULL COMMENT '订单金额',
  `order_status` int(2) DEFAULT NULL COMMENT '订单状态(1:未支付 2:已支付 3:支付失败)',
  `pay_type` int(2) DEFAULT NULL COMMENT '支付方式(1:微信支付 2:粉币支付 3:支付宝支付)',
  `sys_order_no` varchar(128) DEFAULT NULL COMMENT '内部订单号',
  `wx_order_no` varchar(128) DEFAULT NULL COMMENT '微信订单号',
  `product_id` varchar(128) DEFAULT NULL COMMENT '产品号码',
  `alipay_order_no` varchar(128) DEFAULT NULL COMMENT '支付宝订单号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟许可';

DROP TABLE IF EXISTS `t_union_main_package`;
CREATE TABLE `t_union_main_package` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `year` double(8,1) DEFAULT NULL COMMENT '年限',
  `price` double(8,2) DEFAULT NULL COMMENT '收费金额',
  `level` int(11) DEFAULT NULL COMMENT '商家等级',
  `number` int(11) DEFAULT NULL COMMENT '成员数',
  `desc` varchar(20) DEFAULT NULL COMMENT '年限描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='盟主服务套餐';

DROP TABLE IF EXISTS `t_union_main_dict`;
CREATE TABLE `t_union_main_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `item_key` varchar(50) DEFAULT NULL COMMENT '字典关键字',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟入盟申请必填信息';

DROP TABLE IF EXISTS `t_union_main_notice`;
CREATE TABLE `t_union_main_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `content` varchar(500) DEFAULT NULL COMMENT '公告内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟公告';

DROP TABLE IF EXISTS `t_union_main_transfer`;
CREATE TABLE `t_union_main_transfer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '转移时间',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `from_member_id` int(11) DEFAULT NULL COMMENT '转移前盟主盟员id',
  `to_member_id` int(11) DEFAULT NULL COMMENT '转移后盟主盟员id',
  `confirm_status` int(2) DEFAULT NULL COMMENT '确认状态(1:确认中 2:已确认 3:已拒绝)',
  `is_advice` int(2) DEFAULT '0' COMMENT '是否需要提示(0:否  1:是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟转移';

DROP TABLE IF EXISTS `t_union_opportunity`;
CREATE TABLE `t_union_opportunity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `type` int(2) DEFAULT NULL COMMENT '推荐类型(1:线上 2:线下)',
  `from_member_id` int(11) DEFAULT NULL COMMENT '推荐盟员id',
  `to_member_id` int(11) DEFAULT NULL COMMENT '接收盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `accept_status` int(2) DEFAULT NULL COMMENT '受理状态(1:受理中 2:已接受 3:已拒绝)',
  `accept_price` double(8,2) DEFAULT NULL COMMENT '受理金额',
  `brokerage_money` double(8,2) DEFAULT NULL COMMENT '佣金金额',
  `client_name` varchar(50) DEFAULT NULL COMMENT '客户姓名',
  `client_phone` varchar(20) DEFAULT NULL COMMENT '客户电话',
  `business_msg` varchar(500) DEFAULT NULL COMMENT '业务备注',
  `is_urge_brokerage` int(2) DEFAULT '0' COMMENT '是否需要催促佣金(0:否 1:是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='商机';

DROP TABLE IF EXISTS `t_union_opportunity_ratio`;
CREATE TABLE `t_union_opportunity_ratio` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `from_member_id` int(11) DEFAULT NULL COMMENT '设置佣金比率盟员id',
  `to_member_id` int(11) DEFAULT NULL COMMENT '受惠佣金比率盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `ratio` double(8,2) DEFAULT NULL COMMENT '佣金比率（百分比）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='商机佣金比率';

DROP TABLE IF EXISTS `t_union_brokerage_income`;
CREATE TABLE `t_union_brokerage_income` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(2) DEFAULT NULL COMMENT '类型(1:售卡 2:商机)',
  `money` double(8,2) DEFAULT NULL COMMENT '佣金金额',
  `bus_id` int(11) DEFAULT NULL COMMENT '收入商家id',
  `member_id` int(11) DEFAULT NULL COMMENT '收入盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `card_id` int(11) DEFAULT NULL COMMENT '联盟卡id',
  `opportunity_id` int(11) DEFAULT NULL COMMENT '商机id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='佣金收入';

DROP TABLE IF EXISTS `t_union_brokerage_pay`;
CREATE TABLE `t_union_brokerage_pay` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '支付时间',
  `type` int(2) DEFAULT NULL COMMENT '支付类型(1:微信 2:支付宝)',
  `status` int(2) DEFAULT NULL COMMENT '支付状态(1:未支付 2:支付成功 3:已退款)',
  `money` double(8,2) DEFAULT NULL COMMENT '支付金额',
  `from_bus_id` int(11) DEFAULT NULL COMMENT '支付商家id',
  `to_bus_id` int(11) DEFAULT NULL COMMENT '收款商家id',
  `from_member_id` int(11) DEFAULT NULL COMMENT '支付盟员id',
  `to_member_id` int(11) DEFAULT NULL COMMENT '收款盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `opportunity_id` int(11) DEFAULT NULL COMMENT '商机id',
  `verifier_id` int(11) DEFAULT NULL COMMENT '平台管理员id',
  `order_no` varchar(60) DEFAULT NULL COMMENT '订单号',
  `wx_order_no` varchar(60) DEFAULT NULL COMMENT '微信订单编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='佣金支出';

DROP TABLE IF EXISTS `t_union_brokerage_withdrawal`;
CREATE TABLE `t_union_brokerage_withdrawal` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '提现时间',
  `bus_id` int(11) DEFAULT NULL COMMENT '提现商家id',
  `money` double(8,2) DEFAULT NULL COMMENT '提现金额',
  `verifier_id` int(11) DEFAULT NULL COMMENT '平台管理者id',
  `verifier_name` varchar(100) DEFAULT NULL COMMENT '平台管理者名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='佣金提现';

DROP TABLE IF EXISTS `t_union_card_fan`;
CREATE TABLE `t_union_card_fan` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `phone` varchar(30) DEFAULT NULL COMMENT '手机号',
  `number` varchar(30) DEFAULT NULL COMMENT '卡号',
  `integral` double(8,2) DEFAULT '0.00' COMMENT '积分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟卡粉丝信息';

DROP TABLE IF EXISTS `t_union_card`;
CREATE TABLE `t_union_card` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(2) DEFAULT NULL COMMENT '类型(1:折扣卡 2:活动卡)',
  `validity` datetime DEFAULT NULL COMMENT '有效期',
  `integral` double(8,2) DEFAULT '0.00' COMMENT '积分',
  `member_id` int(11) DEFAULT NULL COMMENT '盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `fan_id` int(11) DEFAULT NULL COMMENT '联盟卡粉丝id',
  `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟卡';

DROP TABLE IF EXISTS `t_union_card_activity`;
CREATE TABLE `t_union_card_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `price` double(8,2) DEFAULT NULL COMMENT '价格',
  `img` varchar(300) DEFAULT NULL COMMENT '展示图',
  `amount` int(11) DEFAULT NULL COMMENT '发行量',
  `validity_day` int(11) DEFAULT NULL COMMENT '有效天数',
  `apply_begin_time` datetime DEFAULT NULL COMMENT '报名开始时间',
  `apply_end_time` datetime DEFAULT NULL COMMENT '报名结束时间',
  `sell_begin_time` datetime DEFAULT NULL COMMENT '售卡开始时间',
  `sell_end_time` datetime DEFAULT NULL COMMENT '售卡结束时间',
  `illustration` varchar(800) DEFAULT NULL COMMENT '说明',
  `is_project_check` int(2) DEFAULT NULL COMMENT '项目是否需要审核(0:否 1:是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='活动';

DROP TABLE IF EXISTS `t_union_card_project`;
CREATE TABLE `t_union_card_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
  `status` int(2) DEFAULT NULL COMMENT '项目状态(1:未提交 2:审核中 3:已通过 4:不通过)',
  `member_id` int(11) DEFAULT NULL COMMENT '盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='项目';

DROP TABLE IF EXISTS `t_union_card_project_flow`;
CREATE TABLE `t_union_card_project_flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `illustration` varchar(800) DEFAULT NULL COMMENT '说明',
  `project_id` int(11) DEFAULT NULL COMMENT '项目id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='项目流程';

DROP TABLE IF EXISTS `t_union_card_project_item`;
CREATE TABLE `t_union_card_project_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `type` int(2) DEFAULT NULL COMMENT '类型(1:非ERP文本优惠 2:ERP文本优惠 3:ERP商品优惠)',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `number` int(11) DEFAULT NULL COMMENT '数量',
  `size` varchar(100) DEFAULT NULL COMMENT '规格',
  `erp_type` int(2) DEFAULT NULL COMMENT 'ERP类型(1:车小算 2:样子)',
  `shop_id` int(11) DEFAULT NULL COMMENT '门店id',
  `erp_text_id` int(11) DEFAULT NULL COMMENT 'ERP系统的文本项目id',
  `erp_goods_id` int(11) DEFAULT NULL COMMENT 'ERP系统的商品项目id',
  `project_id` int(11) DEFAULT NULL COMMENT '项目id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='项目优惠';

DROP TABLE IF EXISTS `t_union_card_record`;
CREATE TABLE `t_union_card_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `order_no` varchar(60) DEFAULT NULL COMMENT '订单号',
  `wx_order_no` varchar(60) DEFAULT NULL COMMENT '微信订单号',
  `order_desc` varchar(100) DEFAULT NULL COMMENT '订单描述',
  `pay_type` int(2) DEFAULT NULL COMMENT '支付类型(1:微信支付 2:支付宝支付)',
  `pay_status` int(2) DEFAULT NULL COMMENT '支付状态(1:未支付 2:支付成功 3:支付失败 4:已退款)',
  `pay_money` double(8,2) DEFAULT NULL COMMENT '支付金额',
  `card_id` int(11) DEFAULT NULL COMMENT '联盟卡id',
  `fan_id` int(11) DEFAULT NULL COMMENT '联盟卡粉丝id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟卡购买记录';

DROP TABLE IF EXISTS `t_union_card_sharing_ratio`;
CREATE TABLE `t_union_card_sharing_ratio` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `ratio` double(8,2) DEFAULT NULL COMMENT '分成比例',
  `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
  `member_id` int(11) DEFAULT NULL COMMENT '盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟卡售卡分成比例';

DROP TABLE IF EXISTS `t_union_card_sharing_record`;
CREATE TABLE `t_union_card_sharing_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `sell_price` double(8,2) DEFAULT NULL COMMENT '售卡价格',
  `sharing_ratio` double(8,2) DEFAULT NULL COMMENT '分成比例',
  `sharing_money` double(8,2) DEFAULT NULL COMMENT '售卡分成',
  `member_id` int(11) DEFAULT NULL COMMENT '盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `activity_id` int(11) DEFAULT NULL COMMENT '活动id',
  `card_id` int(11) DEFAULT NULL COMMENT '联盟卡id',
  `fan_id` int(11) DEFAULT NULL COMMENT '联盟卡粉丝id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='联盟卡售卡分成记录';

DROP TABLE IF EXISTS `t_union_consume`;
CREATE TABLE `t_union_consume` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(2) DEFAULT NULL COMMENT '消费类型(1:线上 2:线下)',
  `consume_money` double(8,2) DEFAULT NULL COMMENT '消费总额',
  `pay_money` double(8,2) DEFAULT NULL COMMENT '支付金额',
  `pay_type` int(8) DEFAULT NULL COMMENT '支付方式(0:现金 1:微信 2:支付宝)',
  `pay_status` int(2) DEFAULT NULL COMMENT '支付状态(1:未支付 2:已支付 3:已退款)',
  `use_integral` int(11) DEFAULT 0 COMMENT '使用积分额',
  `give_integral` int(11) DEFAULT 0 COMMENT '赠送积分额',
  `order_no` varchar(128) DEFAULT NULL COMMENT '订单号',
  `wx_order_no` varchar(128) DEFAULT NULL COMMENT '微信订单号',
  `business_type` int(2) DEFAULT NULL COMMENT '消费行业类型(0:线下 >0:其他行业)',
  `business_desc` varchar(200) DEFAULT NULL COMMENT '行业消费描述',
  `business_order_id` int(11) DEFAULT NULL COMMENT '行业订单id',
  `shop_id` int(11) DEFAULT NULL COMMENT '门店id',
  `member_id` int(11) DEFAULT NULL COMMENT '盟员id',
  `union_id` int(11) DEFAULT NULL COMMENT '联盟id',
  `card_id` int(11) DEFAULT NULL COMMENT '联盟卡id',
  `fan_id` int(11) DEFAULT NULL COMMENT '联盟卡粉丝id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='消费核销';

DROP TABLE IF EXISTS `t_union_consume_project`;
CREATE TABLE `t_union_consume_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `type` int(2) DEFAULT NULL COMMENT '消费类型(1:线上 2:线下)',
  `consume_id` int(11) DEFAULT NULL COMMENT '核销消费id',
  `project_item_id` int(11) DEFAULT NULL COMMENT '项目优惠id',
  `project_id` int(11) DEFAULT NULL COMMENT '项目id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='消费核销项目优惠';

DROP TABLE IF EXISTS `t_union_verifier`;
CREATE TABLE `t_union_verifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `del_status` int(2) DEFAULT NULL COMMENT '是否删除(0:否 1:是)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `bus_id` int(11) DEFAULT NULL COMMENT '商家id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='平台管理者';