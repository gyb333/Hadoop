package Study.MapReduce.Example;

 
import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import Study.MapReduce.DistributedJob;
import Study.MapReduce.Beans.OrderBean;
import Study.MapReduce.DataType.FlowBean;
import Study.MapReduce.DataType.FlowCount;
import Study.MapReduce.DataType.FlowCountMapper;
import Study.MapReduce.DataType.FlowCountReducer;

public class OrderTopN  extends DistributedJob{


	/**
	 * 
	 * ����OrderId ����
	 *
	 */
	public static class OrderIdPartitioner extends Partitioner<OrderBean, NullWritable>{

		@Override
		public int getPartition(OrderBean key, NullWritable value, int numPartitions) {
			// ���ն����е�OrderID���ַ�����
			return (key.getOrderId().hashCode() & Integer.MAX_VALUE) % numPartitions;
		}
	 	
	}
	
	/**
	 * 
	 * ����OrderId ����
	 *
	 */
	public static class OrderIdGroupingComparator extends WritableComparator{

		public OrderIdGroupingComparator() {
			super(OrderBean.class,true);
		}
		
		@Override
		public int compare(WritableComparable a, WritableComparable b) {
			OrderBean o1 = (OrderBean) a;
			OrderBean o2 = (OrderBean) b;
			
			return o1.getOrderId().compareTo(o2.getOrderId());
		}
		   
		
	}
	
	@Override
	public void setJobConfig(Job job) {
		
		job.setPartitionerClass(OrderIdPartitioner.class);
		job.setGroupingComparatorClass(OrderIdGroupingComparator.class);
		
	}
	
	public static class OrderTopNMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable>{
		OrderBean orderBean = new OrderBean();
		NullWritable v = NullWritable.get();
		
		@Override
		protected void map(LongWritable key, Text value,Context context)
				throws IOException, InterruptedException {
			
			String[] fields = value.toString().split(",");
			
			orderBean.set(fields[0], fields[1], fields[2], Float.parseFloat(fields[3]), Integer.parseInt(fields[4]));
			
			context.write(orderBean,v);
		}
		
	}
	
	
	
public static class OrderTopnReducer extends Reducer< OrderBean, NullWritable,  OrderBean, NullWritable>{
		
		/**
		 * ��Ȼreduce�����еĲ���keyֻ��һ��������ֻҪ����������һ�Σ�key�е�ֵ�ͻ��
		 */
		@Override
		protected void reduce(OrderBean key, Iterable<NullWritable> values,Context context)
				throws IOException, InterruptedException {
			int i=0;
			for (NullWritable v : values) {
				context.write(key, v);
				if(++i==3) return;
			}
			
		}
		
		
	}
	
	
	
	public static void main(String[] args) throws Exception {
		 
		String clsName = OrderTopN.class.getSimpleName();
		boolean isLocaltion = false;
		Class<? extends Mapper> clsMapper = OrderTopNMapper.class;
		Class<? extends Reducer> clsReducer = OrderTopnReducer.class;
		Class<?> clsMapOutputKey = OrderBean.class;
		Class<?> clsMapOutputValue = NullWritable.class;
		Class<?> clsOutputKey = OrderBean.class;
		Class<?> clsOutputValue = NullWritable.class;
		new OrderTopN().execJob(clsName, isLocaltion, clsMapper, clsReducer, 
				clsMapOutputKey, clsMapOutputValue, clsOutputKey, clsOutputValue);

	}

}
