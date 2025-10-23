# Master Knowledge Base (Claude Brief)

Use this snippet to prime Claude with the product narrative before diving into specialised prompts. Each bullet points to the authoritative source inside `00_docs`—only fetch the extra file when the conversation needs it.

## Product snapshot
- **Product**: RepeatWise MVP – personal flashcard trainer using a 7-box Leitner SRS. Source: `00_docs/01-business/product-overview.md`.
- **Platforms**: Spring Boot backend (`Java 17`, PostgreSQL), React web, React Native mobile. Source: `00_docs/03-design/architecture/backend-detailed-design.md`, `.../frontend-architecture.md`.
- **MVP boundaries**: Personal use only, plain-text cards, push notifications included, collaboration/premium deferred. Source: `00_docs/01-business/mvp-scope.md`.

## Personas & goals
- Primary learners (students, professionals, self-learners) need easy organisation + smart scheduling. Personas + JTBD: `00_docs/01-business/product-overview.md` (sections 3–4).
- Success metrics emphasise retention, daily streaks, and sub-500 ms API latency. See `00_docs/01-business/product-overview.md` §7.

## System capabilities
- **Use cases**: 24 MVP flows covering auth, folders, decks, cards, review modes, import/export, settings, statistics, notifications. Index: `00_docs/02-system-analysis/use-cases/README.md`.
- **Domain contexts**: User/Auth, Content Organisation, SRS, Statistics. Overview: `00_docs/02-system-analysis/domain-model.md`.
- **Non-functional requirements**: Availability 99.5%, P95 API latency 500 ms, security baselines, etc. Source: `00_docs/02-system-analysis/nfr.md`.

## Design anchors
- **API blueprint**: `00_docs/03-design/api/api-endpoints-summary.md` (+ sub-files for each resource).
- **Architecture & patterns**: Strategy/Composite/Visitor usage plus module breakdown. Start at `00_docs/03-design/architecture/design-patterns.md` and `.../backend-detailed-design.md`.
- **Database ERD**: Table descriptions, constraints, indexing. See `00_docs/03-design/database/schema.md` & `.../indexing-strategy.md`.
- **Security**: JWT + refresh rotation, per-user data isolation. Reference `00_docs/03-design/security/authn-authz-model.md`.

## Detail design accelerators
- **Entity specs & DTO contracts**: `00_docs/04-detail-design/01-entity-specifications.md`, `02-api-request-response-specs.md`.
- **Business logic flows + pseudo code**: `00_docs/04-detail-design/03-business-logic-flows.md`, `04-srs-algorithm-implementation.md`.
- **Validation & errors**: `05-validation-rules.md`, `06-error-handling-specs.md`.
- **Front-end wireframes & UX contracts**: `07-frontend-web-specs.md`, `08-frontend-mobile-specs.md`, `09/10 wireframes`.

## How to feed Claude
1. Paste this page to establish context.
2. State your task (e.g. "Implement Deck CRUD API").
3. Attach the specific prompt pack (API, domain, implementation) listed under the relevant section.
4. Only after Claude asks for more detail should you copy the canonical spec (or the exact excerpt) from `00_docs`.

> Rule of thumb: each prompt pack is <200 tokens; each source doc excerpt should cover only the function/class you are actively editing.
