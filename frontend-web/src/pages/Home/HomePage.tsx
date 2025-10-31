/**
 * Home Page
 * 
 * Landing page for unauthenticated users
 * 
 * Features:
 * - Hero section
 * - Features overview
 * - Call to action (Sign up / Login)
 */

import * as React from 'react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Layout } from '@/components/common/Layout'
import { Header } from '@/components/common/Header'
import {
  BookOpen,
  Zap,
  BarChart3,
  FolderTree,
  Sparkles,
  CheckCircle2,
} from 'lucide-react'
import { ROUTES } from '@/constants/routes'

export function HomePage() {
  const features = [
    {
      icon: <BookOpen className="h-6 w-6" />,
      title: 'Smart Flashcards',
      description:
        'Create and manage flashcards with ease. Support for rich text and multimedia.',
    },
    {
      icon: <Zap className="h-6 w-6" />,
      title: 'Spaced Repetition',
      description:
        'Learn efficiently with our proven SRS algorithm. Remember more with less effort.',
    },
    {
      icon: <FolderTree className="h-6 w-6" />,
      title: 'Organize Everything',
      description:
        'Organize your decks with folders. Unlimited depth hierarchy for better organization.',
    },
    {
      icon: <BarChart3 className="h-6 w-6" />,
      title: 'Track Progress',
      description:
        'Monitor your learning progress with detailed statistics and analytics.',
    },
  ]

  return (
    <Layout className="min-h-screen">
      <Header showSearch={false} />
      <main className="flex-1">
        {/* Hero Section */}
        <section className="container mx-auto px-4 py-16 md:py-24">
          <div className="flex flex-col items-center text-center space-y-8">
            <div className="space-y-4">
              <h1 className="text-4xl font-bold tracking-tight sm:text-5xl md:text-6xl">
                Master Anything with
                <span className="text-primary"> RepeatWise</span>
              </h1>
              <p className="mx-auto max-w-[700px] text-lg text-muted-foreground md:text-xl">
                An intelligent flashcard application powered by spaced repetition
                to help you learn faster and remember longer.
              </p>
            </div>
            <div className="flex flex-col gap-4 sm:flex-row">
              <Button asChild size="lg">
                <Link to={ROUTES.REGISTER}>Get Started</Link>
              </Button>
              <Button asChild variant="outline" size="lg">
                <Link to={ROUTES.LOGIN}>Sign In</Link>
              </Button>
            </div>
          </div>
        </section>

        {/* Features Section */}
        <section className="container mx-auto px-4 py-16 md:py-24">
          <div className="space-y-12">
            <div className="text-center space-y-4">
              <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
                Powerful Features
              </h2>
              <p className="text-muted-foreground max-w-[700px] mx-auto">
                Everything you need to learn effectively
              </p>
            </div>
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
              {features.map((feature, index) => (
                <div
                  key={index}
                  className="flex flex-col items-center space-y-2 text-center p-6 rounded-lg border bg-card"
                >
                  <div className="text-primary mb-2">{feature.icon}</div>
                  <h3 className="text-lg font-semibold">{feature.title}</h3>
                  <p className="text-sm text-muted-foreground">
                    {feature.description}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* How It Works Section */}
        <section className="container mx-auto px-4 py-16 md:py-24 bg-muted/50">
          <div className="space-y-12">
            <div className="text-center space-y-4">
              <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
                How It Works
              </h2>
              <p className="text-muted-foreground max-w-[700px] mx-auto">
                Get started in minutes
              </p>
            </div>
            <div className="grid gap-8 md:grid-cols-3">
              <div className="flex flex-col items-center text-center space-y-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
                  <span className="text-xl font-bold">1</span>
                </div>
                <h3 className="text-lg font-semibold">Create Your Decks</h3>
                <p className="text-sm text-muted-foreground">
                  Organize your study materials into decks and folders
                </p>
              </div>
              <div className="flex flex-col items-center text-center space-y-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
                  <span className="text-xl font-bold">2</span>
                </div>
                <h3 className="text-lg font-semibold">Add Cards</h3>
                <p className="text-sm text-muted-foreground">
                  Create flashcards with questions and answers
                </p>
              </div>
              <div className="flex flex-col items-center text-center space-y-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
                  <span className="text-xl font-bold">3</span>
                </div>
                <h3 className="text-lg font-semibold">Review & Learn</h3>
                <p className="text-sm text-muted-foreground">
                  Review cards using spaced repetition algorithm
                </p>
              </div>
            </div>
          </div>
        </section>

        {/* CTA Section */}
        <section className="container mx-auto px-4 py-16 md:py-24">
          <div className="flex flex-col items-center text-center space-y-8 rounded-lg border bg-card p-12">
            <Sparkles className="h-12 w-12 text-primary" />
            <div className="space-y-4">
              <h2 className="text-3xl font-bold tracking-tight sm:text-4xl">
                Ready to Start Learning?
              </h2>
              <p className="text-muted-foreground max-w-[600px]">
                Join thousands of learners using RepeatWise to master their
                subjects
              </p>
            </div>
            <div className="flex flex-col gap-4 sm:flex-row">
              <Button asChild size="lg">
                <Link to={ROUTES.REGISTER}>Create Free Account</Link>
              </Button>
              <Button asChild variant="outline" size="lg">
                <Link to={ROUTES.LOGIN}>Sign In</Link>
              </Button>
            </div>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="border-t py-8">
        <div className="container mx-auto px-4">
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <div className="flex items-center gap-2">
              <span className="font-semibold text-primary">RepeatWise</span>
              <span className="text-sm text-muted-foreground">
                © {new Date().getFullYear()} All rights reserved
              </span>
            </div>
            <div className="flex items-center gap-6 text-sm text-muted-foreground">
              <Link
                to={ROUTES.HOME}
                className="hover:text-foreground transition-colors"
              >
                Privacy
              </Link>
              <Link
                to={ROUTES.HOME}
                className="hover:text-foreground transition-colors"
              >
                Terms
              </Link>
              <Link
                to={ROUTES.HOME}
                className="hover:text-foreground transition-colors"
              >
                Contact
              </Link>
            </div>
          </div>
        </div>
      </footer>
    </Layout>
  )
}

