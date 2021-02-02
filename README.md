# A not so simple JAX-RS example

## Usage

Compile, package, and run Integration Tests (verify). Launch the REST Server.
```shell
git clone --branch jakarta \
  https://github.com/emmanuelbruno/cours-java-librarymanager-rest.git
mvn clean verify && \
  mvn exec:java
```

Get a Hello message
```shell
curl -v http://127.0.0.1:9998/myapp/biblio
```

Init the database with two authors
```shell
curl -v -X PUT "http://localhost:9998/myapp/biblio/init"
```

Get author 1 in JSON
```shell
curl -v -H "Accept: application/json"  \
  http://127.0.0.1:9998/myapp/biblio/auteurs/1
```

Get author 2 in XML
```shell
curl -v -H "Accept: text/xml"  \
  http://127.0.0.1:9998/myapp/biblio/auteurs/2
```

Get authors in JSON
```shell
curl -v -H "Accept: application/json"  \
  http://127.0.0.1:9998/myapp/biblio/auteurs
```

Removes all authors
```shell
curl -v -X DELETE "http://localhost:9998/myapp/biblio/authors"
```

Adds an author
```shell
curl -v -H "Accept: application/json"  \
  -H "Content-type: application/json"  \
  -X POST \
  -d '{"nom":"John","prenom":"Smith","biographie":"My life"}' \
  "http://localhost:9998/myapp/biblio/auteurs/"
```

Fully update an author
```shell
curl -v -H "Accept: application/json"  \
  -H "Content-type: application/json"  \
  -X PUT \
  -d '{"nom":"Martin","prenom":"Jean","biographie":"ma vie"}' \
  "http://localhost:9998/myapp/biblio/auteurs/1"
```

If a resource doesn't exist an exception is raised, and the 404 http status code is returned
```shell
curl -v -H "Accept: application/json"  \
  http://127.0.0.1:9998/myapp/biblio/auteurs/1000
```

