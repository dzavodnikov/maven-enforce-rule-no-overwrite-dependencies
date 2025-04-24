# POM examples

This directory contain test examples of POM files.

POMs that should not failed starts with `correct-` prefix. POMs with errors starts with `wrong-` prefixes.

Folder `repository` contain fake repository for unit-testing.

## Common scheme

```mermaid
flowchart LR
    subgraph transitive["transitive dependencies"]
        transitive_hamcrest[["
            org.hamcrest
            hamcrest-core
            1.3
            _compile_
        "]]
    end

    subgraph parent0["parent0.xml"]
        parent0_junit[["
            junit
            junit
            4.13.1
            test
        "]]
    end
    parent0_junit-->transitive_hamcrest

    subgraph parent1["parent1.xml"]
        parent1_commons_io[["
            commons-io
            commons-io
            2.18.0
            _compile_
        "]]
    end
    parent1-->parent0

    subgraph correct_no_deps["correct-no-deps.xml"]
    end
    correct_no_deps-->parent1

    subgraph correct_with_deps["correct-with-deps.xml"]
        correct_with_deps_commons_io("
            commons-io
            commons-io
        ")
    end
    correct_with_deps-->parent1

    subgraph wrong_version_parent1["wrong-version-parent1.xml"]
        wrong_version_parent1_commons_io("
            commons-io
            commons-io
            **2.19.0**
        ")
    end
    wrong_version_parent1-->parent1

    subgraph wrong_version_parent0["wrong-version-parent0.xml"]
        wrong_version_parent0_commons_io("
            junit
            junit
            **4.13.2**
        ")
    end
    wrong_version_parent0-->parent1

    subgraph wrong_version_transitive["wrong-version-transitive.xml"]
        wrong_version_transitive_commons_io("
            org.hamcrest
            hamcrest-core
            **2.1**
        ")
    end
    wrong_version_transitive-->parent1

    subgraph wrong_scope_parent1["wrong-scope-parent1.xml"]
        wrong_scope_parent1_commons_io("
            commons-io
            commons-io
            **test**
        ")
    end
    wrong_scope_parent1-->parent1

    subgraph wrong_scope_parent0["wrong-scope-parent0.xml"]
        wrong_scope_parent0_commons_io("
            junit
            junit
            **compile**
        ")
    end
    wrong_scope_parent0-->parent1

    subgraph wrong_scope_transitive["wrong-scope-transitive.xml"]
        wrong_scope_transitive_commons_io("
            org.hamcrest
            hamcrest-core
            1.3
            **provided**
        ")
    end
    wrong_scope_transitive-->parent1
```

## Run

```sh
$ mvn -f SOME_FILE.xml verify
```
