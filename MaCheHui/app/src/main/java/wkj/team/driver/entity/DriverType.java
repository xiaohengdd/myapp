package wkj.team.driver.entity;

import java.util.List;

public class DriverType {
	//第二层 汽车牌子

	private String I;
	private String N;
	private String L;

	private String sortLetter;
	
	List<DriverTypes> List;

	public String getI() {
		return I;
	}

	public void setI(String i) {
		I = i;
	}

	public String getN() {
		return N;
	}

	public void setN(String n) {
		N = n;
	}

	public String getL() {
		return L;
	}

	public void setL(String l) {
		L = l;
	}

	public List<DriverTypes> getList() {
		return List;
	}

	public void setList(List<DriverTypes> list) {
		List = list;
	}

	public String getSortLetter() {
		return sortLetter;
	}

	public void setSortLetter(String sortLetter) {
		this.sortLetter = sortLetter;
	}

}
