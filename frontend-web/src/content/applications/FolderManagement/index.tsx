/**
 * FolderManagement Page
 * Main page for managing folders (UC-007 to UC-011)
 */

import { useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { Container, Grid, Card, CardContent, Typography, Box, Button } from '@mui/material'
import { Add as AddIcon } from '@mui/icons-material'
import PageTitle from '@/components/PageTitle'
import PageTitleWrapper from '@/components/PageTitleWrapper'
import { useFolders } from '@/hooks/useFolders'
import FolderTree from './components/FolderTree'
import CreateFolderModal from './components/CreateFolderModal'
import RenameFolderModal from './components/RenameFolderModal'
import MoveFolderModal from './components/MoveFolderModal'
import CopyFolderModal from './components/CopyFolderModal'
import DeleteFolderDialog from './components/DeleteFolderDialog'
import type { FolderResponse } from '@/api/types/folder.types'

const FolderManagement = () => {
  const [selectedFolderId, setSelectedFolderId] = useState<string | null>(null)

  // Modal states
  const [createModalOpen, setCreateModalOpen] = useState(false)
  const [createParentId, setCreateParentId] = useState<string | null>(null)
  const [createParentName, setCreateParentName] = useState<string>('Root')

  const [renameModalOpen, setRenameModalOpen] = useState(false)
  const [renameFolder, setRenameFolder] = useState<FolderResponse | null>(null)

  const [moveModalOpen, setMoveModalOpen] = useState(false)
  const [moveFolder, setMoveFolder] = useState<FolderResponse | null>(null)

  const [copyModalOpen, setCopyModalOpen] = useState(false)
  const [copyFolder, setCopyFolder] = useState<FolderResponse | null>(null)

  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [deleteFolder, setDeleteFolder] = useState<FolderResponse | null>(null)

  // Fetch folders
  const { data: folders = [], isLoading, error } = useFolders()

  // Handlers
  const handleCreateFolder = (parentId?: string | null) => {
    const parent = parentId ? folders.find((f) => f.id === parentId) : null
    setCreateParentId(parentId || null)
    setCreateParentName(parent?.name || 'Root')
    setCreateModalOpen(true)
  }

  const handleRenameFolder = (folder: FolderResponse) => {
    setRenameFolder(folder)
    setRenameModalOpen(true)
  }

  const handleMoveFolder = (folder: FolderResponse) => {
    setMoveFolder(folder)
    setMoveModalOpen(true)
  }

  const handleCopyFolder = (folder: FolderResponse) => {
    setCopyFolder(folder)
    setCopyModalOpen(true)
  }

  const handleDeleteFolder = (folder: FolderResponse) => {
    setDeleteFolder(folder)
    setDeleteDialogOpen(true)
  }

  return (
    <>
      <Helmet>
        <title>Folder Management - RepeatWise</title>
      </Helmet>

      <PageTitleWrapper>
        <PageTitle
          heading="Folder Management"
          subHeading="Organize your decks with folders. Create, rename, move, copy, and delete folders."
          docs="https://docs.repeatwise.com/folder-management"
        />
      </PageTitleWrapper>

      <Container maxWidth="lg">
        <Grid container spacing={3}>
          {/* Folder Tree */}
          <Grid item xs={12} md={6}>
            <FolderTree
              folders={folders}
              isLoading={isLoading}
              error={error}
              selectedFolderId={selectedFolderId}
              onSelectFolder={setSelectedFolderId}
              onCreateFolder={handleCreateFolder}
              onRenameFolder={handleRenameFolder}
              onMoveFolder={handleMoveFolder}
              onCopyFolder={handleCopyFolder}
              onDeleteFolder={handleDeleteFolder}
            />
          </Grid>

          {/* Folder Details / Actions */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                {selectedFolderId ? (
                  (() => {
                    const selectedFolder = folders.find((f) => f.id === selectedFolderId)
                    return selectedFolder ? (
                      <Box>
                        <Typography variant="h5" gutterBottom>
                          {selectedFolder.name}
                        </Typography>
                        {selectedFolder.description && (
                          <Typography variant="body2" color="text.secondary" paragraph>
                            {selectedFolder.description}
                          </Typography>
                        )}

                        <Box sx={{ mt: 3 }}>
                          <Typography variant="subtitle2" gutterBottom>
                            Information
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            • Depth: Level {selectedFolder.depth}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            • Decks: {selectedFolder.deckCount || 0}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            • Created: {new Date(selectedFolder.createdAt).toLocaleDateString()}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            • Updated: {new Date(selectedFolder.updatedAt).toLocaleDateString()}
                          </Typography>
                        </Box>

                        <Box sx={{ mt: 3, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                          <Button
                            variant="outlined"
                            size="small"
                            onClick={() => handleCreateFolder(selectedFolder.id)}
                          >
                            New Subfolder
                          </Button>
                          <Button
                            variant="outlined"
                            size="small"
                            onClick={() => handleRenameFolder(selectedFolder)}
                          >
                            Rename
                          </Button>
                          <Button
                            variant="outlined"
                            size="small"
                            onClick={() => handleMoveFolder(selectedFolder)}
                          >
                            Move
                          </Button>
                          <Button
                            variant="outlined"
                            size="small"
                            onClick={() => handleCopyFolder(selectedFolder)}
                          >
                            Copy
                          </Button>
                          <Button
                            variant="outlined"
                            size="small"
                            color="error"
                            onClick={() => handleDeleteFolder(selectedFolder)}
                          >
                            Delete
                          </Button>
                        </Box>
                      </Box>
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        Folder not found
                      </Typography>
                    )
                  })()
                ) : (
                  <Box textAlign="center" py={4}>
                    <Typography variant="h6" color="text.secondary" gutterBottom>
                      No folder selected
                    </Typography>
                    <Typography variant="body2" color="text.secondary" paragraph>
                      Select a folder from the tree to view details and perform actions.
                    </Typography>
                    <Button
                      variant="contained"
                      startIcon={<AddIcon />}
                      onClick={() => handleCreateFolder(null)}
                    >
                      Create Root Folder
                    </Button>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Container>

      {/* Modals */}
      <CreateFolderModal
        open={createModalOpen}
        onClose={() => setCreateModalOpen(false)}
        parentId={createParentId}
        parentName={createParentName}
      />

      <RenameFolderModal
        open={renameModalOpen}
        onClose={() => setRenameModalOpen(false)}
        folder={renameFolder}
      />

      <MoveFolderModal
        open={moveModalOpen}
        onClose={() => setMoveModalOpen(false)}
        folder={moveFolder}
      />

      <CopyFolderModal
        open={copyModalOpen}
        onClose={() => setCopyModalOpen(false)}
        folder={copyFolder}
      />

      <DeleteFolderDialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
        folder={deleteFolder}
      />
    </>
  )
}

export default FolderManagement
