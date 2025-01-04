helm install users-pg-data ./mount-volume -f ./users.pg-data-volume.values.yaml

helm install users-db ./postgres-db -f ./users.pg.values.yaml

kubectl port-forward svc/users-db 15432:5432

helm install ./users-app -f ./users.app.values.yaml

kubectl port-forward svc/users-app 8000:8080

helm install ./mail-sender -f ./mail-sender.values.yaml
