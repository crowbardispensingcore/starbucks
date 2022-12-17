clean:
	./mvnw clean

compile: clean
	./mvnw compile

build: compile
	./mvnw package -DskipTests

run: build
	#./mvnw spring-boot:run -Dspring-boot.run.arguments="--CASHIER_CLIENT_MYSQL_USERNAME=root --CASHIER_CLIENT_MYSQL_PASSWORD=cmpe172"
	java -jar target/starbucks-cashier-client-1.0.jar \
		--spring.datasource.username=root \
		--spring.datasource.password=cmpe172 \
		--api.host.endpoint=34.171.154.111/api \
		--api.key=Zkfokey2311 \
		--spring.rabbitmq.host=localhost \
		--spring.rabbitmq.username=guest \
		--spring.rabbitmq.password=guest

# Docker

account = dockersjsupaulnguyen

docker-build: build
	docker build -t starbucks-cashier-client .
	docker images

docker-push:
	docker login
	docker build -t $(account)/starbucks-cashier-client:latest .
	docker push $(account)/starbucks-cashier-client:latest
