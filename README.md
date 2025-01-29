# Digital-Twin_Set-up

## Seting up Digital Twin

1. Download this repository
2. Install java 17 with [homebrew](https://formulae.brew.sh/formula/openjdk@17) (Install homebrew first from [here](https://brew.sh))
3. Install [docker desktop](https://www.docker.com/products/docker-desktop/)
4. Open a comand prompt and type the folowing comands:
- `docker network create spark-network`
- `docker run -d --name spark-master --network spark-network --mount type=volume,source=spark-master_logs,target=/opt/bitnami/spark/logs -h spark-master -p 8080:8080 -p 7077:7077 bitnami/spark:latest /opt/bitnami/spark/bin/spark-class org.apache.spark.deploy.master.Master`
- `docker run -d --name spark-worker --network spark-network --mount type=volume,source=spark-worker_logs,target=/opt/bitnami/spark/logs -h spark-worker -p 8081:8081 bitnami/spark:latest /opt/bitnami/spark/bin/spark-class org.apache.spark.deploy.worker.Worker spark://spark-master:7077`
5. Check to se if they are running by going to the [http://localhost:8080](http://localhost:8080) and [http://localhost:8080](http://localhost:8080).
6. Open the terminal in the SIC-integration folder of this repository and type the folwing commands to upload the code to apaches spark:
- Create a folder for the test files with the folwing tree commands: `docker exec -it spark-master bash`, `mkdir inputTest` and `exit`
- Add the test files: `docker cp TestJson/test4.json spark-master:/opt/bitnami/spark/inputTest/`
- For development it is recomended to use an IDE with Maven like Intelij. In the IDE go to project settings and check that java17 is selected
Settings > Project structure > SDK – (java version 17), Language level – SDK default.
- To update the project code first pachage it. (You can do this in Intelij by going to Maven > Lifecycle > Package) and then running the command to submit the code to apache spark with: `docker cp target/SIC-Integration-1.0-SNAPSHOT.jar spark-master:/opt/bitnami/spark/`
7. How to run and update the aplication:
- Run the aplication: `docker exec -it spark-master /opt/bitnami/spark/bin/spark-submit --packages io.delta:delta-spark_2.12:3.2.0 --master spark://spark-master:7077 --class org.example.Main /opt/bitnami/spark/SIC-Integration-1.0-SNAPSHOT.jar` 
- Explore the apache spark directory with bash: `docker exec -it spark-master bash` 

## Seting up the Digital Twin Interface

1. Instal [apache superset](https://superset.apache.org/docs/installation/docker-compose) preferably using docker compose.
2. Since the default ports are used by spark u need to go to the docker-compose.yml file and change the ports from 8088:8088 to something like 8098:8088
3. Open apache supeset and hit the upload button.
4. if after entering login the website does not load and u are stuck restart superset and run it with `docker compose -f docker-compose-non-dev.yml up`, remeber that you need to change the port in the docker-compose-non-dev.yml from 8088:8088 to 8098:8088 (if it still doesn't load let it run for some time and try again)
5. Go to Visulaization dashboards/Apache_superset in this directory and submit the folder.

## Seting up the Digital Twin Monitoring System

1. Go to [https://github.com/prometheus/node_exporter/releases](https://github.com/prometheus/node_exporter/releases) and download the newest release of the darwin-amd64 version of the node exporter and then extract it.
2. Go into the newly downloaded folder and run the folowing command to move the node exporter to its right location: `sudo mv node_exporter /usr/local/bin/`
3. Open a new terminal type the following command twice: `cd..`
4. Run the folowing command: `sudo nano Library/LaunchDaemons/node_exporter.plist`
5. In the window that opens paste the folowing: `<?xml version="1.0" encoding="UTF-8"?> 
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd"> 
<plist version="1.0"> 
<dict> 
  <key>KeepAlive</key> 
  <true/> 
  <key>Label</key> 
  <string>node_exporter</string> 
  <key>ProgramArguments</key> 
  <array> 
    <string>/usr/local/bin/node_exporter</string> 
  </array> 
  <key>RunAtLoad</key> 
  <true/> 
</dict> 
</plist>`
6. Save the file with control O and then enter folowed by control X.
7. Run the folowing command to start node exporter: `sudo launchctl load /Library/LaunchDaemons/node_exporter.plist`
8. To check if the node exporter is running go to [http://localhost:9100/metrics](http://localhost:9100/metrics)
9. To configure apache spark for monitoring open a terminal Monitoring_set_up_&_configuration_files/Apache Spark form this directory and run the folwing commands to configure loging and metric colection: `docker cp log4j2.properties spark-master:/opt/bitnami/spark/conf/`, `docker cp log4j2.properties spark-worker:/opt/bitnami/spark/conf/`, `docker cp log4j2.properties spark-master:/opt/bitnami/spark/conf/` and `docker cp log4j2.properties spark-worker:/opt/bitnami/spark/conf/`
10. To start prometheus open a terminal and go to Monitoring_set_up_&_configuration_files/Prometheus from this directory and run the folwing comand: `docker compose up -d`.
11. You can check that prometheus is running by going to [http://localhost:9090/](to http://localhost:9090/).
12. To start loki and promtail open a terminal and go to Monitoring_set_up_&_configuration_files/Loki+Promtail from this directory and run the folowing comand: `docker compose up -d`.
13. To check that loki+prometail are working go to http://localhost:3100/ there should be a small mesage.
14. To run grafana open a terminal and go to Monitoring_set_up_&_configuration_files/Grafana in this directory and then run: `docker compose up -d`
15. To access grafana go to [http://localhost:3000/](http://localhost:3000/).
16. Log in with username: admin and pasword: admin.
17. Add in the prexisting dashboard to grafana by going to Dashboards>New Dashboard and submiting the json found in this directory at Vizualization dashboards/Grafana. You need to create two separate dashbaords.
