# Warframe LFG
[![CircleCI](https://circleci.com/gh/cjsauer/warframe-lfg.svg?style=svg)](https://circleci.com/gh/cjsauer/warframe-lfg)

The best place to find Warframe teammates on the web.

## Development

There are only a few primary dependencies you'll need to have installed to develop
on this project:

- [Java JDK 8+][1]
- [Clojure CLI][8]
- [Node.js (LTS recommended)][2]
- [Yarn package manager][3]

### Running Locally

Before developing locally, first install the required NPM dependencies
(you'll only have to do this _once_):

```
yarn install
```

Now we're ready to start the local development server and watch source files
for changes:

```
yarn shadow-cljs watch app
```

In a separate terminal, let's also watch [SCSS][5] files for changes:

```
./bin/watch-scss.sh
```

With those commands running, you're ready to open your web browser to
[localhost:8000][4].

#### Server only

If you just need to run the server process alone (the API), you can use
the following:

```
clojure -A:server
```

This will run just the API, which you can test using `curl`:

```
TBD
```

Note that when using `yarn shadow-cljs watch app` the development server that is
started already supports API calls, so you only need to use this command when
you want to run _only_ the API (on a VM for example). Also be aware that this
process does not serve static resources. These should be made available by a
more appropriate service (e.g. [S3][7]).

### Clojure(Script) REPL

This step is optional, but if you know your way around the [Clojure(Script)][6]
programming language then you'll be interested in starting up a REPL. With the
above terminals still running their respective commands, open a third terminal
and enter:

```
yarn shadow-cljs cljs-repl app
```

If all goes well, you should be immediately dropped into the `cljs.user`
namespace at the REPL. Try out a command:

```
cljs.user=> (js/alert "It worked!")
```

You should see an alert box displaying `It worked!` in your browser.

### Testing

Tests can be run with:

```
./bin/test-all.sh
```

This will run both Clojure and ClojureScript tests.

### Release Build

To build a fully optimized release artifact, run the following:

```
# One-time compilation of SCSS
./bin/compile-scss.sh

# Compile ClojureScript with advanced optimizations
yarn shadow-cljs release app
```

This results in a fully prepared `resources/public/` directory, ready for
hosting.

[1]: https://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: https://nodejs.org/en/
[3]: https://yarnpkg.com/en/
[4]: http://localhost:8000
[5]: https://sass-lang.com/
[6]: https://clojure.org/
[7]: https://aws.amazon.com/s3/
[8]: https://clojure.org/guides/getting_started
