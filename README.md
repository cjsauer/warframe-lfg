# Warframe LFG
[![CircleCI](https://circleci.com/gh/cjsauer/warframe-lfg.svg?style=svg)](https://circleci.com/gh/cjsauer/warframe-lfg)

The best place to find Warframe teammates on the web.

## Development

There are a few primary dependencies you'll need to have installed to develop on
this project:

- [Java JDK 8+][1]
- [Clojure CLI][8]
- [Node.js (LTS recommended)][2]
- [Yarn package manager][3]
- [AWS CLI][10]
- [A running Datomic system][9]

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

Now for the backend, start up the SOCKS proxy to connect your local dev machine
to the bastion host running in AWS:

```
./bin/datomic-socks-proxy -r us-east-1 datomic-ions-stack
```

Obviously you'll want to use the AWS region and name of your own running
[Datomic system][9].

With those commands running, you're ready to open your web browser to
[localhost:8000][4].

#### Clojure(Script) REPL

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

A Clojure REPL can be started in a similar fashion:

```
yarn shadow-cljs clj-repl
```

Visit the [Editor Integration][11] section of the shadow-cljs docs for
information on integrating the REPLs into your editor of choice.

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
hosting (e.g. from an S3 bucket).

### Deployment

See the [.circleci/config.yml][12] for deployment steps. In a nutshell,
we deploy the aformentioned `resources/public/` directory to an S3 bucket
with public read access. Then, the [Ions][14] can be deployed with a single
command:

```
clojure -A:dev:deploy
```

This will push and deploy the [configured Ions][13] to the running Datomic
system.

[1]: https://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: https://nodejs.org/en/
[3]: https://yarnpkg.com/en/
[4]: http://localhost:8000
[5]: https://sass-lang.com/
[6]: https://clojure.org/
[7]: https://aws.amazon.com/s3/
[8]: https://clojure.org/guides/getting_started
[9]: https://docs.datomic.com/cloud/setting-up.html
[10]: http://docs.aws.amazon.com/general/latest/gr/managing-aws-access-keys.html
[11]: https://shadow-cljs.github.io/docs/UsersGuide.html#_editor_integration
[12]: ./.circleci/config.yml
[13]: ./resources/datomic/ion-config.edn
[14]: https://docs.datomic.com/cloud/ions/ions.html
