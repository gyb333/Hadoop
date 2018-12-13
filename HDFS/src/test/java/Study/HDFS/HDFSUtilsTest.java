package Study.HDFS;

 

import java.io.*;

import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Test;

public class HDFSUtilsTest {

	private static final String BasePath="/bigdata/hdfs";

	@Test
	public void CreateFolder() {
		try {
			
			 
			HDFSUtils.CreateFolder(BasePath);
			Assert.assertTrue(true);
		} catch (IOException e) {
			Assert.assertTrue(false);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test
	public void UplodeFile() throws IOException {
		String strPath="D:\\work\\Study\\BigData\\test.dat";

		HDFSUtils.UplodeFile(BasePath+"/test",strPath);
		Assert.assertTrue(true);
	}

	@Test
	public void  DownloadFile() throws IOException {

		HDFSUtils.DownloadFile(BasePath+"/test",System.out);
		Assert.assertTrue(true);
	}



	@Test
	public void GetLocation() throws IOException {
		HDFSUtils.GetLocation(BasePath+"/test");
		Assert.assertTrue(true);
	}

	@Test
	public  void DeleteFile() throws IOException {
		HDFSUtils.DeleteFile(BasePath+"/test");
		Assert.assertTrue(true);
	}

	@Test
	public void GetCluster() throws IOException {
		DatanodeInfo[] nodes=HDFSUtils.GetCluster();
		for(DatanodeInfo node:nodes){
			System.out.println(node.getName());
		}
		Assert.assertTrue(true);
	}

}
