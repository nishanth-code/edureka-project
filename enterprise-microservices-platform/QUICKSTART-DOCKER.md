# Quick Start Guide - Docker & GHCR

One-liners for common tasks.

## Prerequisites (One Time Setup)

```bash
# 1. Create GitHub PAT
# https://github.com/settings/tokens?type=beta
# Scopes: write:packages, read:packages

# 2. Login to Docker
export CR_PAT=<your_github_token>
echo $CR_PAT | docker login ghcr.io -u nishanth-code --password-stdin

# 3. Verify login
docker pull ghcr.io/nishanth-code/enterprise-microservices-platform/service-registry:latest
```

## Build & Push Commands

### Build All Services (CI/CD Style - Timestamp Version)

```bash
cd enterprise-microservices-platform
chmod +x build-and-push.sh
./build-and-push.sh true
```

**Result:** Images tagged with `2024.02.18-a1b2c3d` + `latest`

### Build All Services (Release Style - Semantic Version)

```bash
cd enterprise-microservices-platform
./build-and-push.sh 1.0.0 true
```

**Result:** Images tagged with `1.0.0` + `latest`

### Build Only Without Pushing

```bash
cd enterprise-microservices-platform
./build-and-push.sh 1.0.0 false
```

**Result:** Local images only, no GHCR push

### Build Single Service

```bash
docker build -f order-service/Dockerfile \
  -t ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0 \
  -t ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest \
  enterprise-microservices-platform/

docker push ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0
docker push ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
```

## Deployment Commands

### Deploy All Services from GHCR

```bash
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml up -d
```

### Check Status

```bash
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml ps
```

### View Logs

```bash
# All services
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml logs -f

# Specific service
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml logs -f order-service
```

### Stop All Services

```bash
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml down
```

### Cleanup Everything

```bash
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml down -v --rmi all
```

## Verification Commands

### Verify Images Exist in GHCR

```bash
# Via Docker
docker pull ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest

# Via GitHub CLI
gh api user/packages -q '.[] | select(.package_type=="container") | .name'

# Via Browser
# https://github.com/nishanth-code?tab=packages
```

### Verify Services Running

```bash
# Check all containers
docker ps -a | grep enterprise

# Check Eureka registry
curl http://localhost:8761/eureka/apps

# Check service health
curl http://localhost:8082/actuator/health
curl http://localhost:8085/actuator/health
```

### Verify Swagger UIs

```bash
# API Gateway
http://localhost:8081/swagger-ui.html

# Individual services
http://localhost:8082/swagger-ui.html  # auth-service
http://localhost:8083/swagger-ui.html  # product-catalog-service
http://localhost:8085/swagger-ui.html  # order-service
http://localhost:8086/swagger-ui.html  # notification-service
http://localhost:8087/swagger-ui.html  # aggregation-service
```

## GitHub Actions Commands

### Trigger Manual Workflow

```bash
gh workflow run docker-build-push.yml

# With custom version
gh workflow run docker-build-push.yml -f version=1.0.0

# List recent runs
gh run list --workflow=docker-build-push.yml --limit=5

# View specific run
gh run view <run_id> --log
```

## Common Issues & Fixes

### Docker Login Failed

```bash
docker logout ghcr.io
export CR_PAT=<your_token>
echo $CR_PAT | docker login ghcr.io -u nishanth-code --password-stdin
```

### Image Not Found

```bash
# List local images
docker images | grep enterprise

# Try pulling
docker pull ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
```

### Services Not Starting

```bash
# Check logs
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml logs service-registry

# Check network
docker network inspect microservices-network

# Restart service
docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml restart order-service
```

### Port Already in Use

```bash
# Find process using port 8761
lsof -i :8761

# Kill process
kill -9 <PID>

# Or use different ports in docker-compose-ghcr.yml
```

## Version Management

### Current Version
See `enterprise-microservices-platform/VERSION.md`

### Check Image Version

```bash
docker inspect ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest \
  | grep -i version

# Or
docker pull ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0
docker inspect ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0
```

## Production Deployment Checklist

- [ ] GitHub PAT created with correct scopes
- [ ] Docker authenticated to GHCR
- [ ] `.github/workflows/docker-build-push.yml` in place
- [ ] `docker-compose-ghcr.yml` configured
- [ ] `build-and-push.sh` executable (`chmod +x`)
- [ ] `.dockerignore` optimized
- [ ] `VERSION.md` updated
- [ ] All images built and pushed: `./build-and-push.sh 1.0.0 true`
- [ ] Images verified in GHCR
- [ ] Services deployed: `docker-compose -f docker-compose-ghcr.yml up -d`
- [ ] All services healthy: `docker-compose -f docker-compose-ghcr.yml ps`
- [ ] Eureka dashboard accessible: http://localhost:8761/
- [ ] API endpoints responding

## Useful Aliases

Add to `.bashrc` or `.zshrc`:

```bash
alias dcg='docker-compose -f enterprise-microservices-platform/docker-compose-ghcr.yml'
alias build-push='cd enterprise-microservices-platform && ./build-and-push.sh'
alias ghcr-login='echo $CR_PAT | docker login ghcr.io -u nishanth-code --password-stdin'

# Usage:
# dcg up -d
# dcg ps
# dcg logs -f
# build-push 1.0.0 true
# ghcr-login
```

## Files Reference

| File | Purpose |
|------|---------|
| `docker-compose-ghcr.yml` | Pull images from GHCR and run |
| `build-and-push.sh` | Build and push images to GHCR |
| `.dockerignore` | Exclude files from images |
| `.github/workflows/docker-build-push.yml` | Automated CI/CD pipeline |
| `DOCKER-GHCR-GUIDE.md` | Comprehensive guide (this directory) |
| `VERSION.md` | Version history and management |

---

**Last Updated:** February 18, 2024  
**Maintainer:** nishanth-code  
**Registry:** ghcr.io
