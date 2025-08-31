# 📑 Documentation Index

Table of contents managing the entire docs/ directory, with detailed descriptions of **required items** for dev/QA/AI to work consistently.

| ID  | File Path                                          | Required Items                                                                                                           | Status     |
|-----|----------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|------------|
| B01 | 01-business/brd.md                                 | System objectives, scope, core values, business drivers                                                                 | ✅ Done    |
| B02 | 01-business/glossary.md                            | List of terms, abbreviations, standard definitions                                                                      | ✅ Done    |
| B03 | 01-business/stakeholder-map.md                     | List of stakeholders, roles, decision rights, responsibilities                                                          | ✅ Done    |
| B04 | 01-business/business-rules.md                      | Business rules, BR-xxx format, condition descriptions, results                                                          | ✅ Done    |
| B05 | 01-business/user-journeys.md                       | End-to-end user journeys, key steps, pain points                                                                       | ✅ Done    |
| S01 | 02-system-analysis/system-spec.md                  | Scope (in/out), actors, use case list, main/alt flow, data I/O, validation, error, acceptance criteria                  | ✅ Done    |
| S02 | 02-system-analysis/nfr.md                          | Performance (latency, TPS), availability (SLA/SLO), security, localization, HA/DR, logging, scalability                | ✅ Done    |
| S03 | 02-system-analysis/data-dictionary.md              | List of data, types, domain values, nullability, validation rules                                                       | ✅ Done    |
| S04 | 02-system-analysis/domain-model.md                 | Domain entities, relationships, bounded contexts, UML/ER diagrams                                                       | ✅ Done    |
| S05 | 02-system-analysis/use-cases/UC-001-user-registration.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S06 | 02-system-analysis/use-cases/UC-005-create-set.md  | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S07 | 02-system-analysis/user-stories/US-001-user-registration.md | User story (As a … I want … so that …), acceptance criteria (Gherkin), DoR/DoD                                         | ✅ Done    |
| S08 | 02-system-analysis/user-stories/US-005-create-set.md | User story (As a … I want … so that …), acceptance criteria (Gherkin), DoR/DoD                                         | ✅ Done    |
| S09 | 02-system-analysis/use-cases/UC-002-user-login.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S10 | 02-system-analysis/use-cases/UC-011-perform-review-session.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S11 | 02-system-analysis/use-cases/UC-016-reschedule-reminder.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S12 | 02-system-analysis/use-cases/UC-019-view-learning-statistics.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S13 | 02-system-analysis/user-stories/US-002-user-login.md | User story (As a … I want … so that …), acceptance criteria (Gherkin), DoR/DoD                                         | ✅ Done    |
| S14 | 02-system-analysis/user-stories/US-011-perform-review-session.md | User story (As a … I want … so that …), acceptance criteria (Gherkin), DoR/DoD                                         | ✅ Done    |
| S15 | 02-system-analysis/user-stories/US-016-reschedule-reminder.md | User story (As a … I want … so that …), acceptance criteria (Gherkin), DoR/DoD                                         | ✅ Done    |
| S16 | 02-system-analysis/user-stories/US-019-view-learning-statistics.md | User story (As a … I want … so that …), acceptance criteria (Gherkin), DoR/DoD                                         | ✅ Done    |
| S17 | 02-system-analysis/use-cases/UC-003-user-profile-management.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S18 | 02-system-analysis/use-cases/UC-004-password-reset.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S19 | 02-system-analysis/use-cases/UC-006-edit-set-information.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S20 | 02-system-analysis/use-cases/UC-007-delete-set.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S21 | 02-system-analysis/use-cases/UC-008-view-set-list.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S22 | 02-system-analysis/use-cases/UC-009-view-set-details.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| S23 | 02-system-analysis/use-cases/UC-010-start-learning-cycle.md | UC name, objectives, pre-conditions, flow steps, exceptions, post-conditions, BR-xxx mapping                            | ✅ Done    |
| D01 | 03-design/architecture/c4-context.md               | C4 L1: system + external systems, overall data flow                                                                     | ⬜ Draft   |
| D02 | 03-design/architecture/c4-container.md             | C4 L2: container diagram (FE, BE, DB, queue, cache, external services)                                                  | ⬜ Draft   |
| D03 | 03-design/architecture/c4-component.md             | C4 L3: main components/modules, boundaries, responsibilities                                                             | ⬜ Draft   |
| D04 | 03-design/api/openapi.yaml                         | Endpoint list, methods, request/response schemas, error responses, security schemes                                     | ⬜ Draft   |
| D05 | 03-design/api/error-catalog.md                     | Standard error codes, HTTP status mapping, dev/user messages, handling suggestions                                      | ⬜ Draft   |
| D06 | 03-design/database/schema.md                       | List of tables, columns, PK, FK, constraints, indexes, defaults, audit columns                                         | ⬜ Draft   |
| D07 | 03-design/database/erd.md                          | ERD diagrams, cardinality, relationship notes                                                                           | ⬜ Draft   |
| D08 | 03-design/security/threat-model.md                 | STRIDE: spoofing, tampering, repudiation, info disclosure, DoS, privilege escalation; mitigation measures               | ⬜ Draft   |
| D09 | 03-design/security/authn-authz-model.md            | AuthN model (OIDC/SAML), AuthZ (RBAC/ABAC), session/JWT, refresh/rotation                                              | ⬜ Draft   |
| DD1 | 04-detail-design/modules/<module>-detail-design.md | Objectives, API consume/expose, DTO↔Entity, business logic pseudocode, rules, error mapping, observability, test suggestions | ⬜ Draft   |
| DD2 | 04-detail-design/contracts/dto-mapping.md          | DTO ↔ entity mapping, data type conversion, masking, rounding rules                                                     | ⬜ Draft   |
| DD3 | 04-detail-design/contracts/event-contracts.md      | Event schemas (topic/key/headers/payload), idempotency, retry, DLQ                                                      | ⬜ Draft   |
| Q01 | 05-quality/test-strategy.md                        | Test types (unit, integration, perf, security), tools, test data, coverage targets                                      | ⬜ Draft   |
| Q02 | 05-quality/rtm.md                                  | Requirements Traceability Matrix: Req → Design → Code → Test                                                           | ⬜ Draft   |
| Qxx | 05-quality/test-cases/TC-xxx.md                    | ID, description, pre-conditions, steps, expected results, mapping to UC/US                                             | ⬜ Draft   |
| DV1 | 06-devops/ci-cd.md                                 | Pipeline stages (build, test, scan, deploy), gates, artifacts, rollback                                                 | ⬜ Draft   |
| DV2 | 06-devops/observability.md                         | Logs (format, masking), metrics, tracing, dashboards, SLO alerts                                                        | ⬜ Draft   |
| DV3 | 06-devops/deployment-runbook.md                    | Pre/post deploy checklists, rollback plan, blue/green/canary strategies                                                 | ⬜ Draft   |
| P01 | 07-project/roadmap.md                              | Timeline by quarter/month, objectives, milestones                                                                       | ⬜ Draft   |
| P02 | 07-project/delivery-plan.md                        | WBS, effort estimates, critical path                                                                                    | ⬜ Draft   |
| P03 | 07-project/risk-register.md                        | List of risks, probability, impact, mitigation                                                                          | ⬜ Draft   |
| P04 | 07-project/decision-log.md                         | Architectural Decision Records (ADR): decisions, reasons, dates                                                         | ⬜ Draft   |
| P05 | 07-project/def-of-ready-dor.md                     | "Ready" criteria (clear acceptance, sufficient data, resolved dependencies)                                             | ⬜ Draft   |
| P06 | 07-project/def-of-done-dod.md                      | "Done" criteria (code, tests pass, docs updated, deployable)                                                            | ⬜ Draft   |

> 📘 See also: [guideline.md](guideline.md) for detailed instructions on how to write content for each file.
