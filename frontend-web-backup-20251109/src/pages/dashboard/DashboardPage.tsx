import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/slices/auth.slice'
import { APP_ROUTES } from '@/config/app.config'

export default function DashboardPage() {
  const navigate = useNavigate()
  const { user, logout } = useAuthStore()

  const handleLogout = async () => {
    await logout()
    navigate(APP_ROUTES.LOGIN)
  }

  // Mock statistics - In production, fetch from API
  const stats = {
    totalCards: 120,
    dueCards: 45,
    streakDays: 7,
    newCards: 12,
    reviewedToday: 45,
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center space-x-4">
              <h1 className="text-2xl font-bold text-blue-600">RepeatWise</h1>
              <span className="text-sm text-gray-500">Spaced Repetition System</span>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">
                Welcome, <span className="font-medium">{user?.name || user?.email}</span>
              </span>
              <button
                onClick={() => navigate(APP_ROUTES.SETTINGS)}
                className="px-3 py-2 text-sm bg-gray-100 hover:bg-gray-200 rounded-md"
              >
                Settings
              </button>
              <button
                onClick={handleLogout}
                className="px-3 py-2 text-sm bg-red-600 text-white hover:bg-red-700 rounded-md"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h2 className="text-3xl font-bold mb-8">Dashboard</h2>

        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          {/* Total Cards */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-center">
              <p className="text-gray-500 text-sm mb-2">Total Cards</p>
              <p className="text-4xl font-bold text-blue-600">{stats.totalCards}</p>
            </div>
          </div>

          {/* Due Cards */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-center">
              <p className="text-gray-500 text-sm mb-2">Due Cards</p>
              <p className="text-4xl font-bold text-orange-600">{stats.dueCards}</p>
            </div>
          </div>

          {/* Streak */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="text-center">
              <p className="text-gray-500 text-sm mb-2">Streak</p>
              <p className="text-4xl font-bold text-green-600">
                {stats.streakDays} <span className="text-2xl">🔥</span>
              </p>
              <p className="text-xs text-gray-500 mt-1">days</p>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow p-6 mb-8">
          <h3 className="text-xl font-bold mb-4">Quick Actions</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <button
              onClick={() => alert('Start Review feature coming soon!')}
              className="px-4 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            >
              Start Review
            </button>
            <button
              onClick={() => alert('Create Deck feature coming soon!')}
              className="px-4 py-3 bg-green-600 text-white rounded-md hover:bg-green-700"
            >
              Create Deck
            </button>
            <button
              onClick={() => alert('Import Cards feature coming soon!')}
              className="px-4 py-3 bg-purple-600 text-white rounded-md hover:bg-purple-700"
            >
              Import Cards
            </button>
            <button
              onClick={() => alert('View Statistics feature coming soon!')}
              className="px-4 py-3 bg-indigo-600 text-white rounded-md hover:bg-indigo-700"
            >
              View Statistics
            </button>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="bg-white rounded-lg shadow p-6 mb-8">
          <h3 className="text-xl font-bold mb-4">Recent Activity</h3>
          <ul className="space-y-3">
            <li className="flex items-center text-gray-700">
              <span className="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
              <span>Reviewed {stats.reviewedToday} cards today</span>
            </li>
            <li className="flex items-center text-gray-700">
              <span className="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
              <span>Created "Vocabulary Deck" yesterday</span>
            </li>
            <li className="flex items-center text-gray-700">
              <span className="w-2 h-2 bg-purple-500 rounded-full mr-3"></span>
              <span>Imported {stats.totalCards} cards 2 days ago</span>
            </li>
          </ul>
        </div>

        {/* Box Distribution */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-bold mb-4">Box Distribution</h3>
          <div className="space-y-3">
            {/* Simple bar chart visualization */}
            {[
              { box: 1, count: 30, color: 'bg-red-500' },
              { box: 2, count: 25, color: 'bg-orange-500' },
              { box: 3, count: 20, color: 'bg-yellow-500' },
              { box: 4, count: 15, color: 'bg-green-500' },
              { box: 5, count: 12, color: 'bg-blue-500' },
              { box: 6, count: 10, color: 'bg-indigo-500' },
              { box: 7, count: 8, color: 'bg-purple-500' },
            ].map((item) => (
              <div key={item.box} className="flex items-center">
                <span className="w-16 text-sm text-gray-600">Box {item.box}:</span>
                <div className="flex-1 flex items-center">
                  <div className="flex-1 bg-gray-200 rounded-full h-6 mr-3">
                    <div
                      className={`${item.color} h-6 rounded-full flex items-center justify-center text-white text-xs font-medium`}
                      style={{ width: `${(item.count / 30) * 100}%` }}
                    >
                      {item.count > 5 && item.count}
                    </div>
                  </div>
                  <span className="w-12 text-sm text-gray-600">{item.count} cards</span>
                </div>
              </div>
            ))}
          </div>
          <p className="mt-4 text-sm text-gray-500">
            Total: {stats.totalCards} cards across 7 boxes
          </p>
        </div>
      </main>
    </div>
  )
}
