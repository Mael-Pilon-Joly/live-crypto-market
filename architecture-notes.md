## front end: 

- API Gateway, unpon receiving request, calls an AWS lambda, that validates a cognito token auth from the cognito API.
- API Gateway is not in the VPC
- Lambda will be in a private gateway, to communicate with backend service.
- There will be a lambda authorizer to allow/deny access to backend services


## backend:

- backend is building an image with docker
- a bastion host is hosted on public subnet in vpc
- bastion used to pull image, then copy to private backend instance
- from backend instance, load and run docker image

command:

sudo docker pull image maelpj/stock-trading-backend:latest
sudo docker save -o stock-trading-backend.tar maelpj/stock-trading-backend:latest
sudo scp -i realtime-trading-backend-key.pem stock-trading-backend.tar ec2-user@10.0.140.140:/home/ec2-user/
ssh -i realtime-trading-backend-key.pem ec2-user@10.0.140.140

then in the backend instance

sudo docker stop stock-trading-backend
sudo docker rm stock-trading-backend
sudo docker load -i stock-trading-backend.tar
sudo docker run -d -p 8080:8080 --name stock-trading-backend maelpj/stock-trading-backend:latest
