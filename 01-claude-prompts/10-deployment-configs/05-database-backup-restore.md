# Database Backup & Restore

**Sources**: `DOCKER-QUICKSTART.md` (backup section), NFRs.

## Backup
- Nightly `pg_dump` to S3-compatible storage; retain 7 days.
- Include schema + data, exclude large audit tables if added later.

## Restore
- Provision fresh database, run `pg_restore`; ensure app services stopped.
- After restore, invalidate refresh tokens (security) and rebuild stats caches.

## Claude tips
- When updating scripts, remind Claude to parameterise credentials and include checksum verification.
