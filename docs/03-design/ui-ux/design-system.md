# Design System - RepeatWise

## 1. Brand Identity

### 1.1 Brand Values
- **Learning**: Continuous improvement and knowledge acquisition
- **Consistency**: Reliable and predictable learning patterns
- **Progress**: Visual feedback and achievement tracking
- **Simplicity**: Clean and intuitive user experience

### 1.2 Brand Colors

#### Primary Colors
- **Primary Blue**: `#2563EB` - Main brand color, used for primary actions and highlights
- **Primary Blue Light**: `#3B82F6` - Hover states and secondary elements
- **Primary Blue Dark**: `#1D4ED8` - Active states and emphasis

#### Secondary Colors
- **Success Green**: `#10B981` - Positive actions, completion states
- **Warning Orange**: `#F59E0B` - Warnings, pending states
- **Error Red**: `#EF4444` - Errors, destructive actions
- **Info Blue**: `#06B6D4` - Information, neutral states

#### Neutral Colors
- **Gray 50**: `#F9FAFB` - Background colors
- **Gray 100**: `#F3F4F6` - Light borders and dividers
- **Gray 200**: `#E5E7EB` - Borders and separators
- **Gray 300**: `#D1D5DB` - Disabled states
- **Gray 400**: `#9CA3AF` - Placeholder text
- **Gray 500**: `#6B7280` - Secondary text
- **Gray 600**: `#4B5563` - Body text
- **Gray 700**: `#374151` - Headings
- **Gray 800**: `#1F2937` - Primary headings
- **Gray 900**: `#111827` - Dark mode text

### 1.3 Typography

#### Font Family
- **Primary Font**: Inter (Google Fonts)
- **Fallback**: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif

#### Font Sizes
- **Display Large**: `48px/60px` - Hero sections, main headlines
- **Display Medium**: `36px/44px` - Section headlines
- **Display Small**: `30px/38px` - Subsection headlines
- **Heading Large**: `24px/32px` - Page titles
- **Heading Medium**: `20px/28px` - Section titles
- **Heading Small**: `18px/26px` - Subsection titles
- **Body Large**: `16px/24px` - Body text, descriptions
- **Body Medium**: `14px/20px` - Default body text
- **Body Small**: `12px/16px` - Captions, metadata
- **Caption**: `10px/14px` - Small labels, timestamps

#### Font Weights
- **Light**: 300 - Subtle text, captions
- **Regular**: 400 - Body text, default
- **Medium**: 500 - Emphasis, buttons
- **Semibold**: 600 - Headings, important text
- **Bold**: 700 - Strong emphasis, primary headings

### 1.4 Spacing System

#### Base Unit: 4px
- **4px**: Extra small spacing (between related elements)
- **8px**: Small spacing (between elements in same group)
- **12px**: Medium spacing (between different groups)
- **16px**: Large spacing (between sections)
- **24px**: Extra large spacing (between major sections)
- **32px**: Huge spacing (page margins, major breaks)
- **48px**: Massive spacing (hero sections, major page breaks)

#### Component Spacing
- **Button Padding**: `12px 24px`
- **Card Padding**: `16px 20px`
- **Input Padding**: `12px 16px`
- **Modal Padding**: `24px 32px`

### 1.5 Border Radius
- **Small**: `4px` - Buttons, small components
- **Medium**: `8px` - Cards, inputs, medium components
- **Large**: `12px` - Modals, large components
- **Extra Large**: `16px` - Hero sections, major containers

### 1.6 Shadows
- **Small**: `0 1px 2px 0 rgba(0, 0, 0, 0.05)` - Cards, subtle elevation
- **Medium**: `0 4px 6px -1px rgba(0, 0, 0, 0.1)` - Modals, medium elevation
- **Large**: `0 10px 15px -3px rgba(0, 0, 0, 0.1)` - Dropdowns, high elevation
- **Extra Large**: `0 20px 25px -5px rgba(0, 0, 0, 0.1)` - Hero sections

## 2. Component Library

### 2.1 Buttons

#### Primary Button
```css
background: #2563EB
color: white
padding: 12px 24px
border-radius: 8px
font-weight: 500
font-size: 14px
```

#### Secondary Button
```css
background: transparent
color: #2563EB
border: 1px solid #2563EB
padding: 12px 24px
border-radius: 8px
font-weight: 500
font-size: 14px
```

#### Ghost Button
```css
background: transparent
color: #6B7280
padding: 12px 24px
border-radius: 8px
font-weight: 500
font-size: 14px
```

#### Danger Button
```css
background: #EF4444
color: white
padding: 12px 24px
border-radius: 8px
font-weight: 500
font-size: 14px
```

### 2.2 Input Fields

#### Text Input
```css
border: 1px solid #D1D5DB
border-radius: 8px
padding: 12px 16px
font-size: 14px
background: white
```

#### Input States
- **Focus**: `border-color: #2563EB, box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1)`
- **Error**: `border-color: #EF4444, background: #FEF2F2`
- **Disabled**: `background: #F9FAFB, color: #9CA3AF`

### 2.3 Cards

#### Standard Card
```css
background: white
border-radius: 12px
padding: 20px
box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1)
border: 1px solid #E5E7EB
```

#### Interactive Card
```css
background: white
border-radius: 12px
padding: 20px
box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1)
border: 1px solid #E5E7EB
transition: all 0.2s ease
cursor: pointer
```

### 2.4 Progress Indicators

#### Progress Bar
```css
background: #E5E7EB
border-radius: 9999px
height: 8px
overflow: hidden
```

#### Progress Fill
```css
background: linear-gradient(90deg, #10B981, #059669)
height: 100%
transition: width 0.3s ease
```

#### Circular Progress
```css
width: 60px
height: 60px
border-radius: 50%
border: 4px solid #E5E7EB
border-top: 4px solid #2563EB
animation: spin 1s linear infinite
```

### 2.5 Badges

#### Status Badge
```css
padding: 4px 12px
border-radius: 9999px
font-size: 12px
font-weight: 500
```

#### Badge Variants
- **Success**: `background: #D1FAE5, color: #065F46`
- **Warning**: `background: #FEF3C7, color: #92400E`
- **Error**: `background: #FEE2E2, color: #991B1B`
- **Info**: `background: #DBEAFE, color: #1E40AF`

### 2.6 Modals

#### Modal Container
```css
background: rgba(0, 0, 0, 0.5)
position: fixed
top: 0
left: 0
right: 0
bottom: 0
display: flex
align-items: center
justify-content: center
z-index: 1000
```

#### Modal Content
```css
background: white
border-radius: 12px
padding: 24px
max-width: 500px
width: 90%
max-height: 80vh
overflow-y: auto
box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1)
```

### 2.7 File Upload Components

#### File Upload Area
```css
border: 2px dashed #D1D5DB
border-radius: 8px
padding: 24px
text-align: center
cursor: pointer
transition: border-color 0.2s ease
background: #F9FAFB
```

#### File Upload States
- **Default**: `border-color: #D1D5DB, background: #F9FAFB`
- **Hover**: `border-color: #2563EB, background: #EFF6FF`
- **Active**: `border-color: #1D4ED8, background: #DBEAFE`
- **Error**: `border-color: #EF4444, background: #FEF2F2`

### 2.8 Progress Indicators

#### Upload/Download Progress
```css
background: #E5E7EB
border-radius: 9999px
height: 8px
overflow: hidden
margin: 16px 0
```

#### Progress Fill
```css
background: linear-gradient(90deg, #2563EB, #1D4ED8)
height: 100%
transition: width 0.3s ease
```

### 2.9 Loading States

#### Loading Spinner
```css
width: 40px
height: 40px
border-radius: 50%
border: 4px solid #E5E7EB
border-top: 4px solid #2563EB
animation: spin 1s linear infinite
```

#### Skeleton Loading
```css
background: linear-gradient(90deg, #F3F4F6 25%, #E5E7EB 50%, #F3F4F6 75%)
background-size: 200% 100%
animation: shimmer 1.5s infinite
```

### 2.10 Error States

#### Error Container
```css
background: #FEF2F2
border: 1px solid #FECACA
border-radius: 8px
padding: 16px
margin: 16px 0
```

#### Error Icon
```css
color: #EF4444
font-size: 20px
margin-right: 8px
```

### 2.11 Empty States

#### Empty State Container
```css
text-align: center
padding: 48px 24px
color: #6B7280
```

#### Empty State Icon
```css
font-size: 48px
margin-bottom: 16px
opacity: 0.5
```

### 2.12 Toggle Switches

#### Toggle Switch
```css
width: 44px
height: 24px
background: #D1D5DB
border-radius: 12px
position: relative
cursor: pointer
transition: background-color 0.2s ease
```

#### Toggle Switch Active
```css
background: #2563EB
```

#### Toggle Switch Handle
```css
width: 20px
height: 20px
background: white
border-radius: 50%
position: absolute
top: 2px
left: 2px
transition: transform 0.2s ease
box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1)
```

#### Toggle Switch Handle Active
```css
transform: translateX(20px)
```

## 3. Data Visualization

### 3.1 Chart Colors
- **Primary**: `#2563EB`
- **Secondary**: `#10B981`
- **Tertiary**: `#F59E0B`
- **Quaternary**: `#EF4444`
- **Quinary**: `#8B5CF6`

### 3.2 Chart Types
- **Line Charts**: Progress over time, score trends
- **Bar Charts**: Set comparisons, performance metrics
- **Pie Charts**: Category distribution, status breakdown
- **Progress Rings**: Individual set progress
- **Heatmaps**: Learning activity patterns

### 3.3 Chart Guidelines
- Use consistent color scheme across all charts
- Include clear labels and legends
- Provide hover states with detailed information
- Ensure accessibility with proper contrast ratios
- Support responsive design for mobile devices

## 4. Responsive Design

### 4.1 Breakpoints
- **Mobile**: `320px - 767px`
- **Tablet**: `768px - 1023px`
- **Desktop**: `1024px - 1439px`
- **Large Desktop**: `1440px+`

### 4.2 Mobile-First Approach
- Design for mobile first, then enhance for larger screens
- Use touch-friendly target sizes (minimum 44px)
- Optimize navigation for thumb reach
- Ensure readable text sizes on small screens

### 4.3 Responsive Patterns
- **Stack**: Elements stack vertically on mobile
- **Collapse**: Sidebar collapses to hamburger menu
- **Scale**: Charts and images scale appropriately
- **Hide**: Non-essential elements hide on small screens

## 5. Accessibility

### 5.1 Color Contrast
- **Normal Text**: Minimum 4.5:1 contrast ratio
- **Large Text**: Minimum 3:1 contrast ratio
- **UI Components**: Minimum 3:1 contrast ratio

### 5.2 Keyboard Navigation
- All interactive elements must be keyboard accessible
- Logical tab order
- Visible focus indicators
- Skip links for main content

### 5.3 Screen Reader Support
- Semantic HTML structure
- Proper ARIA labels
- Alt text for images
- Descriptive link text

### 5.4 Motion and Animation
- Respect user's motion preferences
- Provide option to reduce motion
- Ensure animations don't cause seizures
- Use subtle, purposeful animations

## 6. Icon System

### 6.1 Icon Style
- **Style**: Outlined with filled variants for active states
- **Size**: 16px, 20px, 24px, 32px
- **Weight**: 1.5px stroke width
- **Color**: Inherit from parent or use semantic colors

### 6.2 Common Icons
- **Learning**: Book, Graduation Cap, Brain
- **Progress**: Chart, Trending Up, Target
- **Actions**: Plus, Edit, Delete, Share
- **Navigation**: Home, Settings, Profile, Back
- **Status**: Check, Warning, Error, Info

## 7. Animation Guidelines

### 7.1 Duration
- **Fast**: 150ms - Micro-interactions
- **Normal**: 300ms - Standard transitions
- **Slow**: 500ms - Page transitions, complex animations

### 7.2 Easing
- **Ease Out**: `cubic-bezier(0, 0, 0.2, 1)` - Entering elements
- **Ease In**: `cubic-bezier(0.4, 0, 1, 1)` - Exiting elements
- **Ease In Out**: `cubic-bezier(0.4, 0, 0.2, 1)` - Complex animations

### 7.3 Animation Types
- **Fade**: Opacity transitions for content changes
- **Slide**: Position changes for navigation
- **Scale**: Size changes for interactive feedback
- **Rotate**: Loading states and progress indicators

## 8. Implementation Guidelines

### 8.1 CSS Variables
Use CSS custom properties for consistent theming:
```css
:root {
  --color-primary: #2563EB;
  --color-success: #10B981;
  --color-warning: #F59E0B;
  --color-error: #EF4444;
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --border-radius: 8px;
  --shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
}
```

### 8.2 Component Naming
Use BEM methodology for CSS class naming:
```css
.card {}
.card--interactive {}
.card__header {}
.card__content {}
.card__footer {}
```

### 8.3 File Organization
```
styles/
├── base/
│   ├── reset.css
│   ├── typography.css
│   └── variables.css
├── components/
│   ├── buttons.css
│   ├── inputs.css
│   ├── cards.css
│   └── modals.css
├── layouts/
│   ├── grid.css
│   ├── navigation.css
│   └── sidebar.css
└── utilities/
    ├── spacing.css
    ├── colors.css
    └── animations.css
``` 
