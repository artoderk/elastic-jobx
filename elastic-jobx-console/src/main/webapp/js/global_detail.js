$(function() {
    renderSettings();
    bindSubmitJobSettingsForm();
    $('[href="#settings"]').click(function(event) {
        renderSettings();
    });
    $('[href="#strategies"]').click(function(event) {
        initFileInput();
        renderStrategies();
    });
    bindUploadSuccess();
    bindRemoveButtons();
});

function renderSettings() {
    $.get("global/configs", {}, function (data) {
        bindDatepicker(data.skipTimeStart, data.skipTimeEnd);
    });
}

function bindSubmitJobSettingsForm() {
    $("#global-config-form").submit(function(event) {
        event.preventDefault();
        var skipTimeStart = $("#skipTimeStart").val();
        var skipTimeEnd = $("#skipTimeEnd").val();
        // Check
        if (skipTimeStart != "" && skipTimeEnd != "") {
            if (skipTimeStart == skipTimeEnd) {
                showFailureDialog("time-equal-failure-dialog");
                return;
            }
        }
        $.post("global/configs", {skipTimeStart: skipTimeStart, skipTimeEnd: skipTimeEnd}, function(data) {
            showSuccessDialog();
        });
    });
}

function renderStrategies() {
    $.get("global/strategies", {}, function (data) {
        $("#strategyTable tbody").empty();
        
        if (data.length == 0) {
        	$("#strategyTable").hide();
        } else {
        	$("#strategyTable").show();
        }

        for (var i = 0;i < data.length;i++) {
            var baseTd = "<td>" + data[i].no + "</td><td>" + data[i].name + "</td><td>" + data[i].path + "</td>";
            var removeButton = "<td>" + "<button operation='remove' class='btn btn-danger' ip='" + data[i].name + "'>删除</button>" + "</td>";
            var trClass = "info";
            $("#strategyTable tbody").append("<tr class='" + trClass + "'>" + baseTd + removeButton + "</tr>");
        }
    });
}

function bindRemoveButtons() {
    $(document).on("click", "button[operation='remove']", function(event) {
        $.post("global/delStrategy", {name : $(event.currentTarget).attr("name")}, function (data) {
        	renderStrategies();
            showSuccessDialog();
        });
        // 不支持delete??
//        $.ajax({
//        	  type: 'DELETE',
//        	  url: '/global/strategy',
//        	  data: {name : $(event.currentTarget).attr("name")},
//        	  dataType: "json",
//        	  success: function (data) {
//                  renderStrategies();
//                  showSuccessDialog();
//              }
//        });
    });
}

$('#strategyFile').on('filepreajax', function(event, previewId, index, jqXHR) {
    if ($('#strategyName').val() == "") {
        return {
            message: '[分片策略类路径]不能为空',
            data: {}
        };
    }
});


function bindUploadSuccess() {
	$('#strategyFile').on('fileuploaded', function(event, data, previewId, index) {
		$('.kv-file-remove').trigger('click');
		$('#strategyName').val("");
		renderStrategies();
		showSuccessDialog();
	});
}

function initFileInput() {
    $('#strategyFile').fileinput({
        language: 'zh', //设置语言
        uploadUrl: 'global/strategy', //上传的地址
        uploadExtraData : function() {
            var data = {
            	strategyName : $("#strategyName").val(),
            };
            return data;
        },
        allowedFileExtensions : ['groovy'],//接收的文件后缀
        showUpload: true, //是否显示上传按钮
        showCaption: false,//是否显示标题
        maxFileSize : 20,
        maxFileCount: 1,
        browseClass: "btn btn-primary" //按钮样式
    });
}

function bindDatepicker(skipTimeStart , skipTimeEnd) {
    $('#datetimepicker1').datetimepicker({
        defaultDate: skipTimeStart,
        format: 'YYYY-MM-DD HH:mm',
        showClose:true
    });
    $('#datetimepicker2').datetimepicker({
        defaultDate: skipTimeEnd,
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
