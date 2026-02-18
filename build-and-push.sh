#!/bin/bash

#############################################################################
# Enterprise Microservices Platform - Docker Build and Push Script
# This script builds and pushes Docker images to GitHub Container Registry
# 
# Usage:
#   ./build-and-push.sh                    # Build and push all services with latest + timestamp
#   ./build-and-push.sh 1.0.0              # Build and push with version 1.0.0 + latest
#   ./build-and-push.sh 1.0.0 true         # Build and push with version 1.0.0 + latest
#   ./build-and-push.sh 1.0.0 false        # Build only without pushing
#
#############################################################################

set -e  # Exit on error

# Configuration
GITHUB_USERNAME="nishanth-code"
REGISTRY="ghcr.io"
IMAGE_PREFIX="${REGISTRY}/${GITHUB_USERNAME}/enterprise-microservices-platform"
WORKSPACE_DIR="enterprise-microservices-platform"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Services to build
SERVICES=(

 
  
  
  
  "aggregation-service"

  "service-registry"
  "config-server"
   "api-gateway"
   "auth-service"
    "product-catalog-service"
    "inventory-service"
  "order-service"
  "notification-service"
)

# Functions
print_header() {
  echo -e "${BLUE}=================================${NC}"
  echo -e "${BLUE}$1${NC}"
  echo -e "${BLUE}=================================${NC}"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

# Parse arguments
VERSION="${1:-}"
PUSH="${2:-true}"

# Default to timestamp version if not provided
if [ -z "$VERSION" ]; then
  VERSION="$(date +%Y.%m.%d)-$(git rev-parse --short HEAD)"
  print_warning "No version provided, using timestamp: $VERSION"
fi

# Validate version format
if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]] && ! [[ "$VERSION" =~ ^[0-9]{4}\.[0-9]{2}\.[0-9]{2}- ]]; then
  print_warning "Version format should be X.Y.Z or YYYY.MM.DD-HASH. Using: $VERSION"
fi

print_header "Docker Build and Push Configuration"
echo "Registry:        $REGISTRY"
echo "Username:        $GITHUB_USERNAME"
echo "Image Prefix:    $IMAGE_PREFIX"
echo "Version:         $VERSION"
echo "Push to Registry: $PUSH"
echo "Services:        ${#SERVICES[@]}"

# Check prerequisites
print_header "Checking Prerequisites"

if ! command -v docker &> /dev/null; then
  print_error "Docker is not installed"
  exit 1
fi
print_success "Docker is installed"

if ! command -v git &> /dev/null; then
  print_error "Git is not installed"
  exit 1
fi
print_success "Git is installed"

# Check if logged into Docker registry
if [ "$PUSH" = "true" ]; then
  if ! docker info | grep -q "Registries"; then
    print_warning "You might not be logged into Docker registry"
    print_warning "Run: docker login $REGISTRY"
  fi
  print_success "Docker registry check passed"
fi

# Check workspace
if [ ! -d "$WORKSPACE_DIR" ]; then
  print_error "Workspace directory '$WORKSPACE_DIR' not found"
  exit 1
fi
print_success "Workspace directory found: $WORKSPACE_DIR"

# Build images
print_header "Building Docker Images"

SUCCESS_COUNT=0
FAILED_COUNT=0

for SERVICE in "${SERVICES[@]}"; do
  DOCKERFILE="${WORKSPACE_DIR}/${SERVICE}/Dockerfile"
  IMAGE_NAME="${IMAGE_PREFIX}/${SERVICE}"
  
  if [ ! -f "$DOCKERFILE" ]; then
    print_error "Dockerfile not found: $DOCKERFILE"
    ((FAILED_COUNT++))
    continue
  fi
  
  echo ""
  echo -e "${BLUE}Building: $SERVICE${NC}"
  
  # Build image
  if docker build \
    -f "$DOCKERFILE" \
    -t "${IMAGE_NAME}:${VERSION}" \
    -t "${IMAGE_NAME}:latest" \
    "$WORKSPACE_DIR" \
    2>&1 | head -20; then
    
    print_success "Built: ${IMAGE_NAME}:${VERSION}"
    
    # Push image if requested
    if [ "$PUSH" = "true" ]; then
      echo "Pushing: ${IMAGE_NAME}:${VERSION}"
      if docker push "${IMAGE_NAME}:${VERSION}"; then
        print_success "Pushed: ${IMAGE_NAME}:${VERSION}"
      else
        print_error "Failed to push: ${IMAGE_NAME}:${VERSION}"
        ((FAILED_COUNT++))
        continue
      fi
      
      echo "Pushing: ${IMAGE_NAME}:latest"
      if docker push "${IMAGE_NAME}:latest"; then
        print_success "Pushed: ${IMAGE_NAME}:latest"
      else
        print_error "Failed to push: ${IMAGE_NAME}:latest"
        ((FAILED_COUNT++))
        continue
      fi
    fi
    
    ((SUCCESS_COUNT++))
  else
    print_error "Failed to build: $SERVICE"
    ((FAILED_COUNT++))
  fi
done

# Summary
print_header "Build Summary"
echo "Successfully built: $SUCCESS_COUNT/${#SERVICES[@]}"
echo "Failed:             $FAILED_COUNT/${#SERVICES[@]}"

if [ $FAILED_COUNT -gt 0 ]; then
  print_error "Some services failed to build"
  exit 1
fi

print_success "All services built successfully!"

if [ "$PUSH" = "true" ]; then
  print_header "Next Steps"
  echo "1. Verify images in GitHub Container Registry:"
  echo "   https://github.com/nishanth-code?tab=packages"
  echo ""
  echo "2. Deploy with docker-compose:"
  echo "   docker-compose -f docker-compose-ghcr.yml up -d"
  echo ""
  echo "3. Check service status:"
  echo "   docker-compose -f docker-compose-ghcr.yml ps"
fi

print_success "Build and push completed!"
