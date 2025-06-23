/*
 Navicat Premium Data Transfer

 Source Server         : my
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : xm-rescue

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 27/05/2025 22:42:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色标识',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 171 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (167, 'admin', '123', '慢羊羊', 'http://localhost:9090/files/1742386089783-陈.jpg', 'ADMIN', '17709890575', '1790086332');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户评论',
  `pid` int(0) NULL DEFAULT NULL COMMENT '父级ID',
  `time` timestamp(0) NOT NULL COMMENT '评论时间',
  `root_id` int(0) NULL DEFAULT NULL COMMENT '根节点ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色',
  `admin_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_comment_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (74, '通过平台捐赠了多次物资，操作确实便捷😊，但希望能增加捐赠后的反馈机制。比如，捐赠物资发放到灾区后，能否提供一些现场照片或受助者的感谢信息？这样我们能更直观地感受到爱心的传递，也会更有参与感(៸៸᳐⦁⩊⦁៸៸᳐ )੭ ﾞ。', 12, NULL, '2025-03-26 00:02:15', 74, 'USER', NULL);
INSERT INTO `comment` VALUES (76, '亲爱的用户，您的牵挂让我们感受到公益的温度🌱！我们完全理解您对物资去向的关切，即将上线的‘爱心短信提醒’功能会第一时间告知您物资抵达信息，并同步现场照片。您的善意终会开花结果，感谢您的信任与等待！', NULL, 74, '2025-03-26 13:52:27', 74, 'ADMIN', 167);
INSERT INTO `comment` VALUES (81, '芜湖哈哈', 170, NULL, '2025-05-12 19:13:53', 81, 'USER', NULL);

-- ----------------------------
-- Table structure for donate
-- ----------------------------
DROP TABLE IF EXISTS `donate`;
CREATE TABLE `donate`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物资名称',
  `descr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物资描述',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物资图片',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `time` date NULL DEFAULT NULL COMMENT '提交时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核理由',
  `used` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '使用状态',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户标识',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_donate_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '物资捐赠' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of donate
-- ----------------------------
INSERT INTO `donate` VALUES (32, '防护服', '一百套医用防护服', '医疗用品', 'http://localhost:9090/files/1742987081556-防护服.jpg', 12, '2025-03-26', '通过', '谢谢，感谢您对救援事业的支持', '已使用', 'USER');
INSERT INTO `donate` VALUES (36, '书籍', '一箱书籍', '其他', 'http://localhost:9090/files/1747113366158-家庭消防.png', 12, '2025-05-13', '通过', '数据库', '已使用', 'USER');

-- ----------------------------
-- Table structure for help
-- ----------------------------
DROP TABLE IF EXISTS `help`;
CREATE TABLE `help`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '救援事项',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '救援内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '救援类型',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '现场图片',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事故地点',
  `longitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '经度',
  `latitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '纬度',
  `time` timestamp(0) NULL DEFAULT NULL COMMENT '救援时间',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '救援者名称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理状态',
  `record_id` int(0) NULL DEFAULT NULL COMMENT '出警记录',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_help_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_help_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '救援信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of help
-- ----------------------------
INSERT INTO `help` VALUES (21, '车祸', '出车祸了，有人受伤，情况很紧急', '交通事故', 'http://localhost:9090/files/1742990022260-车祸图片.jpeg', '大连市旅顺口区大连科技学院北门', NULL, NULL, '2025-03-26 19:56:57', 12, '17751071572', '已解决', 44);
INSERT INTO `help` VALUES (37, '规范化', '否', '意外伤害', NULL, '发生大幅', NULL, NULL, '2025-05-16 16:05:51', 12, '12345678901', '待处理', NULL);
INSERT INTO `help` VALUES (38, '分三点', '第三方', '火灾', NULL, 'f三段·1', NULL, NULL, '2025-05-16 16:06:09', 12, '12345678901', '待处理', NULL);

-- ----------------------------
-- Table structure for knowledge
-- ----------------------------
DROP TABLE IF EXISTS `knowledge`;
CREATE TABLE `knowledge`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `read_count` int(0) NULL DEFAULT 0 COMMENT '阅读量',
  `date` date NULL DEFAULT NULL COMMENT '发布日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '救援科普信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of knowledge
-- ----------------------------
INSERT INTO `knowledge` VALUES (8, '低血糖急救知识', '<p>低血糖临床症状主要表现为低血糖综合症，发病时可以有心慌、心悸、饥饿、软弱、手足颤抖、皮肤苍白、出汗、心率增加、血压轻度升高等症状。</p><p>急救可以这样做：</p><p>如果只是轻度低血糖，患者神志清醒，可以吃几粒糖果、几块饼干，或喝半杯糖水，可以达到迅速纠正低血糖的效果，一般十几分钟后低血糖症状就会消失。</p><p>如果使用以上方法仍没有效果，或是病人出现神智不清症状，则应立即送医院急救。</p><p><img src=\"http://localhost:9090/files/1747097342666-低血糖.jpg\" style=\"max-width:100%;\" contenteditable=\"false\"/></p>', 2, '2025-02-17');
INSERT INTO `knowledge` VALUES (11, '溺水急救知识', '<p>救助溺水者，分秒必争。淹溺的进程很快，若抢救不及时一般4~6分钟即可呼吸心跳停止死亡。淹溺者溺水6~9分钟死亡率达到65%，超过25分钟，则达100%死亡。</p><p>但是，若在1~2分钟内得到正确救护，挽救成功率可以达到100%。</p><p>急救这样做：</p><p>①迅速清除溺水者口、鼻中的污物。</p><p>②解开领口，使其平卧，以保持呼吸道通畅，再给予2次人工呼吸。</p><p>③将溺水者头部歪向一侧，进行心肺复苏按压，在按压的同时，溺水者胃部的积水也会自然流出。</p><p>④按照每30次心肺复苏按压配合2次人工呼吸的节奏循环进行，直到溺水者苏醒，或是专业的急救人员到来为止。<img src=\"http://localhost:9090/files/1740138937024-935.jpg\" style=\"max-width:100%;\" contenteditable=\"false\"/></p>', 1, '2025-02-21');
INSERT INTO `knowledge` VALUES (12, '烧伤、烫伤急救知识', '<p>烧伤、烫伤造成的伤害80%以上都是余热造成的，所以急救的关键就是减少余热的损害。</p><p>急救这样做：</p><p>冲：以流动的自来水冲洗或浸泡在冷水中，直到冷却局部并减轻疼痛或者用冷毛巾敷在伤处至少10分钟。不可把冰块直接放在伤口上，以免使皮肤组织受伤。如果现场没有水，可用其他任何凉的无害的液体，如牛奶或罐装的饮料。</p><p>脱：在穿着衣服被热水、热汤烫伤时，千万不要脱下衣服，而是先直接用冷水浇在衣服上降温。充分泡湿伤口后小心除去衣物，如衣服和皮肤粘在一起时，切勿撕拉，只能将未粘着部分剪去，粘着的部分留在皮肤上以后处理，再用清洁纱布覆盖伤面，以防污染。有水泡时千万不要弄破。</p><p><img src=\"http://localhost:9090/files/1747097380328-烫伤.jpg\" style=\"max-width:100%;\" contenteditable=\"false\"/></p>', 16, '2025-02-21');
INSERT INTO `knowledge` VALUES (13, '触电急救知识', '<p>急救这样做：</p><p>当发现有人触电时，尽快找到电闸，切断电源是当务之急。如果暂时找不到电源，可就近找一样绝缘的东西，如木棍或塑料管子，挑开触电者与电源的接触，然后检查触电者的反应。</p><p>如果发现其已经没有了心跳和呼吸，应立即就地对其进行人工呼吸和胸外按压，同时让别人拨打120。<img src=\"http://localhost:9090/files/1742993444656-触电.jpg\" style=\"max-width:100%;\" contenteditable=\"false\"/></p>', 5, '2025-03-26');
INSERT INTO `knowledge` VALUES (14, '代金卡', '<p>大大<img src=\"http://localhost:9090/files/1747113639548-bg1.jpg\" style=\"max-width:100%;\" contenteditable=\"false\"/></p>', 1, '2025-05-13');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `time` date NOT NULL COMMENT '创建时间',
  `user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user`(`user`) USING BTREE,
  CONSTRAINT `user` FOREIGN KEY (`user`) REFERENCES `admin` (`username`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (11, '春季应急救援技能培训报名启动', '为提升救援队伍的专业能力，现组织春季应急救援技能培训，培训内容：\n心肺复苏、创伤急救等基础技能强化；\n自然灾害现场指挥与协调；\n新型救援装备操作与维护。', '2025-03-26', 'admin');
INSERT INTO `notice` VALUES (12, '急救培训报名', '4月2号开始报名', '2025-03-27', 'admin');
INSERT INTO `notice` VALUES (15, '大数据老的快', '速度很快就来', '2025-05-13', 'admin');

-- ----------------------------
-- Table structure for records
-- ----------------------------
DROP TABLE IF EXISTS `records`;
CREATE TABLE `records`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `vol_id` int(0) NULL DEFAULT NULL COMMENT '救助员ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态',
  `time` datetime(0) NOT NULL COMMENT '出警时间',
  `help_id` int(0) NULL DEFAULT NULL COMMENT '事件ID',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_records_volunteer`(`vol_id`) USING BTREE,
  INDEX `fk_records_help`(`help_id`) USING BTREE,
  CONSTRAINT `fk_records_help` FOREIGN KEY (`help_id`) REFERENCES `help` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_records_volunteer` FOREIGN KEY (`vol_id`) REFERENCES `volunteer` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '出警信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of records
-- ----------------------------
INSERT INTO `records` VALUES (44, 177, '已救援', '2025-04-17 22:05:55', 21, '尽快');

-- ----------------------------
-- Table structure for training_records
-- ----------------------------
DROP TABLE IF EXISTS `training_records`;
CREATE TABLE `training_records`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `volunteer_id` int(0) NOT NULL COMMENT '救助员ID',
  `training_date` date NOT NULL COMMENT '培训日期',
  `training_topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '培训主题',
  `duration` int(0) NOT NULL COMMENT '培训时长（小时）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_training_volunteer`(`volunteer_id`) USING BTREE,
  CONSTRAINT `fk_training_volunteer` FOREIGN KEY (`volunteer_id`) REFERENCES `volunteer` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '培训记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of training_records
-- ----------------------------
INSERT INTO `training_records` VALUES (3, 177, '2025-05-13', '热', 24);
INSERT INTO `training_records` VALUES (4, 177, '2025-05-12', '大时代', 36);
INSERT INTO `training_records` VALUES (5, 177, '2025-05-13', '大大', 36);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色标识',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 174 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '普通用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (12, 'user', '123', 'USER', 'http://localhost:9090/files/1742381416424-陈2.jpg', 'USER', '17751071572', '1790086332@qq.com');
INSERT INTO `user` VALUES (170, 'wh', '123', 'wh', 'http://localhost:9090/files/1747100354704-baiye.png', 'USER', '17751071572', '1790086332@qq.com');
INSERT INTO `user` VALUES (173, 'hbb', '123456', 'hbb', NULL, 'USER', NULL, NULL);

-- ----------------------------
-- Table structure for vol_apply
-- ----------------------------
DROP TABLE IF EXISTS `vol_apply`;
CREATE TABLE `vol_apply`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '审核状态',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电话',
  `sex` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '性别',
  `birth` date NOT NULL COMMENT '生日',
  `card_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证照片',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '个人介绍',
  `time` date NULL DEFAULT NULL COMMENT '创建时间',
  `card_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号码',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_vol_apply_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_vol_apply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '救助员申请' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vol_apply
-- ----------------------------
INSERT INTO `vol_apply` VALUES (13, 12, '通过', '17751071572', '男', '2000-03-21', 'http://localhost:9090/files/1742992873684-身份证.jpg', '我叫 白夜，男25岁。怀揣着对救助工作的满腔热忱，我渴望成为一名救助员，为困境中的人们伸出援手。\n我拥有扎实的急救知识储备，通过专业培训考取了相关急救证书，熟练掌握心肺复苏、止血包扎、骨折固定等技能。并且定期参加复训与模拟演练，确保技能始终在线。\n实践方面，我积极投身志愿者活动。参与社区急救知识普及，让更多人掌握急救方法；投身自然灾害救援，协助救助伤员、分发物资。这些经历不仅提升了实操能力，更让我学会在复杂状况下冷静应对。', '2025-03-26', '321322200003215430');
INSERT INTO `vol_apply` VALUES (19, NULL, '通过', '17751071572', '男', '2002-10-10', 'http://localhost:9090/files/1747097896640-身份证.jpg', '我有很多专业技能证书', '2025-05-13', '321322200210105430');
INSERT INTO `vol_apply` VALUES (20, 173, '待审核', '15541888327', '男', '2001-09-26', 'http://localhost:9090/files/1747488521642-bg1.jpg', 'hjg', '2025-05-17', '211422200109266416');

-- ----------------------------
-- Table structure for volunteer
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人照片',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `birth` date NULL DEFAULT NULL COMMENT '生日',
  `sex` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '介绍',
  `card_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证照片',
  `card_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色',
  `free` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否空闲',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 179 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '救助员信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of volunteer
-- ----------------------------
INSERT INTO `volunteer` VALUES (176, 'user', '123', 'USER', 'http://localhost:9090/files/1742381416424-陈2.jpg', '17751071572', '2000-03-21', '男', '我叫 白夜，男25岁。怀揣着对救助工作的满腔热忱，我渴望成为一名救助员，为困境中的人们伸出援手。\n我拥有扎实的急救知识储备，通过专业培训考取了相关急救证书，熟练掌握心肺复苏、止血包扎、骨折固定等技能。并且定期参加复训与模拟演练，确保技能始终在线。\n实践方面，我积极投身志愿者活动。参与社区急救知识普及，让更多人掌握急救方法；投身自然灾害救援，协助救助伤员、分发物资。这些经历不仅提升了实操能力，更让我学会在复杂状况下冷静应对。', 'http://localhost:9090/files/1742992873684-身份证.jpg', '321322200210105430', 'VOLUNTEER', '是');
INSERT INTO `volunteer` VALUES (177, 'wh', '123', 'WUHUHAHA', 'http://localhost:9090/files/1747100058662-baiye.png', '17709890575', '2000-04-10', '男', '好高大上', 'http://localhost:9090/files/1743651371304-back3.jpg', '321322200210105430', 'VOLUNTEER', '是');
INSERT INTO `volunteer` VALUES (178, 'hbb', '123', 'hbb', 'http://localhost:9090/files/1742387195451-back5.png', '17751071572', '2002-10-10', '男', '我有很多专业技能证书', 'http://localhost:9090/files/1747097896640-身份证.jpg', '321322200210105430', 'VOLUNTEER', '是');

SET FOREIGN_KEY_CHECKS = 1;
