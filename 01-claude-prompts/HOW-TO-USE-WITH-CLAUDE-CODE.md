# How to Use This Knowledge Base with Claude Code

> **Practical guide with real examples for RepeatWise development**

---

## 🎯 Prerequisites

1. ✅ Claude Code installed (`npm i -g @anthropic-ai/claude-code`)
2. ✅ API key configured (`export ANTHROPIC_API_KEY=sk-ant-...`)
3. ✅ Project structure created (`create-claude-prompts-complete.bat`)
4. ✅ Navigated to project root (`cd d:\workspace\repeatwise`)

---

## 📊 Token Budget Strategy

### Daily Budget Planning

**Conservative** ($2/day = ~$60/month):
```
25-40 tasks/day
= 2-3 complete domains/day
= Backend MVP in 3-4 days
```

**Moderate** ($5/day = ~$150/month):
```
50-70 tasks/day
= 4-5 complete domains/day
= Backend MVP in 2 days
```

**Aggressive** ($10/day = ~$300/month):
```
100-150 tasks/day
= Full backend + frontend in 3 days
```

---

## 🚀 Usage Patterns

### Pattern 1: Implement Single Entity (Low Complexity)

**Context Size**: ~8-10 KB (~3,000 tokens)

**Files to include**:
```bash
01-claude-prompts/01-domains/[domain].md          # 4 KB
01-claude-prompts/03-code-templates/backend/02-entity-template.java  # 1 KB
01-claude-prompts/11-coding-standards/01-java-coding-standards.md    # 2 KB
```

**Command**:
```bash
cd d:\workspace\repeatwise

claude-code \
  --file 01-claude-prompts/01-domains/02-folder-domain.md \
  --file 01-claude-prompts/03-code-templates/backend/02-entity-template.java \
  --file 01-claude-prompts/11-coding-standards/01-java-coding-standards.md \
  "Implement Folder entity following the domain specification.
   Include:
   - JPA annotations (indexes, constraints)
   - Bidirectional relationships (parent-child)
   - Validation annotations
   - Business methods (addSubFolder, updatePath, countTotalItems)
   - Soft delete support
   
   Output file: backend-api/src/main/java/com/repeatwise/entity/Folder.java"
```

**Expected Output**: 
- `Folder.java` created with ~200 lines
- All requirements met
- Ready to commit

**Cost**: ~$0.03-$0.05

---

### Pattern 2: Implement Complete Layer (Medium Complexity)

**Context Size**: ~15-20 KB (~6,000 tokens)

**Files to include**:
```bash
01-claude-prompts/01-domains/[domain].md          # 4 KB
01-claude-prompts/02-implementation-guides/03-backend-layer-implementation.md  # 6 KB
01-claude-prompts/03-code-templates/backend/*.java  # 5 KB (multiple)
01-claude-prompts/11-coding-standards/01-java-coding-standards.md  # 2 KB
```

**Command**:
```bash
claude-code \
  --file 01-claude-prompts/01-domains/03-deck-domain.md \
  --file 01-claude-prompts/02-implementation-guides/03-backend-layer-implementation.md \
  --file 01-claude-prompts/03-code-templates/backend/02-entity-template.java \
  --file 01-claude-prompts/03-code-templates/backend/03-repository-template.java \
  --file 01-claude-prompts/03-code-templates/backend/05-service-impl-template.java \
  --file 01-claude-prompts/03-code-templates/backend/06-controller-template.java \
  --file 01-claude-prompts/11-coding-standards/01-java-coding-standards.md \
  "Implement complete Deck domain with all layers:
   
   1. Entity (Deck.java)
      - JPA annotations
      - Relationships to User, Folder, Card
      - Validation
   
   2. Repository (DeckRepository.java)
      - Spring Data JPA interface
      - Custom queries
   
   3. Service Interface (IDeckService.java)
      - CRUD methods
      - Move/copy operations
   
   4. Service Implementation (DeckServiceImpl.java)
      - Business logic
      - Validation
      - Transaction management
   
   5. Controller (DeckController.java)
      - REST endpoints
      - Request/response DTOs
      - Exception handling
   
   6. DTOs (CreateDeckRequest, UpdateDeckRequest, DeckDTO)
   
   7. Mapper (DeckMapper.java)
      - MapStruct interface
   
   Follow coding standards strictly. Use templates as reference."
```

**Expected Output**:
- 7 files created
- Complete domain implementation
- Ready for testing

**Cost**: ~$0.10-$0.15

---

### Pattern 3: Implement Use Case End-to-End (High Complexity)

**Context Size**: ~25-30 KB (~10,000 tokens)

**Files to include**:
```bash
01-claude-prompts/00-MASTER-KNOWLEDGE-BASE.md     # 8 KB
01-claude-prompts/01-domains/[domain].md          # 4 KB
01-claude-prompts/05-use-case-mappings/[uc].md    # 3 KB
01-claude-prompts/06-api-specifications/[api].md  # 3 KB
Backend templates                                  # 5 KB
Frontend templates                                 # 3 KB
Coding standards                                   # 4 KB
```

**Command**:
```bash
claude-code \
  --file 01-claude-prompts/00-MASTER-KNOWLEDGE-BASE.md \
  --file 01-claude-prompts/01-domains/03-deck-domain.md \
  --file 01-claude-prompts/05-use-case-mappings/03-deck-management-use-cases.md \
  --file 01-claude-prompts/06-api-specifications/04-deck-apis.md \
  "Implement UC-011: Create Deck end-to-end:
   
   BACKEND:
   1. POST /api/decks endpoint in DeckController
   2. DeckService.createDeck() with validation
   3. Check folder ownership
   4. Check deck name uniqueness
   5. Exception handling
   6. Unit tests for service
   7. Integration test for API
   
   FRONTEND WEB:
   1. CreateDeckDialog component (Shadcn/ui)
   2. Form with React Hook Form + Zod validation
   3. useCreateDeck hook (TanStack Query)
   4. Optimistic update
   5. Error handling with toast
   6. Component test
   
   Ensure both backend and frontend work together."
```

**Expected Output**:
- Backend: Controller, Service, Tests
- Frontend: Component, Hook, Form, Test
- Fully integrated feature

**Cost**: ~$0.20-$0.30

---

### Pattern 4: Debug Existing Code (Very Low Complexity)

**Context Size**: ~3-5 KB (~1,500 tokens)

**Files to include**:
```bash
[existing-buggy-file].java                        # 1-2 KB
01-claude-prompts/11-coding-standards/01-java-coding-standards.md  # 2 KB
```

**Command**:
```bash
claude-code \
  --file backend-api/src/main/java/com/repeatwise/service/impl/FolderServiceImpl.java \
  --file 01-claude-prompts/11-coding-standards/01-java-coding-standards.md \
  "Fix NullPointerException at line 87:
   
   Error log:
   java.lang.NullPointerException: Cannot invoke 'getDepth()' on null
       at FolderServiceImpl.moveFolder(FolderServiceImpl.java:87)
   
   Analysis:
   - Occurs when moving folder with null parent
   - getDepth() called on null parent
   
   Fix:
   - Add null check before calling getDepth()
   - Use early return pattern (no else)
   - Follow coding standards"
```

**Expected Output**:
- Bug fixed
- Null checks added
- Code follows standards

**Cost**: ~$0.02-$0.03

---

## 📚 Real Implementation Examples

### Example 1: Implement User Domain (Complete Backend)

```bash
# Step 1: Entity
claude-code \
  --file 01-claude-prompts/01-domains/01-user-auth-domain.md \
  --file 01-claude-prompts/03-code-templates/backend/02-entity-template.java \
  "Implement User entity with JPA annotations, email validation, password hashing"

# Step 2: Repository
claude-code \
  --file 01-claude-prompts/01-domains/01-user-auth-domain.md \
  --file 01-claude-prompts/03-code-templates/backend/03-repository-template.java \
  "Implement UserRepository with findByEmail, existsByEmail methods"

# Step 3: Service
claude-code \
  --file 01-claude-prompts/01-domains/01-user-auth-domain.md \
  --file 01-claude-prompts/03-code-templates/backend/05-service-impl-template.java \
  "Implement UserService with register, login, updateProfile methods"

# Step 4: Controller
claude-code \
  --file 01-claude-prompts/01-domains/01-user-auth-domain.md \
  --file 01-claude-prompts/06-api-specifications/01-authentication-apis.md \
  --file 01-claude-prompts/03-code-templates/backend/06-controller-template.java \
  "Implement AuthController with /register, /login, /refresh, /logout endpoints"

# Total cost: ~$0.15-$0.20
# Time saved: ~4-6 hours of manual coding
```

---

### Example 2: Implement Frontend Component

```bash
claude-code \
  --file 01-claude-prompts/01-domains/03-deck-domain.md \
  --file 01-claude-prompts/06-api-specifications/04-deck-apis.md \
  --file 01-claude-prompts/03-code-templates/frontend-web/03-feature-component-template.tsx \
  --file 01-claude-prompts/03-code-templates/frontend-web/07-tanstack-query-hook-template.ts \
  --file 01-claude-prompts/11-coding-standards/02-typescript-coding-standards.md \
  "Create DeckList component:
   
   Component (DeckList.tsx):
   - Fetch decks with useDecks() hook
   - Display as grid with DeckCard components
   - Pagination (20 items/page)
   - Loading skeleton
   - Error state with retry
   - Empty state
   - Search filter (local)
   
   Hook (useDecks.ts):
   - TanStack Query hook
   - GET /api/decks?folderId={id}
   - Cache key: ['decks', folderId]
   - Stale time: 5 minutes
   
   Styles:
   - Tailwind CSS
   - Responsive grid (1 col mobile, 2 tablet, 3 desktop)
   - Dark mode support
   
   Output files:
   - frontend-web/src/components/deck/DeckList.tsx
   - frontend-web/src/hooks/useDecks.ts"

# Cost: ~$0.08-$0.12
# Time saved: ~2-3 hours
```

---

### Example 3: Implement SRS Algorithm

```bash
claude-code \
  --file 01-claude-prompts/00-MASTER-KNOWLEDGE-BASE.md \
  --file 01-claude-prompts/01-domains/05-review-srs-domain.md \
  --file 00_docs/03-design/architecture/srs-algorithm-design.md \
  --file 00_docs/04-detail-design/04-srs-algorithm-implementation.md \
  "Implement SRS 7-box algorithm:
   
   SRSService.java:
   - rateCard(Card, Rating) method
   - Implement AGAIN, HARD, GOOD, EASY logic
   - Update current_box, due_date
   - Create ReviewLog entry
   - Handle forgotten cards
   
   SRSStrategy interface:
   - ForgottenCardStrategy
   - ReviewOrderStrategy
   
   Box intervals:
   - Box 1: 1 day
   - Box 2: 3 days
   - Box 3: 7 days
   - Box 4: 14 days
   - Box 5: 30 days
   - Box 6: 60 days
   - Box 7: 120 days
   
   Follow algorithm design document exactly."

# Cost: ~$0.15-$0.20
# Time saved: ~5-8 hours (complex algorithm)
```

---

## 🎓 Advanced Techniques

### Technique 1: Batch Implementation

Create a script to implement multiple domains:

```bash
#!/bin/bash
# implement-all-domains.sh

DOMAINS=(
    "01-user-auth-domain"
    "02-folder-domain"
    "03-deck-domain"
    "04-card-domain"
    "05-review-srs-domain"
)

for domain in "${DOMAINS[@]}"; do
    echo "Implementing $domain..."
    
    claude-code \
        --file "01-claude-prompts/01-domains/$domain.md" \
        --file "01-claude-prompts/02-implementation-guides/03-backend-layer-implementation.md" \
        --file "01-claude-prompts/11-coding-standards/01-java-coding-standards.md" \
        "Implement complete $domain: Entity, Repository, Service, Controller, DTOs, Mapper, Tests"
    
    echo "✓ $domain done!"
done

echo "All domains implemented!"
```

**Usage**: `./implement-all-domains.sh`

**Cost**: ~$1.50-$2.50 total  
**Time saved**: ~40-60 hours of manual coding

---

### Technique 2: Iterative Refinement

```bash
# Iteration 1: Basic structure
claude-code "Create Folder entity with basic fields"

# Iteration 2: Add relationships
claude-code "Add parent-child relationship to Folder entity"

# Iteration 3: Add business logic
claude-code "Add addSubFolder, updatePath methods to Folder"

# Iteration 4: Add validation
claude-code "Add validation annotations to Folder entity"

# Iteration 5: Add tests
claude-code "Create unit tests for Folder entity business methods"
```

**Benefit**: More control, easier review  
**Cost**: Same or slightly less (smaller context per iteration)

---

### Technique 3: Context Caching (Conversation Reuse)

Use Claude Code in **interactive mode** to maintain context:

```bash
claude-code  # Start interactive session

# All subsequent prompts reuse context
You: "Implement User entity"
Claude: [creates User.java]

You: "Now implement UserRepository"
Claude: [creates UserRepository.java, already knows User entity]

You: "Now implement UserService"
Claude: [creates UserService.java, knows both User and UserRepository]

# Context loaded once, used multiple times!
```

**Benefit**: Massive token savings  
**Cost**: ~50% less than separate calls

---

## ⚠️ Common Mistakes & Solutions

### ❌ Mistake 1: Loading Too Much Context

```bash
# BAD: Load entire knowledge base
claude-code --file 01-claude-prompts/**/*.md "implement user entity"
# Token usage: ~30,000 tokens
# Cost: ~$0.50
# Quality: Confused by too much info
```

```bash
# GOOD: Load only what's needed
claude-code \
  --file 01-claude-prompts/01-domains/01-user-auth-domain.md \
  --file 01-claude-prompts/03-code-templates/backend/02-entity-template.java \
  "implement User entity"
# Token usage: ~3,000 tokens
# Cost: ~$0.03
# Quality: Focused, accurate
```

---

### ❌ Mistake 2: Vague Prompts

```bash
# BAD
claude-code "create a user thing"
# Result: Generic code, doesn't follow standards
```

```bash
# GOOD
claude-code \
  --file 01-claude-prompts/01-domains/01-user-auth-domain.md \
  "Implement User entity following domain spec:
   - UUID primary key
   - Email field with @Email validation
   - Password hash (NOT plain password)
   - Name, timezone, language, theme fields
   - Soft delete (deleted_at timestamp)
   - Audit fields (created_at, updated_at)
   
   Output: backend-api/src/main/java/com/repeatwise/entity/User.java"
# Result: Exact implementation, follows all requirements
```

---

### ❌ Mistake 3: Not Reviewing Output

```bash
# BAD workflow
claude-code "implement something"
git add .
git commit -m "AI generated code"
git push
# Risk: Bugs, incorrect logic, security issues
```

```bash
# GOOD workflow
claude-code "implement something"
git diff                    # Review changes
mvn test                    # Run tests
mvn spring-boot:run         # Test manually
git add -p                  # Stage selectively
git commit -m "feat: ..."   # Commit with message
git push
# Result: High-quality, verified code
```

---

## 📊 Token Tracking

Track your usage to stay within budget:

```bash
# Create tracking file
echo "Date,Task,Files,Tokens,Cost" > claude-usage.csv

# After each task
echo "2025-01-15,User Entity,3,3000,0.03" >> claude-usage.csv

# View daily total
awk -F',' '{sum+=$5} END {print "Total: $"sum}' claude-usage.csv
```

---

## 🎯 Success Checklist

After each Claude Code task:

```
☐ Output files created in correct locations
☐ Code follows coding standards (checked manually)
☐ No syntax errors (IDE shows no errors)
☐ Tests written and passing (mvn test)
☐ Code reviewed manually (git diff)
☐ Committed with proper message
☐ Token usage logged
☐ Cost within daily budget
```

---

## 📞 Support & Troubleshooting

### Issue: Claude generates incorrect code
**Solution**: 
1. Provide more specific prompt
2. Include more context files (domain spec, coding standards)
3. Reference specific sections: "Follow section 5.2 in domain spec"

### Issue: Token usage too high
**Solution**:
1. Use fewer context files (only essential)
2. Use interactive mode for related tasks
3. Split complex tasks into smaller iterations

### Issue: Output doesn't follow standards
**Solution**:
1. Always include coding standards file
2. Explicitly mention: "Follow coding standards strictly"
3. Review and refactor manually if needed

---

## 🚀 Next Steps

1. ✅ Review this guide
2. ✅ Try Pattern 1 (simple entity implementation)
3. ✅ Review output carefully
4. ✅ Gradually increase complexity (Pattern 2, 3)
5. ✅ Track token usage and adjust strategy

**Remember**: Claude Code is a powerful assistant, but YOU are the developer. Always review, test, and validate generated code.

---

**Happy Coding!** 🎉

**Estimated time to implement entire RepeatWise MVP**:
- With Claude Code: 3-5 days
- Without Claude Code: 30-45 days

**ROI**: 10x-15x productivity gain
