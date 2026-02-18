# Version Management

Current project versions and tagging strategy.

## Current Release

- **Latest Stable:** 1.0.0
- **Next Version:** 1.1.0 (in development)
- **Release Date:** 2024-02-18

## Version History

### v1.0.0 (Current Stable)
- ✅ All 9 microservices working
- ✅ Synchronous inter-service communication (Feign clients)
- ✅ Eureka service discovery
- ✅ API Gateway with JWT authentication
- ✅ Swagger UI on all services
- ✅ Resilience4j circuit breakers
- ✅ Docker images ready
- ✅ GHCR integration
- ✅ GitHub Actions CI/CD pipeline

**Build Command:**
```bash
./build-and-push.sh 1.0.0 true
```

**Deploy Command:**
```bash
docker-compose -f docker-compose-ghcr.yml up -d
```

### v1.1.0 (Planned)
- 📋 Database migration scripts
- 📋 Enhanced error handling
- 📋 Service-to-service request tracing
- 📋 Health check improvements
- 📋 Documentation updates

## Version Tagging Strategy

### For Production Releases

Use semantic versioning: `MAJOR.MINOR.PATCH`

```bash
# Release 1.0.0
./build-and-push.sh 1.0.0 true
git tag v1.0.0
git push origin v1.0.0

# Image tags created:
# ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:1.0.0
# ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
```

### For CI/CD Builds

Use timestamp versioning: `YYYY.MM.DD-GitHash`

```bash
# Automatic on every push to main
./build-and-push.sh true

# Image tags created:
# ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:2024.02.18-a1b2c3d
# ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:main
# ghcr.io/nishanth-code/enterprise-microservices-platform/order-service:latest
```

## Services and Versions

All services version together for consistency.

| Service | Current | Status |
|---------|---------|--------|
| service-registry | 1.0.0 | ✅ Stable |
| config-server | 1.0.0 | ✅ Stable |
| api-gateway | 1.0.0 | ✅ Stable |
| auth-service | 1.0.0 | ✅ Stable |
| product-catalog-service | 1.0.0 | ✅ Stable |
| inventory-service | 1.0.0 | ✅ Stable |
| order-service | 1.0.0 | ✅ Stable |
| notification-service | 1.0.0 | ✅ Stable |
| aggregation-service | 1.0.0 | ✅ Stable |

## Build and Push Workflow

### Manual Release

```bash
# 1. Update version in this file
# 2. Build and push
cd enterprise-microservices-platform
./build-and-push.sh 1.1.0 true

# 3. Create git tag
git tag v1.1.0
git push origin v1.1.0

# 4. Deploy docs step
# Update deployment documentation with new version
```

### Automatic CI/CD

Triggered on every push to `main`:

1. Detects changes in `enterprise-microservices-platform/`
2. Builds all 9 services
3. Tags with timestamp + `latest`
4. Pushes to GHCR
5. Verifies images exist

## Rollback Strategy

### If Issue Discovered

```bash
# Deploy previous stable version
docker-compose -f docker-compose-ghcr.yml down
docker-compose -f docker-compose-ghcr.yml pull  # Pulls :latest
# Or specify version explicitly:
sed 's/:latest/:1.0.0/g' docker-compose-ghcr.yml | docker-compose -f - up -d
```

### If Image Corrupted

```bash
# Delete bad version
gh api repos/nishanth-code/enterprise-microservices-platform/packages/container/order-service -X DELETE

# Rebuild and push
./build-and-push.sh 1.0.0 true
```

## Next Release Checklist

When ready to release next version:

- [ ] All tests passing
- [ ] All services healthy
- [ ] Documentation updated
- [ ] CHANGELOG updated
- [ ] Version numbers updated
- [ ] Build and push validation
- [ ] Deployment verification
- [ ] GitHub release created

## Notes

- All 9 services must use same version
- Never reuse version tags
- Keep `latest` as stable pointer
- Maintain 5 latest versions minimum
- Document breaking changes in releases
