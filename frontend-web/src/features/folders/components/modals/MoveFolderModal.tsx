import { useEffect, useMemo, useState } from 'react'
import { LoadingButton } from '@mui/lab'
import {
  Box,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  FormControlLabel,
  Radio,
  RadioGroup,
  Stack,
  Typography,
  Chip,
  alpha,
} from '@mui/material'
import {
  FolderOpen as FolderOpenIcon,
  DriveFileMove as DriveFileMoveIcon,
  Block as BlockIcon,
  Home as HomeIcon,
  Folder as FolderIcon,
  FolderOutlined as FolderOutlinedIcon,
  SubdirectoryArrowRight as SubdirectoryArrowRightIcon,
} from '@mui/icons-material'
import type { FolderTreeNode, MoveFolderRequest } from '@/api/types/folder.types'

interface MoveFolderModalProps {
  open: boolean
  folder: FolderTreeNode | null
  tree: FolderTreeNode[]
  isSubmitting: boolean
  onClose: () => void
  onSubmit: (payload: MoveFolderRequest) => Promise<void>
}

interface OptionItem {
  id: string | null
  label: string
  depth: number
}

const collectDescendantIds = (node: FolderTreeNode | null): Set<string> => {
  const ids = new Set<string>()
  if (!node) {
    return ids
  }

  const walk = (current: FolderTreeNode) => {
    ids.add(current.id)
    current.children.forEach(walk)
  }

  walk(node)
  return ids
}

const formatLabel = (name: string, depth: number) => {
  if (depth === 0) {
    return name
  }
  return `${'  '.repeat(depth)}• ${name}`
}

const flattenTree = (nodes: FolderTreeNode[], depth = 0): OptionItem[] => {
  const items: OptionItem[] = []
  nodes.forEach((node) => {
    items.push({
      id: node.id,
      label: node.name,
      depth,
    })
    items.push(...flattenTree(node.children, depth + 1))
  })
  return items
}

export const MoveFolderModal = ({
  open,
  folder,
  tree,
  isSubmitting,
  onClose,
  onSubmit,
}: MoveFolderModalProps) => {
  const invalidIds = useMemo(() => collectDescendantIds(folder), [folder])
  const options = useMemo<OptionItem[]>(() => {
    return [{ id: null, label: 'Root folder', depth: 0 }, ...flattenTree(tree)]
  }, [tree])

  const [selected, setSelected] = useState<string | null>(() => folder?.parentFolderId ?? null)

  useEffect(() => {
    if (open) {
      setSelected(folder?.parentFolderId ?? null)
    }
  }, [open, folder])

  const handleSubmit = async () => {
    await onSubmit({
      targetParentFolderId: selected,
    })
  }

  const handleChange = (value: string) => {
    if (value === 'null') {
      setSelected(null)
      return
    }
    setSelected(value)
  }

  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="sm"
      PaperProps={{
        sx: {
          borderRadius: 3,
          boxShadow: (theme) => theme.shadows[24],
        },
      }}
    >
      <DialogTitle
        sx={{
          pb: 2,
          pt: 3,
          px: 3,
          background: (theme) =>
            theme.palette.mode === 'dark'
              ? `linear-gradient(135deg, ${alpha(theme.palette.primary.dark, 0.2)} 0%, ${alpha(theme.palette.primary.dark, 0.05)} 100%)`
              : `linear-gradient(135deg, ${alpha(theme.palette.primary.light, 0.15)} 0%, ${alpha(theme.palette.primary.light, 0.03)} 100%)`,
        }}
      >
        <Stack direction="row" alignItems="center" spacing={1.5} mb={1}>
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              width: 40,
              height: 40,
              borderRadius: 2,
              bgcolor: 'primary.main',
              color: 'primary.contrastText',
            }}
          >
            <DriveFileMoveIcon />
          </Box>
          <Typography variant="h5" fontWeight={700}>
            Move Folder
          </Typography>
        </Stack>
        <Typography variant="body2" color="text.secondary" sx={{ ml: 7 }}>
          Select the destination for <strong>{folder?.name}</strong>
        </Typography>
      </DialogTitle>
      <Divider />
      <DialogContent sx={{ px: 3, py: 3 }}>
        <Stack spacing={3}>
          {/* Current Folder Info */}
          <Box
            sx={{
              p: 2.5,
              borderRadius: 2.5,
              border: (theme) => `2px solid ${alpha(theme.palette.primary.main, 0.2)}`,
              backgroundColor: (theme) =>
                theme.palette.mode === 'dark'
                  ? alpha(theme.palette.primary.dark, 0.1)
                  : alpha(theme.palette.primary.light, 0.08),
              position: 'relative',
              overflow: 'hidden',
              '&::before': {
                content: '""',
                position: 'absolute',
                top: 0,
                left: 0,
                width: 4,
                height: '100%',
                bgcolor: 'primary.main',
              },
            }}
          >
            <Stack direction="row" alignItems="center" spacing={1.5} mb={1}>
              <FolderOpenIcon sx={{ color: 'primary.main', fontSize: 20 }} />
              <Typography variant="subtitle2" color="text.secondary" fontWeight={600} textTransform="uppercase">
                Current Location
              </Typography>
            </Stack>
            <Typography variant="h6" fontWeight={700} sx={{ mb: 0.5, ml: 4.5 }}>
              {folder?.name}
            </Typography>
            <Typography variant="caption" color="text.secondary" sx={{ ml: 4.5, display: 'block' }}>
              Choose a new destination from the list below
            </Typography>
          </Box>

          {/* Destination Selection */}
          <Box>
            <Typography variant="subtitle2" fontWeight={600} mb={1.5} color="text.secondary">
              SELECT DESTINATION
            </Typography>
            <Box
              sx={{
                borderRadius: 2.5,
                border: (theme) => `1px solid ${theme.palette.divider}`,
                backgroundColor: 'background.paper',
                maxHeight: 360,
                overflowY: 'auto',
                boxShadow: (theme) => `inset 0 1px 3px ${alpha(theme.palette.common.black, 0.05)}`,
                '&::-webkit-scrollbar': {
                  width: '8px',
                },
                '&::-webkit-scrollbar-track': {
                  backgroundColor: 'transparent',
                },
                '&::-webkit-scrollbar-thumb': {
                  backgroundColor: (theme) => alpha(theme.palette.primary.main, 0.3),
                  borderRadius: '4px',
                  '&:hover': {
                    backgroundColor: (theme) => alpha(theme.palette.primary.main, 0.5),
                  },
                },
              }}
            >
              <RadioGroup
                value={selected === null ? 'null' : selected ?? ''}
                onChange={(event) => handleChange(event.target.value)}
              >
                {options.map((option) => {
                  const disabled = option.id ? invalidIds.has(option.id) : false
                  const isSelected = selected === option.id
                  const isRoot = option.id === null

                  return (
                    <FormControlLabel
                      key={option.id ?? 'root'}
                      value={option.id === null ? 'null' : option.id}
                      control={
                        <Radio
                          size="small"
                          sx={{
                            '&.Mui-checked': {
                              color: 'primary.main',
                            },
                          }}
                        />
                      }
                      disabled={disabled}
                      label={
                        <Stack direction="row" alignItems="center" spacing={1} width="100%">
                          {/* Indentation for nested folders */}
                          {option.depth > 0 && (
                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                              {Array.from({ length: option.depth }).map((_, idx) => (
                                <SubdirectoryArrowRightIcon
                                  key={idx}
                                  sx={{
                                    fontSize: 16,
                                    color: 'text.disabled',
                                    opacity: 0.4,
                                    ml: idx === 0 ? 0 : -0.5,
                                  }}
                                />
                              ))}
                            </Box>
                          )}

                          {/* Folder Icon */}
                          {isRoot ? (
                            <HomeIcon
                              sx={{
                                fontSize: 20,
                                color: disabled ? 'text.disabled' : 'primary.main',
                              }}
                            />
                          ) : (
                            <>
                              {isSelected && !disabled ? (
                                <FolderIcon
                                  sx={{
                                    fontSize: 20,
                                    color: disabled ? 'text.disabled' : 'warning.main',
                                  }}
                                />
                              ) : (
                                <FolderOutlinedIcon
                                  sx={{
                                    fontSize: 20,
                                    color: disabled ? 'text.disabled' : 'action.active',
                                  }}
                                />
                              )}
                            </>
                          )}

                          <Box flex={1}>
                            <Typography
                              variant="body2"
                              fontWeight={isSelected ? 600 : 500}
                              sx={{
                                color: disabled ? 'text.disabled' : 'text.primary',
                              }}
                            >
                              {option.id === null ? 'Root Folder' : option.label}
                            </Typography>
                            {disabled && (
                              <Stack direction="row" alignItems="center" spacing={0.5} mt={0.5}>
                                <BlockIcon sx={{ fontSize: 12, color: 'error.main' }} />
                                <Typography variant="caption" color="error.main">
                                  Cannot move into descendant
                                </Typography>
                              </Stack>
                            )}
                          </Box>
                          {isSelected && !disabled && (
                            <Chip
                              label="Selected"
                              size="small"
                              color="primary"
                              sx={{
                                height: 20,
                                fontSize: '0.7rem',
                                fontWeight: 600,
                              }}
                            />
                          )}
                        </Stack>
                      }
                      sx={{
                        alignItems: 'center',
                        px: 2,
                        py: 1.5,
                        m: 0,
                        borderBottom: (theme) => `1px solid ${theme.palette.divider}`,
                        transition: 'all 0.2s ease',
                        '&:last-child': {
                          borderBottom: 'none',
                        },
                        '&:hover': {
                          backgroundColor: (theme) =>
                            disabled ? 'transparent' : alpha(theme.palette.primary.main, 0.05),
                        },
                        '& .MuiRadio-root': {
                          p: 0.5,
                        },
                        ...(isSelected &&
                          !disabled && {
                            backgroundColor: (theme) => alpha(theme.palette.primary.main, 0.08),
                          }),
                      }}
                    />
                  )
                })}
              </RadioGroup>
            </Box>
          </Box>
        </Stack>
      </DialogContent>
      <Divider />
      <DialogActions
        sx={{
          px: 3,
          py: 2.5,
          gap: 1.5,
          backgroundColor: (theme) =>
            theme.palette.mode === 'dark' ? alpha(theme.palette.background.paper, 0.5) : 'grey.50',
        }}
      >
        <LoadingButton
          onClick={onClose}
          color="inherit"
          disabled={isSubmitting}
          variant="outlined"
          sx={{
            px: 3,
            py: 1,
            borderRadius: 2,
            textTransform: 'none',
            fontWeight: 600,
          }}
        >
          Cancel
        </LoadingButton>
        <LoadingButton
          loading={isSubmitting}
          onClick={handleSubmit}
          variant="contained"
          sx={{
            px: 3,
            py: 1,
            borderRadius: 2,
            textTransform: 'none',
            fontWeight: 600,
            boxShadow: 2,
            '&:hover': {
              boxShadow: 4,
            },
          }}
        >
          Move Folder
        </LoadingButton>
      </DialogActions>
    </Dialog>
  )
}

export default MoveFolderModal

