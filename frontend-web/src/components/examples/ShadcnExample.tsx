import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

/**
 * Example usage of shadcn/ui components
 * 
 * This file demonstrates how to use the shadcn/ui components
 * that have been set up in the project.
 */
export function ExampleUsage() {
  return (
    <div className="container mx-auto p-8 space-y-8">
      {/* Button Examples */}
      <Card>
        <CardHeader>
          <CardTitle>Button Examples</CardTitle>
          <CardDescription>Various button variants</CardDescription>
        </CardHeader>
        <CardContent className="flex gap-4">
          <Button variant="default">Default</Button>
          <Button variant="destructive">Destructive</Button>
          <Button variant="outline">Outline</Button>
          <Button variant="secondary">Secondary</Button>
          <Button variant="ghost">Ghost</Button>
          <Button variant="link">Link</Button>
        </CardContent>
      </Card>

      {/* Form Example */}
      <Card>
        <CardHeader>
          <CardTitle>Form Example</CardTitle>
          <CardDescription>Using Input and Label components</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" placeholder="Enter your email" />
          </div>
          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input id="password" type="password" placeholder="Enter your password" />
          </div>
          <Button>Submit</Button>
        </CardContent>
      </Card>
    </div>
  )
}
