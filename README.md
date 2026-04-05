# Sensum

This repository uses a dual-track branching strategy — one track for Eltratec, one for FERI.

```
main-eltratec  ← stable, production-ready code for Eltratec
main-feri      ← stable, production-ready code for FERI
dev-eltratec   ← active development for Eltratec
dev-feri       ← active development for FERI
```

---

## Local Setup

Clone the repository and set up tracking branches so each dev branch
automatically pushes and pulls from its corresponding main branch.

```bash
git clone https://github.com/Altr0y/Sensum.git
cd Sensum

git checkout -b dev-eltratec origin/dev-eltratec
git checkout -b dev-feri origin/dev-feri
```

---

## Workflow

**Eltratec track:**
```bash
git checkout dev-eltratec
# ... make changes ...
git add .
git commit -m "DEV-xx feat: your message here"
git push
# then open a Pull Request: dev-eltratec → main-eltratec
```

**FERI track:**
```bash
git checkout dev-feri
# ... make changes ...
git add .
git commit -m "DEV-xx feat: your message here"
git push
# then open a Pull Request: dev-feri → main-feri
```

---

## Feature Branches

For larger changes, create a feature branch off the appropriate dev branch:

```bash
git checkout dev-eltratec
git checkout -b DEV-xx-short-description

# ... make changes ...
git add .
git commit -m "DEV-xx feat: your message here"
git push origin DEV-xx-short-description
# then open a Pull Request: DEV-xx-short-description → dev-eltratec
```

---

## Commit Message Format

Commit messages must include the Jira issue key so that commits, branches,
and pull requests are automatically linked to the corresponding Jira work item.

```
DEV-<number> <type>(<optional scope>): <description>
```

| Type | When to use |
|---|---|
| `feat` | New feature for the API or UI |
| `fix` | Bug fix |
| `refactor` | Code restructure without behavior change |
| `perf` | Performance improvement |
| `style` | Formatting only, no logic change |
| `test` | Adding or fixing tests |
| `docs` | Documentation only |
| `build` | Dependencies, build tools, versions |
| `ops` | CI/CD, deployment, infrastructure |
| `chore` | Maintenance tasks, `.gitignore`, init |

Examples:
```
DEV-42 feat(api): add sensor data endpoint
DEV-55 fix(auth): redirect to login on expired session
DEV-78 refactor(middleware): simplify SOAP-to-REST mapping
DEV-12 docs(readme): update local setup instructions
DEV-99 build: upgrade .NET dependencies to latest stable
```

### Breaking Changes

Append `!` after the type and add a `BREAKING CHANGE` footer:

```
DEV-103 feat(grafana)!: replace polling with WebSocket streaming

BREAKING CHANGE: clients must now connect via WebSocket instead of REST polling
```

---

## Pull Request Format

PR titles must also include the Jira issue key so the PR is linked in Jira:

```
DEV-<number> <type>(<optional scope>): <description>
```

Example:
```
DEV-42 feat(api): add sensor data endpoint
```
