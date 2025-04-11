# maven-enforce-rule-no-overwrite-dependencies

Rule for [Maven Enforcer plugin](https://maven.apache.org/enforcer/enforcer-rules/) that verify that child POMs do
not overwrite dependencies from parent POM.

For example, we have dependency `com.example:utils:1.0.0` in parent POM:

```xml
...
<groupId>com.example</groupId>
<artifactId>parent</artifactId>
<version>1.0.0</version>
<packaging>pom</packaging>
...
<dependencyManagement>
    ...
    <dependencies>
        ...
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>utils</artifactId>
            <version>1.0.0</version>
        </dependency>
        ...
    </dependencies>
    ...
</dependencyManagement>
...
```

This dependency have transitive dependency to `com.example:log:1.0.0`:

```mermaid
flowchart LR
    utils["com.example:utils:1.0.0"] --> log["com.example:log:1.0.0"]
```

Now we create a child POM that uses parent POM and _by mistake_ redefine version of dependencies:

```xml
...
<parent>
    <groupId>com.example</groupId>
    <artifactId>parent</artifactId>
    <version>[1.0.0,2.0.0)</version>
</parent>
...
<groupId>com.example</groupId>
<artifactId>program</artifactId>
<version>1.0.0</version>
...
<dependencies>
    ...
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>utils</artifactId>
        <version>1.0.1</version> <!-- This is should be skipped. -->
    </dependency>
    ...
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>log</artifactId>
        <version>1.1.0</version> <!-- This should not be defined: came as transitive dependency. -->
    </dependency>
    ...
</dependencies>
...
```

This rule show error that will help to fix that problem:

```xml
...
<parent>
    <groupId>org.company</groupId>
    <artifactId>parent</artifactId>
    <version>[1.0.0,2.0.0)</version>
</parent>
...
<groupId>com.example</groupId>
<artifactId>program</artifactId>
<version>1.0.0</version>
...
<dependencies>
    ...
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>utils</artifactId>
    </dependency>
    ...
</dependencies>
...
```

## License

Distributed under MIT License.
