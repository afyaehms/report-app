package org.openmrs.module.reportapp.birt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IParameterGroupDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirtReportParameterParser {
	
	protected static Logger logger = LoggerFactory.getLogger(BirtReportParameterParser.class);
	
	public static List<ReportParameter> parse(String report, BirtEngineFactory birtEngineFactory) {
		IReportRunnable design;
		try {
			design = birtEngineFactory.getObject().openReportDesign(report);
		} catch (EngineException e) {
			logger.error(e.getMessage());
			throw new RuntimeErrorException(new Error(e.getCause()));
		}

		IGetParameterDefinitionTask task = birtEngineFactory.getObject()
				.createGetParameterDefinitionTask(design);
		Collection params = task.getParameterDefns(true);

		Iterator parametersIterator = params.iterator();
		
		List<ReportParameter> parameterOptions = new ArrayList<ReportParameter>();
		// Iterate over all parameters
		while (parametersIterator.hasNext()) {
			IParameterDefnBase param = (IParameterDefnBase) parametersIterator.next();
			// Group section found
			if (param instanceof IParameterGroupDefn) {
				// Get Group Name
				IParameterGroupDefn group = (IParameterGroupDefn) param;
				String groupName = group.getName();
				// Get the parameters within a group
				Iterator groupedParametersIterator = group.getContents().iterator();
				while (groupedParametersIterator.hasNext()) {
					IScalarParameterDefn scalar = (IScalarParameterDefn) groupedParametersIterator.next();
					ReportParameter parameterOption = buildParameterOption(task, scalar, groupName);
					parameterOptions.add(parameterOption);
				}
			} else {
				IScalarParameterDefn scalar = (IScalarParameterDefn) param;
				ReportParameter parameterOption = buildParameterOption(task, scalar, null);
				parameterOptions.add(parameterOption);
			}
		}

		task.close();
		
		return parameterOptions;
	}
	
	private static ReportParameter buildParameterOption(
			IGetParameterDefinitionTask task,
			IScalarParameterDefn parameter,
			String groupName) {
		ReportParameter parameterOption = new ReportParameter();
		parameterOption.setParameterGroupName(groupName);
		parameterOption.setParameterName(parameter.getName());
		parameterOption.setPromptText(parameter.getPromptText());
		parameterOption.setParameterDataType(parameter.getDataType());
		switch (parameter.getDataType()) {
			case IScalarParameterDefn.TYPE_DATE:
			case IScalarParameterDefn.TYPE_DATE_TIME:
				parameterOption.setReadableParameterDataType("date");
				break;
			case IScalarParameterDefn.TYPE_BOOLEAN:
				parameterOption.setReadableParameterDataType("boolean");
				break;
			default:
				parameterOption.setReadableParameterDataType("text");
		}
		if (parameter.getControlType() == IScalarParameterDefn.LIST_BOX) {
			Collection selectionList = task.getSelectionList(parameter
					.getName());
			// Selection contains data
			if (selectionList != null) {
				for (Iterator selectionListIterator = selectionList.iterator(); selectionListIterator.hasNext();) {
					// Print out the selection choices
					IParameterSelectionChoice selectionItem = (IParameterSelectionChoice) selectionListIterator.next();
					ReportParameterChoice parameterChoice = new ReportParameterChoice();
					parameterChoice.setValue(selectionItem.getValue().toString());
					parameterChoice.setLabel(selectionItem.getLabel());
					parameterOption.addParameterChoice(parameterChoice);
				}
			}
		}
		return parameterOption;
	}
}
