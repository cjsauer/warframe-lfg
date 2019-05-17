#!/usr/bin/env bash

set -e

# Test Clojure
clojure -A:test -m cognitect.test-runner -d src/test

# Test ClojureScript
yarn shadow-cljs compile ci
yarn karma start --single-run
