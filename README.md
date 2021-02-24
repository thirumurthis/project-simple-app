#### project-simple-app

- This is a simple project with 
   - Frontend developed using Angular
   - backend developed using Spring-boot.
 
 both the project contains Dockerfile, to deploy in the local kubernetes cluster.
 
 backend service, can be accessed with API `http://localhost:8080/v1/message` if deployed in localhost
 frontend service, will access the backend service, and display the response.
 
 There is a simple json response served by the backend, random number gnerated with a date the response created.
 
Frontend :- has two docker file, multi-stage build. 
  - with `npm install` and `npm ci` option.

Deploy the manifest to kubernetes using below commands
```
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/backend.yaml

kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/frontend.yaml
```
