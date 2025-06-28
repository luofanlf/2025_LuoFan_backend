#!/bin/bash

# 手动部署脚本 - AWS EC2
# 用法: ./deploy.sh [EC2_IP] [SSH_KEY_PATH]

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 变量设置
EC2_IP=${1:-"47.129.211.124"}
SSH_KEY=${2:-"~/.ssh/your-key.pem"}
EC2_USER="ubuntu"
CONTAINER_NAME="tech-challenge-backend"
IMAGE_NAME="tech-challenge-backend"

echo -e "${YELLOW}🚀 开始部署到 AWS EC2: $EC2_IP${NC}"

# 检查SSH密钥文件
if [ ! -f "$SSH_KEY" ]; then
    echo -e "${RED}❌ SSH密钥文件不存在: $SSH_KEY${NC}"
    echo "请提供正确的SSH密钥路径，例如："
    echo "./deploy.sh $EC2_IP ~/.ssh/your-key.pem"
    exit 1
fi

# 检查连接
echo -e "${YELLOW}🔍 检查EC2连接...${NC}"
if ! ssh -i "$SSH_KEY" -o ConnectTimeout=10 -o StrictHostKeyChecking=no "$EC2_USER@$EC2_IP" "echo '连接成功'" > /dev/null 2>&1; then
    echo -e "${RED}❌ 无法连接到EC2实例: $EC2_IP${NC}"
    echo "请检查："
    echo "1. EC2实例是否运行"
    echo "2. SSH密钥是否正确"
    echo "3. 安全组是否允许SSH访问"
    exit 1
fi

echo -e "${GREEN}✅ EC2连接成功${NC}"

# 部署脚本
DEPLOY_SCRIPT=$(cat << 'EOF'
#!/bin/bash
set -e

# 设置变量
REPO_URL="https://github.com/YOUR_USERNAME/tech-challenge.git"
PROJECT_DIR="/home/ubuntu/tech-challenge"
CONTAINER_NAME="tech-challenge-backend"
IMAGE_NAME="tech-challenge-backend"

echo "🔄 更新系统包..."
sudo apt update -y > /dev/null 2>&1

echo "🐳 检查并安装Docker..."
if ! command -v docker &> /dev/null; then
    sudo apt install -y docker.io
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -a -G docker ubuntu
    echo "Docker已安装，请重新登录以生效组权限"
fi

# 确保Docker服务运行
sudo systemctl start docker

echo "🛑 停止现有容器..."
if docker ps -q -f name=$CONTAINER_NAME; then
    docker stop $CONTAINER_NAME
fi

if docker ps -aq -f name=$CONTAINER_NAME; then
    docker rm $CONTAINER_NAME
fi

echo "🗑️ 清理旧镜像..."
if docker images -q $IMAGE_NAME; then
    docker rmi $IMAGE_NAME
fi

echo "📁 克隆最新代码..."
rm -rf $PROJECT_DIR
git clone $REPO_URL $PROJECT_DIR
cd $PROJECT_DIR

echo "🏗️ 构建Docker镜像..."
docker build -t $IMAGE_NAME .

echo "🚀 启动新容器..."
docker run -d \
  --name $CONTAINER_NAME \
  --restart unless-stopped \
  -p 80:8080 \
  -p 8081:8081 \
  $IMAGE_NAME

echo "⏳ 等待服务启动..."
sleep 30

echo "🔍 执行健康检查..."
if curl -f http://localhost:8081/healthcheck > /dev/null 2>&1; then
    echo "✅ 健康检查通过"
else
    echo "⚠️ 健康检查失败，服务可能还未就绪"
fi

echo "🧪 测试API..."
API_RESPONSE=$(curl -s -X POST http://localhost:80/api/coins \
  -H "Content-Type: application/json" \
  -d '{"totalAmount": 11.00, "denominations": [500, 200, 100, 50, 20, 10, 5, 2, 1]}')

if [[ $API_RESPONSE == *"totalAmount"* ]]; then
    echo "✅ API测试通过"
    echo "响应: $API_RESPONSE"
else
    echo "❌ API测试失败"
    echo "响应: $API_RESPONSE"
    exit 1
fi

echo "📊 容器状态:"
docker ps --filter name=$CONTAINER_NAME

echo "🧹 清理未使用的镜像..."
docker system prune -f

echo "🎉 部署完成！"
echo "应用访问地址: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)/api/coins"
EOF
)

# 执行远程部署
echo -e "${YELLOW}📦 执行远程部署...${NC}"
ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$EC2_USER@$EC2_IP" "$DEPLOY_SCRIPT"

# 验证部署
echo -e "${YELLOW}🔍 验证部署结果...${NC}"
sleep 5

# 测试API
echo -e "${YELLOW}🧪 测试API端点...${NC}"
API_RESPONSE=$(curl -s -X POST "http://$EC2_IP/api/coins" \
  -H "Content-Type: application/json" \
  -d '{"totalAmount": 11.00, "denominations": [500, 200, 100, 50, 20, 10, 5, 2, 1]}' \
  || echo "API调用失败")

if [[ $API_RESPONSE == *"totalAmount"* ]]; then
    echo -e "${GREEN}✅ API测试成功${NC}"
    echo -e "${GREEN}响应: $API_RESPONSE${NC}"
else
    echo -e "${RED}❌ API测试失败${NC}"
    echo -e "${RED}响应: $API_RESPONSE${NC}"
fi

echo -e "${GREEN}🎉 部署流程完成！${NC}"
echo -e "${GREEN}应用访问地址: http://$EC2_IP/api/coins${NC}"
echo -e "${GREEN}健康检查地址: http://$EC2_IP:8081/healthcheck${NC}"

echo -e "${YELLOW}💡 提示：${NC}"
echo "- 查看容器日志: ssh -i $SSH_KEY $EC2_USER@$EC2_IP 'docker logs tech-challenge-backend'"
echo "- 查看容器状态: ssh -i $SSH_KEY $EC2_USER@$EC2_IP 'docker ps'" 