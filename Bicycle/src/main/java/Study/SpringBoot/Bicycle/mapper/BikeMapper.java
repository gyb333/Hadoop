package Study.SpringBoot.Bicycle.mapper;


import Study.SpringBoot.Bicycle.pojo.Bike;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface BikeMapper {

	public Bike getById(Long id);
	
	public List<Bike> findAll();

	public void save(Bike Bike);

	public void deleteByIds(Long[] ids);

	public void update(Bike Bike);

	public Bike login(Bike Bike);
}
