## Branch Setup

This repository uses a dual-track branching strategy — one track for Eltratec, one for FERI.
```
main-eltratec  ← stable, production-ready code for Eltratec
main-feri      ← stable, production-ready code for FERI
dev-eltratec   ← active development for Eltratec
dev-feri       ← active development for FERI
```

### Local Setup

Clone the repository and set up tracking branches so each dev branch
automatically pushes and pulls from its corresponding main branch.
```bash
git clone https://github.com/<org>/Sensum.git
cd Sensum

git checkout -b dev-eltratec origin/dev-eltratec
git checkout -b dev-feri origin/dev-feri
```

### Workflow

**Eltratec track:**
```bash
git checkout dev-eltratec
# ... make changes ...
git add .
git commit -m "[DEV-xx] feat: your message here"
git push
# then open a Pull Request: dev-eltratec → main-eltratec
```

**FERI track:**
```bash
git checkout dev-feri
# ... make changes ...
git add .
git commit -m "[DEV-xx] feat: your message here"
git push
# then open a Pull Request: dev-feri → main-feri
```

### Feature Branches

For larger changes, create a feature branch off the appropriate dev branch:
```bash
git checkout dev-eltratec
git checkout -b feature/your-feature-name

# ... make changes ...
git add .
git commit -m "[DEV-xx] feat: your message here"
git push origin feature/your-feature-name
# then open a Pull Request: feature/your-feature-name → dev-eltratec
```

### Commit Message Format
```
[DEV-<number>] <type>(<optional scope>): <description>
```

Examples:
```
[DEV-42] feat(api): add sensor data endpoint
[DEV-55] fix(auth): redirect to login on expired session
[DEV-78] refactor(middleware): simplify SOAP-to-REST mapping
[DEV-12] docs(readme): update local setup instructions
```
