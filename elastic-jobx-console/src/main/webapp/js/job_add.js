$(function() {
    initSettings();
    bindSubmitJobAddForm();
    bindDatepicker();
});

function initSettings() {
	$("#jobName").val("");
//	$("#jobClass").val("com.dangdang.example.elasticjob.core.job.SimpleJobDemo");
	$("#jobClass").attr("value", "");
	$("#shardingTotalCount").val(1);
	$("#jobParameter").val("");
//	$("#cron").val("0/5 * * * * ?");
	$("#cron").attr("value", "");
	$("#concurrentDataProcessThreadCount").val(1);
	$("#processCountIntervalSeconds").val(300);
	$("#fetchDataCount").val(1);
	$("#maxTimeDiffSeconds").val(-1);
	$("#monitorPort").val(-1);
	$("#monitorExecution").attr("checked", true);
	$("#failover").attr("checked", false);
	$("#misfire").attr("checked", true);
	$("#skipStartTime").val("");
	$("#skipEndTime").val("");
	$("#jobShardingStrategyClass").val("");
	$("#description").val("");
}

function bindSubmitJobAddForm() {
    $("#job-add-form").submit(function(event) {
        event.preventDefault();
        var jobName = $("#jobName").val();
        var jobClass = $("#jobClass").val();
        var shardingTotalCount = $("#shardingTotalCount").val();
        var jobParameter = $("#jobParameter").val();
        var cron = $("#cron").val();
        var concurrentDataProcessThreadCount = $("#concurrentDataProcessThreadCount").val();
        var processCountIntervalSeconds = $("#processCountIntervalSeconds").val();
        var fetchDataCount = $("#fetchDataCount").val();
        var maxTimeDiffSeconds = $("#maxTimeDiffSeconds").val();
        var monitorPort = $("#monitorPort").val();
        var monitorExecution = $("#monitorExecution").prop("checked");
        var failover = $("#failover").prop("checked");
        var misfire = $("#misfire").prop("checked");
        var skipStartTime = $("#skipStartTime").val();
        var skipEndTime = $("#skipEndTime").val();
        var shardingItemParameters = $("#shardingItemParameters").val();
        var jobShardingStrategyClass = $("#jobShardingStrategyClass").val();
        var description = $("#description").val();
        // Check
        if (skipStartTime != "" && skipEndTime != "") {
        	if (skipStartTime == skipEndTime) {
        		showFailureDialog("time-equal-failure-dialog");
        		return;
        	}
        }
        $.post("job/add", {jobName: jobName, jobClass : jobClass, shardingTotalCount: shardingTotalCount, jobParameter: jobParameter, cron: cron, concurrentDataProcessThreadCount: concurrentDataProcessThreadCount, processCountIntervalSeconds: processCountIntervalSeconds, fetchDataCount: fetchDataCount, maxTimeDiffSeconds: maxTimeDiffSeconds, monitorPort: monitorPort, monitorExecution: monitorExecution, failover: failover, misfire: misfire, skipStartTime: skipStartTime, skipEndTime: skipEndTime, shardingItemParameters: shardingItemParameters, jobShardingStrategyClass: jobShardingStrategyClass, description: description}, function(data) {
            if (data == 1) {
                renderJobsForDashboardNav();
                initSettings();
                showSuccessDialog();    
            } else {
                showFailureDialog("add-job-failure-dialog");
            }
        });
    });
}

function bindDatepicker() {
	 $('#datetimepicker1').datetimepicker({
		 format: 'YYYY-MM-DD HH:mm',
         showClose:true
     });
     $('#datetimepicker2').datetimepicker({
         useCurrent: false, //Important! See issue #1075
         format: 'YYYY-MM-DD HH:mm',
         showClose:true
     });
     $("#datetimepicker1").on("dp.change", function (e) {
         $('#datetimepicker2').data("DateTimePicker").minDate(e.date);
     });
     $("#datetimepicker2").on("dp.change", function (e) {
         $('#datetimepicker1').data("DateTimePicker").maxDate(e.date);
     });
}

