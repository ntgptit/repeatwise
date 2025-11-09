import { forwardRef, type ReactNode } from 'react'
import PropTypes from 'prop-types'
import { styled } from '@mui/material/styles'

const ScrollbarRoot = styled('div')(({ theme }) => ({
  height: '100%',
  maxHeight: '100%',
  overflowY: 'auto',
  overflowX: 'hidden',
  WebkitOverflowScrolling: 'touch',
  '&::-webkit-scrollbar': {
    width: 6,
  },
  '&::-webkit-scrollbar-track': {
    backgroundColor: theme.colors.alpha.black[5],
  },
  '&::-webkit-scrollbar-thumb': {
    backgroundColor: theme.colors.alpha.black[20],
    borderRadius: theme.general.borderRadiusLg,
    transition: theme.transitions.create(['background-color']),
  },
  '&::-webkit-scrollbar-thumb:hover': {
    backgroundColor: theme.colors.alpha.black[40],
  },
}))

type ScrollbarProps = {
  className?: string
  children?: ReactNode
}

const Scrollbar = forwardRef<HTMLDivElement, ScrollbarProps>(({ className, children }, ref) => (
  <ScrollbarRoot ref={ref} className={className}>
    {children}
  </ScrollbarRoot>
))

Scrollbar.propTypes = {
  children: PropTypes.node,
  className: PropTypes.string,
}

export default Scrollbar
