# 使用官方 Node.js 镜像作为基础镜像
FROM node:latest as build

# 设置工作目录
WORKDIR /app

# 将 package.json 和 yarn.lock 拷贝到工作目录
COPY package.json yarn.lock ./

# 安装依赖
RUN yarn install

# 拷贝项目文件到工作目录
COPY . .

# 构建 React 项目
RUN yarn build

# 使用 Nginx 镜像作为最终运行环境
FROM nginx:latest

# 将构建好的项目文件拷贝到 Nginx 默认的静态文件目录
COPY --from=build /app/build /usr/share/nginx/html

# 暴露端口，如果需要在容器外部访问，需要将对应端口映射到宿主机
EXPOSE 3000

# 容器启动时运行 Nginx
CMD ["nginx", "-g", "daemon off;"]
