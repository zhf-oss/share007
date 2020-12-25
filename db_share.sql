/*
 Navicat Premium Data Transfer

 Source Server         : HW
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : 139.9.135.103:3306
 Source Schema         : db_share

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 25/12/2020 14:50:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for arc_type
-- ----------------------------
DROP TABLE IF EXISTS `arc_type`;
CREATE TABLE `arc_type`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of arc_type
-- ----------------------------
INSERT INTO `arc_type` VALUES (20, 'Java技术', 'Java技术相关', 1);
INSERT INTO `arc_type` VALUES (21, '数据库技术', '数据库技术相关', 2);
INSERT INTO `arc_type` VALUES (22, 'Web前端技术', 'Web前端技术相关', 3);
INSERT INTO `arc_type` VALUES (23, 'J2EE技术', 'J2EE技术相关', 4);
INSERT INTO `arc_type` VALUES (24, '分布式微服技术', '分布式微服技术相关', 5);
INSERT INTO `arc_type` VALUES (25, '移动APP开发技术', '移动APP开发技术相关', 6);
INSERT INTO `arc_type` VALUES (26, '微信小程序开发', '微信小程序开发相关', 7);
INSERT INTO `arc_type` VALUES (27, '服务器技术', '服务器技术相关', 8);
INSERT INTO `arc_type` VALUES (28, '人工智能', '人工智能', 9);
INSERT INTO `arc_type` VALUES (29, '数据挖掘', '数据挖掘', 10);
INSERT INTO `arc_type` VALUES (30, '大数据云计算', '大数据云计算', 11);
INSERT INTO `arc_type` VALUES (31, '区块链', '区块链', 12);
INSERT INTO `arc_type` VALUES (32, '机器学习', '机器学习', 13);
INSERT INTO `arc_type` VALUES (33, '算法', '算法', 14);
INSERT INTO `arc_type` VALUES (34, 'Java架构', 'Java架构', 8);
INSERT INTO `arc_type` VALUES (35, '其他', '其他', 15);
INSERT INTO `arc_type` VALUES (36, '软件测试', '软件测试', 14);

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `check_date` datetime(6) NULL DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `download1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_hot` bit(1) NOT NULL,
  `is_useful` bit(1) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `points` int(0) NULL DEFAULT NULL,
  `publish_date` datetime(6) NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `state` int(0) NULL DEFAULT NULL,
  `view` int(0) NULL DEFAULT NULL,
  `arc_type_id` int(0) NULL DEFAULT NULL,
  `user_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `arc_type_id`(`arc_type_id`) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `article_ibfk_2` FOREIGN KEY (`arc_type_id`) REFERENCES `arc_type` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (8, NULL, '测试内容8', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题8', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 120, 20, 1);
INSERT INTO `article` VALUES (9, NULL, '测试内容9', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'1', b'0', '测试标题9', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 103, 20, 1);
INSERT INTO `article` VALUES (10, NULL, '测试内容10', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题10', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 103, 20, 1);
INSERT INTO `article` VALUES (11, NULL, '测试内容11', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'1', b'0', '测试标题11', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 102, 20, 1);
INSERT INTO `article` VALUES (12, NULL, '测试内容12', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'1', b'0', '测试标题12', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 103, 20, 1);
INSERT INTO `article` VALUES (13, NULL, '测试内容13', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题13', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 101, 20, 1);
INSERT INTO `article` VALUES (14, NULL, '测试内容14', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题14', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (15, NULL, '测试内容15', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题15', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (16, NULL, '测试内容16', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题16', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (17, NULL, '测试内容17', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题17', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 103, 20, 1);
INSERT INTO `article` VALUES (18, NULL, '测试内容18', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题18', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (19, NULL, '测试内容19', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题19', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (20, NULL, '测试内容20', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题20', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (21, NULL, '测试内容21', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题21', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (22, NULL, '测试内容22', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题22', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 101, 20, 1);
INSERT INTO `article` VALUES (23, NULL, '测试内容23', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题23', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (24, NULL, '测试内容24', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题24', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 102, 20, 1);
INSERT INTO `article` VALUES (25, NULL, '测试内容25', 'https://pan.baidu.com/s/1nJNnAfdyi4g_72N18DS6hQ', b'0', b'0', '测试标题25', '1234', 10, '2018-09-11 05:55:44.000000', '', 2, 100, 20, 1);
INSERT INTO `article` VALUES (60, NULL, '哈哈哈胜多负少', 'http://zhf.nat123.fun/user/article/toPublishArticlePage', b'1', b'1', '朱某', '2545', 10, '2020-08-26 14:25:16.824000', NULL, 2, 23, 36, 6);
INSERT INTO `article` VALUES (69, NULL, 'sdfsd', 'zdfsd', b'0', b'0', 'haha', '2145', 10, NULL, 'Sdfsd', 1, 10, 23, 6);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `comment_date` datetime(6) NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `state` int(0) NULL DEFAULT NULL,
  `article_id` int(0) NULL DEFAULT NULL,
  `user_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK5yx0uphgjc6ik6hb82kkw501y`(`article_id`) USING BTREE,
  INDEX `FK8kcum44fvpupyw6f5baccx25c`(`user_id`) USING BTREE,
  CONSTRAINT `FK5yx0uphgjc6ik6hb82kkw501y` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK8kcum44fvpupyw6f5baccx25c` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence`  (
  `next_val` bigint(0) NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hibernate_sequence
-- ----------------------------
INSERT INTO `hibernate_sequence` VALUES (76);

-- ----------------------------
-- Table structure for link
-- ----------------------------
DROP TABLE IF EXISTS `link`;
CREATE TABLE `link`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort` int(0) NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of link
-- ----------------------------
INSERT INTO `link` VALUES (1, 'Java1234', 1, 'http://www.java1234.com');
INSERT INTO `link` VALUES (2, 'Java1234博客', 2, 'http://blog.java1234.com');
INSERT INTO `link` VALUES (3, 'VAPTCHA', 3, 'https://www.vaptcha.com');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_see` bit(1) NOT NULL,
  `publish_date` datetime(6) NULL DEFAULT NULL,
  `user_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKb3y6etti1cfougkdr0qiiemgv`(`user_id`) USING BTREE,
  CONSTRAINT `FKb3y6etti1cfougkdr0qiiemgv` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (68, '【审核通过】您发布的【罗某】帖子成功！', b'1', '2020-08-27 13:19:20.901000', 6);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_off` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_vip` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `points` int(0) NULL DEFAULT NULL,
  `register_date` datetime(6) NULL DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_sign` bit(1) NOT NULL,
  `sign_sort` int(0) NULL DEFAULT NULL,
  `sign_time` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '862592396@qq.com', '20200823093715.jpg', '0', '1', 'ef7892b96f93fe4644180359922ad433', 508, '2020-08-19 11:39:34.000000', '管理员', '小杰', b'1', 2, '2020-08-27 14:47:25.452000');
INSERT INTO `user` VALUES (6, '44', '20200821044936.jpg', '0', '0', 'cb35360d64d80bf9210e0cff1ad5079e', 54, '2020-08-20 12:23:52.124000', '会员', 'zhf', b'1', 1, '2020-08-27 14:45:22.846000');
INSERT INTO `user` VALUES (38, '12343', 'default.jpg', '0', '0', 'cb35360d64d80bf9210e0cff1ad5079e', 0, '2020-08-25 08:14:37.561000', '会员', 'hanjie', b'0', NULL, NULL);
INSERT INTO `user` VALUES (72, '123', 'default.jpg', '0', '0', 'cb35360d64d80bf9210e0cff1ad5079e', 0, '2020-08-28 14:05:06.555000', '会员', 'lyh', b'0', NULL, NULL);
INSERT INTO `user` VALUES (73, '33', 'default.jpg', '0', '0', 'cb35360d64d80bf9210e0cff1ad5079e', 0, '2020-08-28 14:07:29.559000', '会员', 'zzh', b'0', NULL, NULL);

-- ----------------------------
-- Table structure for user_download
-- ----------------------------
DROP TABLE IF EXISTS `user_download`;
CREATE TABLE `user_download`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `download_date` datetime(6) NULL DEFAULT NULL,
  `article_id` int(0) NULL DEFAULT NULL,
  `user_id` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKtahufby21ulp09xol3wo020v9`(`article_id`) USING BTREE,
  INDEX `FK6dxnoive5hv5ph9tys69dfhel`(`user_id`) USING BTREE,
  CONSTRAINT `FK6dxnoive5hv5ph9tys69dfhel` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKtahufby21ulp09xol3wo020v9` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_download
-- ----------------------------
INSERT INTO `user_download` VALUES (44, '2020-08-26 07:00:03.060000', 8, 6);

SET FOREIGN_KEY_CHECKS = 1;
