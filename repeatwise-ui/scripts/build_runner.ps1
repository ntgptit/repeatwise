# Build Runner Script for RepeatWise UI
# This script runs build_runner to generate code for Freezed, json_serializable, and Riverpod

Write-Host "Running build_runner for RepeatWise UI..." -ForegroundColor Green

# Check if Flutter is available
try {
    $flutterVersion = flutter --version
    Write-Host "Flutter found: $flutterVersion" -ForegroundColor Green
} catch {
    Write-Host "Flutter not found in PATH. Please ensure Flutter is installed and added to PATH." -ForegroundColor Red
    exit 1
}

# Run build_runner
Write-Host "Generating code with build_runner..." -ForegroundColor Yellow
flutter pub run build_runner build --delete-conflicting-outputs

Write-Host "Build runner completed!" -ForegroundColor Green
