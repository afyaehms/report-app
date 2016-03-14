<%
	ui.decorateWith("appui", "standardEmrPage")
ui.includeCss("uicommons", "datatables/dataTables_jui.css")
ui.includeJavascript("patientqueueapp", "jquery.dataTables.min.js")
%>

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
</style>

<form method="get" id="report-search-form" onsubmit="return false">
    <input type="text" id="report-search" placeholder="Search by Report Name" /><i id="report-search-clear-button" class="small icon-remove-sign"></i>
</form>

<table id="reports-table">
	<thead>
		<tr>
			<td>Name</td>
			<td>Description</td>
            <td>Actions</td>
		</tr>
	</thead>
	<tbody>
		<% reports.each { report -> %>
			<tr data-report-id="${report.id}">
				<td>${report.name}</td>
				<td>${report.description}</td>
                <td>
					<% report.reportTypes.each { type -> %>
						<a href="${birtViewerUrl}frameset?__report=${dataUrl + type.path}" target="_blank" >
							<i class="icon-list-alt small"></i>
						</a>
					<% } %>

                </td>
			</tr>
		<% } %>
	</tbody>

</table>

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

    jq("#report-search").on("keyup", function(){
        var searchPhrase = jq(this).val();
        console.log("Searching for: " + searchPhrase);
        reportsDataTable.search(searchPhrase).draw();
    });

    jq("#report-search-clear-button").on("click", function(){
        jq("#report-search").val('');
        reportsDataTable.search('').draw();
    });
});
</script>
