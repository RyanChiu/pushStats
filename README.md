# pushStats
A maven project which pushes data out by implementing customized producer based Maxwell.

[HOW IT WORKS]
1.Export the project into a jar file with "eclipse", for example "postpush.jar", and move/copy it into "maxwell/lib".
2.Add following lines into your "maxwell/config.properties":(tahnks for the article:https://www.codetd.com/article/10644891)
#[producer]
output_server_id=true
custom_producer.factory=com.pushstats.producerfactory.CustomProducerFactory
custom_producer.server_id=23
custom_producer.destination_URL=http://localhost/destination/api
3.Get into your maxwell directory, and restart/start your maxwell like "bin/maxwell" (the same effect with "bin/maxwell --config ./config.properties")
4.Try to fire a test within mysql, such as "insert sometable (name, time, stuff) values ("xxx", now(), "stuff like that");", the "maxwell" will show the "changed" data on the screen and push the "data" part out by being params of the "destination_URL".
