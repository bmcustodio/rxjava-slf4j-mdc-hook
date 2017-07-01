# rxjava-slf4j-mdc-hook

[![Build Status](https://travis-ci.org/brunomcustodio/rxjava-slf4j-mdc-hook.svg?branch=master)](https://travis-ci.org/brunomcustodio/rxjava-slf4j-mdc-hook)
[![codecov](https://codecov.io/gh/brunomcustodio/rxjava-slf4j-mdc-hook/branch/master/graph/badge.svg)](https://codecov.io/gh/brunomcustodio/rxjava-slf4j-mdc-hook)

An
[RxJava](https://github.com/ReactiveX/RxJava)
[hook](https://github.com/ReactiveX/RxJava/pull/4007)
which enables
[SLF4J](https://github.com/qos-ch/slf4j)'s
[`MDC`](http://www.slf4j.org/api/org/apache/log4j/MDC.html)
propagation.

## Usage

If you're using RxJava ≥ 1.1.7:

```java
RxJavaHooks.setOnScheduleAction(new MdcPropagatingOnScheduleAction());
```

If you're using RxJava ≤ 1.1.6:

```java
RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
  @Override
  public Action0 onSchedule(final Action0 action) {
    return new MdcPropagatingAction(action);
  }
});
```

**Note:**
Both
`RxJavaPlugins#getInstance()`
and
`RxJavaSchedulersHook#onSchedule(Action0)`
are deprecated since 1.1.7.

## Binaries

`rxjava-slf4j-mdc-hook` is available from both JCenter and Maven Central:

**Gradle:**

```
compile 'io.github.bmcstdio:rxjava-slf4j-mdc-hook:1.1.2'
```

**Maven:**

```
<dependency>
  <groupId>io.github.bmcstdio</groupId>
  <artifactId>rxjava-slf4j-mdc-hook</artifactId>
  <version>1.1.2</version>
</dependency>
```

## Building

```
$ git clone https://github.com/brunomcustodio/rxjava-slf4j-mdc-hook.git
$ cd rxjava-slf4j-mdc-hook
$ ./gradlew build
```

## License

Copyright 2016-2017 brunomcustodio

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
