#!/bin/bash

# Exit on error
set -e

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo "Checking prerequisites..."

if ! command_exists k6; then
    echo "k6 is not installed. Please install it first:"
    echo "sudo gpg -k"
    echo "sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69"
    echo "echo 'deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main' | sudo tee /etc/apt/sources.list.d/k6.list"
    echo "sudo apt-get update"
    echo "sudo apt-get install k6"
    exit 1
fi

# Check if the application is running with a timeout
echo "Checking if application is running..."
if ! timeout 5 curl -s http://localhost:8080/api/health > /dev/null; then
    echo "Application is not running or not responding. Please start it first:"
    echo "mvn spring-boot:run"
    echo "Then wait for the application to start completely before running this script."
    exit 1
fi

# Create directories for reports
echo "Creating report directories..."
mkdir -p performance-reports

# Run k6 load test
echo "Running k6 load test..."
k6 run src/test/performance/load-test.js --out json=performance-reports/k6-results.json || {
    echo "k6 load test failed. Check the logs for details."
    exit 1
}

# Generate HTML report from k6 results
echo "Generating k6 HTML report..."
k6 run src/test/performance/load-test.js --out html=performance-reports/k6-report.html || {
    echo "Failed to generate k6 HTML report."
    exit 1
}

echo "Performance tests completed successfully."
echo "Reports are available in the performance-reports directory:"
echo "- k6-results.json: Raw test results"
echo "- k6-report.html: HTML report with visualizations" 