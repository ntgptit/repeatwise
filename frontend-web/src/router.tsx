import {
  Suspense,
  lazy,
  type ComponentType,
  type LazyExoticComponent,
  type ReactElement,
} from 'react'
import { Navigate, useRoutes, type RouteObject } from 'react-router-dom'

import SidebarLayout from 'src/layouts/SidebarLayout'
import BaseLayout from 'src/layouts/BaseLayout'

import SuspenseLoader from 'src/components/SuspenseLoader'

const loadable = <P extends object>(Component: LazyExoticComponent<ComponentType<P>>) => {
  const LoadableComponent = (props: P): ReactElement => (
    <Suspense fallback={<SuspenseLoader />}>
      <Component {...props} />
    </Suspense>
  )

  return LoadableComponent
}

// Pages
const Overview = loadable(lazy(() => import('src/content/overview')))
const Login = loadable(lazy(() => import('src/content/pages/Login')))
const Register = loadable(lazy(() => import('src/content/pages/Register')))
const Settings = loadable(lazy(() => import('src/content/pages/Settings')))

// Dashboards
const Crypto = loadable(lazy(() => import('src/content/dashboards/Crypto')))

// Applications
const Messenger = loadable(lazy(() => import('src/content/applications/Messenger')))
const Transactions = loadable(lazy(() => import('src/content/applications/Transactions')))
const UserProfile = loadable(lazy(() => import('src/content/applications/Users/profile')))
const UserSettings = loadable(lazy(() => import('src/content/applications/Users/settings')))

// Folder Management
const FolderManagement = loadable(
  lazy(() => import('src/features/folders/components/FolderManagementPage'))
)

// Components
const Buttons = loadable(lazy(() => import('src/content/pages/Components/Buttons')))
const Modals = loadable(lazy(() => import('src/content/pages/Components/Modals')))
const Accordions = loadable(lazy(() => import('src/content/pages/Components/Accordions')))
const Tabs = loadable(lazy(() => import('src/content/pages/Components/Tabs')))
const Badges = loadable(lazy(() => import('src/content/pages/Components/Badges')))
const Tooltips = loadable(lazy(() => import('src/content/pages/Components/Tooltips')))
const Avatars = loadable(lazy(() => import('src/content/pages/Components/Avatars')))
const Cards = loadable(lazy(() => import('src/content/pages/Components/Cards')))
const Forms = loadable(lazy(() => import('src/content/pages/Components/Forms')))

// Status
const Status404 = loadable(lazy(() => import('src/content/pages/Status/Status404')))
const Status500 = loadable(lazy(() => import('src/content/pages/Status/Status500')))
const StatusComingSoon = loadable(lazy(() => import('src/content/pages/Status/ComingSoon')))
const StatusMaintenance = loadable(lazy(() => import('src/content/pages/Status/Maintenance')))

const routes: RouteObject[] = [
  {
    path: '',
    element: <BaseLayout />,
    children: [
      {
        path: '/',
        element: <Navigate to="/workspace/folders" replace />,
      },
      {
        path: 'login',
        element: <Login />,
      },
      {
        path: 'register',
        element: <Register />,
      },
      {
        path: 'overview',
        element: <Overview />,
      },
      {
        path: 'status',
        children: [
          {
            path: '',
            element: <Navigate to="404" replace />,
          },
          {
            path: '404',
            element: <Status404 />,
          },
          {
            path: '500',
            element: <Status500 />,
          },
          {
            path: 'maintenance',
            element: <StatusMaintenance />,
          },
          {
            path: 'coming-soon',
            element: <StatusComingSoon />,
          },
        ],
      },
      {
        path: '*',
        element: <Status404 />,
      },
    ],
  },
  {
    path: 'workspace',
    element: <SidebarLayout />,
    children: [
      {
        path: '',
        element: <Navigate to="folders" replace />,
      },
      {
        path: 'folders',
        element: <FolderManagement />,
      },
    ],
  },
  {
    path: 'dashboards',
    element: <SidebarLayout />,
    children: [
      {
        path: '',
        element: <Navigate to="crypto" replace />,
      },
      {
        path: 'crypto',
        element: <Crypto />,
      },
      {
        path: 'messenger',
        element: <Messenger />,
      },
    ],
  },
  {
    path: 'management',
    element: <SidebarLayout />,
    children: [
      {
        path: '',
        element: <Navigate to="transactions" replace />,
      },
      {
        path: 'transactions',
        element: <Transactions />,
      },
      {
        path: 'profile',
        children: [
          {
            path: '',
            element: <Navigate to="details" replace />,
          },
          {
            path: 'details',
            element: <UserProfile />,
          },
          {
            path: 'settings',
            element: <UserSettings />,
          },
        ],
      },
    ],
  },
  {
    path: 'settings',
    element: <SidebarLayout />,
    children: [
      {
        path: '',
        element: <Settings />,
      },
    ],
  },
  {
    path: '/components',
    element: <SidebarLayout />,
    children: [
      {
        path: '',
        element: <Navigate to="buttons" replace />,
      },
      {
        path: 'buttons',
        element: <Buttons />,
      },
      {
        path: 'modals',
        element: <Modals />,
      },
      {
        path: 'accordions',
        element: <Accordions />,
      },
      {
        path: 'tabs',
        element: <Tabs />,
      },
      {
        path: 'badges',
        element: <Badges />,
      },
      {
        path: 'tooltips',
        element: <Tooltips />,
      },
      {
        path: 'avatars',
        element: <Avatars />,
      },
      {
        path: 'cards',
        element: <Cards />,
      },
      {
        path: 'forms',
        element: <Forms />,
      },
    ],
  },
]

const AppRoutes = (): ReactElement | null => {
  return useRoutes(routes)
}

export default AppRoutes
