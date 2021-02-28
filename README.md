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
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/backend-svc.yaml

kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/frontend.yaml
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/frontend-svc.yaml

```
-----
#### With docker running
  - if the backend project is started with the port forward of 8081.
  - in the environment.prod.ts, adding baseURL as http://localhost:8081/v1 will access the exposed docker backend service.

-----
To test the angular app locally, below is how to pass JSON from the nginx server

- Update the environment.prod.ts, to ` baseURL : "./v1" `(if not updated)
- build the angluar app using 
```
$ npm run build -- --prod

## The artifacts gets build at "dist/simple-app" in this case

> simple-app@0.0.0 build
> ng build

√ Browser application bundle generation complete.
√ Copying assets complete.
√ Index html generation complete.

Initial Chunk Files | Names         |      Size
vendor.js           | vendor        |   2.42 MB
polyfills.js        | polyfills     | 141.29 kB
main.js             | main          |  16.18 kB
runtime.js          | runtime       |   6.15 kB
styles.css          | styles        | 119 bytes

                    | Initial Total |   2.58 MB
```

- Run `unbuntu/nginx` docker image, with the volume mapped to local directory. Copy the dist/simple-app/*.* to /var/www/simple-app directory within the docker container.
- since the nginx is already running, we need to remove the default config from the /etc/nginx/sitest-availabe/default (unlink the config)
- create config under `/etc/nginx/conf.d/simple-app.local.conf` and add the below content.
```
## proxy 
upstream Backend_8080 {
  server 127.0.0.1:8080;
}

## any request form :80 will be proxied to 8080 
server {
   listen 80;
   location / {
    proxy_pass http://Backend_8080/;
   }
}

## mocked the json data to flow from the /v1/message url
server {
   listen 8080;
   root /var/www/simple-app;

   location /v1/message {
      default_type application/json;
      return 200 '{"data" : [ 10, 12], "transactionId": "00000100001", "date" : "2021-02-27" }';
   }
}
```
- now reload the nginx config using `$ nginx -s reload` or `systemctl reload nginx.service` command.
- if the docker port forwarding is used with 8080:80, from browser use `http://localhost:8080`.
- within docker container use `curl http://127.0.0.1:80` to view the content.

Response in the browser
![image](https://user-images.githubusercontent.com/6425536/109375449-a6341200-7871-11eb-9c19-4f46149ecee4.png)

