cd frontend
sudo docker build -t frontend .
sudo docker run -it -d -p 80:80 frontend