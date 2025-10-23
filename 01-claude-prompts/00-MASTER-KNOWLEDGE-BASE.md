# Folder Domain - Complete Specification

> **Size**: 4 KB | **Tokens**: ~1,500 | **Pattern**: Composite

---

## 1. Domain Overview

**Purpose**: Hierarchical folder organization (tree structure) for decks.

**Key Features**:
- Unlimited depth (max 10 levels)
- Self-referencing (parent-child relationship)
- Materialized path for fast queries
- Soft delete with cascade
- Recursive statistics

**Related Use Cases**: UC-005 to UC-010

---

## 2. Entity Specification

### Folder Entity
```java
@Entity
@Table(name = "folders", indexes = {
    @Index(name = "idx_folders_user_parent", columnList = "user_id,parent_folder_id"),
    @Index(name = "idx_folders_path", columnList = "user_id,path")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Folder extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;  // Composite pattern
    
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> subFolders = new ArrayList<>();
    
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deck> decks = new ArrayList<>();
    
    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
    
    @Column(length = 500)
    @Size(max = 500)
    private String description;
    
    @Column(nullable = false)
    @Min(0) @Max(10)
    private Integer depth;  // 0 = root, max 10
    
    @Column(nullable = false, length = 1000)
    private String path;  // Materialized path: /uuid1/uuid2/uuid3
    
    // Soft delete
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Business methods
    public void addSubFolder(Folder subFolder) {
        if (this.depth >= 9) {
            throw new MaxDepthExceededException("Cannot exceed depth 10");
        }
        subFolder.setParentFolder(this);
        subFolder.setDepth(this.depth + 1);
        subFolder.setUser(this.user);
        subFolder.updatePath();
        this.subFolders.add(subFolder);
    }
    
    public void updatePath() {
        if (parentFolder == null) {
            this.path = "/" + this.id;
        } else {
            this.path = parentFolder.getPath() + "/" + this.id;
        }
    }
    
    public int countTotalItems() {
        int count = 1;  // Self
        for (Folder sub : subFolders) {
            count += sub.countTotalItems();  // Recursive
        }
        return count + decks.size();
    }
    
    public boolean isDescendantOf(Folder other) {
        return this.path.startsWith(other.getPath() + "/");
    }
}
```

---

## 3. Repository

```java
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    
    // Find root folders (no parent)
    List<Folder> findByUserIdAndParentFolderIsNullAndDeletedAtIsNull(UUID userId);
    
    // Find subfolders of a parent
    List<Folder> findByParentFolderIdAndDeletedAtIsNull(UUID parentId);
    
    // Find by path (descendants)
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId " +
           "AND f.path LIKE :pathPattern AND f.deletedAt IS NULL")
    List<Folder> findDescendants(@Param("userId") UUID userId, 
                                  @Param("pathPattern") String pathPattern);
    
    // Check name uniqueness
    boolean existsByUserIdAndParentFolderIdAndNameAndDeletedAtIsNull(
        UUID userId, UUID parentId, String name);
    
    // Find with decks loaded (prevent N+1)
    @EntityGraph(attributePaths = {"decks"})
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.deletedAt IS NULL")
    List<Folder> findAllWithDecks(@Param("userId") UUID userId);
}
```

---

## 4. Service Interface

```java
public interface IFolderService {
    
    // CRUD operations
    FolderDTO createFolder(CreateFolderRequest request);
    FolderDTO updateFolder(UUID id, UpdateFolderRequest request);
    void deleteFolder(UUID id);
    FolderDTO getFolderById(UUID id);
    
    // Tree operations
    List<FolderTreeNodeDTO> getFolderTree(UUID userId);
    List<FolderDTO> getRootFolders(UUID userId);
    List<FolderDTO> getSubFolders(UUID parentId);
    
    // Move operations
    FolderDTO moveFolder(UUID folderId, MoveFolderRequest request);
    
    // Copy operations
    FolderDTO copyFolderSync(UUID folderId, CopyFolderRequest request);
    CompletableFuture<FolderDTO> copyFolderAsync(UUID folderId, CopyFolderRequest request);
    
    // Statistics
    FolderStatsDTO getFolderStats(UUID folderId);
    
    // Path operations
    List<BreadcrumbDTO> getBreadcrumb(UUID folderId);
}
```

---

## 5. Key Business Logic

### Create Folder
```java
@Transactional
public FolderDTO createFolder(CreateFolderRequest request) {
    // Validate user exists
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    // Validate parent folder if specified
    Folder parentFolder = null;
    if (request.getParentFolderId() != null) {
        parentFolder = folderRepository.findById(request.getParentFolderId())
            .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));
        
        // Check ownership
        if (!parentFolder.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Cannot create folder in another user's folder");
        }
        
        // Check max depth
        if (parentFolder.getDepth() >= 9) {
            throw new MaxDepthExceededException("Max folder depth is 10");
        }
    }
    
    // Check name uniqueness
    if (folderRepository.existsByUserIdAndParentFolderIdAndNameAndDeletedAtIsNull(
            user.getId(), 
            request.getParentFolderId(), 
            request.getName())) {
        throw new DuplicateResourceException("Folder name already exists in this location");
    }
    
    // Create folder
    Folder folder = Folder.builder()
        .user(user)
        .parentFolder(parentFolder)
        .name(request.getName())
        .description(request.getDescription())
        .depth(parentFolder == null ? 0 : parentFolder.getDepth() + 1)
        .build();
    
    folder.updatePath();
    
    Folder saved = folderRepository.save(folder);
    return folderMapper.toDTO(saved);
}
```

### Move Folder
```java
@Transactional
public FolderDTO moveFolder(UUID folderId, MoveFolderRequest request) {
    Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));
    
    // Cannot move to self
    if (folderId.equals(request.getNewParentId())) {
        throw new InvalidOperationException("Cannot move folder to itself");
    }
    
    // Get new parent
    Folder newParent = null;
    if (request.getNewParentId() != null) {
        newParent = folderRepository.findById(request.getNewParentId())
            .orElseThrow(() -> new ResourceNotFoundException("New parent not found"));
        
        // Cannot move to descendant
        if (newParent.isDescendantOf(folder)) {
            throw new InvalidOperationException("Cannot move folder to its descendant");
        }
        
        // Check max depth
        int newDepth = newParent.getDepth() + 1 + folder.getMaxDescendantDepth();
        if (newDepth > 10) {
            throw new MaxDepthExceededException("Move would exceed max depth of 10");
        }
    }
    
    // Update folder
    folder.setParentFolder(newParent);
    folder.setDepth(newParent == null ? 0 : newParent.getDepth() + 1);
    folder.updatePath();
    
    // Update all descendants recursively
    updateDescendantsPaths(folder);
    
    Folder saved = folderRepository.save(folder);
    return folderMapper.toDTO(saved);
}

private void updateDescendantsPaths(Folder folder) {
    for (Folder subFolder : folder.getSubFolders()) {
        subFolder.setDepth(folder.getDepth() + 1);
        subFolder.updatePath();
        updateDescendantsPaths(subFolder);  // Recursive
    }
}
```

### Copy Folder (Sync/Async)
```java
@Transactional
public FolderDTO copyFolderSync(UUID folderId, CopyFolderRequest request) {
    Folder source = folderRepository.findById(folderId)
        .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));
    
    // Check size threshold
    int totalItems = source.countTotalItems();
    if (totalItems > 50) {
        throw new InvalidOperationException("Folder too large for sync copy. Use async.");
    }
    
    Folder copied = copyFolderRecursive(source, request.getNewParentId(), request.getNewName());
    return folderMapper.toDTO(copied);
}

private Folder copyFolderRecursive(Folder source, UUID newParentId, String newName) {
    Folder copy = Folder.builder()
        .user(source.getUser())
        .parentFolder(newParentId == null ? null : folderRepository.findById(newParentId).orElse(null))
        .name(newName != null ? newName : source.getName() + " (Copy)")
        .description(source.getDescription())
        .build();
    
    copy.setDepth(copy.getParentFolder() == null ? 0 : copy.getParentFolder().getDepth() + 1);
    copy.updatePath();
    
    Folder savedCopy = folderRepository.save(copy);
    
    // Copy subfolders
    for (Folder subFolder : source.getSubFolders()) {
        copyFolderRecursive(subFolder, savedCopy.getId(), null);
    }
    
    // Copy decks
    for (Deck deck : source.getDecks()) {
        deckService.copyDeck(deck.getId(), savedCopy.getId());
    }
    
    return savedCopy;
}

@Async
public CompletableFuture<FolderDTO> copyFolderAsync(UUID folderId, CopyFolderRequest request) {
    // Same logic as sync but in background thread
    FolderDTO result = copyFolderSync(folderId, request);
    return CompletableFuture.completedFuture(result);
}
```

### Get Folder Statistics
```java
public FolderStatsDTO getFolderStats(UUID folderId) {
    Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));
    
    FolderStatsDTO stats = new FolderStatsDTO();
    stats.setFolderId(folderId);
    
    // Use Visitor pattern to traverse tree
    FolderStatsVisitor visitor = new FolderStatsVisitor();
    folder.accept(visitor);
    
    stats.setTotalFolders(visitor.getTotalFolders());
    stats.setTotalDecks(visitor.getTotalDecks());
    stats.setTotalCards(visitor.getTotalCards());
    stats.setDueCards(visitor.getDueCards());
    stats.setNewCards(visitor.getNewCards());
    
    return stats;
}
```

---

## 6. DTOs

### CreateFolderRequest
```java
@Data
public class CreateFolderRequest {
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    private UUID parentFolderId;  // null = root level
    
    @NotNull
    private UUID userId;
}
```

### FolderDTO
```java
@Data
public class FolderDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID parentFolderId;
    private Integer depth;
    private String path;
    private Integer subFolderCount;
    private Integer deckCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### FolderTreeNodeDTO
```java
@Data
public class FolderTreeNodeDTO {
    private UUID id;
    private String name;
    private Integer depth;
    private List<FolderTreeNodeDTO> children;
    private Integer deckCount;
    private Integer totalCards;
    private Integer dueCards;
}
```

---

## 7. Controller Endpoints

```java
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {
    
    private final IFolderService folderService;
    
    @GetMapping
    public ResponseEntity<List<FolderTreeNodeDTO>> getFolderTree(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(folderService.getFolderTree(user.getId()));
    }
    
    @PostMapping
    public ResponseEntity<FolderDTO> createFolder(@Valid @RequestBody CreateFolderRequest request) {
        return ResponseEntity.status(201).body(folderService.createFolder(request));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FolderDTO> getFolderById(@PathVariable UUID id) {
        return ResponseEntity.ok(folderService.getFolderById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FolderDTO> updateFolder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFolderRequest request) {
        return ResponseEntity.ok(folderService.updateFolder(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable UUID id) {
        folderService.deleteFolder(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/move")
    public ResponseEntity<FolderDTO> moveFolder(
            @PathVariable UUID id,
            @Valid @RequestBody MoveFolderRequest request) {
        return ResponseEntity.ok(folderService.moveFolder(id, request));
    }
    
    @PostMapping("/{id}/copy")
    public ResponseEntity<?> copyFolder(
            @PathVariable UUID id,
            @Valid @RequestBody CopyFolderRequest request) {
        
        // Check size to decide sync vs async
        int totalItems = folderService.countItems(id);
        
        if (totalItems <= 50) {
            // Sync copy
            return ResponseEntity.ok(folderService.copyFolderSync(id, request));
        } else {
            // Async copy
            String jobId = UUID.randomUUID().toString();
            folderService.copyFolderAsync(id, request);
            return ResponseEntity.accepted()
                .body(Map.of("jobId", jobId, "status", "PROCESSING"));
        }
    }
    
    @GetMapping("/{id}/stats")
    public ResponseEntity<FolderStatsDTO> getFolderStats(@PathVariable UUID id) {
        return ResponseEntity.ok(folderService.getFolderStats(id));
    }
    
    @GetMapping("/{id}/breadcrumb")
    public ResponseEntity<List<BreadcrumbDTO>> getBreadcrumb(@PathVariable UUID id) {
        return ResponseEntity.ok(folderService.getBreadcrumb(id));
    }
}
```

---

## 8. Validation Rules

| Field | Rule | Error Message |
|-------|------|---------------|
| name | Required, 1-100 chars | "Folder name is required and must be 1-100 characters" |
| name | Unique within parent | "Folder name already exists in this location" |
| description | Max 500 chars | "Description must not exceed 500 characters" |
| depth | 0-10 | "Folder depth cannot exceed 10 levels" |
| parentFolder | Must exist | "Parent folder not found" |
| parentFolder | Must belong to user | "Cannot create folder in another user's folder" |

---

## 9. Performance Notes

### Indexes
```sql
CREATE INDEX idx_folders_user_parent ON folders(user_id, parent_folder_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_folders_path ON folders(user_id, path) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX idx_folders_name_parent ON folders(user_id, parent_folder_id, name) WHERE deleted_at IS NULL;
```

### N+1 Prevention
```java
// Load with decks
@EntityGraph(attributePaths = {"decks"})
List<Folder> findAllWithDecks(UUID userId);

// Or use JOIN FETCH
@Query("SELECT f FROM Folder f LEFT JOIN FETCH f.decks WHERE f.user.id = :userId")
List<Folder> findAllWithDecks(@Param("userId") UUID userId);
```

---

## 10. Testing

```java
@SpringBootTest
@Transactional
class FolderServiceTest {
    
    @Autowired
    private IFolderService folderService;
    
    @Test
    void createRootFolder_Success() {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("Test Folder");
        request.setUserId(testUser.getId());
        
        FolderDTO result = folderService.createFolder(request);
        
        assertNotNull(result.getId());
        assertEquals("Test Folder", result.getName());
        assertEquals(0, result.getDepth());
    }
    
    @Test
    void createFolder_ExceedsMaxDepth_ThrowsException() {
        // Create 10 nested folders
        UUID parentId = createNestedFolders(10);
        
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("Too Deep");
        request.setParentFolderId(parentId);
        request.setUserId(testUser.getId());
        
        assertThrows(MaxDepthExceededException.class, 
            () -> folderService.createFolder(request));
    }
    
    @Test
    void moveFolder_ToDescendant_ThrowsException() {
        // Create parent and child
        Folder parent = createFolder("Parent", null);
        Folder child = createFolder("Child", parent.getId());
        
        MoveFolderRequest request = new MoveFolderRequest();
        request.setNewParentId(child.getId());
        
        assertThrows(InvalidOperationException.class,
            () -> folderService.moveFolder(parent.getId(), request));
    }
}
```

---

**File Size**: 4 KB  
**Tokens**: ~1,500  
**Source**: `00_docs/03-design/database/schema.md`, UC-005 to UC-010  
**Related**: `02-implementation-guides/03-backend-layer-implementation.md`
