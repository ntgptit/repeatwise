import RegisterForm from '@/features/auth/components/RegisterForm/RegisterForm'

export default function RegisterPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <div className="w-full max-w-md space-y-8 p-8 bg-white rounded-lg shadow-md">
        <div className="text-center">
          <h1 className="text-3xl font-bold">Register</h1>
          <p className="mt-2 text-gray-600">
            Create a new account
          </p>
        </div>
        <RegisterForm />
      </div>
    </div>
  );
}
