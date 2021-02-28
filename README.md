#### project-simple-app

- This is a simple project with 
   - Frontend project is developed usign Angular. 
     - 1. **Use Dockerfile_to_access_plain_backend => where the frontend project acts as proxy**
      - use `docker build -t frontend:v2 -f Dockerfile_to_access_plain_backend .` to build the image.

     - 2. **Use Dockerfile => to create a image which fetch the data from backend service and displays**
       - use `docker build -t frontend:v3 .`
       - the `environment.*ts` (prod), the `baseURL : 'http://localhost:8080/v1`
          - since the backend server REST API path is `/v1/message`.

   - backend developed using Spring-boot. 
     - **Use the Dockerfile, to build the image**
     - use `docker build -t backend:v1 .`
     -  the backend is a service provides json response, random number gnerated with a date.
 
#### Frontend project to access the backend service from the docker.
  - run the docker container of frontend
    ` docker run -d -p 8080:80 --name frontend thirumurthi/frontend:v3`
  - run the docker container of backend
    ` docker run -d -p 8081:8080 --name backend thirumurthi/backend:v1`

  - use `http://localhost:8080` from browser to access frontend, which will pull server from backend.

  - Response in the browser

![image](https://user-images.githubusercontent.com/6425536/109375449-a6341200-7871-11eb-9c19-4f46149ecee4.png)

#### To execute the image in kubernetes and use the frontend as simple proxy.
  - use the image `thirumurthi/frontend:v3`, which actuall has the below nginx.conf used
  ```
  upstream Backend {
    # backend is the internal DNS name used by the backend Service inside Kubernetes
    server backend;
  }

server {
    listen 80;
    location / {
        # The following statement will proxy traffic to the upstream named Backend
        proxy_pass http://Backend/;

        # error logs within nginx image conatiner
        access_log /var/log/nginx/simple.access.log;
        error_log /var/log/nginx/simple.error.log;
     }
  }
  ```
```
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/backend.yaml
## Below uses the service type clsuterIP
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/backend-svc.yaml

## this uses the frontend:v2 where the frontend acts as a proxy
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/frontend.yaml
## below uses service of type loadbalancer, (Note: using the katacoda kubernetes platform works)
kubectl apply -f https://github.com/thirumurthis/project-simple-app/raw/main/frontend-svc.yaml
```

  - Once deployed, use `kubectl get svc` to get the ExpternalIP for the frotnend.
  - Once IP is obtained, use the `curl http://<ExternalIp>:80/v1/message` to fetch the backend service message. 


#### In case if we need to use the frontend service needs to access the backend service and display the recived message.
  - the backend project deployed with the backend service needs to be of type LoadBalancer type, this needs to be exposed so can be accessed outside the cluster.
  - In case of Azure, we can deploy backend service of type, loadbalancer, and use the External ip to create a Application gateway Rule.

#### Few notes, 
- Frontend project contains couple of docker files, which contains multi-stage build.
  - One of the docker file, use  
  - `npm install`
  - `npm ci` - this has better performance.

-----
##### To build angular app to run production optimized scripts use.
```
$ npm run build -- --prod
```
#### actual output of the command
```
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
- Development Tips:
- Run `unbuntu/nginx` docker image, map the local folder to volume mapped to container directory.
 - Copy the `dist/simple-app/*.* `to `/var/www/simple-app` directory within the docker container.
- since the nginx set with the default configuration, remove the default config from the `/etc/nginx/sitest-availabe/default` by using `unlink` Linux command.
- create config under `/etc/nginx/conf.d/simple-app.local.conf` and add the below content.

#### Below is few more tips where nginx can be used to send json response.

```
## proxy 
upstream Backend_8080 {
  ### 127.0.0.1 => is localhost
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
- Whenever the nginx config is updated, reload the nginx config using `$ nginx -s reload` or `systemctl reload nginx.service` or `service reload nginx` command.
- if the docker container ran with port forwarding 8080:80, use `http://localhost:8080` from browser.
- within docker container use `curl http://127.0.0.1:80` to view the backend message.


