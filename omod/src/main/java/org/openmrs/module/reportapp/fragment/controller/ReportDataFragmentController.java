package org.openmrs.module.reportapp.fragment.controller;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IDataExtractionTask;
import org.eclipse.birt.report.engine.api.IDataIterator;
import org.eclipse.birt.report.engine.api.IExtractionResults;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IResultMetaData;
import org.eclipse.birt.report.engine.api.IResultSetItem;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.openmrs.module.reportapp.birt.BirtEngineFactory;
import org.openmrs.module.reportapp.birt.BirtReportParameterParser;
import org.openmrs.module.reportapp.birt.ReportParameter;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportDataFragmentController {
	protected static Logger logger = LoggerFactory.getLogger(ReportDataFragmentController.class);	

	public SimpleObject getReportData(
			@RequestParam("reportName") String reportName,
			@RequestParam("reportTitle") String reportTitle,
			@SpringBean BirtEngineFactory birtEngineFactory,
			HttpServletRequest request) {
		// get report name and launch the engine
		ServletContext sc = request.getSession().getServletContext();
		IReportEngine birtReportEngine = birtEngineFactory.getObject();

		logger.trace("image directory " + sc.getRealPath("/WEB-INF/view/module/reportapp/resources/images"));
		String reportPath = sc.getRealPath("/WEB-INF/view/module/reportapp/resources/reports/") + reportName + ".rptdesign";

		IReportRunnable design;
		try {
			// Open report design
			design = birtReportEngine.openReportDesign(reportPath);

			List<ReportParameter> reportParameters = BirtReportParameterParser.parse(reportPath, birtEngineFactory);
			Map<String, Object> reportParametersFromRequest = getReportParametersFromRequest(request, reportParameters);

			design = birtReportEngine.openReportDesign(reportPath);
			//Create task to run the report - use the task to execute the report and save to disk.
			IRunTask runTask = birtReportEngine.createRunTask(design); 
			
			runTask.setParameterValues(reportParametersFromRequest);
			
			//Set parent classloader for engine
			runTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, this.getClass().getClassLoader()); 
			
			//run the report and destroy the engine
			runTask.run(sc.getRealPath("/WEB-INF/view/module/reportapp/resources/reports/") + reportName + ".rptdocument");
			runTask.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		//Open previously created report document
		IReportDocument iReportDocument;
		List<SimpleObject> reportResults = new ArrayList<SimpleObject>();
		try {
			iReportDocument = birtReportEngine.openReportDocument(sc.getRealPath("/WEB-INF/view/module/reportapp/resources/reports/") + reportName + ".rptdocument");
		
			//Create Data Extraction Task
			IDataExtractionTask dataExtract = birtReportEngine.createDataExtractionTask(iReportDocument);
			
			//Get list of result sets		
			@SuppressWarnings("unchecked")
			ArrayList<Object> resultSetList = (ArrayList<Object>)dataExtract.getResultSetList( );
			
			//Choose first result set
			IResultSetItem resultItem = (IResultSetItem)resultSetList.get( 0 );
			String dispName = resultItem.getResultSetName( );
			dataExtract.selectResultSet( dispName );
			
			IExtractionResults extractedResults = dataExtract.extract();
			IDataIterator dataIterator = null;
			if ( extractedResults != null ){
				dataIterator = extractedResults.nextResultIterator( );
				//iterate through the results
				if ( dataIterator != null  ){
					IResultMetaData reportMetadata = dataIterator.getResultMetaData();
					int columnLength = reportMetadata.getColumnCount();
					while ( dataIterator.next( ) ){	
						SimpleObject reportRow = new SimpleObject();
						for (int i = 0; i < columnLength - 1; i++) {
							reportRow.put(reportMetadata.getColumnLabel(i), dataIterator.getValue(i));
							logger.trace(reportMetadata.getColumnLabel(i) + " = " + dataIterator.getValue(i));
						}
						reportResults.add(reportRow);
					}
					dataIterator.close();
				}
			}
			
			dataExtract.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return SimpleObject.create("reportData", reportResults);
	}

	private Map<String, Object> getReportParametersFromRequest(HttpServletRequest request, List<ReportParameter> expectedParameters) {
		Map<String, Object> reportParameters = new HashMap<String, Object>();
		Map requestParameters = request.getParameterMap();
		for (ReportParameter parameter: expectedParameters) {
			String parameterValue = ((String[])requestParameters.get(parameter.getParameterName()))[0];
			switch (parameter.getParameterDataType()) {
			case IScalarParameterDefn.TYPE_DATE:
			case IScalarParameterDefn.TYPE_DATE_TIME:
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					reportParameters.put(parameter.getParameterName(), new Date(sdf.parse(parameterValue).getTime()));
				} catch (ParseException e) {
					logger.error(e.getMessage());
					throw new RuntimeErrorException(new Error(e.getCause()));
				}
				break;
			case IScalarParameterDefn.TYPE_FLOAT:
				reportParameters.put(parameter.getParameterName(), Float.parseFloat(parameterValue));
				break;
			case IScalarParameterDefn.TYPE_INTEGER:
				reportParameters.put(parameter.getParameterName(), Integer.parseInt(parameterValue));
				break;
			default:
				reportParameters.put(parameter.getParameterName(), parameterValue);
			}
		}
		return reportParameters;
	}
}
