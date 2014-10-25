package com.lifeng.storm.spout;

import backtype.storm.spout.MultiScheme;
import backtype.storm.spout.Scheme;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.kafka.*;
import storm.kafka.trident.GlobalPartitionInformation;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class KafkaSpoutFactory {
    private static String zkHosts = "192.168.56.4:2181";
    private static String topic = "stormTopic";
    private static String spoutId = "kafkaSpout";
    private static String zkRoot = "/kafkaStorm";
    private SpoutConfig spoutConfig;
    private KafkaSpout kafkaSpout;
    private int numPartitions = 2;

    private KafkaSpoutFactory() {
        BrokerHosts brokerHosts = new ZkHosts(zkHosts);
//        GlobalPartitionInformation info = new GlobalPartitionInformation();
//        info.addPartition(0, new Broker("192.168.56.6",9092));
//        BrokerHosts brokerHosts = new StaticHosts(info);
        spoutConfig = new SpoutConfig(brokerHosts, topic, zkRoot, spoutId);
        spoutConfig.forceFromStart = true;
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        kafkaSpout = new KafkaSpout(spoutConfig);
    }

    public static KafkaSpout getInstance() {
        return new KafkaSpoutFactory().kafkaSpout;
    }
}
