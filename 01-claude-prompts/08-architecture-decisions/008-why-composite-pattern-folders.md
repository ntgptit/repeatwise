# ADR – Why Composite Pattern for Folders

**Decision**: Represent folder hierarchy using Composite pattern.

**Rationale**
- Allows uniform handling of folders and decks as tree nodes.
- Simplifies traversal for statistics (Visitor pattern) and operations like copy/move.
- Matches materialized path storage for quick reads.

**Alternatives**: Ad-hoc recursion, adjacency lists without abstraction.

**Claude tips**
- Encourage Claude to implement `FolderTreeNode` abstractions instead of manual recursion everywhere.
