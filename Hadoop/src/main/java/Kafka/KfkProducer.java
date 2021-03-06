package Kafka;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.producer.*;

public class KfkProducer {
	private static final String path = "KafkaProducer.Properties";
	private static final String topic = "my-topic";

	public static void main(String[] args) {

		try {
			Properties props = CommonUtils.getProperties(path);
			//设置自定义分区
			props.put("partitioner.class", "Study.Kafka.CustomPartitioner");
			//为生产者注册拦截器
			props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
					Arrays.asList("Study.Kafka.TimeInterceptor","Study.Kafka.CounterInterceptor"));
			Producer<String, String> producer = new KafkaProducer<String, String>(props);


			for (int i = 1001; i <= 1100; i++) {
				//null-key:轮询所有分区，负载均衡
				//key:hash(key)%分区数
				//自定义分区：取决于实现方式
//				producer.send(new ProducerRecord<String, String>(topic, "test-msg" + i, Integer.toString(i)));				
				//消息的成功发送：-1 不等待集群反馈、0 等待leader的反馈、all 等待所有副本的反馈、1
				producer.send(new ProducerRecord<String, String>(topic,1,"cb-key"+i, "cb-value" + i), new Callback() {

					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (metadata != null) {
							System.out.println(metadata.partition() + "---" + metadata.offset());
						}
					}
				});
			}

			producer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
