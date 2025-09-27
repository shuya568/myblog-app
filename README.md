# ブログ執筆アプリ 設計方針・進め方まとめ

## 全体方針

* **Markdownを保存形式**とし、表示時にHTMLへ変換＆サニタイズ。
* **Spring Boot (REST API) + Next.js (フロント)** の構成。
* 認証は **Spring Security (JWT/Bearer)** を採用。
* **Cloud SQL (PostgreSQL)** をDBとし、Cloud RunにAPI・Webを分離デプロイ。
* **DevContainer** と docker-compose で開発環境を統一。
* 画像は **Cloud Storage** に保存、署名付きURLでアップロード。
* エディタは **Milkdown** を利用。

---

## アーキテクチャ概要

```
[Next.js (Cloud Run:web)]
  ├─ 公開: /blog/[slug] (SSG/ISR)
  ├─ ダッシュボード: /dashboard/* (CSR/認証必須)
  └─ API呼び出し: Authorization: Bearer <JWT>

[Spring Boot (Cloud Run:api)]
  ├─ Auth: /api/auth/login /refresh /me
  ├─ Posts: /api/posts /posts/{id}
  ├─ Media: /api/media/signed-url (GCS)
  └─ Cloud SQL(PostgreSQL)

[Cloud SQL(Postgres)]  [Cloud Storage]
```

---

## データモデル（基本）

* **users**: id, email, password_hash, display_name, role, created_at
* **posts**: id, author_id, title, slug, status(DRAFT/PUBLISHED), markdown, html_cached, excerpt, published_at
* **tags**: id, name, slug
* **post_tags**: post_id, tag_id
* **media**: id, owner_id, gcs_path, mime, created_at

> Markdownで保存し、HTMLはキャッシュ列を持たせると表示高速化できる。

---

## API設計（主要）

### Auth

* `POST /api/auth/login` → JWT発行 (access 15m, refresh 14d)
* `POST /api/auth/refresh`
* `GET /api/auth/me`

### Posts

* `GET /api/posts` (一覧, statusで絞込)
* `GET /api/posts/{id or slug}`
* `POST /api/posts` (AUTHOR以上)
* `PUT /api/posts/{id}`
* `POST /api/posts/{id}/publish`

### Media

* `POST /api/media/signed-url` → GCS署名URL発行

---

## フロント (Next.js)

* App Router利用。
* **公開ページ**: SSG/ISRで配信、SEO対応。
* **ダッシュボード**: CSR + fetchでAPI呼び出し。
* **Milkdown**: Markdown編集、画像アップロードは署名URLを介してGCSに直接PUT。
* **Markdown表示**: SSRで変換 or サーバのhtml_cachedを使用。

---

## バックエンド (Spring Boot)

* Spring Web / Spring Security / Spring JPA / Lombok。
* Cloud SQL接続は **Socket Factory** 利用。
* FlywayでDBマイグレーション。
* Markdown→HTML変換は **flexmark-java** などを利用可能。
* OWASP Java HTML Sanitizer でサニタイズ。

---

## 開発環境 (DevContainer)

* devcontainer.jsonでJava, Nodeをインストール。
* docker-composeでPostgres, pgAdmin起動。
* VSCode拡張：Java, Gradle, ESLint, Prettier。
* `.env`で環境変数管理。

---

## デプロイ (Cloud Run)

### 共通

* api/webをCloud Runにデプロイ。
* Cloud SQLはPrivate IP + VPC接続。
* ストレージはCloud Storage、バケット分離(dev/prod)。

### api (Java)

* JibやBuildpacksでコンテナ化。
* メモリ512MB〜から調整。

### web (Next.js)

* SSRありなのでCloud Run。
* `NEXT_PUBLIC_API_BASE_URL`を環境変数で指定。

---

## マイルストーン

1. **MVP**: ローカルで記事CRUD + Markdown保存 + Milkdown導入。
2. **認証**: JWTによるログイン、権限管理。
3. **画像**: GCS署名URLアップロード実装。
4. **機能拡張**: タグ・検索・スラッグ対応。
5. **本番デプロイ**: Cloud Run + Cloud SQL + GCS。

---

## 技術選定のポイント

* MilkdownはMarkdown統一運用で最大活用。
* JWT方式はCORS問題を回避しやすい。
* HTMLサニタイズは保存時に行うと楽。
* 公開記事はSSG/ISRで高速化、ダッシュボードはCSRで柔軟に。

