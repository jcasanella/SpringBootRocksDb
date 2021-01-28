# SpringBoot using RocksDB

This small example uses SpringBoot (APIs) to work with RocksDb

## Usage

Start the MainApp and you can test using these calls:

* Ingest: ```curl -iv -X POST -H "Content-Type: application/json" -d '{"bar":"baz"}' http://localhost:8080/api/foo```

* Find: ```curl -iv -X GET -H "Content-Type: application/json" http://localhost:8080/api/foo```

* Delete: ```curl -iv -X DELETE -H "Content-Type: application/json" http://localhost:8080/api/foo```