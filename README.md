#### project-simple-app

- This is a simple project with 
   - Frontend developed using Angular, running on nginx image. (which access the backend service)
   - backend developed using Spring-boot. 
 
#### Frontend project accessing the Backend serivice from Docker.
  - In docker, run both the containers
    - Before running the docker, update docker environment.prod.ts or environment.ts => `baseURL : http://localhost:8081/v1`
      - run frontend using `docker run --name frontend --port 8080:80 -v C:/thiru/nginx-101/:/var/work/ thirumurthi/frontend:v3`
    - run backend using `docker run --name backend --port 8081:80 thirumurthi/backend:v1`

#### with updated nginx configuration, we can access the backend service using the service name.
  - in this approach update the nginx configuration as below
  ```
  upstream Backend {
    # backend is the internal DNS name used by the backend Service inside Kubernetes
    server backend;
  }

server {
    listen 8080;
    location / {
        # The following statement will proxy traffic to the upstream named Backend
        proxy_pass http://Backend/;
        access_log /var/log/nginx/simple.access.log;
        error_log /var/log/nginx/simple.error.log;
     }
  }
  ```
  - This needs to be copied to the `/etc/nginx/conf.d/` location
```
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/backend.yaml
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/backend-svc.yaml

kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/frontend.yaml
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/frontend-svc.yaml
```
  - Now, `kubectl get svc` for the frotnend provides the external ip, which can be used to access the backend application


#### In docker application
  - backend service, can be accessed with API `http://localhost:8080/v1/message` if deployed in localhost
 frontend service, will access the backend service, and display the response.
 
 There is a simple json response served by the backend, random number gnerated with a date the response created.
 
Frontend :- has two docker file, multi-stage build. 
  - with `npm install` and `npm ci` option.

-----
To test the angular app locally in the nginx server, below is way to pass JSON from the nginx server config

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

