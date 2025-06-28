# 🚀 快速启动 - GitHub Actions CI/CD

## 准备工作清单

### ✅ 1. 项目文件确认
确保以下文件已存在：
- [x] `.github/workflows/ci-cd.yml` - GitHub Actions工作流
- [x] `Dockerfile` - Docker镜像构建文件
- [x] `pom.xml` - Maven配置（包含单元测试）
- [x] `deploy.sh` - 手动部署脚本（可选）

### ✅ 2. GitHub Secrets 配置

在GitHub仓库中设置以下Secrets：

```
Repository → Settings → Secrets and variables → Actions → New repository secret
```

| Secret 名称 | 值 | 说明 |
|-------------|---|------|
| `EC2_HOST` | `47.129.211.124` | 您的EC2公网IP |
| `EC2_USER` | `ec2-user` | EC2用户名 |
| `EC2_SSH_KEY` | `-----BEGIN RSA PRIVATE KEY-----...` | 完整的SSH私钥内容 |

### ✅ 3. AWS EC2 配置确认

确保您的EC2实例：
- ✅ 运行 Amazon Linux 2
- ✅ 安全组开放端口：22 (SSH), 80 (HTTP), 8081 (健康检查)
- ✅ 可以通过SSH密钥访问

## 🎯 一键部署

### 方式1：GitHub Actions 自动部署
```bash
# 1. 推送代码到main分支
git add .
git commit -m "Setup CI/CD pipeline"
git push origin main

# 2. 在GitHub上查看Actions执行状态
# Repository → Actions → 查看运行状态
```

### 方式2：手动部署（备用）
```bash
# 使用部署脚本
./deploy.sh 47.129.211.124 ~/.ssh/your-key.pem
```

## 🔍 验证部署

部署完成后，测试以下端点：

```bash
# API测试
curl -X POST http://47.129.211.124/api/coins \
  -H "Content-Type: application/json" \
  -d '{"totalAmount": 11.00, "denominations": [500, 200, 100, 50, 20, 10, 5, 2, 1]}'

# 健康检查
curl http://47.129.211.124:8081/healthcheck
```

期望响应：
```json
{"totalAmount":11.0,"solution":{"1":1,"10":1},"totalCoins":2}
```

## 📊 监控和日志

### GitHub Actions 日志
- 访问：`Repository → Actions → 选择运行记录`
- 查看：测试结果、部署状态、错误信息

### EC2 容器日志
```bash
# SSH到EC2查看
ssh -i your-key.pem ec2-user@47.129.211.124

# 查看容器状态
docker ps

# 查看应用日志
docker logs tech-challenge-backend

# 查看容器资源使用
docker stats tech-challenge-backend
```

## 🔄 工作流程

```
代码推送 → 自动测试 → 测试通过 → 自动部署 → 健康检查 → 部署完成
    ↓           ↓           ↓           ↓           ↓           ↓
  main分支   单元测试    ✅ 通过    SSH到EC2   API测试    服务可用
                        ❌ 失败       ↓
                       停止部署   Docker构建
```

## 🛠️ 故障排除

### 常见问题快速解决

1. **SSH连接失败**
   ```bash
   # 检查密钥权限
   chmod 600 ~/.ssh/your-key.pem
   
   # 测试连接
   ssh -i ~/.ssh/your-key.pem ec2-user@47.129.211.124
   ```

2. **Docker构建失败**
   ```bash
   # SSH到EC2检查空间
   df -h
   
   # 清理Docker
   docker system prune -a
   ```

3. **端口无法访问**
   ```bash
   # 检查安全组规则
   # AWS Console → EC2 → Security Groups → 检查入站规则
   ```

4. **服务启动失败**
   ```bash
   # 查看详细日志
   docker logs tech-challenge-backend --tail 50
   ```

## 📝 提交信息模板

```bash
# 触发完整CI/CD流程的提交信息
git commit -m "feat: add new feature

- 添加新功能
- 更新测试用例
- 更新文档

Triggers: full deployment"
```

## 🎉 成功标志

看到以下信息表示部署成功：

1. ✅ GitHub Actions 显示绿色勾号
2. ✅ API 返回正确 JSON 响应
3. ✅ 健康检查端点正常
4. ✅ EC2 容器状态为 "Up"

---

**🚀 现在您的项目已具备完整的 CI/CD 能力！每次推送到 main 分支都会自动测试和部署。** 