helm install users-pg-data ./mount-volume -f ./users.pg-data-volume.values.yaml

helm install users-db ./postgres-db -f ./users.pg.values.yaml
# OR
helm install users-db oci://registry-1.docker.io/bitnamicharts/postgresql -f ./users.pg-bitnami.values.yaml

kubectl port-forward svc/users-db 15432:5432

helm install kafka oci://registry-1.docker.io/bitnamicharts/kafka -f kafka-bitnami.values.yaml

kubectl port-forward svc/kafka 9092:9092

helm install users-app ./users-app -f ./users.app.values.yaml

kubectl port-forward svc/users-app 8000:8080

helm install mail-sender ./mail-sender -f ./mail-sender.values.yaml
