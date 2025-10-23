# Troubleshooting Quick Tips

**Sources**: Various prompts.

- **Auth 401**: Check refresh cookie rotation, JWT secret mismatch.
- **Folder stats stale**: Run cache invalidation job; verify `folder_stats.last_calculated_at` updates.
- **Import stuck**: Inspect job table status; ensure async executor running.
- **Review queue empty**: Confirm due cards exist and limits not exceeded; timezone header.
- **Frontend CORS**: Verify API base URL and proxy settings.

Use when debugging before deep-diving into docs.
