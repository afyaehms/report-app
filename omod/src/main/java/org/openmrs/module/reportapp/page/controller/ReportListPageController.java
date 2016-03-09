package org.openmrs.module.reportapp.page.controller;

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.report.BirtReportService;
import org.openmrs.module.report.model.BirtReport;
import org.openmrs.ui.framework.page.PageModel;

public class ReportListPageController {
	
	public void get(PageModel model) {
		BirtReportService birtReportService = Context.getService(BirtReportService.class);
		int total = birtReportService.countBirtReport(null, null, null);
		List<BirtReport> reports = birtReportService.listBirtReport(null, null, null, 0, total);
		model.addAttribute("reports", reports);
	}

}
