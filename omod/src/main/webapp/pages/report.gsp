<% 
ui.decorateWith("appui", "standardEmrPage")
ui.includeCss("uicommons", "datatables/dataTables_jui.css")
ui.includeJavascript("patientqueueapp", "jquery.dataTables.min.js")
%>

<script>
var requestParameters = {},
	reportData,
	columnOptionsBuilder,
	columnOptions,
	reportDataTable,
	reportDataTableObject;
jq(function(){
	reportDataTableObject = jq("#report-data");
	jq("button#submit").on("click", function(e){
		e.preventDefault();
		var formData = jq("form#report-parameters").serializeArray();
		jq.each(formData, function () {
			requestParameters[this.name] = this.value || '';
		});
		jq.getJSON(emr.fragmentActionLink("reportapp", "reportData", "getReportData", requestParameters), function (data){
			reportData = data.reportData;
			if (reportData.length > 0) {
				var columnOptionsBuilder = "[";
				jq.each(Object.keys(reportData[0]), function(index, value) {
					columnOptionsBuilder += '{ "title" : "'+ value.toUpperCase() +'", "data" : "' + value + '"},'
				});
				columnOptionsBuilder = columnOptionsBuilder.substring(0, columnOptionsBuilder.length - 1);
				columnOptionsBuilder += "]";
			}
			columnOptions = JSON.parse(columnOptionsBuilder);
			reportDataTable = reportDataTableObject.DataTable({
				"data" : reportData,
				"columns" : columnOptions,
				"searchind" : false,
				"jQueryUI" : true,
				"lengthChange" : false,
				"pageLength" : 15,
				"pagingType" : "full_numbers",
				"ordering" : false,
				"destroy": true,
				"dom" : 't<"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg"ip>',
				"language" : {
					"info": "Report data",
					"infoEmpty": "No report data",
					"zeroRecords": "No report data",
					"paginate": {
						"first": "First",
						"previous": "Previous",
						"next": "Next",
						"last": "Last"
					}
				}
			});
		})
	});
});
</script>

<form id="report-parameters">
	<input type="hidden" value="${reportName}" name="reportName" />
	<input type="hidden" value="${reportTitle}" name="reportTitle" />
<% reportParameters.eachWithIndex { parameter, index ->
	if (parameter.readableParameterDataType == "date") { %>
		${ ui.includeFragment("uicommons", "field/datetimepicker", [ id: "$parameter.parameterName", label: "$parameter.promptText", formFieldName: "$parameter.parameterName", useTime: false ]) }
	<% } else if (parameter.readableParameterDataType == "boolean") { %>
		${ ui.includeFragment("uicommons", "field/checkbox", [ label: "$parameter.promptText", formFieldName: "$parameter.parameterName" ]) }
	<% } else if (parameter.readableParameterDataType == "text" && parameter.parameterChoices?.size() > 0) { 
		def parameterOptions = parameter.parameterChoices.collect {[ label: "$it.label", value: "$it.value", selected: false]}
		System.out.println(parameterOptions)%>
		${ ui.includeFragment("uicommons", "field/dropDown", [ label: "$parameter.promptText", formFieldName: "$parameter.parameterName", options: parameterOptions ]) }
	<% } else { %>
		${ ui.includeFragment("uicommons", "field/text", [ label: "$parameter.promptText", formFieldName: "$parameter.parameterName" ]) }
	<% }
} %>
<button id="submit">Get Report</button>
</form>

<h2>${reportTitle?:"Report Title"}</h2>

<table id="report-data"></table>