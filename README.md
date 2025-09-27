# Blog App

練習用のブログ執筆アプリです。  
バックエンドは Spring Boot、フロントエンドは Next.js を使用しています。  
データベースは PostgreSQL (docker-compose) で動作します。

## セットアップ

### 前提条件

- Docker / Docker Compose
- VSCode (DevContainer 推奨)

### 開発環境の起動

```bash
# DB 起動
docker compose up -d

# バックエンド
cd backend
./gradlew bootRun

# フロントエンド
cd frontend
npm install
npm run dev
```

## アクセス

- フロントエンド: [http://localhost:3000](http://localhost:3000)
- バックエンド API: [http://localhost:8080](http://localhost:8080)
