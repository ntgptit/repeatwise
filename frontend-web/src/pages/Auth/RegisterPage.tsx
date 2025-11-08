import RegisterForm from '@/features/auth/components/RegisterForm/RegisterForm'

export default function RegisterPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-md space-y-8 p-8 bg-white rounded-lg shadow-md">
        <div className="text-center">
          <div className="mb-6">
            <h2 className="text-4xl font-bold text-blue-600">RepeatWise</h2>
            <p className="text-sm text-gray-500 mt-1">Spaced Repetition System</p>
          </div>
          <h1 className="text-3xl font-bold">Create Account</h1>
          <p className="mt-2 text-gray-600">
            Register to start learning
          </p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
}
