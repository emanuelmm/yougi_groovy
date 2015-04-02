# yougi_groovy

download wildfly 8.2.0.Final

http://download.jboss.org/wildfly/8.2.0.Final/wildfly-8.2.0.Final.zip

install mariadb

download this

http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.34/mysql-connector-java-5.1.34.jar

```shell
create database ug;
grant all privileges on ug.* to ug@'localhost' identified by 'ug';
flush privileges;
```

open a terminal...

```shell
cd wildfly/bin
./standalone.sh
```

open another terminal...

```shell
cd wildfly/bin

./jboss-cli.sh --connect --command='deploy ~/Downloads/mysql-connector-java-5.1.34.jar'

./jboss-cli.sh --connect --command='data-source add --name=UgDS --jndi-name=java:/jdbc/UgDS \
--driver-name=mysql-connector-java-5.1.34.jar_com.mysql.jdbc.Driver_5_1 \
--connection-url=jdbc:mysql://127.0.0.1:3306/ug --user-name=ug --password=ug'

./jboss-cli.sh --connect --command='/subsystem=datasources/data-source=UgDS:test-connection-in-pool'

./jboss-cli.sh --connect --command='/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=smtp-gmail:add(host=smtp.gmail.com,port=465)'

./jboss-cli.sh --connect --command='/subsystem=mail/mail-session=Ug:add(jndi-name=java:/mail/ug)'

./jboss-cli.sh --connect --command='/subsystem=mail/mail-session=Ug/server=smtp:add(outbound-socket-binding-ref=smtp-gmail,username=your_email@gmail.com,password=secret, ssl=true)'

touch ../standalone/configuration/app.properties

echo 'admins=admin,leader,helper,member,partner
     leaders=leader,helper,member,partner
     members=member
     helpers=helper,member
     partners=partner' > ../standalone/configuration/app.properties

```

download this

https://raw.githubusercontent.com/yougi/yougi_groovy/master/sec.cli

put inside widfly/bin directory and run


```shell

./jboss-cli.sh -c --file=sec.cli

```

