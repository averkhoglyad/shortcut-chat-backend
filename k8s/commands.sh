kubectl apply -f .\users\config\configmap.yaml \
              -f .\users\config\secret.yaml \
              -f .\users\postgres\volume.yaml \
              -f .\users\postgres\statefulset.yaml \
              -f .\users\postgres\service.yaml \
              -f .\users\deployment.yaml \
              -f .\users\service.yaml

kubectl port-forward pods/users-db-statefulset-0 15432:5432

kubectl port-forward svc/users-app-service 8000:8080

