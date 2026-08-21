# Library Management System

A full-stack library management platform built as a co-op software development
internship project at Viettel Software. It covers the full lending workflow —
cataloging, borrowing, reader discussions, and admin tools — with a
role-based permission system and a trilingual interface.

## Features

- **Catalog & search** — browse books by title, author, or genre, with cover
  images and live copy counts.
- **Borrowing & returns** — borrow books, track due dates, and receive
  automatic email reminders before and after a book becomes overdue.
- **Reader discussions** — post about books and comment on other readers'
  posts.
- **Role-based access control** — a permission ("feature") system where each
  role is granted a specific set of actions (create, read, update, delete,
  export, etc.) per resource.
- **Security** — JWT-based authentication, BCrypt password hashing, and
  Cloudflare Turnstile CAPTCHA protection on login and registration.
- **Admin dashboard** — manage books, borrows, users, roles, and site-wide
  announcements from one place.
- **Data export** — export users, books, borrows, or system logs to Excel,
  with a configurable date range for logs.
- **Multilingual UI** — the full interface is available in Vietnamese,
  English, and French.
- **Email notifications** — account verification, password reset, and
  borrow-related emails, sent as multilingual HTML templates.

## Tech stack

**Backend**
- Java 21, Spring Boot
- Spring Security + JWT authentication
- Spring Data JPA / Hibernate
- MySQL 8
- Redis cache
- Apache POI (Excel export)
- Logback (daily-rotating file logs)
- JavaMailSender (SMTP email)
- i18n for translation

**Frontend**
- Angular (standalone components)
- Tailwind CSS
- i18n for translation

**Infrastructure**
- Docker & Docker Compose
- Nginx (serves the frontend and reverse-proxies `/api` to the backend)
- Docker Hub (image distribution)

## Project structure

```
.
├── be/                       # Spring Boot backend
│   ├── Dockerfile.be
│   ├── docker-entrypoint.sh
│   └── src/
├── fe/                       # Angular frontend
│   ├── Dockerfile.fe
│   ├── nginx.conf
│   └── src/
├── docker-compose.yaml       # Local development (builds images from source)
└── .env.example
```

## Live demo
> **Recommendation:** For the best experience, visit **[tylerdao.site](https://tylerdao.site)**
> and sign up with your **real email address**. Email features (account
> verification, password reset, and borrow reminders) will work out of the
> box — no configuration needed. If you run the project locally with Docker
> Compose, you will need to supply your own Gmail app password and SMTP
> credentials in the `.env` file, and the pre-created demo accounts will not
> receive emails since it is using a dummy email address.

The project is deployed at **[tylerdao.site](https://tylerdao.site)**.

A pre-created root account is available to explore the full feature set (except email notifications feature),
including the admin dashboard and management tools:

| | |
|---|---|
| Username | `root` |
| Password | `root_password` |

> This is a demo account for evaluation purposes only.

## Running with Docker

### 1. Create your own `.env` file

**Yes - every environment (your local machine, a teammate's machine, a
server) needs its own `.env` file.** It is never committed to the repo, since
it holds real credentials (database password, JWT secret, mail password).

Copy the example file and fill in real values:

```bash
cp .env.example .env
```

| Variable | Description |
|---|---|
| `DB_ROOT_PASSWORD` | MySQL root password |
| `DB_NAME` | Database name (e.g. `library_management`) |
| `DB_USER` | Application database user |
| `DB_PASSWORD` | Application database password |
| `DB_HOST` | `db` (the Docker Compose service name — leave as-is) |
| `JWT_SECRET` | A long, random string (32+ characters) used to sign JWTs |
| `JWT_EXPIRATION` | Token lifetime in milliseconds (e.g. `86400000` = 24h) |
| `MAIL_HOST` / `MAIL_PORT` | SMTP server (e.g. `smtp.gmail.com`, `587`) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP credentials used to send emails |
| `REDIS_HOST` / `REDIS_PORT` | `redis` / `6379` (leave as-is) |
| `FRONTEND_URL` | The frontend's origin, used for CORS (e.g. `http://localhost`) |
| `BACKEND_URL` | The backend's public base URL, used in email links (e.g. `http://localhost/api`) |
| `TURNSTILE_SECRET_KEY` |The secret key for cloudflare's capcha check (currently dummy key that always return true, leave it as-it for demo purpose) |
| `DOCKERHUB_USERNAME` | Only needed for `docker-compose.prod.yaml` |

Generate a random JWT secret:

```bash
openssl rand -base64 48
```

### 2. Run it

```bash
docker compose up --build
```

This builds both images from the `be/` and `fe/` source directories. The app
is available at `http://localhost`.

### Stopping the containers

```bash
docker compose down          # stops containers, keeps data (DB, logs, uploads)
docker compose down -v       # also wipes all volumes — irreversible
```

### Alternative: running the pre-built images

The backend and frontend images are also published on Docker Hub as
[`tylerdao/library-management-be`](https://hub.docker.com/r/tylerdao/library-management-be)
and [`tylerdao/library-management-fe`](https://hub.docker.com/r/tylerdao/library-management-fe),
if you'd rather run them without cloning and building from source.

Pulling the images alone isn't enough to run the app — Docker images only
contain what's baked in at build time; they don't include your `.env`
values. **You still need your own `.env` file** (same as above) and a
compose file that references the images instead of building them:

```yaml
# docker-compose.images.yaml
services:
  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db-data:/var/lib/mysql

  redis:
    image: redis:7-alpine

  backend:
    image: tylerdao/library-management-be:latest
    depends_on:
      - db
      - redis
    environment:
      DB_HOST: db
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}
      MAIL_HOST: ${MAIL_HOST}
      MAIL_PORT: ${MAIL_PORT}
      MAIL_USERNAME: ${MAIL_USERNAME}
      MAIL_PASSWORD: ${MAIL_PASSWORD}
      REDIS_HOST: redis
      REDIS_PORT: 6379
      FRONTEND_URL: ${FRONTEND_URL}
      BACKEND_URL: ${BACKEND_URL}
    ports:
      - "8080:8080"

  frontend:
    image: tylerdao/library-management-fe:latest
    depends_on:
      - backend
    ports:
      - "80:80"

volumes:
  db-data:
```

```bash
docker compose -f docker-compose.images.yaml pull
docker compose -f docker-compose.images.yaml up -d
```

### Useful commands

```bash
# View logs
docker compose logs backend -f

# Open a MySQL shell
docker compose exec db mysql -u root -p

# Check container status
docker compose ps
```

## Available documentation

| Document | Location |
|---|---|
| README | This file |
| API documentation | [tylerdao.site/api/swagger-ui/index.html](https://tylerdao.site/api/swagger-ui/index.html#/) |

The API documentation is interactive — you can authorize with a JWT token using the **Authorize** button and test endpoints directly from the browser.

## License

Internal project — not licensed for external use.
