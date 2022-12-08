# Week 1

- Deploy Starbucks REST API to GCP

## Starbucks API

GCP MySQL instance private IP address: `172.20.80.3`

MySQL username: `root`

MySQL password: `cmpe172`

kong-proxy endpoint: `34.171.154.111`

starbucks-api endpoint: `34.171.154.111/api `

apiKey: `Zkfokey2311`

### Deployment process

```
// Create jumpbox
kubectl create -f jumpbox.yaml

// Connect to jumpbox shell
kubectl exec -it jumpbox -- /bin/bash
apt update
apt upgrade -y
apt install curl -y
apt install iputils-ping -y
apt install telnet -y
apt install httpie -y
apt install mysql-client -y

// Test MySQL instance connection
mysql -u root -p -h 172.20.80.3

// Create api
kubectl create -f api-deployment.yaml --save-config

// Check api logs
kubectl logs -f spring-starbucks-api-deployment-746588dbbf-v4pck

// Create api service
kubectl create -f api-service.yaml

// Create Kong Ingress
kubectl apply -f https://bit.ly/k4k8s
kubectl apply -f kong-ingress-rule.yaml
kubectl apply -f kong-strip-path.yaml
kubectl patch ingress starbucks-api -p '{"metadata":{"annotations":{"konghq.com/override":"kong-strip-path"}}}'

// Add Kong Key-Auth plugin
kubectl apply -f kong-key-auth.yaml
kubectl patch service spring-starbucks-api-service -p '{"metadata":{"annotations":{"konghq.com/plugins":"kong-key-auth"}}}'

// Configure api key
kubectl apply -f kong-consumer.yaml

// Create Kubernetes secret
kubectl create secret generic apikey --from-literal=kongCredType=key-auth --from-literal=key=Zkfokey2311

// Apply api key
kubectl apply -f kong-credentials.yaml

// Test api without key
curl $KONG/api/ping
// Test with key
curl $KONG/api/ping --header 'apikey: Zkfokey2311'
```

# Week 2

- Fix a bug where the spring-starbucks-api in GCP uses H2 database instead of MySQL
  - Add `SPRING_PROFILES_ACTIVE` environment variable to `api-deployment.yaml`
  - Redeploy the pods by applying the new deployment config, deleting the old pods and letting kubernetes re-pull and re-deploy
- Working on starbucks-cashier-client

# Week 3

- Add Starbucks cashier register client
  - Test cashier client using the deployed API

# Week 4

- Add login for cashier client

## Spring Security

Ref: https://www.javadevjournal.com/spring-security/spring-security-login/

### How to Generate and Use A BCrypt Password

1. Go to https://bcrypt-generator.com/
2. Store the generated value to the database

# Week 5

- Update API credentials to environment variables

  - How to re-apply manifest

    `kubectl apply -f my-deployment.yaml`

- Deploy Cashier Client
  - Use a LoadBalancer service

## Starbucks Cashier Client

External IP: `34.173.192.103:80`

Cashier credential:

- Username: `student`
- Password: `cmpe172`
  - Password is encrypted with `bcrypt` and manually added to `cashier_user` table in `starbucks` cloud SQL
    - `INSERT INTO cashier_user (id, username, password) VALUES (1, "student", "$2a$10$5WDiSSMvuPkW9Pn6rqIDQuohEIZEN.Bt6wmVhNGh8tVynLahCJag2");`

- Deploy a single RabbitMQ instance to GKE

## RabbitMQ

Management Console IP: `34.170.74.86:80`

Internal IP: `10.104.1.244`

Default username: `guest`

Default password: `guest`