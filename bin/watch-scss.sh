#!/user/bin/env bash

yarn sass --watch \
     resources/scss/main.scss:resources/public/css/main.css \
     resources/scss/:resources/public/css/
