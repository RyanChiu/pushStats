# pushStats
A maven project which pushes data out by implementing customized producer based Maxwell.

<b>[HOW IT WORKS]</b><br/>
1.Export the project into a jar file with "eclipse", for example "postpush.jar", and move/copy it into "maxwell/lib".<br/><br/>
2.Add following lines into your "maxwell/config.properties":(tahnks for the article:https://www.codetd.com/article/10644891)<br/>
#[producer]<br/>
output_server_id=true<br/>
custom_producer.factory=com.pushstats.producerfactory.CustomProducerFactory<br/>
custom_producer.server_id=23<br/>
custom_producer.destination_URL=http://localhost/destination/api<br/><br/>
3.Get into your maxwell directory, and restart/start your maxwell like "bin/maxwell" (the same effect with "bin/maxwell --config ./config.properties")<br/><br/>
4.Try to fire a test within mysql, such as "insert sometable (name, time, stuff) values ("xxx", now(), "stuff like that");", the "maxwell" will show the "changed" data on the screen and push the "data" part out by being params of the "destination_URL".
