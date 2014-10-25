package com.lifeng.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import com.lifeng.storm.bolt.PrinterBolt;
import com.lifeng.storm.spout.KafkaSpoutFactory;
import storm.kafka.KafkaSpout;

public class kafkaDemo {
    public static void main(String[] args) throws Exception {
        KafkaSpout kafkaSpout = KafkaSpoutFactory.getInstance();
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("kafkaSpout",kafkaSpout,2);
        builder.setBolt("print",new PrinterBolt(),8).shuffleGrouping("kafkaSpout");
        Config conf = new Config();
        conf.setDebug(true);
        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());
            //Thread.sleep(10000);
            //cluster.shutdown();
        }
    }
}
