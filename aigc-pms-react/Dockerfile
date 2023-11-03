# 使用 Node.js 镜像作为基础镜像
FROM node:14-alpine

# 设置工作目录
WORKDIR /app

# 将 package.json 和 package-lock.json 复制到容器中
COPY package*.json ./

# 安装项目依赖（使用淘宝镜像
RUN npm install -g cnpm --registry=https://registry.npm.taobao.org

# 将项目文件复制到容器中
COPY . .

# 构建生产版本
RUN npm run build

# 设置容器的默认命令
CMD ["npm", "start"]
