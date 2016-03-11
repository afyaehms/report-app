package org.openmrs.module.reportapp.page.controller;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.reportapp.birt.BirtEngineFactory;
import org.openmrs.module.reportapp.birt.BirtReportParameterParser;
import org.openmrs.module.reportapp.birt.ReportParameter;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ReportPageController {
	public void get(
			@RequestParam("reportName") String reportName,
			@RequestParam("reportTitle") String reportTitle,
			@SpringBean BirtEngineFactory birtEngineFactory,
			HttpServletRequest request,
			PageModel model,
			UiUtils ui) throws Exception {
		ServletContext sc = request.getSession().getServletContext();
		String reportPath = sc.getRealPath("/WEB-INF/view/module/reportapp/resources/reports/" +reportName+".rptdesign");
		List<ReportParameter> reportParameters = BirtReportParameterParser.parse(reportPath, birtEngineFactory);
		
		model.addAttribute("reportParameters", reportParameters);
		model.addAttribute("reportName", reportName);
		model.addAttribute("reportTitle", reportTitle);
	}
}
