# ReFact

## Prerequisites
Java, Maven

## Installation

```
$ git clone https://github.com/luigi-asprino/refact.git
$ cd refact/
$ mvn install
```

## Usage 

### Example 1

Applying [Query1](https://raw.githubusercontent.com/luigi-asprino/refact/master/examples/ex1/queries/query1.sparql) and [Query2](https://raw.githubusercontent.com/luigi-asprino/refact/master/examples/ex1/queries/query2.sparql) over [Framester SPARQL Endpoint](https://w3id.org/framester/sparql)

```
$ mvn exec:java -Dexec.mainClass="it.cnr.istc.stlab.refact.Refact" -Dexec.args="examples/ex1/example.properties" -DjvmArgs="-Xmx16g"
```

The result (i.e. `out.ttl`) will be found under `examples/ex1/`.

### Example 2

Applying [Query1](https://raw.githubusercontent.com/luigi-asprino/refact/master/examples/ex2/queries/query1.sparql) over the dataset resulting from the union of the files in `examples/ex2/input/`.

```
$ mvn exec:java -Dexec.mainClass="it.cnr.istc.stlab.refact.Refact" -Dexec.args="examples/ex2/example.properties" -DjvmArgs="-Xmx16g"
```

The result (i.e. `out.ttl`) will be found under `examples/ex2/`.
