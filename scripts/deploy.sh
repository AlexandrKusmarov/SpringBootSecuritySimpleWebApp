#!/usr/bin/env bash

mvn clean package

echo 'Copy fiels...'

scp -i ~/.ssh/id_rsa \SpringBoot/target/SpringBoot-1.0-SNAPSHOT.jar \alexubuntu@192.168.56.101:/home/alexubuntu
#scp -i ~/.ssh/id_rsa \SpringBoot/src/main/resources/db/migration/V1__InitDB.sql \alexubuntu@192.168.56.101:/home/alexubuntu
#scp -i ~/.ssh/id_rsa \SpringBoot/src/main/resources/db/migration/V2__Add_admin.sql \alexubuntu@192.168.56.101:/home/alexubuntu
#scp -i ~/.ssh/id_rsa \SpringBoot/src/main/resources/db/migration/V3__Encode_Passwords.sql \alexubuntu@192.168.56.101:/home/alexubuntu

echo 'Restarting server...'

ssh -i ~/.ssh/id_rsa alexubuntu@192.168.56.101 << EoF

pgrep java | xargs kill -9
nohup java -jar SpringBoot-1.0-SNAPSHOT.jar > log.txt &

EoF

echo 'Bye...'
