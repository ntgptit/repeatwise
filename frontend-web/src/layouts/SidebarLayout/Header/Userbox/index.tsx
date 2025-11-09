import { useRef, useState } from 'react'

import { NavLink, useNavigate } from 'react-router-dom'

import {
  Avatar,
  Box,
  Button,
  Divider,
  Hidden,
  lighten,
  List,
  ListItem,
  ListItemText,
  Popover,
  Typography,
} from '@mui/material'

import InboxTwoToneIcon from '@mui/icons-material/InboxTwoTone'
import { styled } from '@mui/material/styles'
import ExpandMoreTwoToneIcon from '@mui/icons-material/ExpandMoreTwoTone'
import AccountBoxTwoToneIcon from '@mui/icons-material/AccountBoxTwoTone'
import LockOpenTwoToneIcon from '@mui/icons-material/LockOpenTwoTone'
import AccountTreeTwoToneIcon from '@mui/icons-material/AccountTreeTwoTone'
import { useAuthStore } from '@/store/slices/auth.slice'

const UserBoxButton = styled(Button)(
  ({ theme }) => `
        padding-left: ${theme.spacing(1)};
        padding-right: ${theme.spacing(1)};
`
)

const MenuUserBox = styled(Box)(
  ({ theme }) => `
        background: ${theme.colors.alpha.black[5]};
        padding: ${theme.spacing(2)};
`
)

const UserBoxText = styled(Box)(
  ({ theme }) => `
        text-align: left;
        padding-left: ${theme.spacing(1)};
`
)

const UserBoxLabel = styled(Typography)(
  ({ theme }) => `
        font-weight: ${theme.typography.fontWeightBold};
        color: ${theme.palette.secondary.main};
        display: block;
`
)

const UserBoxDescription = styled(Typography)(
  ({ theme }) => `
        color: ${lighten(theme.palette.secondary.main, 0.5)}
`
)

function HeaderUserbox() {
  const navigate = useNavigate()
  const { user, logout } = useAuthStore()

  const displayName = user?.name || user?.username || user?.email || 'User'
  const displayEmail = user?.email || ''

  const ref = useRef<HTMLButtonElement | null>(null)
  const [isOpen, setOpen] = useState<boolean>(false)

  const handleOpen = (): void => {
    setOpen(true)
  }

  const handleClose = (): void => {
    setOpen(false)
  }

  const handleLogout = async (): void => {
    try {
      await logout()
      navigate('/login')
    } catch (error) {
      console.error('Logout failed:', error)
      // Even if logout fails, redirect to login
      navigate('/login')
    }
  }

  return (
    <>
      <UserBoxButton color="secondary" ref={ref} onClick={handleOpen}>
        <Avatar variant="rounded" alt={displayName}>
          {displayName.charAt(0).toUpperCase()}
        </Avatar>
        <Hidden mdDown>
          <UserBoxText>
            <UserBoxLabel variant="body1">{displayName}</UserBoxLabel>
            <UserBoxDescription variant="body2">{displayEmail}</UserBoxDescription>
          </UserBoxText>
        </Hidden>
        <Hidden smDown>
          <ExpandMoreTwoToneIcon sx={{ ml: 1 }} />
        </Hidden>
      </UserBoxButton>
      <Popover
        anchorEl={ref.current}
        onClose={handleClose}
        open={isOpen}
        anchorOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right',
        }}
      >
        <MenuUserBox sx={{ minWidth: 210 }} display="flex">
          <Avatar variant="rounded" alt={displayName}>
            {displayName.charAt(0).toUpperCase()}
          </Avatar>
          <UserBoxText>
            <UserBoxLabel variant="body1">{displayName}</UserBoxLabel>
            <UserBoxDescription variant="body2">{displayEmail}</UserBoxDescription>
          </UserBoxText>
        </MenuUserBox>
        <Divider sx={{ mb: 0 }} />
        <List sx={{ p: 1 }} component="nav">
          <ListItem button to="/settings" component={NavLink} onClick={handleClose}>
            <AccountTreeTwoToneIcon fontSize="small" />
            <ListItemText primary="Settings" />
          </ListItem>
        </List>
        <Divider />
        <Box sx={{ m: 1 }}>
          <Button color="primary" fullWidth onClick={handleLogout}>
            <LockOpenTwoToneIcon sx={{ mr: 1 }} />
            Sign out
          </Button>
        </Box>
      </Popover>
    </>
  )
}

export default HeaderUserbox
