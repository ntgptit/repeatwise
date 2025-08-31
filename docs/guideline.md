# 📝 Documentation Guideline

This file describes **how to write content for each file in the docs/ directory**, helping ensure consistency and completeness.

---

## 01-business

### brd.md
- **System Objectives**: why the system exists.
- **Business Scope**: in-scope, out-of-scope.
- **Core Values**: benefits brought to business.
- **Business Drivers**: reasons, implementation motivations.

### glossary.md
- **English/Vietnamese/Korean terms (if applicable)**.
- **Clear definitions**.
- **Abbreviations**: list and explanations.

### stakeholder-map.md
- **Stakeholder names**.
- **Roles** (decision-maker, reviewer, user).
- **Responsibilities**.

### business-rules.md
- **BR-xxx codes**.
- **Conditions**.
- **Results when met/not met**.
- **Illustrative examples**.

### user-journeys.md
- **Personas** (representative users).
- **Journey steps**.
- **Pain points** and **expected solutions**.

---

## 02-system-analysis

### system-spec.md
- **Scope in/out**.
- **Actors** and permissions.
- **Use case list**.
- **Business flow (main/alt)**.
- **Data I/O** (name, type, validation).
- **Validation & Business Rule mapping**.
- **Error & Exception**.
- **Acceptance criteria** (Gherkin).

### nfr.md
- **Performance**: latency, TPS.
- **Availability**: SLA, failover.
- **Security**: authn/authz, encryption.
- **Localization**: multi-language, timezone.
- **Scalability**: scale-out/in.
- **Observability**: logs, metrics, trace.

### data-dictionary.md
- **Data names**.
- **Data types**.
- **Domain values**.
- **Constraints/validation**.
- **Nullable/mandatory**.

### domain-model.md
- **Entities**.
- **Relations**.
- **Bounded contexts**.
- **UML/ERD diagrams**.

### use-cases/UC-xxx-spec.md
- **Name & objectives**.
- **Pre-conditions**.
- **Step-by-step flow**.
- **Exception/alternative flow**.
- **Post-conditions**.
- **Mapping to BR-xxx**.

### user-stories/US-xxx.md
- **Template**: *As a … I want … so that …*.
- **Acceptance criteria** (Gherkin).
- **Definition of Ready / Done**.

---

## 03-design

### architecture (c4-context/container/component)
- **Context**: external systems, boundaries.
- **Container**: FE, BE, DB, cache, external API.
- **Component**: main modules, interfaces, responsibilities.

### api/openapi.yaml
- **Endpoints** (path, method).
- **Request schemas**.
- **Response schemas**.
- **Error schemas**.
- **Security schemes**.

### api/error-catalog.md
- **Error codes**.
- **HTTP status**.
- **Developer messages**.
- **User messages**.
- **Suggested actions**.

### database/schema.md
- **Table names**.
- **Columns, types, defaults, constraints**.
- **PK/FK**.
- **Indexes**.
- **Audit columns**.

### database/erd.md
- **ERD diagrams**.
- **Cardinality**.
- **Relationship notes**.

### security/threat-model.md
- **STRIDE analysis**.
- **Mitigation measures**.

### security/authn-authz-model.md
- **AuthN**: OIDC/SAML.
- **AuthZ**: RBAC/ABAC.
- **Session/JWT**: rotation, expiry.

### ui-ux/wireframes.md
- **Main screens**.
- **States (loading, empty, error)**.
- **Navigation flow**.

---

## 04-detail-design

### modules/<module>-detail-design.md
- **Module objectives**.
- **API consume/expose**.
- **DTO ↔ Entity mapping**.
- **Business logic (pseudocode)**.
- **Rules & Error mapping**.
- **Observability (logs, metrics)**.
- **Testability**.

### contracts/dto-mapping.md
- **DTO names**.
- **Entity names**.
- **Mapping rules**.
- **Masking/rounding**.

### contracts/event-contracts.md
- **Event names**.
- **Topic/key/headers**.
- **Payload schemas**.
- **Idempotency/retry/DLQ**.

---

## 05-quality

### test-strategy.md
- **Test types**.
- **Scope**.
- **Tools**.
- **Coverage targets**.
- **Test data strategy**.

### rtm.md
- **Requirement IDs**.
- **Design mapping**.
- **Code mapping**.
- **Test case mapping**.
- **Status**.

### test-cases/TC-xxx.md
- **Test IDs**.
- **Pre-conditions**.
- **Steps**.
- **Expected results**.
- **Mapping to UC/US**.

---

## 06-devops

### ci-cd.md
- **Pipeline stages**.
- **Build/test/lint/scan**.
- **Artifacts**.
- **Deploy**.
- **Rollback**.

### deployment-runbook.md
- **Pre-checklist**.
- **Deploy steps**.
- **Post-checklist**.
- **Rollback plan**.

### observability.md
- **Log formats**.
- **Required metrics**.
- **Tracing spans**.
- **Dashboards**.
- **Alerts**.

### backup-dr.md
- **RPO/RTO**.
- **Backup frequency**.
- **Restore plan**.
- **Drills**.

---

## 07-project

### roadmap.md
- **Milestones**.
- **Timeline**.
- **Goals**.

### delivery-plan.md
- **WBS**.
- **Effort estimates**.
- **Critical path**.

### risk-register.md
- **Risk IDs**.
- **Descriptions**.
- **Probability**.
- **Impact**.
- **Mitigation**.

### decision-log.md
- **ADR IDs**.
- **Decisions**.
- **Dates**.
- **Reasons**.

### def-of-ready-dor.md
- **Ready criteria**.

### def-of-done-dod.md
- **Done criteria**.
