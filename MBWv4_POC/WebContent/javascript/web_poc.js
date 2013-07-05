/**
 * 
 */
	/*$('#confrmDmnReg .cofirmBtn').on("click", function() {
		$('#confrmDmnReg .wait').css('display','block');
		var domainName = $("#dmnRegistration").html();
		var change = $.trim($('#change').val());
		$.get('domainSearch.do?domainName='+domainName+'&event=register', function(responseText) { 
			if(domainName==responseText){
				$('#confrmDmnReg').css('display', 'none');
				$('.regNewDmn').css('display','none');
	           // $('.designStep4').css('display', 'block');
	            if(change=="true"){
	            	$.get('updateDesign?dmnName='+domainName+'&step=puchasedmn', function(responseText) {
	                	document.location.href=strDashbaord;
	                	window.open(responseText, "_blank");
	                });	
	        	}else{
	                $('.designStep4').css('display', 'block');
	        		$.get('updateDesign?dmnName='+domainName+'&step=puchasedmn', function(responseText) { 
	                	$('#desingSteps').html(responseText); 
	                });}
	    		$.get('updateDesign?dmnName='+domainName+'&step=puchasedmn', function(responseText) { 
	            	$('#desingSteps').html(responseText); 
	            });
			} else {
				$('#confrmDmnReg').css('display', 'none');
				$("#rerNewDmnError").val('Domain registration failed. Please try again later.');
				$("#rerNewDmnError").css('display','block');
			}
         });
	});	*/
function fnregister(name) {
	$('#domainName').val(name);
	document.forms[1].submit();
 }
