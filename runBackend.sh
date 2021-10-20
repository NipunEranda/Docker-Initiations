cd web-api
sudo docker build -f src/main/docker/Dockerfile.jvm -t backend .
sudo docker run -i --rm -p 8080:8080 backend