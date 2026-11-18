# Changelog
All notable changes to this project will be documented in this file.

## [0.3.3] - 2026-11-18

- security: fix CVE-2026-114938 ("Java4Shell"), a critical RCE in the AVX-512
  accelerated Base64 decoder of OpenJDK 17.0.6+/21/25/27 - the docker runtime
  image is now pinned to amazoncorretto:21.0.13-alpine (Corretto 21.0.13.1.2,
  November 2026 CSPU) and the build fails if the base image lacks the fix
- security: bump jackson to 2.18.12, fixing 17 known CVEs of the 2.18.0 line
  (jackson-core DoS/ReDoS in number/token parsing, reachable via the JSON
  endpoint; jackson-databind validator and annotation bypasses), and the
  transitively pulled jsoup to 1.24.1 (CVE-2026-71497, CVE-2026-75140)
- add security notes to the README (interim mitigation via
  -XX:-UseBASE64Intrinsics, ARM64 hosts are not affected)

## [0.3.2] - 2026-03-15

- bumped jersey and picocli to latest available version

## [0.3.1] - 2024-09-27

- add markdown parser

## [0.3.0] - 2024-09-26

- bumped jersey and picocli to latest available version
- switched to multi-stage docker image
- switched to corretto 21 base images
- configuration via environment variables
- add docker compose file
- code formatting (google style)
- disable file logging - all log messages are written to console/stdout

## [0.2.0] - 2020-10-24

- switched from apache http client to jersey http client

## [0.1.0] - 2020-09-17

- initial version
