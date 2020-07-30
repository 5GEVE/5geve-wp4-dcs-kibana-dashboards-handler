# DCS
To include kibana-dashboard module use `--recursive` flag (do it only the first time, if the repository does not contain the kibana-dashboard repository)

```git clone --recursive git@github.com:5GEVE/5geve-wp4-dcs-kibana-dashboards-generator.git```

## Build

Before building the application, remember that, depending on the scenario where this component has to be deployed, the application.properties variables must change consequently


```shell script
# within dcs project
cd 5geve-wp4-dcs-kibana-dashboards-generator
mvn clean install # install the module in local .m2 maven repository
cd ..
mv 5geve-wp4-dcs-kibana-dashboards-generator kibana-dashboards
mvn clean install
```

## DCS REST endpoint docs

Swagger UI
- http://localhost:8080/swagger-ui/index.html

OpenAPI descriptor
- http://localhost:8080/v3/api-docs.yaml

## Copyright

This work has been done by Telcaria Ideas S.L. for the 5G EVE European project under the [Apache 2.0 License](LICENSE).
