import java.util.ArrayList;

public class Property {
	
	public String name;
	public int inputEnd;
	public ArrayList<Entity> entityList;
	
	public Property(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.inputEnd = 0;
		this.entityList = new ArrayList<Entity>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInputEnd() {
		return inputEnd;
	}

	public void setInputEnd(int inputEnd) {
		this.inputEnd = inputEnd;
	}

	public ArrayList<Entity> getEntityList() {
		return entityList;
	}
	
	public void addEntity(Entity newEntity) {
		entityList.add(newEntity);
	}

	public void setEntityList(ArrayList<Entity> entityList) {
		this.entityList = entityList;
	}

}
