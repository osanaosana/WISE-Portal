<%@ include file="include.jsp"%>

<!DOCTYPE html>
<html xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />

<link href="<spring:theme code="stylesheet"/>" media="screen"
	rel="stylesheet" type="text/css" />
<link href="<spring:theme code="globalstyles"/>" media="screen"
	rel="stylesheet" type="text/css" />

<script type="text/javascript"
	src="<spring:theme code="generalsource"/>"></script>

<script type="text/javascript" src="<spring:theme code="jquerysource"/>"></script>
<script type="text/javascript"
	src="<spring:theme code="jqueryuisource"/>"></script>



<title><spring:message code="application.title" /></title>

<script type="text/javascript">
$(document).ready(function(){
	
	//focus cursor into the First Name field on page ready 
	if($('#runCode_part1').length){
		$('#runCode_part1').focus();
	}
});

function findPeriods() {
		var successCallback = function(responseText) {
			var periodSelect = document.getElementById("runCode_part2");
			periodSelect.innerHTML = "";
			// o.responseText can be either "not found" (when runcode doesn't point to an existing run
			// or "1,2,3,4,5,...", a comma-separated values of period names
			if (responseText == "not found" || responseText.length < 2) {
				alert("<spring:message code="student.addproject.7" />");
			} else {
				var op = document.createElement('option');
				op
						.appendChild(document
								.createTextNode("<spring:message code="student.addproject.8" />"));
				op.value = 'none';
				periodSelect.appendChild(op);

				var periodsArr = responseText.split(",");
				for ( var i = 0; i < periodsArr.length; i++) {
					var periodName = periodsArr[i];
					if (periodName != "") {
						var op = document.createElement('option');
						op.appendChild(document.createTextNode(periodName));
						op.value = periodName;
						periodSelect.appendChild(op);
					}
				}
				periodSelect.disabled = false;
			}
		};

		var failureCallback = function(responseText) {
			alert('<spring:message code="student.addproject.10" />');
		};
		var runcode = document.getElementById("runCode_part1").value;
		if (runcode != null && runcode != "") {
			$.ajax({
				url : "/webapp/runinfo.html?runcode=" + runcode,
				dataType : "text",
				success : successCallback,
				error : failureCallback
			});
		}
	}

	function save() {
		var runcode = document.getElementById("runCode_part1").value;
		var period = document.getElementById("runCode_part2").value;

		if (period != null && period == "none") {
			alert('<spring:message code="student.addproject.8" />');
		} else if (runcode != null && period != null && period != "none") {
			var projectCode = document.getElementById("projectcode");
			projectCode.value = runcode + "-" + period;
			document.getElementById("addproject").submit();
		} else {
			alert('<spring:message code="student.addproject.7" />');
		}
	}

	function setup() {
		var runcode = document.getElementById('runCode_part1').value;
		if (runcode != null && runcode != "") {
			findPeriods();
		}
	}
</script>

</head>

<body style="background: #FFFFFF" onload="setup();">
	<div class="dialogContent">
		<div class="dialogSection">
			<div class="sectionHead">Instructions</div>
			<ol id="addProjectInstructions">
				<li><spring:message code="student.addproject.1" /></li>
				<li><spring:message code="student.addproject.2" /></li>
				<li><spring:message code="student.addproject.3" /></li>
			</ol>

			<div class="dialogSection formSection" id="addProjectForm">

				<!-- Support for Spring errors object -->
				<div class="errorMsgNoBg">
					<spring:bind path="addProjectParameters.*">
						<c:forEach var="error" items="${status.errorMessages}">
							<p>
								<c:out value="${error}" />
							</p>
						</c:forEach>
					</spring:bind>
				</div>
				<form:form method="post" commandName="addProjectParameters"
					id="addproject" autocomplete='off'>
					<div>
						<label for="runCode_part1" id="runCode_part1_label"><spring:message
								code="student.addproject.4" /></label>
						<form:input onblur="findPeriods();" path="runCode_part1"
							id="runCode_part1" size="25" maxlength="25" tabindex="1" />
					</div>
					<div>
						<a onclick="findPeriods();" style="font-size: .9em;"><spring:message
								code="student.addproject.5" /></a>
					</div>
					<div>
						<label for="runCode_part2" id="runCode_part2_label"><spring:message
								code="student.addproject.6" /></label>
						<form:select path="runCode_part2" id="runCode_part2" tabindex="2"
							disabled="true"></form:select>
					</div>

					<form:hidden path="projectcode" id="projectcode" />

					<div>
						<input type="button" onclick="save();" value="Add Project" />
					</div>
				</form:form>
			</div>
		</div>
	</div>
</body>
</html>
