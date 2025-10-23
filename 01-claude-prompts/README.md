# 📚 RepeatWise - Claude Code Knowledge Base

> **Token-Optimized Documentation for AI-Assisted Development**

## 🎯 Purpose

This folder contains **distilled, action-oriented documentation** optimized for Claude Code consumption. Each file is designed to minimize token usage while maximizing code generation accuracy.

---

## 📋 Quick Start

### For Backend Development
```bash
# Essential files to include in context:
01-domains/[specific-domain].md          # Domain specification
03-code-templates/backend/[template].java # Code template
11-coding-standards/01-java-coding-standards.md # Coding rules
```

### For Frontend Development
```bash
# Essential files to include in context:
01-domains/[specific-domain].md          # Domain specification
03-code-templates/frontend-web/[template].tsx # Code template
11-coding-standards/02-typescript-coding-standards.md # Coding rules
```

---

## 📁 Folder Structure

### ⭐ Priority 1: Core Domain Documents
```
01-domains/
├── 01-user-auth-domain.md      # User, authentication, JWT
├── 02-folder-domain.md          # Folder tree (Composite pattern)
├── 03-deck-domain.md            # Deck management
├── 04-card-domain.md            # Flashcard CRUD
├── 05-review-srs-domain.md      # SRS algorithm
├── 06-statistics-domain.md      # Analytics
├── 07-settings-domain.md        # User/Deck settings
└── 08-import-export-domain.md   # CSV/Excel I/O
```

**Usage**: Always start here. Contains complete domain specification.

---

### ⭐ Priority 2: Implementation Guides
```
02-implementation-guides/
├── 00-implementation-roadmap.md     # Dependency order
├── 03-backend-layer-implementation.md # How to implement layers
├── 04-security-jwt-implementation.md  # JWT auth setup
└── 06-frontend-web-implementation.md  # React component patterns
```

**Usage**: Read for overall strategy, then reference specific domain.

---

### ⭐ Priority 3: Code Templates
```
03-code-templates/
├── backend/
│   ├── 02-entity-template.java         # JPA entity structure
│   ├── 03-repository-template.java     # Repository pattern
│   ├── 05-service-impl-template.java   # Service implementation
│   └── 06-controller-template.java     # REST controller
└── frontend-web/
    ├── 03-feature-component-template.tsx # React component
    └── 07-tanstack-query-hook-template.ts # API hooks
```

**Usage**: Reference to ensure consistent code style.

---

### Priority 4: Supporting Documents
```
04-validation-rules/        # Business constraints
05-use-case-mappings/       # UC → Code mapping
06-api-specifications/      # REST API details
07-database-schemas/        # DB table specs
08-architecture-decisions/  # Why we chose X
11-coding-standards/        # Code quality rules
12-quick-references/        # Cheatsheets
```

---

## 🎯 Token Optimization Strategy

### 1. **Progressive Context Loading**
Don't load everything at once. Load incrementally:

```
Step 1: Load domain document only
Step 2: If unclear, load implementation guide
Step 3: If still unclear, load code template
Step 4: If still unclear, load validation rules
```

### 2. **File Size Guidelines**
| File Type | Max Size | Why |
|-----------|----------|-----|
| Domain docs | 3-5 KB | Core specs only, no fluff |
| Templates | 1-2 KB | Minimal but complete example |
| Standards | 2-3 KB | Rules only, no explanations |
| Guides | 5-8 KB | Step-by-step instructions |

### 3. **Content Principles**
✅ **DO**:
- Direct, imperative language
- Code examples over prose
- Bullet points over paragraphs
- Tables over text descriptions
- References to other files, not duplication

❌ **DON'T**:
- Explain why (ADRs cover this separately)
- Repeat information from other files
- Include unnecessary examples
- Write long introductions
- Add motivational fluff

---

## 🚀 Usage Patterns

### Pattern A: Implement New Entity
```bash
Context Files Needed (Total: ~8-10 KB):
1. 01-domains/[domain].md              # 4 KB - Domain spec
2. 03-code-templates/backend/02-entity-template.java # 1 KB - Template
3. 11-coding-standards/01-java-coding-standards.md   # 2 KB - Rules

Prompt:
"Implement [Entity] entity following domain spec. 
 Use template structure. Follow coding standards."
```

**Token Usage**: ~3,000 input tokens → Efficient ✅

---

### Pattern B: Implement Complete Domain (All Layers)
```bash
Context Files Needed (Total: ~15-20 KB):
1. 01-domains/[domain].md                          # 4 KB
2. 02-implementation-guides/03-backend-layer-implementation.md # 6 KB
3. 03-code-templates/backend/*.java                # 5 KB (multiple)
4. 11-coding-standards/01-java-coding-standards.md # 2 KB

Prompt:
"Implement complete [Domain]:
 - Entity, Repository, Service, Controller
 - DTOs and Mappers
 - Follow layer implementation guide
 - Use provided templates"
```

**Token Usage**: ~6,000 input tokens → Acceptable ✅

---

### Pattern C: Implement Frontend Feature
```bash
Context Files Needed (Total: ~10-12 KB):
1. 01-domains/[domain].md                    # 4 KB
2. 06-api-specifications/[api].md            # 3 KB
3. 03-code-templates/frontend-web/*.tsx      # 3 KB
4. 11-coding-standards/02-typescript-coding-standards.md # 2 KB

Prompt:
"Create [Feature] component:
 - Fetch data from [API endpoint]
 - Use TanStack Query
 - Follow component template
 - Apply TypeScript standards"
```

**Token Usage**: ~4,000 input tokens → Efficient ✅

---

### Pattern D: Debug & Fix
```bash
Context Files Needed (Total: ~3-5 KB):
1. [Existing code file]                       # 1-2 KB
2. 11-coding-standards/[relevant-standard].md # 2 KB

Prompt:
"Fix bug in [File] at line [X]:
 [Error message]
 Follow coding standards for fix."
```

**Token Usage**: ~1,500 input tokens → Very Efficient ✅✅

---

## 📊 Token Budget Guidelines

### Conservative Approach (Recommended)
```
Single Task:
- Context: 3,000-5,000 tokens (~10-15 KB)
- Prompt: 200-500 tokens
- Output: 1,000-2,000 tokens
- Total: ~5,000-8,000 tokens per task
- Cost: $0.03-$0.08 per task (Sonnet 4.5)

Daily Budget ($2/day):
- ~25-40 tasks/day
- Sufficient for implementing 2-3 complete domains/day
```

### Aggressive Approach (Complex Tasks)
```
Complex Task:
- Context: 8,000-12,000 tokens (~25-35 KB)
- Prompt: 500-1,000 tokens
- Output: 3,000-5,000 tokens
- Total: ~12,000-18,000 tokens per task
- Cost: $0.10-$0.20 per task

Daily Budget ($5/day):
- ~25-50 tasks/day
- Implement entire backend in 3-5 days
```

---

## 🎓 Best Practices

### 1. **Start Small, Scale Up**
```
Iteration 1: Entity only (3 files, 8 KB)
Iteration 2: + Repository (4 files, 10 KB)
Iteration 3: + Service (5 files, 15 KB)
Iteration 4: + Controller (6 files, 20 KB)
```

### 2. **Reuse Context When Possible**
```bash
# Good: Implement multiple related features in one conversation
claude-code "Implement Folder entity, repository, and service"
# Context loaded once, used 3x

# Bad: Three separate conversations
claude-code "Implement Folder entity"
claude-code "Implement Folder repository"  # Re-loads context
claude-code "Implement Folder service"     # Re-loads context
```

### 3. **Use References, Not Duplication**
```markdown
✅ Good:
"Follow validation rules in 04-validation-rules/01-entity-constraints.md"

❌ Bad:
"Validation rules:
 - Name: required, 1-100 chars
 - Email: valid format
 - ... [30 more lines]"
```

### 4. **Batch Related Tasks**
```bash
# Implement entire domain in one go
claude-code --file 01-domains/03-deck-domain.md \
            --file 02-implementation-guides/03-backend-layer-implementation.md \
            "Implement complete Deck domain (all layers)"

# vs. 4 separate calls (4x token cost)
```

---

## 📖 Document Conventions

### File Naming
```
[NN]-[descriptive-name].md
├── NN: Two-digit number for ordering
└── descriptive-name: Kebab-case, self-explanatory
```

### Section Structure
```markdown
# Title (H1)
Brief description (1-2 sentences)

## Core Content (H2)
Essential information only

### Details (H3)
Granular specifics

**Bold**: Emphasize critical points
`code`: Inline code/filenames
```markdown blocks: Code examples
```

### Cross-References
```markdown
See: [Link Text](../folder/file.md)
Reference: 01-domains/02-folder-domain.md
Template: 03-code-templates/backend/02-entity-template.java
```

---

## 🔍 Finding What You Need

### Quick Lookup Table
| Need | Go To |
|------|-------|
| Domain specification | `01-domains/` |
| How to implement | `02-implementation-guides/` |
| Code example | `03-code-templates/` |
| Validation rule | `04-validation-rules/` |
| API endpoint | `06-api-specifications/` |
| Database table | `07-database-schemas/` |
| Coding rule | `11-coding-standards/` |
| Quick reference | `12-quick-references/` |

### Search Strategy
```bash
# Find all files mentioning "Folder"
grep -r "Folder" 01-claude-prompts/

# Find entity specifications
ls 01-domains/*-domain.md

# Find code templates
ls 03-code-templates/backend/*.java
```

---

## ⚠️ Common Pitfalls

### ❌ Pitfall 1: Loading Too Much Context
```bash
# Bad: Load entire knowledge base (100+ KB)
claude-code --file 01-claude-prompts/**/*.md "implement feature"

# Good: Load only what's needed (10-15 KB)
claude-code --file 01-domains/03-deck-domain.md \
            --file 03-code-templates/backend/02-entity-template.java \
            "implement Deck entity"
```

### ❌ Pitfall 2: Vague Prompts
```bash
# Bad: Claude has to guess
claude-code "create a user thing"

# Good: Specific and references docs
claude-code --file 01-domains/01-user-auth-domain.md \
            "Implement User entity following domain specification, \
             including JPA annotations, validation, and soft delete"
```

### ❌ Pitfall 3: Not Reviewing Output
```bash
# Always review generated code
git diff                    # See what changed
mvn test                    # Run tests
git add -p                  # Stage changes selectively
```

---

## 📞 Support

- **Original Docs**: `../00_docs/` (comprehensive but verbose)
- **This Folder**: Token-optimized for Claude Code
- **Issues**: Review generated code carefully before committing

---

## 🎯 Success Metrics

**You're using this knowledge base well if**:
- ✅ Tasks use <5,000 context tokens on average
- ✅ Generated code matches design specs >95%
- ✅ Minimal manual edits needed
- ✅ Daily API cost <$5 for full-time development

**You're wasting tokens if**:
- ❌ Loading >10 files per task
- ❌ Re-explaining same info in prompts
- ❌ Loading entire folders with `**/*.md`
- ❌ Not batching related tasks

---

**Version**: 1.0  
**Last Updated**: January 2025  
**Optimized For**: Claude Code CLI, Claude.ai Projects

**Token Budget**: This file is 2,048 tokens → Reference sparingly
