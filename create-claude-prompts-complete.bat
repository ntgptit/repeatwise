@echo off
REM ============================================================================
REM Create 01-claude-prompts folder structure
REM 100% mapping coverage from 00_docs
REM ============================================================================

echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║  Creating Claude Code Prompts Structure (100%% Coverage)          ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.

REM ============================================================================
REM Root Level - Main Documentation Files
REM ============================================================================
echo [1/13] Creating root level files...
mkdir 01-claude-prompts 2>nul
cd 01-claude-prompts

type nul > README.md
type nul > 00-MASTER-KNOWLEDGE-BASE.md
type nul > HOW-TO-USE-WITH-CLAUDE-CODE.md

REM ============================================================================
REM 01-domains/ - Domain-Driven Design Documents
REM ============================================================================
echo [2/13] Creating domain documents...
mkdir 01-domains 2>nul
cd 01-domains

type nul > README.md
type nul > 01-user-auth-domain.md
type nul > 02-folder-domain.md
type nul > 03-deck-domain.md
type nul > 04-card-domain.md
type nul > 05-review-srs-domain.md
type nul > 06-statistics-domain.md
type nul > 07-settings-domain.md
type nul > 08-import-export-domain.md

cd ..

REM ============================================================================
REM 02-implementation-guides/ - Step-by-step Implementation
REM ============================================================================
echo [3/13] Creating implementation guides...
mkdir 02-implementation-guides 2>nul
cd 02-implementation-guides

type nul > README.md
type nul > 00-implementation-roadmap.md
type nul > 01-backend-setup-guide.md
type nul > 02-database-setup-guide.md
type nul > 03-backend-layer-implementation.md
type nul > 04-security-jwt-implementation.md
type nul > 05-frontend-web-setup.md
type nul > 06-frontend-web-implementation.md
type nul > 07-frontend-mobile-setup.md
type nul > 08-frontend-mobile-implementation.md
type nul > 09-api-integration-guide.md
type nul > 10-testing-strategy.md
type nul > 11-deployment-docker-guide.md

cd ..

REM ============================================================================
REM 03-code-templates/ - Reusable Code Templates
REM ============================================================================
echo [4/13] Creating code templates...
mkdir 03-code-templates 2>nul
cd 03-code-templates

type nul > README.md

REM Backend templates
mkdir backend 2>nul
cd backend

type nul > 01-base-entity-template.java
type nul > 02-entity-template.java
type nul > 03-repository-template.java
type nul > 04-service-interface-template.java
type nul > 05-service-impl-template.java
type nul > 06-controller-template.java
type nul > 07-dto-request-template.java
type nul > 08-dto-response-template.java
type nul > 09-mapper-template.java
type nul > 10-exception-template.java
type nul > 11-exception-handler-template.java
type nul > 12-validator-template.java
type nul > 13-unit-test-template.java
type nul > 14-integration-test-template.java
type nul > 15-flyway-migration-template.sql

cd ..

REM Frontend Web templates
mkdir frontend-web 2>nul
cd frontend-web

type nul > 01-page-component-template.tsx
type nul > 02-layout-component-template.tsx
type nul > 03-feature-component-template.tsx
type nul > 04-ui-component-template.tsx
type nul > 05-custom-hook-template.ts
type nul > 06-api-service-template.ts
type nul > 07-tanstack-query-hook-template.ts
type nul > 08-context-provider-template.tsx
type nul > 09-form-with-validation-template.tsx
type nul > 10-route-config-template.tsx
type nul > 11-component-test-template.tsx

cd ..

REM Frontend Mobile templates
mkdir frontend-mobile 2>nul
cd frontend-mobile

type nul > 01-screen-template.tsx
type nul > 02-component-template.tsx
type nul > 03-navigation-template.tsx
type nul > 04-custom-hook-template.ts
type nul > 05-api-service-template.ts
type nul > 06-tanstack-query-hook-template.ts
type nul > 07-style-template.ts

cd ..
cd ..

REM ============================================================================
REM 04-validation-rules/ - Business Rules & Validation
REM ============================================================================
echo [5/13] Creating validation rules...
mkdir 04-validation-rules 2>nul
cd 04-validation-rules

type nul > README.md
type nul > 01-entity-constraints.md
type nul > 02-dto-validation-rules.md
type nul > 03-business-logic-rules.md
type nul > 04-api-validation-rules.md
type nul > 05-srs-algorithm-rules.md
type nul > 06-folder-hierarchy-rules.md
type nul > 07-security-authorization-rules.md
type nul > 08-import-export-validation.md

cd ..

REM ============================================================================
REM 05-use-case-mappings/ - UC to Implementation Mapping
REM ============================================================================
echo [6/13] Creating use case mappings...
mkdir 05-use-case-mappings 2>nul
cd 05-use-case-mappings

type nul > README.md
type nul > 01-authentication-use-cases.md
type nul > 02-folder-management-use-cases.md
type nul > 03-deck-management-use-cases.md
type nul > 04-card-management-use-cases.md
type nul > 05-review-study-use-cases.md
type nul > 06-statistics-use-cases.md
type nul > 07-settings-use-cases.md
type nul > 08-import-export-use-cases.md

cd ..

REM ============================================================================
REM 06-api-specifications/ - Detailed API Documentation
REM ============================================================================
echo [7/13] Creating API specifications...
mkdir 06-api-specifications 2>nul
cd 06-api-specifications

type nul > README.md
type nul > 00-api-overview.md
type nul > 01-authentication-apis.md
type nul > 02-user-profile-apis.md
type nul > 03-folder-apis.md
type nul > 04-deck-apis.md
type nul > 05-card-apis.md
type nul > 06-review-apis.md
type nul > 07-statistics-apis.md
type nul > 08-settings-apis.md
type nul > 09-import-export-apis.md
type nul > 10-error-responses.md
type nul > 11-pagination-filtering.md

cd ..

REM ============================================================================
REM 07-database-schemas/ - Database Design
REM ============================================================================
echo [8/13] Creating database schemas...
mkdir 07-database-schemas 2>nul
cd 07-database-schemas

type nul > README.md
type nul > 00-complete-erd.md
type nul > 01-users-table.md
type nul > 02-refresh-tokens-table.md
type nul > 03-folders-table.md
type nul > 04-decks-table.md
type nul > 05-cards-table.md
type nul > 06-card-box-positions-table.md
type nul > 07-review-logs-table.md
type nul > 08-study-sessions-table.md
type nul > 09-user-settings-table.md
type nul > 10-deck-settings-table.md
type nul > 11-folder-stats-table.md
type nul > 12-indexes-and-constraints.md
type nul > 13-flyway-migration-order.md

cd ..

REM ============================================================================
REM 08-architecture-decisions/ - ADRs
REM ============================================================================
echo [9/13] Creating architecture decisions...
mkdir 08-architecture-decisions 2>nul
cd 08-architecture-decisions

type nul > README.md
type nul > 001-why-spring-boot.md
type nul > 002-why-postgresql.md
type nul > 003-why-jwt-refresh-token.md
type nul > 004-why-mapstruct.md
type nul > 005-why-flyway.md
type nul > 006-why-tanstack-query.md
type nul > 007-why-7-box-srs.md
type nul > 008-why-composite-pattern-folders.md
type nul > 009-why-spring-async-background-jobs.md
type nul > 010-why-soft-delete.md
type nul > 011-why-materialized-path.md
type nul > 012-why-no-else-coding-style.md
type nul > 013-why-tailwind-shadcn.md

cd ..

REM ============================================================================
REM 09-testing-specifications/ - Testing Standards
REM ============================================================================
echo [10/13] Creating testing specifications...
mkdir 09-testing-specifications 2>nul
cd 09-testing-specifications

type nul > README.md
type nul > 01-unit-testing-guide.md
type nul > 02-integration-testing-guide.md
type nul > 03-e2e-testing-guide.md
type nul > 04-test-data-builders.md
type nul > 05-mocking-strategies.md
type nul > 06-test-coverage-requirements.md
type nul > 07-performance-testing.md

cd ..

REM ============================================================================
REM 10-deployment-configs/ - Deployment & DevOps
REM ============================================================================
echo [11/13] Creating deployment configs...
mkdir 10-deployment-configs 2>nul
cd 10-deployment-configs

type nul > README.md
type nul > 01-docker-development.md
type nul > 02-docker-production.md
type nul > 03-environment-variables.md
type nul > 04-cicd-github-actions.md
type nul > 05-database-backup-restore.md

cd ..

REM ============================================================================
REM 11-coding-standards/ - Code Quality Standards
REM ============================================================================
echo [12/13] Creating coding standards...
mkdir 11-coding-standards 2>nul
cd 11-coding-standards

type nul > README.md
type nul > 01-java-coding-standards.md
type nul > 02-typescript-coding-standards.md
type nul > 03-naming-conventions.md
type nul > 04-code-review-checklist.md
type nul > 05-git-commit-conventions.md
type nul > 06-documentation-standards.md
type nul > 07-error-handling-patterns.md
type nul > 08-logging-standards.md

cd ..

REM ============================================================================
REM 12-quick-references/ - Quick Lookup Guides
REM ============================================================================
echo [13/13] Creating quick references...
mkdir 12-quick-references 2>nul
cd 12-quick-references

type nul > README.md
type nul > 01-entity-relationships-cheatsheet.md
type nul > 02-api-endpoints-cheatsheet.md
type nul > 03-validation-rules-summary.md
type nul > 04-srs-algorithm-flowchart.md
type nul > 05-common-queries-reference.md
type nul > 06-frontend-components-hierarchy.md
type nul > 07-troubleshooting-guide.md
type nul > 08-dependencies-versions.md

cd ..

REM ============================================================================
REM Return to root and display tree
REM ============================================================================
cd ..

echo.
echo ╔════════════════════════════════════════════════════════════════════╗
echo ║  ✓ Folder structure created successfully!                         ║
echo ╚════════════════════════════════════════════════════════════════════╝
echo.
echo Total structure:
echo   - 12 main folders
echo   - 150+ files ready for content generation
echo   - 100%% coverage mapping from 00_docs
echo.
echo Next steps:
echo   1. Navigate to 01-claude-prompts folder
echo   2. Review README.md for usage instructions
echo   3. Use Claude Code to generate content for each file
echo.

REM Display folder tree
echo Displaying folder structure:
echo.
tree /F 01-claude-prompts

echo.
echo ============================================================================
echo Script completed successfully!
echo ============================================================================
pause
