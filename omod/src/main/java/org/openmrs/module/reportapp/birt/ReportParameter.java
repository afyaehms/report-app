package org.openmrs.module.reportapp.birt;

import java.util.ArrayList;
import java.util.List;

public class ReportParameter {
	private String parameterName;
	private String promptText;
	private String readableParameterDataType;
	private int parameterDataType;
	private String parameterGroupName;
	private List<ReportParameterChoice> parameterChoices;

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public String getReadableParameterDataType() {
		return readableParameterDataType;
	}

	public void setReadableParameterDataType(String parameterType) {
		this.readableParameterDataType = parameterType;
	}

	public int getParameterDataType() {
		return parameterDataType;
	}

	public void setParameterDataType(int parameterDataType) {
		this.parameterDataType = parameterDataType;
	}

	public String getParameterGroupName() {
		return parameterGroupName;
	}

	public void setParameterGroupName(String parameterGroupName) {
		this.parameterGroupName = parameterGroupName;
	}

	public List<ReportParameterChoice> getParameterChoices() {
		return parameterChoices;
	}

	public void addParameterChoice(ReportParameterChoice parameterChoice) {
		if (this.parameterChoices == null) {
			this.parameterChoices = new ArrayList<ReportParameterChoice>();
		}
		this.parameterChoices.add(parameterChoice);
	}
}