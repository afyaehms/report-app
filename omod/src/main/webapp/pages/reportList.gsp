<%
	ui.decorateWith("appui", "standardEmrPage", [title: "Reports"])
	
	ui.includeCss("uicommons", "datatables/dataTables_jui.css")
	ui.includeCss("reportapp", "report.css")
	ui.includeJavascript("patientqueueapp", "jquery.dataTables.min.js")
%>

<script>
	jq(function(){
		reportTable = jq('#reports-table');
		reportsDataTable = reportTable.DataTable({
			searching: true,
			lengthChange: false,
			pageLength: 15,
			jQueryUI: true,
			pagingType: 'full_numbers',
			sort: false,
			dom: 't<"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg"ip>',
			language: {
				zeroRecords: 'No Investigations ordered.',
				paginate: {
					first: 'First',
					previous: 'Previous',
					next: 'Next',
					last: 'Last'
				}
			}
		});

		jq("#phrase").on("keyup", function(){
			var searchPhrase = jq(this).val();
			reportsDataTable.search(searchPhrase).draw();
		});
		
		jq('#getReports').click(function(){
			jq("#phrase").val('');
			reportsDataTable.search('').draw();
		});
	});
</script>

<style>
	#report-search-form input {
	  display: inline;
	  margin-top: 5px;
	}

	#reports-table {
	  margin-top: 1em;
	}
	#report-search-clear-button {
	  position: relative;
	  right: 25px;
	}
	.paging_full_numbers .fg-button {
		margin: 1px;
	}
	.paging_full_numbers {
		width: 62% !important;
	}	
	.dataTables_info {
		float: left;
		width: 35%;
	}
	th:last-child{
		width: 75px!important;
	}
</style>

<div class="clear"></div>
<div id="queue-div">
	<div class="container">
		<div class="example">
			<ul id="breadcrumbs">
				<li>
					<a href="${ui.pageLink('referenceapplication', 'home')}">
						<i class="icon-home small"></i></a>
				</li>

				<li>
					<i class="icon-chevron-right link"></i>
					Reports
				</li>
			</ul>
		</div>
	</div>
	
	<div class="patient-header new-patient-header">
			<div class="demographics">
				<h1 class="name" style="border-bottom: 1px solid #ddd;">
					<span>&nbsp;REPORTS MODULE &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</span>
				</h1>
			</div>
			
			<div class="show-icon">
				&nbsp;
			</div>
			
			<div class="filter">
				<i class="icon-filter" style="color: rgb(91, 87, 166); float: left; font-size: 56px ! important; padding: 0px 10px 0px 0px;"></i>
							
				<div class="second-col">
					<label for="phrase">Filter Reports</label><br/>
					<input id="phrase" type="text" name="phrase" placeholder="Search by Report Name" autocomplete="off">
					<i class="icon-search" style="color: rgb(170, 170, 170); float: right; font-size: 16px ! important; position: relative; margin-right: 96%; margin-top: -30px;"></i>
				</div>
				
				<a class="button confirm" id="getReports" style="float: right; margin: 21px -5px 0 0;">
					<i class="icon-refresh small"></i>
					Get Reports
				</a>
			</div>
		</div>
</div>

<table id="reports-table">
	<thead>		
		<tr>
			<th>#</td>
			<th>REPORT</td>
			<th>DESCRIPTION</td>
            <th>ACTIONS</td>
		</tr>
	</thead>
	<tbody>
		<% if (reports.empty) { %>
			<tr>
				<td colspan="4">
					No Reports!
				</td>
			</tr>
		<% } %>
		<% reports.eachWithIndex { report, index -> %>
			<tr data-report-id="${report.id}">
				<td>${index+1}</td>
				<td>${report.name}</td>
				<td>${report.description}</td>
                <td>
					<% report.reportTypes.each { type -> %>
						<a href="${birtViewerUrl}frameset?__report=${dataUrl + type.path}" target="_blank" >
							<i class="icon-bar-chart small"></i>
							VIEW
						</a>
					<% } %>
                </td>
			</tr>
		<% } %>
	</tbody>

</table>

