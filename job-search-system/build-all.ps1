# ============================================================
# Job Search System - Build All Services (PowerShell)
# Builds all Maven services, skipping tests for speed.
# Run: .\build-all.ps1
# Add -RunTests to include tests: .\build-all.ps1 -RunTests
# ============================================================

param(
    [switch]$RunTests = $false
)

$ErrorActionPreference = "Stop"
$BaseDir = $PSScriptRoot

$services = @(
    "eureka-server",
    "api-gateway",
    "auth-service",
    "employer-service",
    "job-service",
    "jobseeker-service",
    "application-service",
    "basket-service",
    "messaging-service"
)

$mvnArgs = if ($RunTests) { "clean package" } else { "clean package -DskipTests" }
$failed = @()

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Job Search System - Building All Services" -ForegroundColor Cyan
Write-Host "  Tests: $(if ($RunTests) { 'ENABLED' } else { 'SKIPPED (use -RunTests to enable)' })" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

$total = $services.Count
$current = 0

foreach ($service in $services) {
    $current++
    $serviceDir = Join-Path $BaseDir $service

    if (-not (Test-Path $serviceDir)) {
        Write-Host "[$current/$total] SKIP: $service (directory not found)" -ForegroundColor Yellow
        continue
    }

    Write-Host "[$current/$total] Building $service..." -ForegroundColor Yellow
    Push-Location $serviceDir
    try {
        $output = & mvn $mvnArgs.Split(' ') 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  FAILED: $service" -ForegroundColor Red
            $failed += $service
        } else {
            Write-Host "  SUCCESS: $service" -ForegroundColor Green
        }
    }
    catch {
        Write-Host "  ERROR: $service - $_" -ForegroundColor Red
        $failed += $service
    }
    finally {
        Pop-Location
    }
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
if ($failed.Count -eq 0) {
    Write-Host "  All $total services built successfully!" -ForegroundColor Green
} else {
    Write-Host "  $($total - $failed.Count)/$total services built. Failed:" -ForegroundColor Red
    foreach ($f in $failed) {
        Write-Host "    - $f" -ForegroundColor Red
    }
}
Write-Host "============================================================" -ForegroundColor Cyan
