package org.openmrs.module.reportapp.page.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.report.BirtReportService;
import org.openmrs.module.report.model.BirtReport;
import org.openmrs.module.report.model.BirtReportConfig;
import org.openmrs.module.report.util.ReportConstants;
import org.openmrs.ui.framework.page.PageModel;

public class ReportListPageController {
	
	public void get(PageModel model) {

		BirtReportConfig config = ReportConstants.getConfig();		
		String dataUrl = config.getUrlData();
		String birtViewerUrl = config.getUrlBirt();
		dataUrl = addLastStrokeToUrl(dataUrl);		
		birtViewerUrl = addLastStrokeToUrl(birtViewerUrl);
		model.addAttribute("birtViewerUrl", birtViewerUrl);
		model.addAttribute("dataUrl", dataUrl);
		
		BirtReportService birtReportService = Context.getService(BirtReportService.class);
		int total = birtReportService.countBirtReport(null, null, null);
		List<BirtReport> reports = birtReportService.listBirtReport(null, null, null, 0, total);
		model.addAttribute("reports", reports);
	}

	private String addLastStrokeToUrl(String url) {
		if (StringUtils.lastIndexOf(url, "/") != url.length()) {
			url += "/";
		}
		return url;
	}

}
