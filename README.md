# ACMer's Toolkit

ACM 竞赛综合工具箱 — 为 ACM/ICPC 竞赛选手打造的一站式学习辅助工具。

## 功能一览

### 模板管理
- 收集、整理算法模板（动态规划、贪心、图论、数据结构等 8 大分类）
- 支持分类筛选、关键词搜索、一键复制代码
- 模板详情查看与在线编辑

### 题库管理
- 记录值得收藏的题目，支持按难度筛选和搜索
- 通过网址添加题目，自动识别来源（Codeforces / LeetCode / 洛谷 / HDU / POJ / AtCoder）
- 题目描述、题解代码、备注分区管理

### 代码运行器
- 支持 **C (GCC)**、**C++ (G++)**、**Java**、**Python 3** 四种语言
- 在线编译运行，支持标准输入
- 10 秒超时保护，中英文完整支持

### 训练计时器
- 番茄钟模式：训练 + 休息循环
- 预设方案：番茄钟（25分钟）、短休息（5分钟）、标准赛制（5小时）、自定义
- 支持暂停 / 继续 / 停止，训练记录自动保存

### 训练难度控制
- 三级难度：简单（15分钟训练）、中等（25分钟）、困难（50分钟）
- 可自定义训练时长、休息时长、最大轮数
- 设置持久化保存到数据库

### 背景音乐
- 播放 WAV 格式背景音乐，支持多首切换
- 播放 / 暂停 / 停止控制，音量调节
- 循环播放，适合长时间编码

### 学习统计
- 模板分类统计 + 难度分布图表
- 训练历史记录 + 累计时长统计
- 文本柱状图可视化

## 技术栈

| 技术 | 说明 |
|------|------|
| Java Swing | GUI 界面（Eclipse + WindowBuilder） |
| SQLite | 轻量级本地数据库 |
| JDBC | 数据库访问（PreparedStatement + try-with-resources） |
| 多线程 | 计时器、代码运行、音乐播放均使用独立线程 |
| ProcessBuilder | 调用外部编译器/解释器执行代码 |
| Java Sound API | WAV 音频播放 |
| HttpURLConnection | 网络请求获取题目页面 |

## 项目结构

```
ACMer's Toolkit/
├── src/com/app/
│   ├── Main.java                    # 程序入口，数据库初始化
│   ├── entity/                      # 实体类
│   │   ├── User.java                # 用户
│   │   ├── Template.java            # 算法模板
│   │   ├── Problem.java             # 题目
│   │   └── TrainingRecord.java      # 训练记录
│   ├── dao/                         # 数据访问层
│   │   ├── UserDAO.java             # 用户 CRUD + SHA-256 加密
│   │   ├── TemplateDAO.java         # 模板 CRUD
│   │   ├── ProblemDAO.java          # 题目 CRUD
│   │   ├── TrainingRecordDAO.java   # 训练记录 CRUD
│   │   └── SettingsDAO.java         # 设置读写
│   ├── ui/                          # 界面层
│   │   ├── MainFrame.java           # 主窗口（菜单 + 标签页）
│   │   ├── LoginDialog.java         # 登录对话框
│   │   ├── RegisterDialog.java      # 注册对话框
│   │   ├── AboutDialog.java         # 关于对话框
│   │   ├── TemplatePanel.java       # 模板管理面板
│   │   ├── ProblemPanel.java        # 题库管理面板
│   │   ├── CodeRunnerPanel.java     # 代码运行面板
│   │   ├── StatsPanel.java          # 统计面板
│   │   ├── TimerDialog.java         # 训练计时器
│   │   ├── MusicPlayerPanel.java    # 背景音乐播放
│   │   ├── TrainingSettingsDialog.java  # 训练设置
│   │   └── AddProblemByURLDialog.java   # 网址导入题目
│   └── utils/                       # 工具类
│       ├── DBHelper.java            # 数据库连接
│       └── TimeUtils.java           # 时间格式化
├── lib/                             # 依赖库（需自行下载）
│   ├── sqlite-jdbc-3.46.1.3.jar
│   └── jlayer-1.0.1.jar
├── music/                           # 背景音乐（需自行准备）
├── requirements.txt                 # 依赖说明
└── README.md
```

## 快速开始

1. **导入项目**：Eclipse → File → Import → Existing Projects into Workspace
2. **添加依赖**：下载 `sqlite-jdbc-3.46.1.3.jar` 放入 `lib/` 目录
3. **运行**：右键 `Main.java` → Run As → Java Application
4. **注册账号**：文件 → 注册新用户
5. **开始使用**：登录后即可使用所有功能

## 数据库

首次运行自动创建 `acm_toolkit.db`（SQLite），包含以下表：

| 表名 | 说明 |
|------|------|
| user | 用户账号（SHA-256 密码加密） |
| template | 算法模板 |
| problem | 题目收藏 |
| training_record | 训练记录 |
| settings | 应用设置 |

## 作者

**王鑫** | 学号：244090126 | 计算机1班

## 许可

© 2026 All Rights Reserved
