# 002 - Web framework: Javalin

Status: Accepted

## Context

The project needs a lightweight HTTP server to expose a small REST API and optionally serve a frontend. The codebase is a small Java application (no Spring), and we prefer minimal configuration and an web server that can run alongside the existing console UI backend.

## Decision

Use Javalin as the web framework for the project.

Key points of the decision:

- Keep API routes under the `/api` namespace (e.g. `/api/students`).
- Provide a small set of helper endpoints for operations and health checks: `/` (landing/help), `/api/ping` (health), and a custom 404 handler for clearer messages.
- Add a Javalin dependency in `pom.xml` and keep the usage minimal so the app can continue to run in CLI mode when `runWeb` is false.

## Rationale

- Javalin is minimal and easy to integrate into an existing Java project without pulling in large frameworks (Spring Boot). It matches the project's need for a small REST layer and quick experimentation (health endpoints, JSON responses).

## Consequences

- Pros:
  - Fast to add endpoints and test (good for learning and iterative development).
  - Low configuration overhead and small dependency footprint.
  - Easy to serve static files for the SPA or to proxy to a separate frontend dev server.

- Cons / Follow-ups:
  - Not a full-featured application framework: for large enterprise features (dependency injection, advanced security) a move to Spring Boot may be considered in future.
  - Need to add basic middleware manually: logging, CORS, authentication, request validation, error formatting.

## Implementation notes

- Add Javalin dependency to `pom.xml`. 
- Register routes in `web/routes/*` and follow the `/api/*` namespace.
- Add a standard error JSON format for APIs, e.g. `{ "error": "message" }`.

## References

- Task tracker: `docs/task-list.md`
