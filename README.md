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
- Redis
- Apache POI (Excel export)
- Logback (daily-rotating file logs)
- JavaMailSender (SMTP email)
- Maven

**Frontend**
- Angular (standalone components)
- Tailwind CSS
- ngx-translate (i18n)
- RxJS

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

The project is deployed at **[tylerdao.site](https://tylerdao.site)**.

A pre-created root account for local environment is available to explore the full feature set,
including the admin dashboard and management tools:

| | |
|---|---|
| Username | `root` |
| Password | `root_password` |

> This is a demo account for evaluation purposes only.

## Running with Docker

### 1. Create your own `.env` file

**Yes — every environment (your local machine, a teammate's machine, a
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

### Useful commands

```bash
# View logs
docker compose logs backend -f

# Open a MySQL shell
docker compose exec db mysql -u root -p

# Check container status
docker compose ps
```

## License

Internal project — not licensed for external use.
