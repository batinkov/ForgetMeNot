# ForgetMeNot — convenience wrappers around the Gradle build.
#
# These targets are aliases, not build logic: Gradle remains the source of
# truth. Anything that needs real build behaviour belongs in build.gradle.kts,
# not here. Override flags per-invocation, e.g. `make test FLAGS=--info`.

GRADLE := ./gradlew
MODULE := :composeApp
FLAGS  ?= --console=plain

.DEFAULT_GOAL := help

## run: launch the desktop app
run:
	$(GRADLE) $(MODULE):run $(FLAGS)

## test: run the unit tests for every configured target
test:
	$(GRADLE) $(MODULE):allTests $(FLAGS)

## build: compile everything and run the tests
build:
	$(GRADLE) $(MODULE):build $(FLAGS)

## dist: build the runtime image without packaging it
dist:
	$(GRADLE) $(MODULE):createDistributable $(FLAGS)

## run-dist: run the packaged distributable rather than the raw classpath
run-dist:
	$(GRADLE) $(MODULE):runDistributable $(FLAGS)

## package: build the native package for the current OS (.deb here)
package:
	$(GRADLE) $(MODULE):packageDistributionForCurrentOS $(FLAGS)

## tasks: list every Gradle task available on the module
tasks:
	$(GRADLE) $(MODULE):tasks --all $(FLAGS)

## clean: delete build outputs
clean:
	$(GRADLE) clean $(FLAGS)

## help: show this message
help:
	@echo "ForgetMeNot — available targets:"
	@grep -E '^## ' $(MAKEFILE_LIST) | sed 's/^## //' | awk -F': ' '{printf "  %-10s %s\n", $$1, $$2}'

.PHONY: run test build dist run-dist package tasks clean help
