# pushStats
A maven project which pushes data out by implementing customized producer based on Maxwell.

<b>[HOW IT WORKS]</b><br/>
1.Export the project into a jar file with "eclipse", for example "postpush.jar", and move/copy it into "maxwell/lib".<br/><br/>
2.Add following lines into your "maxwell/config.properties":(thanks for the article:https://www.codetd.com/article/10644891)<br/>
#[producer]<br/>
output_server_id=true<br/>
custom_producer.factory=com.pushstats.producerfactory.CustomProducerFactory<br/>
custom_producer.server_id=23<br/>
custom_producer.destination_URL=http://localhost/destination/api<br/><br/>
3.Get into your maxwell directory, and restart/start your maxwell like "bin/maxwell" (the same effect with "bin/maxwell --config ./config.properties")<br/><br/>
4.Try to fire a test within mysql, such as 'insert sometable (name, time, stuff) values ("xxx", now(), "stuff like that");', the "maxwell" will show the "changed" data on the screen and push the "data" part out by being params of the "destination_URL".<br/><br/>

<b>[MAXWELL & SETTINGS]</b>(http://maxwells-daemon.io/quickstart) <br/>
1.download maxwell:<br/>
curl -sLo - https://github.com/zendesk/maxwell/releases/download/v1.27.0/maxwell-1.27.0.tar.gz \
       | tar zxvf -;
cd maxwell-1.27.0;<br/><br/>
2.config mysql, insert into the following lines (such as "vi my.cnf" or "vi /etc/mysql/mysql.conf.d/mysql.cnf"), and don't forget to restart mysql-server after: <br/>
#[mysqld]<br/>
server_id=1<br/>
log-bin=master<br/>
binlog_format=row<br/><br/>
3.create user "maxwell" in mysql and grant some privileges for it: <br/>
mysql> CREATE USER 'maxwell'@'%' IDENTIFIED BY 'XXXXXX';<br/>
mysql> GRANT ALL ON maxwell.* TO 'maxwell'@'%';<br/>
mysql> GRANT SELECT, REPLICATION CLIENT, REPLICATION SLAVE ON *.* TO 'maxwell'@'%';<br/><br/>
4.run maxwell after get into the directory (so many ways there, but here only show the "command line" way): <br/>
bin/maxwell --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --config config.properties<br/>
--config.properties will be like in [HOW IT WORKS].2, but could add some following lines:<br/>
#[misc]<br/>
filter=exclude:*.*,include:db1.table1,include:db2.table2<br/>
#[producer]<br/>
custom_producer.DB_URL_pairs=dbname1|http://some.where.com/some/api,dbname2|http://some.where.else.com/some/other/api # pairs of "DB|URL", separated by ",", means only the changes in DB will be posted to the URL<br/>
custom_producer.fieldsExcluded=db1.table1.field1,db2.table2.field2 # list of the fields which won't be posted, separated by ","
