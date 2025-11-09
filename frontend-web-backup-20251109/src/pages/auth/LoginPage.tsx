import { Card, CardBody, CardHeader } from '@/design-system/components/patterns/Card'
import LoginForm from '@/features/auth/components/LoginForm/LoginForm'

export default function LoginPage() {
  return (
    <div className="relative flex min-h-screen items-center justify-center bg-gradient-to-br from-slate-50 via-white to-slate-100 px-4 py-12 sm:px-8 sm:py-16">
      <div className="absolute inset-0 -z-10 bg-[radial-gradient(circle_at_top,_rgba(59,130,246,0.12),_transparent_45%),_radial-gradient(circle_at_bottom,_rgba(14,165,233,0.12),_transparent_45%)]" />

      <div className="w-full max-w-lg">
        <Card elevated className="space-y-8 p-8 shadow-xl sm:p-10">
          <CardHeader className="mb-2 flex flex-col items-center gap-3 text-center sm:gap-4" title="Đăng nhập">
            <div className="space-y-2">
              <div>
                <span className="text-sm font-semibold uppercase tracking-[0.3em] text-primary/70">
                  RepeatWise
                </span>
              </div>
              <h2 className="text-3xl font-bold text-foreground sm:text-4xl">Chào mừng trở lại</h2>
              <p className="text-sm text-muted-foreground sm:text-base">
                Đăng nhập để tiếp tục lộ trình ghi nhớ thông minh của bạn
              </p>
            </div>
          </CardHeader>

          <CardBody className="space-y-6">
            <LoginForm />
          </CardBody>
        </Card>
      </div>
    </div>
  )
}
