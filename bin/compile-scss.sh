#!/usr/bin/env bash

yarn sass \
     resources/scss/main.scss:resources/public/css/main.css \
     resources/scss/:resources/public/css/
