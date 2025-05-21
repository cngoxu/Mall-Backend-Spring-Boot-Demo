/*
 Navicat Premium Data Transfer

 Source Server         : sqlpub_mall
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : mysql.sqlpub.com:3306
 Source Schema         : mall_lib

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 21/05/2025 16:17:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_balance
-- ----------------------------
DROP TABLE IF EXISTS `t_balance`;
CREATE TABLE `t_balance`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `balance` decimal(10, 2) NOT NULL,
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_balance_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_balance
-- ----------------------------

-- ----------------------------
-- Table structure for t_balance_log
-- ----------------------------
DROP TABLE IF EXISTS `t_balance_log`;
CREATE TABLE `t_balance_log`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `amount` decimal(10, 2) NOT NULL,
  `balance_after` decimal(10, 2) NOT NULL,
  `type` enum('recharge','withdraw','consume','refund') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `serial_no` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `order_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `t_balance_log_ibfk_2`(`order_id`) USING BTREE,
  CONSTRAINT `t_balance_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_balance_log_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `t_order` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_balance_log
-- ----------------------------

-- ----------------------------
-- Table structure for t_cart
-- ----------------------------
DROP TABLE IF EXISTS `t_cart`;
CREATE TABLE `t_cart`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `product_id` int(0) NOT NULL,
  `quantity` int(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id`, `product_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `t_product` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_cart
-- ----------------------------

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `order_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `user_id` int(0) NOT NULL,
  `product_id` int(0) NULL DEFAULT NULL,
  `quantity` int(0) NOT NULL,
  `original_price` decimal(10, 2) NOT NULL,
  `promotion_price` decimal(10, 2) NULL DEFAULT NULL,
  `total_price` decimal(10, 2) NOT NULL,
  `status` enum('pending','paid','shipped','received','completed','cancelled','refunded') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'pending',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `t_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_order_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `t_product` (`product_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES ('OD202505181302448571', 16, 47, 2, 799.00, NULL, 1598.00, 'pending', '2025-05-18 21:02:44', '2025-05-18 21:02:44');

-- ----------------------------
-- Table structure for t_product
-- ----------------------------
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product`  (
  `product_id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `images` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `sales` int(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`product_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE COMMENT '加快针对用户商品的搜索',
  CONSTRAINT `t_product_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_product
-- ----------------------------
INSERT INTO `t_product` VALUES (42, 16, '小米空气净化器4 Pro', '高效过滤PM2.5，CADR 500m³/h，智能APP控制', 1299.00, 'https://pic.rmb.bdstatic.com/bjh/beautify/5fc96ea88353fb8b66b301d800d26b09.jpeg@c_1,w_720,h_953,x_0,y_0?for=bg', 'active', '2025-05-18 20:43:10', 0);
INSERT INTO `t_product` VALUES (43, 16, 'Keep瑜伽垫', '加厚防滑，TPE材质，183cm*61cm', 159.00, 'https://img14.360buyimg.com/pop/jfs/t1/50507/23/31096/112307/6510f8c2F745890fe/82b7fd33e6a95b14.jpg', 'active', '2025-05-18 20:47:21', 0);
INSERT INTO `t_product` VALUES (44, 16, '乐高经典创意积木', '乐高积木， 1500颗粒，多色组合，激发创造力', 199.00, 'https://img2.baidu.com/it/u=3643176956,1090293795&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', 'active', '2025-05-18 20:49:24', 0);
INSERT INTO `t_product` VALUES (45, 16, '华为Watch GT4', '1.43英寸AMOLED屏幕，两周续航，100+运动模式', 1499.00, 'https://gips2.baidu.com/it/u=3178729325,1727756381&fm=3074&app=3074&f=JPEG?w=1920&h=2530&type=normal&func=', 'active', '2025-05-18 20:51:20', 0);
INSERT INTO `t_product` VALUES (46, 16, '飞利浦电动牙刷HX6730', '3种清洁模式，31000次/分钟声波震动', 399.00, 'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%276I%2By5Yip5pmu55S15Yqo54mZ5Yi3XzE3Mjg3MjIzMDguNzIwNjY2NA%3D%3D%27/0.png', 'active', '2025-05-18 20:54:18', 0);
INSERT INTO `t_product` VALUES (47, 16, 'Nike Air Force 1运动鞋', '耐克鞋，经典小白鞋，全粒面皮革，缓震中底', 799.00, 'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%27YWlyZm9yY2Ux5o6o6I2QXzE3MjkwNjI4MzcuNDI4ODgyOA%3D%3D%27/0.png', 'active', '2025-05-18 20:56:46', 4);

-- ----------------------------
-- Table structure for t_product_stock
-- ----------------------------
DROP TABLE IF EXISTS `t_product_stock`;
CREATE TABLE `t_product_stock`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `product_id` int(0) NOT NULL,
  `stock` int(0) NOT NULL,
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `t_product_stock_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `t_product` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_product_stock
-- ----------------------------
INSERT INTO `t_product_stock` VALUES (23, 42, 30, '2025-05-18 20:43:11');
INSERT INTO `t_product_stock` VALUES (24, 43, 60, '2025-05-18 20:47:21');
INSERT INTO `t_product_stock` VALUES (25, 44, 35, '2025-05-18 20:49:24');
INSERT INTO `t_product_stock` VALUES (26, 45, 25, '2025-05-18 20:51:20');
INSERT INTO `t_product_stock` VALUES (27, 46, 40, '2025-05-18 20:54:18');
INSERT INTO `t_product_stock` VALUES (28, 47, 16, '2025-05-18 20:56:46');

-- ----------------------------
-- Table structure for t_product_stock_log
-- ----------------------------
DROP TABLE IF EXISTS `t_product_stock_log`;
CREATE TABLE `t_product_stock_log`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `product_id` int(0) NOT NULL,
  `amount` int(0) NOT NULL,
  `stock_after` int(0) NOT NULL,
  `type` enum('in','out','set') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `serial_no` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `order_id` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `remark` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  INDEX `t_product_stock_log_ibfk_2`(`order_id`) USING BTREE,
  CONSTRAINT `t_product_stock_log_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `t_product` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `t_product_stock_log_ibfk_2` FOREIGN KEY (`order_id`) REFERENCES `t_order` (`order_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_product_stock_log
-- ----------------------------
INSERT INTO `t_product_stock_log` VALUES (30, 42, 30, 30, 'set', 'ST202505181243113248', NULL, '商品小米空气净化器4 Pro初始库存', '2025-05-18 20:43:11');
INSERT INTO `t_product_stock_log` VALUES (31, 43, 60, 60, 'set', 'ST202505181247219049', NULL, '商品Keep瑜伽垫初始库存', '2025-05-18 20:47:22');
INSERT INTO `t_product_stock_log` VALUES (32, 44, 35, 35, 'set', 'ST202505181249240955', NULL, '商品乐高经典创意积木初始库存', '2025-05-18 20:49:25');
INSERT INTO `t_product_stock_log` VALUES (33, 45, 25, 25, 'set', 'ST202505181251200083', NULL, '商品华为Watch GT4初始库存', '2025-05-18 20:51:20');
INSERT INTO `t_product_stock_log` VALUES (34, 46, 40, 40, 'set', 'ST202505181254185067', NULL, '商品飞利浦电动牙刷HX6730初始库存', '2025-05-18 20:54:18');
INSERT INTO `t_product_stock_log` VALUES (35, 47, 20, 20, 'set', 'ST202505181256469323', NULL, '商品Nike Air Force 1运动鞋初始库存', '2025-05-18 20:56:46');
INSERT INTO `t_product_stock_log` VALUES (36, 47, -2, 18, 'out', 'ST202505181303007035', 'OD202505181302448571', '用户购买2件商品', '2025-05-18 21:03:00');
INSERT INTO `t_product_stock_log` VALUES (37, 47, -2, 16, 'out', 'ST202505182001201290', 'OD202505181302448571', '用户购买2件商品', '2025-05-19 04:01:20');

-- ----------------------------
-- Table structure for t_promotion
-- ----------------------------
DROP TABLE IF EXISTS `t_promotion`;
CREATE TABLE `t_promotion`  (
  `promotion_id` int(0) NOT NULL AUTO_INCREMENT,
  `product_id` int(0) NOT NULL,
  `promotion_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `promotion_price` decimal(10, 2) NOT NULL,
  `start_time` timestamp(0) NOT NULL,
  `end_time` timestamp(0) NOT NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT 'active',
  PRIMARY KEY (`promotion_id`) USING BTREE,
  INDEX `product_id`(`product_id`) USING BTREE,
  CONSTRAINT `t_promotion_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `t_product` (`product_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_promotion
-- ----------------------------

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `phone` varchar(15) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `age` int(0) NOT NULL,
  `gender` enum('male','female','other') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'other',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `status` enum('active','inactive') CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE,
  UNIQUE INDEX `phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (16, 'cngo', 'me@cngo.xyz', '13912345678', 21, 'male', 'https://tse2-mm.cn.bing.net/th/id/OIP-C.7GLMYPqMlt2LgkbPsOnDIAAAAA?cb=iwp2&rs=1&pid=ImgDetMain', 'active', '2025-05-12 20:18:02');

-- ----------------------------
-- Table structure for t_user_password
-- ----------------------------
DROP TABLE IF EXISTS `t_user_password`;
CREATE TABLE `t_user_password`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `t_user_password_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user_password
-- ----------------------------
INSERT INTO `t_user_password` VALUES (8, 16, '$2a$10$0vm6U9zcGE6zKkf5KEo06eG89O7VOJDRmt7CJ0NDkM327z0WL4y6q');

SET FOREIGN_KEY_CHECKS = 1;
