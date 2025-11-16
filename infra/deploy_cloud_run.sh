#!/bin/bash

# Hornswoggled Cloud Run Deployment Script
# This script automates the complete deployment of the Hornswoggled backend to Google Cloud Run

set -e  # Exit on error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ID="${GCP_PROJECT_ID:-your-project-id}"
REGION="${GCP_REGION:-us-central1}"
SERVICE_NAME="hornswoggled-backend"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

echo -e "${BLUE}╔═══════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   Hornswoggled Cloud Run Deployment Script   ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════╝${NC}\n"

# Function to print step headers
print_step() {
    echo -e "\n${GREEN}▶ $1${NC}"
}

# Function to print errors
print_error() {
    echo -e "${RED}✗ Error: $1${NC}"
}

# Function to print warnings
print_warning() {
    echo -e "${YELLOW}⚠ Warning: $1${NC}"
}

# Function to print success
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Check prerequisites
print_step "Checking prerequisites..."

# Check if gcloud is installed
if ! command -v gcloud &> /dev/null; then
    print_error "gcloud CLI not found. Please install Google Cloud SDK."
    exit 1
fi
print_success "gcloud CLI found"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker not found. Please install Docker."
    exit 1
fi
print_success "Docker found"

# Check if logged in to gcloud
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" &> /dev/null; then
    print_warning "Not logged in to gcloud"
    echo "Please run: gcloud auth login"
    exit 1
fi
print_success "Authenticated with gcloud"

# Set project
print_step "Setting GCP project to ${PROJECT_ID}..."
gcloud config set project ${PROJECT_ID}
print_success "Project set"

# Enable required APIs
print_step "Enabling required Google Cloud APIs..."
gcloud services enable \
    run.googleapis.com \
    cloudbuild.googleapis.com \
    artifactregistry.googleapis.com \
    firestore.googleapis.com \
    storage.googleapis.com \
    --quiet

print_success "APIs enabled"

# Build Docker image
print_step "Building Docker image..."
cd ../backend

if docker build -t ${SERVICE_NAME}:latest .; then
    print_success "Docker image built successfully"
else
    print_error "Failed to build Docker image"
    exit 1
fi

# Tag image for GCR
print_step "Tagging image for Google Container Registry..."
docker tag ${SERVICE_NAME}:latest ${IMAGE_NAME}:latest
docker tag ${SERVICE_NAME}:latest ${IMAGE_NAME}:$(date +%Y%m%d-%H%M%S)
print_success "Image tagged"

# Configure Docker for GCR
print_step "Configuring Docker authentication..."
gcloud auth configure-docker --quiet
print_success "Docker configured"

# Push to GCR
print_step "Pushing image to Google Container Registry..."
if docker push ${IMAGE_NAME}:latest; then
    print_success "Image pushed successfully"
else
    print_error "Failed to push image"
    exit 1
fi

# Deploy to Cloud Run
print_step "Deploying to Cloud Run..."

gcloud run deploy ${SERVICE_NAME} \
    --image ${IMAGE_NAME}:latest \
    --region ${REGION} \
    --platform managed \
    --allow-unauthenticated \
    --memory 512Mi \
    --cpu 1 \
    --max-instances 10 \
    --min-instances 0 \
    --timeout 60 \
    --set-env-vars "NODE_ENV=production" \
    --set-env-vars "PORT=8080" \
    --quiet

if [ $? -eq 0 ]; then
    print_success "Deployed to Cloud Run successfully"
else
    print_error "Failed to deploy to Cloud Run"
    exit 1
fi

# Get service URL
SERVICE_URL=$(gcloud run services describe ${SERVICE_NAME} \
    --region ${REGION} \
    --format 'value(status.url)')

# Deploy Firestore rules
print_step "Deploying Firestore security rules..."
cd ../infra

if [ -f "firestore.rules" ]; then
    gcloud firestore databases update \
        --type=firestore-native \
        --quiet 2>/dev/null || true

    # Note: Actual rule deployment requires firebase-tools CLI
    print_warning "Firestore rules file created. Deploy manually with: firebase deploy --only firestore:rules"
else
    print_warning "Firestore rules file not found"
fi

# Deploy Firestore indexes
print_step "Deploying Firestore indexes..."
if [ -f "firestore.indexes.json" ]; then
    print_warning "Firestore indexes file created. Deploy manually with: firebase deploy --only firestore:indexes"
else
    print_warning "Firestore indexes file not found"
fi

# Deploy Storage rules
print_step "Deploying Storage security rules..."
if [ -f "storage.rules" ]; then
    print_warning "Storage rules file created. Deploy manually with: firebase deploy --only storage"
else
    print_warning "Storage rules file not found"
fi

# Health check
print_step "Performing health check..."
sleep 5  # Wait for service to be ready

if curl -f -s "${SERVICE_URL}/health" > /dev/null; then
    print_success "Health check passed"
else
    print_warning "Health check failed - service may still be starting"
fi

# Display summary
echo -e "\n${BLUE}╔═══════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║            Deployment Summary                 ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════╝${NC}\n"

echo -e "${GREEN}Service URL:${NC} ${SERVICE_URL}"
echo -e "${GREEN}Region:${NC} ${REGION}"
echo -e "${GREEN}Project ID:${NC} ${PROJECT_ID}"
echo -e "${GREEN}Image:${NC} ${IMAGE_NAME}:latest"

echo -e "\n${YELLOW}Next Steps:${NC}"
echo "1. Set up environment variables:"
echo "   gcloud run services update ${SERVICE_NAME} --update-env-vars KEY=VALUE --region ${REGION}"
echo ""
echo "2. Deploy Firebase rules:"
echo "   cd infra && firebase deploy --only firestore:rules,firestore:indexes,storage"
echo ""
echo "3. Seed initial data:"
echo "   node firebase-init.js"
echo ""
echo "4. Test the API:"
echo "   curl ${SERVICE_URL}"

echo -e "\n${GREEN}✓ Deployment complete!${NC}\n"

# Return to original directory
cd ..

exit 0
