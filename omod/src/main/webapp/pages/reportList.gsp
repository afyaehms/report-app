<%
	ui.decorateWith("appui", "standardEmrPage");
%>

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
                    <a href="">
                        <i class=" icon-search small"></i>
                    </a>

                </td>
			</tr>
		<% } %>
	</tbody>

</table>

<script>
jq(function(){
	reportTable = jq('#reports-table');
	reportsDataTable = reportTable.DataTable({
        searching: false,
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
        },
        "drawCallback" : function (settings) {
        	if(isTableEmpty()){
                //this should ensure that nothing happens when the use clicks the
                //row that contain the text that says 'No data available in table'
                return;
            }
        	//drawCallback is called on each page redraw so we need to remove any previous handlers
            //otherwise there will multiple hence the logic getting executed multiples times as the
            //user the goes back and forth between pages
            reportTable.find('tbody tr').unbind('click');
            reportTable.find('tbody tr').unbind('hover');

            reportTable.find('tbody tr').click(
                function(){
                    var reportId = jq(this).data("reportId");
                    window.location = emr.pageLink("reportapp", "viewReport", { "reportId", reportId });
                }
            );
        }
    });
});
</script>

<style>
#reports-table tbody tr:hover {
	background-color: #f26522;
	cursor: pointer;
}
</style>