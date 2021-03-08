# A not so simple JAX-RS example

## Usage

See a complete set of samples [here](queries/sample-requests.rest)

Compile, package, and run Integration Tests (verify). Launch the REST Server.
```shell
git clone \
  https://github.com/emmanuelbruno/cours-java-librarymanager-rest.git
mvn clean verify && \
  mvn exec:java
```

Get a Hello message
```shell
curl -s -D - http://localhost:9998/myapp/biblio
```

Init the database with two authors
```shell
curl -s -D - -X PUT "http://localhost:9998/myapp/biblio/init"
```

Get author 1 in JSON
```shell
curl -s -D - -H "Accept: application/json"  \
  http://localhost:9998/myapp/biblio/auteurs/1
```

Get author 2 in XML
```shell
curl -s -D - -H "Accept: text/xml"  \
  http://localhost:9998/myapp/biblio/auteurs/2
```

Get authors in JSON
```shell
curl -s -D - -H "Accept: application/json"  \
  http://localhost:9998/myapp/biblio/auteurs
```

Removes an author
```shell
curl -s -D - -X DELETE "http://localhost:9998/myapp/biblio/auteurs/1"
```

Removes all authors
```shell
curl -s -D - -X DELETE "http://localhost:9998/myapp/biblio/auteurs"
```

Adds an author
```shell
curl -s -D - -H "Accept: application/json"  \
  -H "Content-type: application/json"  \
  -X POST \
  -d '{"nom":"John","prenom":"Smith","biographie":"My life"}' \
  "http://localhost:9998/myapp/biblio/auteurs/"
```

Fully update an author
```shell
curl -s -D - -H "Accept: application/json"  \
  -H "Content-type: application/json"  \
  -X PUT \
  -d '{"nom":"Martin","prenom":"Jean","biographie":"ma vie"}' \
  "http://localhost:9998/myapp/biblio/auteurs/1"
```

If a resource doesn't exist an exception is raised, and the 404 http status code is returned
```shell
curl -s -D - -H "Accept: application/json"  \
  http://localhost:9998/myapp/biblio/auteurs/1000
```

Filter resources with query parameters :
```shell
curl -v -H "Accept: application/json"  \
 "http://127.0.0.1:9998/myapp/biblio/auteurs/filter?nom=Durand&prenom⁼Marie"
```

Control sort key with header param (default value "nom") :
```shell
curl -v -H "Accept: application/json"  -H "sortKey: prenom"\
"http://127.0.0.1:9998/myapp/biblio/auteurs/filter"
```
Login and get a Java Web Token
```shell
TOKEN=$(curl -v --user "john.doe@nowhere.com:admin" "http://localhost:9998/myapp/biblio/login")
curl -H "Authorization: Bearer $TOKEN" -v "http://localhost:9998/myapp/biblio/secured
```