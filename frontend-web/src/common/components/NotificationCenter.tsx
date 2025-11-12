import { Fragment, useEffect, useState } from 'react'
import { Alert, Slide, Snackbar, type SnackbarCloseReason, type SnackbarOrigin } from '@mui/material'
import {
  notificationService,
  type NotificationInstance,
} from '@/common/services/notification.service'

const defaultOrigin: SnackbarOrigin = { vertical: 'top', horizontal: 'right' }

const getSeverity = (type: NotificationInstance['type']) => {
  switch (type) {
    case 'error':
      return 'error'
    case 'success':
      return 'success'
    case 'warning':
      return 'warning'
    case 'info':
    default:
      return 'info'
  }
}

const parsePosition = (position?: NotificationInstance['position']): SnackbarOrigin => {
  if (!position) {
    return defaultOrigin
  }

  if (typeof position === 'object') {
    return position as SnackbarOrigin
  }

  const [verticalPart, horizontalPart] = position.split('-')
  const vertical: SnackbarOrigin['vertical'] = verticalPart === 'bottom' ? 'bottom' : 'top'
  const horizontal: SnackbarOrigin['horizontal'] =
    horizontalPart === 'center' || horizontalPart === 'left' ? horizontalPart : 'right'

  return { vertical, horizontal }
}

export const NotificationCenter = () => {
  const [notifications, setNotifications] = useState<NotificationInstance[]>([])

  useEffect(() => {
    return notificationService.subscribe(setNotifications)
  }, [])

  const handleClose = (id: number, reason?: SnackbarCloseReason) => {
    if (reason === 'clickaway') {
      return
    }

    notificationService.close(id)
  }

  return (
    <>
      {notifications.map((notification, index) => (
        <Snackbar
          key={notification.id}
          open
          onClose={(_, reason) => handleClose(notification.id, reason)}
          autoHideDuration={notification.autoClose ? notification.duration : undefined}
          anchorOrigin={parsePosition(notification.position)}
          TransitionComponent={(props) => <Slide {...props} direction="left" />}
          sx={{ mt: index > 0 ? 1 : 0 }}
        >
          <Alert
            variant="filled"
            severity={getSeverity(notification.type ?? 'info')}
            onClose={notification.closeable ? () => notificationService.close(notification.id) : undefined}
            sx={{ width: '100%' }}
          >
            {notification.title ? (
              <Fragment>
                <strong>{notification.title}</strong>
                <br />
                {notification.message}
              </Fragment>
            ) : (
              notification.message
            )}
          </Alert>
        </Snackbar>
      ))}
    </>
  )
}

export default NotificationCenter

