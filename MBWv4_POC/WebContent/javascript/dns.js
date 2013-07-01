function fnTableClose(object) {
	  $(object).closest('table.dmntable').hide();
}

function click2() {
	$('.click2').css('display', 'none');
}

// jquery initializer, do not alter.	
$(document).ready(function() { 
	$('.closeimg').click(function() {
		$(this).closest('tr').css('display', 'none');
	});
	$("#clear").click(function() {
		$('#dns').get(0).reset();
	});
	$('.recordselect').change(function() {
		var selectVal = $(this).find("option:selected").text();
		if("TXT Record" == $.trim(selectVal)) {
			$(this).closest('table').find('.txtrecord').css('display', 'table-row');
		} else if("CNAME Record" == $.trim(selectVal)) {
			$(this).closest('table').find('.crecord').css('display', 'table-row');
		} else if("A Record" == $.trim(selectVal)) {
			$(this).closest('table').find('.arecord').css('display', 'table-row');
		}
		else if("MX Record" == $.trim(selectVal)) {
			$(this).closest('table').find('.mxrecord').css('display', 'table-row');
		}
		else if("AAAA Record" == $.trim(selectVal)) {
			
			$(this).closest('table').find('.aaarecord').css('display', 'table-row');
			//alert(" sdfsdfds" );
		}
		
	});
	

});