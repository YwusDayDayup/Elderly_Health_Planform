
CREATE TABLE IF NOT EXISTS t_user (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(64) NULL,
  avatar_url VARCHAR(255) NULL,
  email VARCHAR(128) NULL,
  phone VARCHAR(32) NULL,
  gender TINYINT NOT NULL DEFAULT 0,
  birthday DATE NULL,
  bio VARCHAR(255) NULL,
  signature VARCHAR(255) NULL,
  region VARCHAR(64) NULL,
  city VARCHAR(64) NULL,
  status TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_username (username),
  KEY idx_user_email (email),
  KEY idx_user_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_user_role (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role VARCHAR(32) NOT NULL,
  create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_role_user_id (user_id),
  KEY idx_user_role_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_file (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  biz_type VARCHAR(32) NULL,
  original_name VARCHAR(255) NOT NULL,
  stored_name VARCHAR(255) NOT NULL,
  content_type VARCHAR(128) NULL,
  size BIGINT NOT NULL,
  file_path VARCHAR(512) NOT NULL,
  file_url VARCHAR(512) NOT NULL,
  create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_file_user_id (user_id),
  KEY idx_file_biz_type (biz_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_msg (
  id BIGINT NOT NULL AUTO_INCREMENT,
  sender_id BIGINT NULL,
  receiver_id BIGINT NULL,
  msg_type VARCHAR(32) NOT NULL,
  title VARCHAR(128) NOT NULL,
  content TEXT NOT NULL,
  biz_id VARCHAR(64) NULL,
  read_flag TINYINT NOT NULL DEFAULT 0,
  read_time DATETIME(3) NULL,
  create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_msg_receiver_read (receiver_id, read_flag),
  KEY idx_msg_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_op_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NULL,
  username VARCHAR(64) NULL,
  trace_id VARCHAR(64) NULL,
  action VARCHAR(64) NOT NULL,
  method VARCHAR(255) NULL,
  path VARCHAR(255) NULL,
  req_json TEXT NULL,
  resp_json TEXT NULL,
  success TINYINT NOT NULL DEFAULT 1,
  error_msg VARCHAR(512) NULL,
  cost_ms BIGINT NOT NULL DEFAULT 0,
  create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  KEY idx_op_log_user_id (user_id),
  KEY idx_op_log_action (action),
  KEY idx_op_log_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
