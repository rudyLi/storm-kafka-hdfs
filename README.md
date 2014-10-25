##storm-kafka-hdfs
===
message worker flow, just disctribe related tools simple use

###zookeeper
* Download file from [apache zookeeper](http://zookeeper.apache.org/doc/trunk/zookeeperStarted.html#sc_Download)
* Simply,just unzip and excute `bin/zkServer.sh start`,config value is default,port is __2181__

> server address is 192.168.56.4:2181

###kafka
#####Enviroment Deploy
1. Download tool from [apache kafka website](http://kafka.apache.org/downloads.html),my version is `kafka_2.9.2-0.8.1.1`
> vierion introduction,for example: kafka_2.8.0-0.8.1.1,
> this means kafaka version is 0.8.1.1 and scala version is 2.8.0. It's is usefull in jar version choosing;
2. copy to dest server, in my example is `192.168.56.6`
3. unzip file `tar zxvf kafka_2.9.2-0.8.1.1.tgz`
4. modify __config/server.server.properties__ under dirrctory of kafka

        zookeeper.connect=192.168.56.4:2181;
        log.dirs=/tmp/kafka-logs (multi directory comma split)
        host.name=192.168.56.6  __If you have not set this value, this brings exception when use storm-kafka to get message ( next to  find why)__
        port=9092
   
5. `bin/kafka-server-start.sh config/server.properties `
6. test,if flow operation is success,kafka install is success
    *  `bin/kafka-topics.sh --create --zookeeper 192.168.56.4:2181 --replication-factor 1 --partitions 1 --topic test`
    *  `bin/kafka-console-producer.sh --broker-list 192.168.56.6:9092 --topic test`, just input some words 
    * `bin/kafka-console-consumer.sh --zookeeper 192.168.56.4:2181 --topic test --from-beginning` , then the console will display the word you imput
    
###storm
####Enviroment deploy(192.168.56.5 and 192.168.56.6)
1. Download file from [storm](https://storm.incubator.apache.org/downloads.html),the version which I choose is 0.9.2
2. cp file to the two server and unzip
3. On nimbus server, you may edit config/storm.yaml as below. if the format is not correct, the program will throw error,excute `nohup bin/storm nimbus &`, if you want to monitor the storm in web ui, excute `bin/storm ui`, the url is `192.168.56.6:8080`

        storm.zookeeper.servers:
           - "192.168.56.4"
           
4. On the supervisor server, edit config/storm.yaml as flow,excute `bin/storm supervisor`

        storm.zookeeper.servers:
           - "192.168.56.4
        nimbus.host: "192.168.56.6"
        supervisor.slots.ports:(default value has 4 slots)
           - 6700
           - 6701
           
5. Now, you can submit toplog to storm cluster, storm has supplied some examples, like the wordcount is the simplets, find the jar in examples directory underthe storm path, in my computer, excute commend bellow, then open the ui you may find this topology named __wordCount__

        `storm jar $Path/examples/storm-starter/storm-starter-topologies-0.9.2-incubating.jar storm.starter.WordCountTopology wordCount`

> important, you must add arg at last, otherwise the topolog is excuted in local mode;

###storm kafka
#### storm 0.9.2 introduction
This version include the storm_kafka jar, you may find under the directory of `external`, but it doesn't include in the classpath, you may cp this jar to the directory of `lib`
#### How to use the storm_kafka to build topolgy
1. You must include jars , see the `pom.xml`;
2. Don't include this dependcy jar in your topolgy jar, otherwise the jar is much bigger, just copy these jars under the directory of `lib`;
> Tips: Use `java tf *.jar` to see what's included
3. In my example , I just `spout` receive message from kafka and `bolt` print message, just see the code;

### Problem I meet
#### situation
Now I just use kafka-console-producer producing message

####Exceptions
1. `Exception in thread "main" java.lang.NoClassDefFoundError`, solution :

        1. copy dependency jars to every serve's directory of storm PATH `lib` (recomended)
        2. Include the dependency jar in your own jar( your own jar become larger and larger)
    
2. `ERROR backtype.storm.util - Async loop died!
java.lang.RuntimeException: java.nio.channels.UnresolvedAddressException`, solution: 

        Just mesioned before, can't find kafka broker host name, you must edit the server.propertis where `host.name` is your server ip

3. `java.lang.RuntimeException: java.lang.ClassCastException: [B cannot be cast to java.lang.String`, when I display every message received, message received is byte array , the message have to decode:

        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

#### Different from previous version
* the method `forceStartOffsetTime` is delated, instead you can change the field `forceFromStart` of spoutConfig, default value is false(It means get message from latest offset)

###TODO
1. Write producer using java or scala
2. write storm-hdfs, include(install hdfs)
3. Find more problem and solve it
4. Find more differt
