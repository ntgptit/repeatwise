# Environment Variables (Claude Prompt)

**Sources**
- `.env.example` files
- Root docs (Docker setup)

## Core variables
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`, `JWT_ACCESS_TOKEN_TTL_MINUTES=15`, `JWT_REFRESH_TOKEN_TTL_DAYS=7`
- `FRONTEND_API_BASE_URL`, `VITE_API_URL`
- `EXPO_PUBLIC_API_URL`, `EXPO_PUBLIC_PUSH_KEY`
- `TZ` for backend containers (default `Asia/Ho_Chi_Minh`)

## Claude tips
- Remind Claude to document new env vars in this list and in `.env.example` to keep dev/prod in sync.
