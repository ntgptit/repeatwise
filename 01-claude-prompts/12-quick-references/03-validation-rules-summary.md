# Validation Summary

**Source**: `04-validation-rules/*`.

- Email: required, <=255, lowercase, regex.
- Password: 8–128 chars, no trimming.
- Folder name: required, <=100, unique per parent, depth ≤10.
- Deck limits: new 1–200, review 10–1000.
- Card text: required, <=2000, plain text.
- Reminder time: 05:00–23:00, `HH:mm` format.
- Import file: CSV/XLSX, ≤5 MB, ≤5k rows, stop after 200 errors.

Use this to remind Claude of key constraints before coding.
