# EasyMeeting

EasyMeeting 是一套自托管的多人视频会议系统，由 **Electron 桌面客户端** 与 **Spring Boot 服务端** 组成。

它支持多人音视频会议、屏幕共享、会中聊天与文件传输、会议预约、历史记录、联系人管理，以及 Windows 下的本地屏幕录制。媒体流采用 WebRTC 点对点传输，服务端负责信令转发与业务处理，部署简单，适合中小规模会议（几人到十几人）使用，也可以作为 WebRTC 与即时通讯方向的学习与二次开发项目。

## 功能特性

**会议**

- 快速会议、加入会议：支持个人会议号与系统生成两种模式
- 预约会议：设置时间、时长、入会密码，并可批量邀请联系人
- 多人音视频：网格布局随人数自动调整，支持顶部 / 侧边缩略图显示
- 屏幕共享：可选择要共享的显示器，会议中随时开启或切换
- 会控功能：主持人可结束会议、踢出成员、拉黑成员、邀请成员入会
- 历史会议：查看会议列表、参会成员、聊天记录，并支持删除记录

**沟通**

- 会中聊天：文本、表情、图片、视频与各类文件
- 文件传输：上传下载带实时进度，下载完成后可直接打开所在文件夹
- 媒体预览：图片缩放浏览、视频在线播放
- 未读提醒：聊天消息未读角标，打开面板后自动清零

**桌面端**

- 本地屏幕录制：内置 FFmpeg，支持录制屏幕并混入麦克风声音
- 多窗口：会议、媒体预览、历史记录、管理后台均为独立窗口
- 无边框界面与自定义标题栏，关闭后最小化到系统托盘

**账号与管理**

- 账号：注册（图形验证码）、登录、修改密码、更新资料与头像
- 联系人：搜索、申请、同意 / 拒绝 / 拉黑、删除
- 管理后台：用户管理、会议管理、系统设置、客户端版本发布

## 技术栈

| 端 | 技术 |
| --- | --- |
| 桌面客户端 | Electron 34、Vue 3、Pinia、Vue Router、Element Plus、Vite / electron-vite |
| 实时音视频 | 原生 WebRTC（点对点）、desktopCapturer、FFmpeg |
| 服务端 | Spring Boot 2.7、MyBatis、MySQL 8 |
| 实时通信 | Netty WebSocket（信令）、Redis / Redisson、RabbitMQ |
| 其他 | axios、artplayer、v-viewer、moment、electron-builder |

## 目录结构

```
easymeeting/
├── easymeeting-client/          # Electron 桌面客户端
│   ├── assets/                  # 内置 ffmpeg（录制用）
│   ├── resources/               # 应用图标
│   └── src/
│       ├── main/                # 主进程：窗口、IPC、WebSocket、录屏、更新
│       ├── preload/             # 预加载脚本
│       └── renderer/            # 渲染进程（Vue 3）
│           └── src/
│               ├── views/       # 页面：登录、会议、聊天、联系人、管理后台、录屏
│               ├── components/  # 通用组件
│               ├── stores/      # Pinia 状态
│               └── utils/       # 请求封装、接口定义、工具函数
└── easymeeting-java/            # Spring Boot 服务端
    ├── sql/                     # 数据库建表脚本
    └── src/main/
        ├── java/com/easymeeting/
        │   ├── controller/      # HTTP 接口
        │   ├── service/         # 业务逻辑
        │   ├── mappers/         # MyBatis 数据访问
        │   ├── entity/          # 实体、DTO、枚举
        │   ├── websocket/       # Netty WebSocket 信令服务
        │   ├── redis/           # Redis 封装
        │   └── utils/           # 工具类
        └── resources/           # 配置文件与 Mapper XML
```

## 快速开始

### 环境要求

- Node.js 18 及以上
- JDK 8、Maven 3.6 及以上
- MySQL 8.0、Redis
- Windows 10 / 11（屏幕录制依赖 Windows 的采集能力）

### 启动服务端

```bash
cd easymeeting-java

# 导入数据库（默认库名 meeting）
mysql -h127.0.0.1 -P3307 -uroot -p < sql/easymeeting.sql

# 按需修改 src/main/resources/application.properties 中的数据库、Redis 连接信息

# 启动服务：HTTP 6060，WebSocket 6061
mvn spring-boot:run
```

### 启动客户端

```bash
cd easymeeting-client

npm install
npm run dev
```

服务端地址在 `.env.development` / `.env.production` 中配置：

```ini
VITE_DOMAIN="http://localhost:6060"       # 服务端地址
VITE_WS="ws://localhost:6061/ws/?token="  # WebSocket 地址
```

### 打包

```bash
npm run build:win      # Windows 安装包，输出到 installPackages/
npm run build:mac
npm run build:linux
```

## 系统架构

```
┌──────────────────────── 客户端（Electron）────────────────────────┐
│  主进程：窗口管理 · IPC · WebSocket 连接 · 录屏 · 本地存储          │
│  渲染进程：Vue 3 界面 · 音视频采集 · WebRTC 点对点连接              │
└───────────────┬───────────────────────────┬──────────────────────┘
                │ HTTP 6060 /api            │ WebRTC 媒体流（点对点）
                ▼                           ▼
      ┌────────────────────┐        ┌────────────────────┐
      │  Spring Boot 服务端 │        │   其他参会者客户端  │
      │  账号 · 会议 · 聊天  │        └────────────────────┘
      │  联系人 · 预约 · 后台│
      └─────────┬──────────┘
                │
      ┌─────────▼──────────┐        ┌────────────────────┐
      │  Netty WS 信令服务  │        │  MySQL · Redis     │
      │  端口 6061          │        │  数据与缓存         │
      └────────────────────┘        └────────────────────┘
```

会议中的信令（SDP、ICE 候选）通过 WebSocket 由服务端转发，音视频数据则在参会者之间直接传输，不经过服务端。

## 说明

本项目用于技术学习与交流，欢迎提交 Issue 与 Pull Request。
