# Docker Image Build & Push to GitHub Container Registry (GHCR)

Production-ready guide for building, tagging, and pushing microservices to GHCR.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Manual Build and Push](#manual-build-and-push)
3. [Automated CI/CD with GitHub Actions](#automated-cicd-with-github-actions)
4. [Deployment from GHCR](#deployment-from-ghcr)
5. [Troubleshooting](#troubleshooting)
6. [Version Management](#version-management)
7. [Best Practices](#best-practices)

---

## Prerequisites

### Required Tools

- **Docker**: Latest version (20.10+)
  ```bash
  docker --version
  # Docker version 20.10.0 or higher
  ```

- **Git**: Any recent version
  ```bash
  git --version
  ```

- **Docker Buildx** (for multi-platform builds)
  ```bash
  docker buildx version
  # If not installed: docker buildx create --use
  ```

### GitHub Setup

#### 1. Create a Personal Access Token (PAT)

1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token (classic)"
3. Select scopes:
   - `write:packages` - Write packages
   - `read:packages` - Read packages
   - `delete:packages` - Delete packages
4. Copy the token (you'll need it for Docker login)

#### 2. Configure Docker Authentication

```bash
# Option 1: Using PAT
export CR_PAT=<your_personal_access_token>
echo $CR_PAT | docker login ghcr.io -u nishanth-code --password-stdin

# Option 2: Using GitHub CLI
gh auth login
gh auth refresh --scopes write:packages,read:packages
```

#### 3. Verify Authentication

```bash
docker logout ghcr.io
docker login ghcr.io
# Username: nishanth-code
# Password: <your_PAT>
```

---

## Manual Build and Push

### Step 1: Navigate to Project Directory

```bash
cd enterprise-microservices-platform
pwd
# Should output: .../edureka-project/enterprise-microservices-platform
```

### Step 2: Make Build Script Executable

```bash
chmod +x build-and-push.sh
```

### Step 3: Build and Push All Services

#### Option A: Using Timestamp Version (Recommended)

```bash
# Build and push all services with timestamp version (e.g., 2024.02.18-a1b2c3d)
./build-and-push.sh true

# Or more explicitly:
./build-and-push.sh "" true
```

**Output:**
```
✓ Successfully built: service-registry
✓ Pushed: ghcr.io/nishanth-code/enterprise-microservices-platform/service-registry:2024.02.18-a1b2c3d
✓ Pushed: ghcr.io/nishanth-code/enterprise-microservices-platform/service-registry:latest
✓ All services built successfully!
```

#### Option B: Using Semantic Versioning

```bash
# Build and push with version 1.0.0 and latest tag
./build-and-push.sh 1.0.0 true

# Build only (dry run) without pushing
./build-and-push.sh 1.0.0 false
```

#### Option C: Build Individual Services

```bash
# Build just the order-service
docker build -f order-service/Dockerfile \
  -t ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0 \
  -t ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest \
  .

# Push the image
docker push ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0
docker push ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
```

### Step 4: Verify Images in GHCR

```bash
# List all images (requires GitHub CLI)
gh api user/packages --paginate -q '.[] | select(.package_type=="container") | .name'

# Or check via web browser
# https://github.com/nishanth-code?tab=packages

# Verify image locally
docker pull ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
docker inspect ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
```

---

## Automated CI/CD with GitHub Actions

### Workflow File

Located at: `.github/workflows/docker-build-push.yml`

### Trigger Conditions

The workflow automatically runs when:
- ✅ Push to `main` or `develop` branch
- ✅ Changes to `enterprise-microservices-platform/` directory
- ✅ Changes to workflow file itself
- ✅ Manual trigger via **Actions** tab

### Automatic Workflow

1. **On Every Push to Main:**
   ```
   Push to main 
   → GitHub Actions triggered
   → Build all 9 service images
   → Push with tags: version, branch, git-sha, latest
   → Verify images exist in registry
   → Notification (success/failure)
   ```

2. **Image Tags Generated:**
   ```
   ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0
   ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:main
   ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:main-a1b2c3d
   ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
   ```

### Manual Workflow Trigger

```bash
# Via GitHub CLI
gh workflow run docker-build-push.yml -f version=1.0.0

# Or via GitHub Web UI:
# 1. Go to Actions tab
# 2. Click "Build and Push Docker Images to GHCR"
# 3. Click "Run workflow"
# 4. Enter version (optional)
# 5. Click "Run workflow"
```

### Monitor Workflow Execution

```bash
# Watch live logs
gh run list --workflow=docker-build-push.yml --limit=5

# View specific run details
gh run view <run_id> --log

# Get latest run status
gh run list --workflow=docker-build-push.yml --limit=1 --json status,conclusion
```

---

## Deployment from GHCR

### Step 1: Authenticate Docker to GHCR

```bash
export CR_PAT=<your_personal_access_token>
echo $CR_PAT | docker login ghcr.io -u nishanth-code --password-stdin
```

### Step 2: Deploy Using docker-compose-ghcr.yml

```bash
# Start all services with GHCR images
docker-compose -f docker-compose-ghcr.yml up -d

# Expected output:
# ✓ Creating network "microservices-network"
# ✓ Pulling service-registry image
# ✓ Creating service-registry
# ✓ Pulling config-server image
# ✓ Creating config-server
# ... (7 more services)
```

### Step 3: Verify Deployment

```bash
# Check all services are running
docker-compose -f docker-compose-ghcr.yml ps

# Check service health
docker-compose -f docker-compose-ghcr.yml ps --services

# View logs for specific service
docker-compose -f docker-compose-ghcr.yml logs order-service

# View all logs
docker-compose -f docker-compose-ghcr.yml logs -f
```

### Step 4: Access Services

| Service | URL | Type |
|---------|-----|------|
| Eureka Service Registry | http://localhost:8761 | Web UI |
| API Gateway | http://localhost:8081 | API |
| Auth Service | http://localhost:8082 | API |
| Product Catalog | http://localhost:8083 | API + Swagger |
| Inventory | http://localhost:8084 | API + Swagger |
| Order | http://localhost:8085 | API + Swagger |
| Notification | http://localhost:8086 | API |
| Aggregation | http://localhost:8087 | API |

### Step 5: Cleanup

```bash
# Stop all services
docker-compose -f docker-compose-ghcr.yml down

# Remove images (cleanup)
docker-compose -f docker-compose-ghcr.yml down --rmi all

# Stop and clean volumes
docker-compose -f docker-compose-ghcr.yml down -v
```

---

## Troubleshooting

### 1. Docker Login Fails

**Error:** `Error response from daemon: Bad credentials`

**Solution:**
```bash
# Logout and login again
docker logout ghcr.io

# Use --password-stdin for better security
export CR_PAT=<your_token>
echo $CR_PAT | docker login ghcr.io -u nishanth-code --password-stdin

# Verify login
docker pull ghcr.io/nishanth-code/enterprise-microservices-platform/service-registry:latest
```

### 2. Image Build Fails

**Error:** `dockerfile not found`

**Solution:**
```bash
# Verify Dockerfile exists
ls -la enterprise-microservices-platform/*/Dockerfile

# Verify you're in correct directory
pwd  # Should be: .../edureka-project
cd enterprise-microservices-platform

# Try building manually
docker build -f order-service/Dockerfile -t test:latest .
```

### 3. Push Fails - No Authorization

**Error:** `denied: installation not allowed to Write to package ...`

**Solution:**
```bash
# Check token scopes
gh auth status --show-token

# Recreate token with correct scopes:
# Settings → Developer settings → Personal access tokens
# Scopes: write:packages, read:packages, delete:packages
```

### 4. Image Not Found in Registry

**Error:** `Error response from daemon: manifest not found`

**Solution:**
```bash
# Verify image was pushed
docker images | grep enterprise-microservices

# Check registry
gh api user/packages -q '.[] | select(.package_type=="container") | .name'

# Verify image tag
docker images | grep order-service

# Try pulling again
docker pull ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
```

### 5. Docker Compose Network Issues

**Error:** `service not found` or `connection refused`

**Solution:**
```bash
# Check network
docker network inspect microservices-network

# Check service DNS
docker-compose -f docker-compose-ghcr.yml exec order-service ping service-registry

# Verify Eureka registration
curl http://localhost:8761/eureka/apps
```

### 6. Services Not Healthy

**Error:** `health check failed`

**Solution:**
```bash
# Check service logs
docker-compose -f docker-compose-ghcr.yml logs service-registry

# Check health status
docker-compose -f docker-compose-ghcr.yml ps

# Restart service
docker-compose -f docker-compose-ghcr.yml restart order-service

# Full restart
docker-compose -f docker-compose-ghcr.yml down && \
docker-compose -f docker-compose-ghcr.yml up -d
```

---

## Version Management

### Versioning Strategy

**Semantic Versioning (Recommended for Releases):**
```
1.0.0 = MAJOR.MINOR.PATCH
```

**Timestamp Versioning (Recommended for CI/CD):**
```
2024.02.18-a1b2c3d = YYYY.MM.DD-GitHash
```

### Version Naming Examples

| Use Case | Version | Command |
|----------|---------|---------|
| Initial Release | `1.0.0` | `./build-and-push.sh 1.0.0 true` |
| Feature Release | `1.1.0` | `./build-and-push.sh 1.1.0 true` |
| Bug Fix | `1.0.1` | `./build-and-push.sh 1.0.1 true` |
| CI/CD Build | `2024.02.18-a1b2c3d` | `./build-and-push.sh true` |

### Tag Strategy

Each image gets multiple tags:

```
ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0
ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0
ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1
ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:main
ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:main-a1b2c3d
```

---

## Best Practices

### 1. Security

✅ **Do:**
- Use `${{ secrets.GITHUB_TOKEN }}` instead of PAT in CI/CD
- Never commit tokens in code
- Keep images minimal (use .dockerignore)
- Scan images for vulnerabilities
- Use non-root user in Dockerfile

❌ **Don't:**
- Store credentials in environment variables
- Use `latest` tag for production (except as pointer)
- Push unbuilt images
- Keep sensitive data in images

### 2. Image Optimization

✅ **Do:**
- Use multi-stage builds
- Minimize layer count
- Clean up package managers
- Use specific base image versions

Dockerfile Example (Multi-stage):
```dockerfile
# Build stage
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3. Version Tagging

✅ **Do:**
- Use semantic versioning for releases
- Tag with commit SHA for traceability
- Keep `latest` pointing to main branch
- Maintain multiple version tags

❌ **Don't:**
- Reuse version tags
- Use vague tags like `new` or `update`
- Mix semantic and timestamp versions
- Delete old image tags

### 4. Registry Management

✅ **Do:**
- Use packages (private) for internal code
- Document package information
- Set retention policies
- Monitor package size

❌ **Don't:**
- Make all packages public (unless intended)
- Store secrets in image descriptions
- Keep unlimited image history
- Ignore size warnings

### 5. CI/CD Pipeline

✅ **Do:**
- Build on every push to main
- Run security scans
- Verify before pushing
- Document deployment steps

❌ **Don't:**
- Push without testing
- Skip verification steps
- Build manually in production
- Mix dev and prod deployments

---

## Complete Workflow Example

### Day 1: Release Version 1.0.0

```bash
# 1. Build and push with version tag
./build-and-push.sh 1.0.0 true

# 2. Create GitHub release
git tag v1.0.0
git push origin v1.0.0

# 3. Deploy to production
docker-compose -f docker-compose-ghcr.yml up -d

# 4. Verify deployment
docker-compose -f docker-compose-ghcr.yml ps
curl http://localhost:8761/eureka/apps
```

### Day 2-10: Development

```bash
# 1. Make code changes
# 2. Commit and push to main
git add .
git commit -m "feat: add new endpoint"
git push origin main

# 3. GitHub Actions automatically:
# - Builds images
# - Tags with: 2024.02.20-a1b2c3d, main, latest
# - Pushes to GHCR
# - Verifies images

# 4. Deploy latest
docker-compose -f docker-compose-ghcr.yml pull
docker-compose -f docker-compose-ghcr.yml up -d
```

### Day 11: Release 1.1.0

```bash
# 1. Build with new version
./build-and-push.sh 1.1.0 true

# 2. Tag and release
git tag v1.1.0
git push origin v1.1.0

# 3. Update production
docker-compose -f docker-compose-ghcr.yml up -d

# 4. Verify all services
docker-compose -f docker-compose-ghcr.yml ps
curl http://localhost:8761/eureka/apps
```

---

## Summary

| Task | Command |
|------|---------|
| Authenticate | `docker login ghcr.io` |
| Build & Push All | `./build-and-push.sh true` |
| Build Specific Version | `./build-and-push.sh 1.0.0 true` |
| Build Only (No Push) | `./build-and-push.sh 1.0.0 false` |
| Deploy from GHCR | `docker-compose -f docker-compose-ghcr.yml up -d` |
| Check Status | `docker-compose -f docker-compose-ghcr.yml ps` |
| View Logs | `docker-compose -f docker-compose-ghcr.yml logs -f order-service` |
| Stop All | `docker-compose -f docker-compose-ghcr.yml down` |
| GitHub Actions Manual Trigger | `gh workflow run docker-build-push.yml -f version=1.0.0` |

---

## Additional Resources

- [GitHub Container Registry Docs](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Semantic Versioning](https://semver.org/)

---

**Last Updated:** February 18, 2024  
**Maintainer:** nishanth-code  
**Repository:** enterprise-microservices-platform
