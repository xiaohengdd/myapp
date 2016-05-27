package wkj.team.driver.entity;

import java.util.List;

public class DriverTypeGSON {
	//第一层，最外层

	private String error_code;
	private String reason;
	private List<DriverType> result;
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public List<DriverType> getResult() {
		return result;
	}
	public void setResult(List<DriverType> result) {
		this.result = result;
	}
}
