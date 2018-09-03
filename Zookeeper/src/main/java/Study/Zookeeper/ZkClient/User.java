package Study.Zookeeper.ZkClient;

import java.io.Serializable;

public class User implements Serializable{

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + "]";
	}

	private Integer id;
	private String name;

	 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}