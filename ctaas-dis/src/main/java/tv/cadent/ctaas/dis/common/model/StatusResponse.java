package tv.cadent.ctaas.dis.common.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusResponse {

	private List<String> errorMessages = new ArrayList<String>();

	public StatusResponse() {

	}
	
	@JsonProperty("errorMessages")
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	public void addErrorMessage(String message) {
		this.errorMessages.add(message);
	}

}
