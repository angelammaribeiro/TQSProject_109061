# Performance Testing for Moliceiro University Restaurant

This document provides instructions for running performance tests using k6 and Lighthouse.

## Prerequisites

1. Install k6:
```bash
# For Ubuntu/Debian
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

2. Install Lighthouse:
```bash
npm install -g lighthouse
```

3. Install Chrome (required for Lighthouse):
```bash
sudo apt-get install google-chrome-stable
```

## Running the Tests

1. Start the application:
```bash
mvn spring-boot:run
```

2. Run the performance tests:
```bash
./run-performance-tests.sh
```

## Test Scenarios

The performance tests include:

1. Load Testing (k6)
   - Ramp up to 50 concurrent users
   - Maintain load for 5 minutes
   - Ramp down to 0 users
   - SLOs:
     - 95% of requests below 200ms
     - Error rate below 1%
     - Custom error rate below 1%

2. Quality Attributes (Lighthouse)
   - Performance metrics
   - Accessibility
   - Best practices
   - SEO

## Reports

After running the tests, reports will be available in the `performance-reports` directory:

1. k6 Results
   - `k6-results.json`: Raw test results
   - `k6-report.html`: HTML report with visualizations

2. Lighthouse Results
   - `lighthouse-report.html`: HTML report
   - `lighthouse-report.json`: Raw JSON data

## Interpreting Results

1. k6 Metrics
   - http_req_duration: Response time distribution
   - http_req_failed: Failed request rate
   - errors: Custom error rate

2. Lighthouse Scores
   - Performance: 0-100 score
   - Accessibility: 0-100 score
   - Best Practices: 0-100 score
   - SEO: 0-100 score

## Troubleshooting

If you encounter issues:

1. Ensure the application is running
2. Check port availability (8080)
3. Verify Chrome installation for Lighthouse
4. Check system resources during tests
5. Review k6 and Lighthouse logs 