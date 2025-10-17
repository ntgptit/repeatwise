# UC-010: View Folder Statistics

## 1. Use Case Information

| Attribute | Value |
|-----------|-------|
| **Use Case ID** | UC-010 |
| **Use Case Name** | View Folder Statistics |
| **Primary Actor** | User (Learner) |
| **Secondary Actors** | Folder Statistics Service, Background Calculation Service |
| **Priority** | Medium (P1) |
| **Complexity** | High |
| **Status** | MVP |

## 2. Brief Description

User views comprehensive statistics for a folder including recursive counts of all sub-folders, decks, and cards. System calculates statistics using Visitor pattern traversal and caches results for performance. Statistics include total cards, due cards, new cards, mature cards, and box distribution.

## 3. Preconditions

- User is logged in
- User has at least one folder with content
- Folder exists and is not deleted
- User owns the folder

## 4. Postconditions

**Success**:
- Folder statistics displayed to user
- Statistics cached in folder_stats table
- Cache timestamp updated (last_computed_at)
- Statistics include all descendants recursively

**Failure**:
- Error message displayed
- Cached statistics shown (if available)
- User remains on folder view

## 5. Main Success Scenario

### Step 1: Navigate to Folder
**Actor**: User clicks on folder "English Learning" in tree view

**System**:
- Displays folder contents (sub-folders and decks)
- Shows breadcrumb: "Home > English Learning"
- Displays folder metadata (name, description, created date)

### Step 2: Request Statistics
**Actor**: User clicks "Statistics" button or "Stats" icon on folder

**System**:
1. Checks folder_stats cache
2. If cache valid (< 5 minutes old): Returns cached statistics immediately
3. If cache stale or missing: Triggers statistics calculation

**End Use Case**

---

**Version**: 1.0
**Last Updated**: 2025-01
**Author**: Product Team
