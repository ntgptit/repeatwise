# Common Components Documentation

This directory contains reusable common components based on the wireframes specification.

## Component Categories

### 1. Layout Components (`Layout.tsx`)

**Layout**: Main wrapper for the entire application
```tsx
<Layout>
  {/* App content */}
</Layout>
```

**PageContainer**: Container with optional sidebar
```tsx
<PageContainer sidebar={<Sidebar />}>
  {/* Main content */}
</PageContainer>
```

**Section**: Content section with title, description, and actions
```tsx
<Section
  title="Section Title"
  description="Section description"
  actions={<Button>Action</Button>}
>
  {/* Section content */}
</Section>
```

### 2. Form Components (`Form.tsx`)

**FormField**: Wrapper for form fields with label and error
```tsx
<FormField label="Email" error="Invalid email" required>
  <Input />
</FormField>
```

**FormInput**: Input field with integrated FormField
```tsx
<FormInput
  label="Email"
  error="Invalid email"
  required
  type="email"
/>
```

**FormTextarea**: Textarea with integrated FormField
```tsx
<FormTextarea
  label="Description"
  error="Too long"
  maxLength={500}
/>
```

**CharacterCounter**: Displays character count
```tsx
<CharacterCounter current={100} max={500} />
```

**PasswordStrength**: Password strength indicator
```tsx
<PasswordStrength password={password} />
```

### 3. Card Components (`Cards.tsx`)

**StatCard**: Display statistics with icons and trends
```tsx
<StatCard
  title="Total Cards"
  value={120}
  description="All cards"
  icon={<Icon />}
  trend={{ value: 10, label: "vs last month", isPositive: true }}
/>
```

**ActionCard**: Card with actions
```tsx
<ActionCard
  title="Quick Actions"
  description="Common actions"
  actions={<Button>Action</Button>}
>
  {/* Card content */}
</ActionCard>
```

**InfoCard**: Display key-value pairs
```tsx
<InfoCard
  title="Deck Information"
  items={[
    { label: "Created", value: "Jan 15, 2024" },
    { label: "Cards", value: 120 }
  ]}
/>
```

### 4. Navigation Components (`Navigation.tsx`)

**Pagination**: Pagination controls
```tsx
<Pagination
  currentPage={1}
  totalPages={10}
  pageSize={20}
  totalItems={200}
  onPageChange={(page) => {}}
  onPageSizeChange={(size) => {}}
/>
```

**SearchBar**: Search input with icon
```tsx
<SearchBar
  placeholder="Search..."
  onSearch={(value) => {}}
/>
```

**FilterDropdown**: Filter dropdown
```tsx
<FilterDropdown
  label="Status"
  options={[
    { value: "all", label: "All" },
    { value: "active", label: "Active" }
  ]}
  value={selected}
  onValueChange={(value) => {}}
/>
```

**SortDropdown**: Sort dropdown
```tsx
<SortDropdown
  options={[
    { value: "name", label: "Name" },
    { value: "date", label: "Date" }
  ]}
  value={sortBy}
  onValueChange={(value) => {}}
/>
```

### 5. Display Components (`Display.tsx`)

**ProgressBar**: Progress indicator
```tsx
<ProgressBar
  value={45}
  max={100}
  label="Progress"
  showPercentage
/>
```

**StatusBadge**: Status badge with colors
```tsx
<StatusBadge status="success" label="Active" />
<StatusBadge status="error" label="Error" />
<StatusBadge status="warning" label="Warning" />
```

**KeyboardShortcutsHint**: Display keyboard shortcuts
```tsx
<KeyboardShortcutsHint
  shortcuts={[
    { key: "1", label: "Again" },
    { key: "2", label: "Hard" }
  ]}
/>
```

### 6. Modal Components (`Modals.tsx`)

**ConfirmationDialog**: Confirmation dialog
```tsx
<ConfirmationDialog
  open={isOpen}
  onOpenChange={setIsOpen}
  title="Delete Item?"
  description="This action cannot be undone"
  confirmLabel="Delete"
  variant="destructive"
  onConfirm={() => {}}
/>
```

**MultiStepWizard**: Multi-step wizard
```tsx
<MultiStepWizard
  steps={[
    { title: "Step 1", content: <Step1Content /> },
    { title: "Step 2", content: <Step2Content /> }
  ]}
  currentStep={currentStep}
  onStepChange={setCurrentStep}
  onComplete={() => {}}
/>
```

### 7. Action Components (`Actions.tsx`)

**ActionButtonGroup**: Group of action buttons
```tsx
<ActionButtonGroup
  actions={[
    { label: "Edit", onClick: () => {}, variant: "outline" },
    { label: "Delete", onClick: () => {}, variant: "destructive" }
  ]}
/>
```

**ActionMenu**: Dropdown menu with actions
```tsx
<ActionMenu
  actions={[
    { label: "Edit", onClick: () => {}, icon: <EditIcon /> },
    { label: "Delete", onClick: () => {}, variant: "destructive", separator: true }
  ]}
/>
```

**QuickActions**: Grid of quick action buttons
```tsx
<QuickActions
  actions={[
    { label: "Start Review", onClick: () => {} },
    { label: "Create Deck", onClick: () => {} }
  ]}
/>
```

### 8. File Upload Component (`FileUpload.tsx`)

**FileUpload**: Drag & drop file upload
```tsx
<FileUpload
  accept=".csv,.xlsx"
  maxSize={50 * 1024 * 1024}
  maxFiles={1}
  onFilesSelected={(files) => {}}
  onError={(error) => {}}
/>
```

## Usage Examples

### Dashboard Layout
```tsx
<Layout>
  <PageContainer sidebar={<Sidebar />}>
    <Section title="Dashboard">
      <div className="grid grid-cols-3 gap-4">
        <StatCard title="Total Cards" value={120} />
        <StatCard title="Due Cards" value={45} />
        <StatCard title="Streak" value={7} />
      </div>
    </Section>
  </PageContainer>
</Layout>
```

### Form with Validation
```tsx
<FormField label="Email" error={errors.email} required>
  <Input type="email" />
</FormField>
<CharacterCounter current={email.length} max={100} />
```

### List with Pagination
```tsx
<div>
  <SearchBar onSearch={handleSearch} />
  <FilterDropdown {...filterProps} />
  <SortDropdown {...sortProps} />
  
  {/* List items */}
  
  <Pagination {...paginationProps} />
</div>
```

## Dependencies

- `@radix-ui/react-dialog` - Modal dialogs
- `@radix-ui/react-dropdown-menu` - Dropdown menus
- `@radix-ui/react-select` - Select dropdowns
- `lucide-react` - Icons
- Shadcn/ui components (Button, Card, Input, etc.)

## Notes

- All components follow Shadcn/ui design patterns
- Components are fully typed with TypeScript
- Components support dark mode via Tailwind CSS
- All components are accessible (keyboard navigation, ARIA attributes)
- Components follow the wireframes specification from `00_docs/02-system-analysis/07-wireframes-web.md`
