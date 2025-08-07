#!/bin/bash

# RepeatWise Horizontal Deployment Script
# This script deploys the entire RepeatWise application stack

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="repeatwise"
NAMESPACE="repeatwise"
DOCKER_REGISTRY=""
BACKEND_IMAGE="repeatwise-backend:latest"
FRONTEND_IMAGE="repeatwise-frontend:latest"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command_exists kubectl; then
        print_error "kubectl is not installed. Please install kubectl first."
        exit 1
    fi
    
    if ! command_exists docker-compose; then
        print_error "docker-compose is not installed. Please install docker-compose first."
        exit 1
    fi
    
    print_success "All prerequisites are satisfied"
}

# Function to build Docker images
build_images() {
    print_status "Building Docker images..."
    
    # Build backend image
    print_status "Building backend image..."
    docker build -t $BACKEND_IMAGE ./repeatwise-server
    
    # Build frontend image
    print_status "Building frontend image..."
    docker build -t $FRONTEND_IMAGE ./repeatwise-ui
    
    print_success "Docker images built successfully"
}

# Function to deploy with Docker Compose
deploy_docker_compose() {
    print_status "Deploying with Docker Compose..."
    
    # Stop existing containers
    docker-compose down --remove-orphans
    
    # Build and start services
    docker-compose up -d --build
    
    print_success "Docker Compose deployment completed"
    print_status "Application is available at:"
    print_status "  - Frontend: http://localhost:3000"
    print_status "  - Backend API: http://localhost:8080"
    print_status "  - Swagger UI: http://localhost:8080/swagger-ui.html"
    print_status "  - Grafana: http://localhost:3001 (admin/admin123)"
    print_status "  - Prometheus: http://localhost:9090"
}

# Function to deploy with Kubernetes
deploy_kubernetes() {
    print_status "Deploying with Kubernetes..."
    
    # Create namespace
    kubectl apply -f kubernetes/namespace.yaml
    
    # Apply ConfigMap
    kubectl apply -f kubernetes/configmap.yaml
    
    # Deploy database
    kubectl apply -f kubernetes/postgres-deployment.yaml
    
    # Deploy Redis
    kubectl apply -f kubernetes/redis-deployment.yaml
    
    # Wait for database and Redis to be ready
    print_status "Waiting for database and Redis to be ready..."
    kubectl wait --for=condition=ready pod -l app=repeatwise-postgres -n $NAMESPACE --timeout=300s
    kubectl wait --for=condition=ready pod -l app=repeatwise-redis -n $NAMESPACE --timeout=300s
    
    # Deploy backend
    kubectl apply -f kubernetes/backend-deployment.yaml
    
    # Deploy frontend
    kubectl apply -f kubernetes/frontend-deployment.yaml
    
    # Deploy ingress
    kubectl apply -f kubernetes/ingress.yaml
    
    print_success "Kubernetes deployment completed"
    print_status "To access the application:"
    print_status "  - Add 'repeatwise.local' to your /etc/hosts file"
    print_status "  - Access the application at: http://repeatwise.local"
}

# Function to scale services
scale_services() {
    local backend_replicas=$1
    local frontend_replicas=$2
    
    print_status "Scaling services..."
    
    if [ "$DEPLOYMENT_TYPE" = "kubernetes" ]; then
        kubectl scale deployment repeatwise-backend --replicas=$backend_replicas -n $NAMESPACE
        kubectl scale deployment repeatwise-frontend --replicas=$frontend_replicas -n $NAMESPACE
        print_success "Services scaled to $backend_replicas backend and $frontend_replicas frontend replicas"
    else
        # For Docker Compose, we need to update the docker-compose.yml file
        print_warning "Scaling in Docker Compose requires manual configuration in docker-compose.yml"
    fi
}

# Function to show status
show_status() {
    print_status "Application status:"
    
    if [ "$DEPLOYMENT_TYPE" = "kubernetes" ]; then
        echo ""
        kubectl get pods -n $NAMESPACE
        echo ""
        kubectl get services -n $NAMESPACE
        echo ""
        kubectl get ingress -n $NAMESPACE
    else
        echo ""
        docker-compose ps
        echo ""
        docker-compose logs --tail=10
    fi
}

# Function to show logs
show_logs() {
    local service=$1
    
    if [ "$DEPLOYMENT_TYPE" = "kubernetes" ]; then
        kubectl logs -f deployment/$service -n $NAMESPACE
    else
        docker-compose logs -f $service
    fi
}

# Function to cleanup
cleanup() {
    print_status "Cleaning up..."
    
    if [ "$DEPLOYMENT_TYPE" = "kubernetes" ]; then
        kubectl delete namespace $NAMESPACE
        print_success "Kubernetes resources cleaned up"
    else
        docker-compose down -v
        print_success "Docker Compose resources cleaned up"
    fi
}

# Main script
main() {
    DEPLOYMENT_TYPE=${1:-"docker-compose"}
    
    case $DEPLOYMENT_TYPE in
        "docker-compose"|"compose")
            check_prerequisites
            build_images
            deploy_docker_compose
            ;;
        "kubernetes"|"k8s")
            check_prerequisites
            build_images
            deploy_kubernetes
            ;;
        "scale")
            scale_services ${2:-3} ${3:-3}
            ;;
        "status")
            show_status
            ;;
        "logs")
            show_logs $2
            ;;
        "cleanup")
            cleanup
            ;;
        *)
            echo "Usage: $0 {docker-compose|kubernetes|scale|status|logs|cleanup}"
            echo ""
            echo "Commands:"
            echo "  docker-compose  - Deploy using Docker Compose"
            echo "  kubernetes      - Deploy using Kubernetes"
            echo "  scale <backend> <frontend> - Scale services"
            echo "  status          - Show application status"
            echo "  logs <service>  - Show service logs"
            echo "  cleanup         - Clean up all resources"
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
