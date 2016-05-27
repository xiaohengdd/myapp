package wkj.team.driver.entity;

import java.util.List;

public class DriverTypes {
	//第三层  汽车型号
	private String I;
	private String N;
	
	private List<DriverItem> List;
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
	
	public List<DriverItem> getList() {
		return List;
	}
	public void setList(List<DriverItem> list) {
		List = list;
	}
	@Override
	public String toString() {
		return "DriverTypes [I=" + I + ", N=" + N + ", List=" + List
				+ ", getI()=" + getI() + ", getN()=" + getN() + ", getList()="
				+ getList() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
	
}
