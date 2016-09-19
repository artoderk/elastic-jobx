<#import "tags/dashboard.ftl" as dashboard>
<div>
    <h1>全局配置</h1>
    <ul class="nav nav-tabs" role="tablist">
        <li id="settings_tab" role="presentation" class="active"><a href="#settings" aria-controls="settings" role="tab" data-toggle="tab">全局设置</a></li>
        <li id="strategies_tab" role="presentation"><a href="#strategies" aria-controls="strategies" role="tab" data-toggle="tab">全局分片策略</a></li>
    </ul>
    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="settings">
            <form id="global-config-form" class="form-horizontal">
                <div class="form-group">
                	<label for="failover" class="col-sm-2 control-label">跳过执行期间</label>
                	<div class="col-sm-2">
	                    <div class='input-group date' id='datetimepicker1'>
			                <input type='text' id='skipTimeStart' class="form-control" title="从这个时间点开始跳过执行" />
			                <span class="input-group-addon">
			                    <span class="glyphicon glyphicon-calendar"></span>
			                </span>
			            </div>
			        </div>
			        <label for="failover" class="col-sm-1 control-label">～</label>
					<div class="col-sm-2">
						<div class='input-group date' id='datetimepicker2'>
			                <input type='text' id='skipTimeEnd' class="form-control"  title="到这个时间点之前跳过执行" />
			                <span class="input-group-addon">
			                    <span class="glyphicon glyphicon-calendar"></span>
			                </span>
			            </div>
		            </div>
                </div>

                <div class="form-group">
                    <label for="triggerHistory" class="col-sm-2 control-label">记录历史执行信息</label>
                    <div class="col-sm-2">
                        <input type="checkbox" id="triggerHistory" name="triggerHistory" data-toggle="tooltip" data-placement="bottom" title="记录历史执行信息，此选项与具体[作业设置->监控作业执行时状态]同时为true时生效" />
                    </div>
                </div>

                <button type="reset" class="btn btn-inverse">重置</button>
                <button type="submit" class="btn btn-primary">确定</button>
            </form>
        </div>
        <div role="tabpanel" class="tab-pane" id="strategies">
            <form id="strategy-add-form" class="form-horizontal" enctype="multipart/form-data">
                <div class="form-group">
                	<label for="strategyFile" class="col-sm-2 control-label">分片策略类路径</label>
                    <div class="col-sm-3">
                        <input type="text" id="strategyName" name="strategyName" class="form-control" data-toggle="tooltip" data-placement="bottom" title="自定义作业分片策略实现类全路径" required />
                    </div>
                </div>
                <div class="form-group">
                    <label for="strategyFile" class="col-sm-2 control-label">分片策略实现类</label>
                    <div class="col-sm-3">
                        <input type="file" id="strategyFile" name="strategyFile" class="form-control" data-toggle="tooltip" data-placement="bottom" title="自定义作业分片策略实现类文件" required />
                    </div>
                </div>
            </form>
            <table id="strategyTable" class="table table-hover">
                <thead>
                <tr>
                    <th>No.</th>
                    <th>分片策略名</th>
                    <th>分片策略全路径</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>
</div>
<@dashboard.successDialog "success-dialog" />
<@dashboard.failureDialog "add-job-failure-dialog" "新增任务失败，任务已存在" />
<@dashboard.failureDialog "time-equal-failure-dialog" "[跳过执行期间]的起止时间段不能相同" />
<@dashboard.failureDialog "connect-reg-center-failure-dialog" "连接失败，请检查注册中心配置" />
<link href="lib/bootstrap/css/fileinput.min.css" rel="stylesheet">
<script src="lib/jquery/jquery-2.1.4.min.js"></script>
<script src="lib/bootstrap/js/moment.min.js"></script>
<script src="lib/bootstrap/js/bootstrap.min.js"></script>
<script src="lib/bootstrap/js/bootstrap-datetimepicker.min.js"></script>
<script src="lib/bootstrap/js/fileinput.js"></script>
<script src="lib/bootstrap/js/fileinput_locale_zh.js"></script>
<script src="js/common.js"></script>
<script src="js/dashboard.js"></script>
<script src="js/global_detail.js"></script>
