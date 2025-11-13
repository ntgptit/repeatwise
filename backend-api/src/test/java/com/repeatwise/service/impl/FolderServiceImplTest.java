package com.repeatwise.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.FolderMapper;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.FolderService.DeletionSummary;

@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FOLDER_ID = UUID.randomUUID();
    private static final UUID PARENT_ID = UUID.randomUUID();

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderMapper folderMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Captor
    private ArgumentCaptor<Folder> folderCaptor;

    private User user;

    @BeforeEach
    void setUp() {
        this.user = createUser(USER_ID);
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(this.user));
    }

    @Test
    void should_CreateFolder_When_InputValid() {
        final var request = CreateFolderRequest.builder()
                .name("  New Folder  ")
                .description("  description  ")
                .build();
        final var mappedFolder = createFolder(null, null, 0, null);
        when(this.folderMapper.toEntity(request)).thenReturn(mappedFolder);
        when(this.folderRepository.getMaxSortOrderForRoot(USER_ID)).thenReturn(1);
        when(this.folderRepository.existsByUserIdAndParentFolderIsNullAndNameIgnoreCaseAndDeletedAtIsNull(USER_ID,
                "New Folder"))
                        .thenReturn(false);
        when(this.folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var expectedResponse = FolderResponse.builder().id(FOLDER_ID).build();
        when(this.folderMapper.toResponse(any(Folder.class))).thenReturn(expectedResponse);

        final var response = this.folderService.createFolder(request, USER_ID);

        assertThat(response).isEqualTo(expectedResponse);
        verify(this.folderRepository).save(this.folderCaptor.capture());
        final var savedFolder = this.folderCaptor.getValue();
        assertThat(savedFolder.getUser()).isEqualTo(this.user);
        assertThat(savedFolder.getName()).isEqualTo("New Folder");
        assertThat(savedFolder.getDescription()).isEqualTo("description");
        assertThat(savedFolder.getSortOrder()).isEqualTo(2);
        assertThat(savedFolder.getPath()).contains(savedFolder.getId().toString());
        verify(this.folderMapper).toResponse(savedFolder);
    }

    @Test
    void should_ThrowException_When_CreateFolderNameExists() {
        final var request = CreateFolderRequest.builder()
                .name("Existing")
                .build();
        when(this.folderRepository.existsByUserIdAndParentFolderIsNullAndNameIgnoreCaseAndDeletedAtIsNull(USER_ID,
                "Existing"))
                        .thenReturn(true);

        final var thrown = catchThrowable(() -> this.folderService.createFolder(request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.FOLDER_NAME_ALREADY_EXISTS);
        verify(this.folderRepository, never()).save(any());
    }

    @Test
    void should_ThrowException_When_CreateFolderDepthExceeded() {
        final var request = CreateFolderRequest.builder()
                .name("Child")
                .parentFolderId(PARENT_ID)
                .build();
        final var parentFolder = createFolder(PARENT_ID, null, 10, "/root/" + PARENT_ID);
        when(this.folderRepository.findByIdAndUserId(PARENT_ID, USER_ID)).thenReturn(Optional.of(parentFolder));

        final var thrown = catchThrowable(() -> this.folderService.createFolder(request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.MAX_FOLDER_DEPTH_EXCEEDED);
        verify(this.folderRepository, never()).save(any());
    }

    @Test
    void should_UpdateFolder_When_NameAndDescriptionProvided() {
        final var request = UpdateFolderRequest.builder()
                .name("  Updated Name ")
                .description("  Updated Description ")
                .build();
        final var existingFolder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        existingFolder.setName("Old Name");
        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(existingFolder));
        when(this.folderRepository.save(existingFolder)).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.folderMapper.toResponse(existingFolder)).thenReturn(FolderResponse.builder().id(FOLDER_ID).build());

        final var response = this.folderService.updateFolder(FOLDER_ID, request, USER_ID);

        assertThat(response.getId()).isEqualTo(FOLDER_ID);
        assertThat(existingFolder.getName()).isEqualTo("Updated Name");
        assertThat(existingFolder.getDescription()).isEqualTo("Updated Description");
        verify(this.folderRepository).save(existingFolder);
    }

    @Test
    void should_ThrowException_When_UpdateFolderNameConflicts() {
        final var request = UpdateFolderRequest.builder()
                .name("Conflicting")
                .build();
        final var parent = createFolder(PARENT_ID, null, 0, "/parent/" + PARENT_ID);
        final var existing = createFolder(FOLDER_ID, PARENT_ID, 1, parent.getPath() + "/" + FOLDER_ID);
        existing.setParentFolder(parent);
        existing.setName("Old");

        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(existing));
        when(this.folderRepository.existsByUserIdAndParentFolderIdAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(
                USER_ID, PARENT_ID, "Conflicting", FOLDER_ID))
                        .thenReturn(true);

        final var thrown = catchThrowable(() -> this.folderService.updateFolder(FOLDER_ID, request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.FOLDER_NAME_ALREADY_EXISTS);
        verify(this.folderRepository, never()).save(any());
    }

    @Test
    void should_MoveFolder_When_TargetDifferentParent() {
        final var targetId = UUID.randomUUID();
        final var request = MoveFolderRequest.builder()
                .targetParentFolderId(targetId)
                .build();
        final var sourceFolder = createFolder(FOLDER_ID, PARENT_ID, 1, "/root/" + PARENT_ID + "/" + FOLDER_ID);
        final var oldParent = createFolder(PARENT_ID, null, 0, "/root/" + PARENT_ID);
        sourceFolder.setParentFolder(oldParent);
        final var targetParent = createFolder(targetId, null, 1, "/root/" + targetId);
        final var descendant = createFolder(UUID.randomUUID(), FOLDER_ID, 2,
                sourceFolder.getPath() + "/" + UUID.randomUUID());

        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(sourceFolder));
        when(this.folderRepository.findByIdAndUserId(targetId, USER_ID)).thenReturn(Optional.of(targetParent));
        when(this.folderRepository.getMaxDepthInSubtree(eq(USER_ID), anyString())).thenReturn(2);
        when(this.folderRepository.existsByUserIdAndParentFolderIdAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(
                USER_ID, targetId, sourceFolder.getName(), FOLDER_ID)).thenReturn(false);
        when(this.folderRepository.getMaxSortOrderForParent(USER_ID, targetId)).thenReturn(3);
        when(this.folderRepository.findDescendantsByPath(eq(USER_ID), anyString())).thenReturn(List.of(descendant));
        when(this.folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.folderMapper.toResponse(sourceFolder)).thenReturn(FolderResponse.builder().id(FOLDER_ID).build());

        final var response = this.folderService.moveFolder(FOLDER_ID, request, USER_ID);

        assertThat(response.getId()).isEqualTo(FOLDER_ID);
        verify(this.folderRepository, times(2)).save(any(Folder.class));
        verify(this.folderRepository).findDescendantsByPath(eq(USER_ID), anyString());
        assertThat(sourceFolder.getParentFolder()).isEqualTo(targetParent);
        assertThat(sourceFolder.getSortOrder()).isEqualTo(4);
        assertThat(sourceFolder.getPath()).isEqualTo(targetParent.getPath() + "/" + sourceFolder.getId());
        assertThat(descendant.getPath()).startsWith(sourceFolder.getPath());
        assertThat(descendant.getDepth()).isEqualTo(3);
        verify(this.folderMapper).toResponse(sourceFolder);
    }

    @Test
    void should_ThrowException_When_MoveFolderIntoDescendant() {
        final var targetId = UUID.randomUUID();
        final var request = MoveFolderRequest.builder()
                .targetParentFolderId(targetId)
                .build();
        final var sourceFolder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        final var targetParent = createFolder(targetId, FOLDER_ID, 1, sourceFolder.getPath() + "/" + targetId);

        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(sourceFolder));
        when(this.folderRepository.findByIdAndUserId(targetId, USER_ID)).thenReturn(Optional.of(targetParent));

        final var thrown = catchThrowable(() -> this.folderService.moveFolder(FOLDER_ID, request, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.CIRCULAR_FOLDER_REFERENCE);
        verify(this.folderRepository, never()).save(any());
    }

    @Test
    void should_CopyFolder_When_WithinLimits() {
        final var destinationId = UUID.randomUUID();
        final var destinationParent = createFolder(destinationId, null, 1, "/root/" + destinationId);
        final var sourceFolder = createFolder(FOLDER_ID, PARENT_ID, 1, "/root/" + PARENT_ID + "/" + FOLDER_ID);
        final var child = createFolder(UUID.randomUUID(), FOLDER_ID, 2,
                sourceFolder.getPath() + "/" + UUID.randomUUID());

        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(sourceFolder));
        when(this.folderRepository.countItemsInSubtree(eq(USER_ID), anyString())).thenReturn(1L);
        when(this.folderRepository.findByIdAndUserId(destinationId, USER_ID)).thenReturn(Optional.of(destinationParent));
        when(this.folderRepository.getMaxDepthInSubtree(eq(USER_ID), anyString())).thenReturn(2);
        when(this.folderRepository.existsByUserIdAndParentFolderIdAndNameIgnoreCaseAndDeletedAtIsNull(
                USER_ID, destinationId, sourceFolder.getName())).thenReturn(false);
        when(this.folderRepository.findChildrenByUserIdAndParentId(USER_ID, FOLDER_ID)).thenReturn(List.of(child));
        when(this.folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.folderMapper.toResponse(any(Folder.class))).thenReturn(FolderResponse.builder().id(FOLDER_ID).build());

        final var response = this.folderService.copyFolder(FOLDER_ID, destinationId, null, USER_ID);

        assertThat(response.getId()).isEqualTo(FOLDER_ID);
        verify(this.folderRepository, times(2)).save(any(Folder.class));
        verify(this.folderRepository).findChildrenByUserIdAndParentId(USER_ID, FOLDER_ID);
        verify(this.folderMapper).toResponse(any(Folder.class));
    }

    @Test
    void should_ThrowException_When_CopyFolderTooLarge() {
        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID))
                .thenReturn(Optional.of(createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID)));
        when(this.folderRepository.countItemsInSubtree(eq(USER_ID), anyString())).thenReturn(600L);

        final var thrown = catchThrowable(() -> this.folderService.copyFolder(FOLDER_ID, null, null, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.FOLDER_TOO_LARGE);
        verify(this.folderRepository, never()).save(any());
    }

    @Test
    void should_DeleteFolder_When_FolderExists() {
        final var folder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        final var child = createFolder(UUID.randomUUID(), FOLDER_ID, 1, folder.getPath() + "/" + UUID.randomUUID());

        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(folder));
        when(this.folderRepository.findDescendantsByPath(eq(USER_ID), anyString())).thenReturn(List.of(child));
        when(this.deckRepository.softDeleteByFolderIds(anyList(), any(LocalDateTime.class))).thenReturn(3);
        when(this.folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.messageSource.getMessage(anyString(), any(), eq(LocaleContextHolder.getLocale())))
                .thenReturn("Deleted");

        final DeletionSummary summary = this.folderService.deleteFolder(FOLDER_ID, USER_ID);

        assertThat(summary.deletedFolders()).isEqualTo(2);
        assertThat(summary.deletedDecks()).isEqualTo(3);
        assertThat(summary.message()).isEqualTo("Deleted");
        verify(this.deckRepository).softDeleteByFolderIds(anyList(), any(LocalDateTime.class));
        verify(this.folderRepository, times(2)).save(any(Folder.class));
    }

    @Test
    void should_GetFolderById_When_FolderExists() {
        final var folder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(folder));
        when(this.folderMapper.toResponse(folder)).thenReturn(FolderResponse.builder().id(FOLDER_ID).build());

        final var response = this.folderService.getFolderById(FOLDER_ID, USER_ID);

        assertThat(response.getId()).isEqualTo(FOLDER_ID);
        verify(this.folderMapper).toResponse(folder);
    }

    @Test
    void should_ThrowException_When_GetFolderByIdNotFound() {
        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.folderService.getFolderById(FOLDER_ID, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.FOLDER_NOT_FOUND);
    }

    @Test
    void should_GetAllFolders_When_UserHasFolders() {
        final var folder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        final var response = FolderResponse.builder().id(FOLDER_ID).build();
        when(this.folderRepository.findAllByUserId(USER_ID)).thenReturn(List.of(folder));
        when(this.folderMapper.toResponse(folder)).thenReturn(response);

        final var result = this.folderService.getAllFolders(USER_ID);

        assertThat(result).containsExactly(response);
        verify(this.folderRepository).findAllByUserId(USER_ID);
        verify(this.folderMapper).toResponse(folder);
    }

    @Test
    void should_GetRootFolders_When_UserHasRootFolders() {
        final var folder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        final var response = FolderResponse.builder().id(FOLDER_ID).build();
        when(this.folderRepository.findRootFoldersByUserId(USER_ID)).thenReturn(List.of(folder));
        when(this.folderMapper.toResponse(folder)).thenReturn(response);

        final var result = this.folderService.getRootFolders(USER_ID);

        assertThat(result).containsExactly(response);
        verify(this.folderRepository).findRootFoldersByUserId(USER_ID);
    }

    @Test
    void should_GetChildFolders_When_ParentExists() {
        final var parent = createFolder(PARENT_ID, null, 0, "/root/" + PARENT_ID);
        when(this.folderRepository.findByIdAndUserId(PARENT_ID, USER_ID)).thenReturn(Optional.of(parent));
        final var child = createFolder(FOLDER_ID, PARENT_ID, 1, parent.getPath() + "/" + FOLDER_ID);
        final var response = FolderResponse.builder().id(FOLDER_ID).build();
        when(this.folderRepository.findChildrenByUserIdAndParentId(USER_ID, PARENT_ID)).thenReturn(List.of(child));
        when(this.folderMapper.toResponse(child)).thenReturn(response);

        final var result = this.folderService.getChildFolders(PARENT_ID, USER_ID);

        assertThat(result).containsExactly(response);
        verify(this.folderRepository).findChildrenByUserIdAndParentId(USER_ID, PARENT_ID);
    }

    @Test
    void should_ThrowException_When_GetChildFoldersParentMissing() {
        when(this.folderRepository.findByIdAndUserId(PARENT_ID, USER_ID)).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.folderService.getChildFolders(PARENT_ID, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.FOLDER_NOT_FOUND);
        verify(this.folderRepository, never()).findChildrenByUserIdAndParentId(any(), any());
    }

    @Test
    void should_RestoreFolder_When_SoftDeletedExists() {
        final var folder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        folder.setDeletedAt(LocalDateTime.now());
        final var child = createFolder(UUID.randomUUID(), FOLDER_ID, 1, folder.getPath() + "/" + UUID.randomUUID());
        child.setDeletedAt(LocalDateTime.now());

        when(this.folderRepository.findDeletedByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(folder));
        when(this.folderRepository.findDescendantsByPath(eq(USER_ID), anyString())).thenReturn(List.of(child));
        when(this.folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(this.folderMapper.toResponse(folder)).thenReturn(FolderResponse.builder().id(FOLDER_ID).build());

        final var response = this.folderService.restoreFolder(FOLDER_ID, USER_ID);

        assertThat(response.getId()).isEqualTo(FOLDER_ID);
        assertThat(folder.getDeletedAt()).isNull();
        assertThat(child.getDeletedAt()).isNull();
        verify(this.deckRepository).restoreByFolderIds(anyList());
        verify(this.folderRepository, times(2)).save(any(Folder.class));
    }

    @Test
    void should_ThrowException_When_RestoreFolderMissing() {
        when(this.folderRepository.findDeletedByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.folderService.restoreFolder(FOLDER_ID, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.FOLDER_NOT_FOUND);
        verify(this.folderRepository, never()).save(any());
    }

    @Test
    void should_GetFolderEntityById_When_FolderExists() {
        final var folder = createFolder(FOLDER_ID, null, 0, "/root/" + FOLDER_ID);
        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.of(folder));

        final var result = this.folderService.getFolderEntityById(FOLDER_ID, USER_ID);

        assertThat(result).isEqualTo(folder);
    }

    @Test
    void should_ThrowException_When_GetFolderEntityByIdMissing() {
        when(this.folderRepository.findByIdAndUserId(FOLDER_ID, USER_ID)).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.folderService.getFolderEntityById(FOLDER_ID, USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.FOLDER_NOT_FOUND);
    }

    private static User createUser(UUID userId) {
        final var user = User.builder()
                .email("user@example.com")
                .passwordHash("x".repeat(60))
                .build();
        user.setId(userId);
        return user;
    }

    private static Folder createFolder(UUID folderId, UUID parentId, int depth, String path) {
        final var folder = Folder.builder()
                .name("Folder")
                .depth(depth)
                .path(path == null ? "" : path)
                .sortOrder(1)
                .build();
        folder.setId(folderId != null ? folderId : UUID.randomUUID());
        if (parentId != null) {
            final var parent = Folder.builder()
                    .name("Parent")
                    .depth(depth - 1)
                    .path(path == null ? "" : path.substring(0, Math.max(path.lastIndexOf("/"), 0)))
                    .build();
            parent.setId(parentId);
            folder.setParentFolder(parent);
        }
        folder.setUser(User.builder()
                .email("owner@example.com")
                .passwordHash("x".repeat(60))
                .build());
        folder.getUser().setId(USER_ID);
        folder.setPath(path != null ? path : "/" + folder.getId());
        folder.setDepth(depth);
        return folder;
    }
}


