$(document).ready(function(){
	
	
	
	tippy('.verUsuarios', {
			content: 'Ver lista del personal a cargo.'
	});
	
	tippy('.agregarUsuarios', {
		content: 'Edita personal a cargo.'
	});
	
	setDataTableCustom();
	
	if(!permisoEscritura) {
		$("#tableUsuariosCrm").find("input,button,textarea,select").not('.close').attr("disabled", true);
	}
	
	validaSelectores();
});

$("#tableUsuariosCrm").on('draw', function() {
	validaSelectores();
});


function setDataTableCustom(){
	/*JS temporal sin ultima columna*/
	$('.data-table-custom').DataTable({
	responsive: true,
	dom: 'fBrltip',
	buttons: [{

	extend:    'excelHtml5',
	text:      '<a class="btn-floating btn-sm teal waves-effect waves-light py-2 my-0">XLS</a>',
	titleAttr: 'Exportar XLS',
	className:"btn-unstyled",
	exportOptions: {
	columns: '',
	format: {
	body: function ( data, row, column, node ) {
	return data.replace( /[$,]/g, '' );
	}
	}

	}

	}
	],
	columnDefs: [
	{targets: '_all', className: "py-2" }
	],
	lengthChange: true,
	language: {
	"sProcessing": "Procesando...",
	"sLengthMenu": "Mostrando _MENU_ registros por página",
	"sInfo": "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
	"sInfoEmpty": "Mostrando registros del 0 al 0 de un total de 0 registros",
	"sInfoFiltered": "(filtrado de un total de _MAX_ registros)",
	"sSearch": "Filtrar:",
	"sLoadingRecords": "Cargando...",
	"oPaginate": {
	"sFirst": "<i class='fa fa-angle-double-left'>first_page</i>",
	"sLast": "<i class='fa fa-angle-double-right'>last_page</i>",
	"sNext": "<i class='fa fa-angle-right' aria-hidden='false'></i>",
	"sPrevious": "<i class='fa fa-angle-left' aria-hidden='false'></i>"
	},
	},
	lengthMenu: [[5,10,15], [5,10,15]],
	pageLength: 10,
	searching: false
	});
	/*JS temporal sin ultima columna*/
}

function validaSelectores() {
	$.each($('.checkAcceso'), function(key, value) {
		if(!$(this).is(':checked')) {
			$(this).closest('.rowUsuario').find('select').attr('disabled', true);
		}
		else {
			$(this).closest('.rowUsuario').find('select').attr('disabled', false);
			/*$(this).closest('.rowUsuario').find('.perfil').change(); */
			
			if($(this).closest('.rowUsuario').find("#perfilTMXUser option:selected").text().indexOf("Manager") != -1) {
				
				$(this).closest('.rowUsuario').find('#analista').addClass('d-none');
				$(this).closest('.rowUsuario').find('#manager').removeClass('d-none');
			}
			else {
				$(this).closest('.rowUsuario').find('#manager').addClass('d-none');
			}
			
		}
	});
	
	$.each($("#tableUsuariosCrm").find('.rowUsuario'), function(key, value) {
		if(($(value).find("#areaTMXUser").val() != idArea) && (!isManagerVentas)) {
			$(value).find('select,a,button').not('.close').attr('disabled', true);
		}
		else {
			if(!isManagerVentas) {
				if(!isManager){
					$(value).find('select,a,button').not('.close').attr('disabled', true);
				}
			}
		}
	});
}

$(".checkAcceso").on('click', function(){
	
	var trigger = $(this);
	
	if(!$(this).is(':checked')) {
		$(this).closest('.rowUsuario').find('select')
			.not('#relevoBaja').not('#cambioCartera').attr('disabled', true).materialSelect();
		
		if($(this).closest('.rowUsuario').find('select.perfil')
				.find('option:selected').text().indexOf("Manager") != -1) {
			
			$.post(validaManagerURL,{
				userId: $(this).closest('.rowUsuario').attr('id')
			}).done(function(data) {
				
				var response = JSON.parse(data);
			
				if(response.code == 0) {
			
					$(trigger).closest('.rowUsuario').find("#relevo").removeClass('d-none');
					
					$(trigger).closest('.rowUsuario').find("#relevo option:not(:first)").remove();
					
					var evento = $(trigger);
					var userId = $(trigger).closest('.rowUsuario').attr('id');
					
					$.post(obtenerUsuariosRelevoURL, {
						userId: userId
					})
						.done(
							function(data) {
								//console.log(data);
								var response = JSON.parse(data);
								
								$.each(response, function(key, value) {
									
									if(value.idUsuario != userId){
									
										evento.closest('.rowUsuario').find("#relevoBaja")
											.append("<option value=\"" + value.idUsuario + "\">"
												+ value.nombreCompleto + "</option>");
									}
								})
								
								evento.closest('.rowUsuario').find("#relevoBaja").materialSelect();
							});
					
					$(trigger).closest('.rowUsuario').find("#manager").addClass('d-none');
					$(trigger).closest('.rowUsuario').find("#cartera").addClass('d-none');
				}
				else{
					
					showMessageError('.navbar', response.msg, 0);
					$(trigger).click();
				}
			});
		}
		else {
			$(this).closest('.rowUsuario').find("#relevo").addClass('d-none');
			$(this).closest('.rowUsuario').find("#manager").addClass('d-none');
			
			var isEjecutivo = $(this).closest('.rowUsuario').find('#isEjecutivo').val();
			
			if(isEjecutivo == 1) {
				$(this).closest('.rowUsuario').find("#cartera").removeClass('d-none');
			}
		}
	}
	else {
		$(this).closest('.rowUsuario').find('input, button').attr('disabled', false)
		$(this).closest('.rowUsuario').find('select').attr('disabled', false).materialSelect();
		if($(this).closest('.rowUsuario').find('select.perfil')
				.find('option:selected').text().indexOf("Manager") != -1) {
			$(this).closest('.rowUsuario').find("#relevo").addClass('d-none');
			$(this).closest('.rowUsuario').find("#manager").removeClass('d-none');
			$(this).closest('.rowUsuario').find("#cartera").addClass('d-none');
		}
		else {
			$(this).closest('.rowUsuario').find("#relevo").addClass('d-none');
			$(this).closest('.rowUsuario').find("#manager").addClass('d-none');
			$(this).closest('.rowUsuario').find("#cartera").addClass('d-none');
		}
	}
});

$("#buscarPersonasCrm").click(function() {
	$("#area").val($("#areaTMX").val());
	$("#usuario").val($("#usuarioTMX").val());
	$("#perfil").val($("#perfilTMX").val());
	
	$("#buscar_form").submit();
});

$("#guardarPersonasCrm").click(function() {
	
	var stringPersonas = JSON.stringify(generaInfoPersonas());
	
	$("#usuarios").val(stringPersonas);
	
	if(error > 0) {
		showMessageError('.navbar', 'Hace falta información, favor de validar', 0);
		error = 0;
	}
	else{
		$("#guardar_form").submit();
	}
});


function generaInfoPersonas() {

	var arrayPersonas = [];
	
	$.each($('.rowUsuario'), function(key, value) {
		
		console.log($(this).attr('id'));
		
		var objAux = new Object();
		
		objAux.idUsuario = $(this).attr('id');
		objAux.acceso = $(this).find('#chktoggle-persona').is(':checked') ? true : false;
		objAux.area = $(this).find('#areaTMXUser').val();
		objAux.oficina = $(this).find('#oficinaTmxUser').val();
		objAux.perfil = $(this).find('#perfilTMXUser').val();
		objAux.jefe = $(this).find('#jefeTMXUser').val();
		objAux.relevo = $(this).find('#relevoBaja').val();
		objAux.relevoCartera = $(this).find('#cambioCartera').val();
		
		if(($(this).find('#areaTMXUser').val() == -1) || ($(this).find('#oficinaTmxUser').val() == -1)
				|| ($(this).find('#perfilTMXUser').val() == -1) /*|| 
				($(this).find('#jefeTMXUser').val() == -1) */) {
			
			if($(this).find('#chktoggle-persona').is(':checked')){
				error++;
			}
			else {
				if(($(this).find('#relevoBaja').val() == -1) && 
						!($(this).find('#relevo').hasClass('d-none'))) {
					error++;
				}
			}
		}
		else {
			if(!$(this).find('#chktoggle-persona').is(':checked')) {
				if(($(this).find('#relevoBaja').val() == -1) && 
						!($(this).find('#relevo').hasClass('d-none'))) {
					error++;
				}
				
				if(($(this).find('#cambioCartera').val() == -1) && 
						!($(this).find('#cartera').hasClass('d-none'))) {
					error++;
				}
			}
			else {
				if(($(this).find('#relevoBaja').val() == -1) && 
						!($(this).find('#relevo').hasClass('d-none'))) {
					error++;
				}
				
				if(($(this).find('#cambioCartera').val() == -1) && 
						!($(this).find('#cartera').hasClass('d-none'))) {
					error++;
				}
			}
		}
		
		arrayPersonas.push(objAux);
	});

	return arrayPersonas;
}

$("#tableUsuariosCrm").on('change', 'select.perfil', function() {
	
	console.log('cambio perfil');
	console.log($(this).val());
	
	var event = $(this);
	
	var auxJefeVar = event.closest('.rowUsuario').find("#jefeTMXUser").val();
	
	var isManager = $(this).closest('.rowUsuario').find('#isManager').val();
	var selectedJefe = "";
	
	
	if($(this).find('option:selected').text().indexOf("Manager") != -1) {
		
		$(this).closest('.rowUsuario').find('#analista').addClass('d-none');
		$(this).closest('.rowUsuario').find('#manager').removeClass('d-none');
		
		var isEjecutivo = $(this).closest('.rowUsuario').find('#isEjecutivo').val();
		
		if(isEjecutivo == 1) {
		
			$(this).closest('.rowUsuario').find("#cartera").removeClass('d-none');
			
			$.post(obtenerUsuariosAreaURL, {
				idArea: $(this).closest('.rowUsuario').find("#areaTMXUser").val()
			}).done(function(data) {
				
				event.closest('.rowUsuario').find("#jefeTMXUser option:not(:first)").remove();
				
				var response = JSON.parse(data);
				
				console.log(response);
				
				var usuarios = JSON.parse(response.usuarios);
				
				$.each(usuarios, function(key, value){
					
					var seleccionado = "";
					
					if((value.idUsuario != event.closest('.rowUsuario').attr('id')) && (value.perfil == response.manager) /*|| (value.perfil == 16) || (value.perfil == 19)*/) {
					
						if(value.idUsuario == auxJefeVar) {
							selectedJefe = "selected"
						}
						
						event.closest('.rowUsuario').find("#jefeTMXUser")
							.append("<option value=\"" + value.idUsuario + "\" " + selectedJefe +
									" >" + value.nombreCompleto + "</option>");
								
					}
				});
			
				selectDestroy(event.closest('.rowUsuario').find("#jefeTMXUser"), false);
			});
		}
	}
	else {
		
		$(this).closest('.rowUsuario').find('#manager').addClass('d-none');
		
		if(isManager == 1) {
			$(this).closest('.rowUsuario').find("#relevo").removeClass('d-none');
			
			
			
			$.post(obtenerUsuariosAreaURL, {
				idArea: $(this).closest('.rowUsuario').find("#areaTMXUser").val()
			}).done(function(data) {
				
				event.closest('.rowUsuario').find("#jefeTMXUser option:not(:first)").remove();
				
				var response = JSON.parse(data);
				
				console.log(response);
				
				var usuarios = JSON.parse(response.usuarios);
				
				$.each(usuarios, function(key, value){
					
					if(value.idUsuario == auxJefeVar) {
						selectedJefe = "selected"
					}
					
					if((value.idUsuario != event.closest('.rowUsuario').attr('id')) && (value.perfil == response.manager) /*|| (value.perfil == 16) || (value.perfil == 19)*/) {
					
						event.closest('.rowUsuario').find("#jefeTMXUser")
							.append("<option value=\"" + value.idUsuario + "\" " + selectedJefe +
									" >" + value.nombreCompleto + "</option>");
								
					}
				});
			
				selectDestroy(event.closest('.rowUsuario').find("#jefeTMXUser"), false);
				
				console.log(data);
				
				$(event).closest('.rowUsuario').find("#relevo option:not(:first)").remove();
				
				$.post(obtenerUsuariosRelevoURL, {
					userId: event.closest('.rowUsuario').attr('id')
				}).done(
					function(data) {
						//console.log(data);
						var response = JSON.parse(data);
						
						$.each(response, function(key, value) {
							
							if(value.idUsuario != event.closest('.rowUsuario').attr('id')){
							
								event.closest('.rowUsuario').find("#relevoBaja")
									.append("<option value=\"" + value.idUsuario + "\">"
										+ value.nombreCompleto + "</option>");
							}
						})
						
						event.closest('.rowUsuario').find("#relevoBaja").materialSelect();
					});
			});
		}
		
		var isEjecutivo = $(this).closest('.rowUsuario').find('#isEjecutivo').val();
		
		if(isEjecutivo == 1) {
			$(this).closest('.rowUsuario').find("#cartera").removeClass('d-none');
		}
		
	}
	
	
	
	/*
	if($(this).find('option:selected').text().indexOf("Analista") != -1) {
		$(this).closest('.rowUsuario').find('#analista').removeClass('d-none');
		$(this).closest('.rowUsuario').find('#manager').addClass('d-none');
	}
	*/
	
	$(this).closest('td').find(".textPerfil").text($(this).find('option:selected').text());

});

$("#tableUsuariosCrm").on('change', 'select.area', function() {
	
	var event = $(this);
	
	var isManager = $(this).closest('.rowUsuario').find('#isManager').val();
	
	$.post(obtenerPerfilesAreaURL, {
		idArea: $(this).val()
	}).done(function(data) {
		
		event.closest('.rowUsuario').find("#perfilTMXUser option:not(:first)").remove();
		
		var response = JSON.parse(data);
		
		console.log(response);
		
		$.each(response, function(key, value){
			
			var seleccionado = "";
			
			if(value._perfilId == event.closest('.rowUsuario').find("#inputPerfil").val()) {
				seleccionado = "selected";
			}
			
			event.closest('.rowUsuario').find("#perfilTMXUser")
				.append("<option value=\"" + value._perfilId + "\" " + seleccionado +
						" >" + value._descripcion + "</option>");
						
		});
	
		selectDestroy(event.closest('.rowUsuario').find("#perfilTMXUser"), false);
		
		event.closest('.rowUsuario').find("#perfilTMXUser").val(-1);
		event.closest('.rowUsuario').find("#perfilTMXUser").change();
		
		console.log(data);
	});
	
	$.post(obtenerUsuariosAreaURL, {
		idArea: $(this).val()
	}).done(function(data) {
		
		event.closest('.rowUsuario').find("#jefeTMXUser option:not(:first)").remove();
		
		var response = JSON.parse(data);
		
		console.log(response);
		
		var usuarios = JSON.parse(response.usuarios);
		
		$.each(usuarios, function(key, value){
			
			var seleccionado = "";
			
			if((value.idUsuario != event.closest('.rowUsuario').attr('id')) && (value.perfil == response.manager) /*|| (value.perfil == 16) || (value.perfil == 19)*/) {
			
				event.closest('.rowUsuario').find("#jefeTMXUser")
					.append("<option value=\"" + value.idUsuario + "\" " +
							" >" + value.nombreCompleto + "</option>");
						
			}
		});
	
		selectDestroy(event.closest('.rowUsuario').find("#jefeTMXUser"), false);
		
		event.closest('.rowUsuario').find("#jefeTMXUser").val(-1);
		event.closest('.rowUsuario').find("#jefeTMXUser").change();
		
		console.log(data);
	});
	
	$(this).closest('td').find(".textArea").text($(this).find('option:selected').text());
	
	if(isManager == 1) {
		$(this).closest('.rowUsuario').find("#relevo").removeClass('d-none');
	}
	
	var isEjecutivo = $(this).closest('.rowUsuario').find('#isEjecutivo').val();
	
	if(isEjecutivo == 1) {
		$(this).closest('.rowUsuario').find("#cartera").removeClass('d-none');
	}
	
});

$("#tableUsuariosCrm").on('change', 'select.jefe', function() {
	
	$(this).closest('td').find(".textJefe").text($(this).find('option:selected').text());
});

$("#guardaPersonal").click(function() {
	
	var dependientes = generaInfoDependientes();
	
	$.post(guardaPersonalURL, {
		userId: $('#modal-asignar-usuarios #userIdPersonal').val(),
		dependientes: JSON.stringify(dependientes)
	})
		.done(function(data) {
			
			var response = JSON.parse(data);
			
			if(response.code == 0) {
				$('#modal-asignar-usuarios').modal('hide');
				showMessageSuccess('.navbar', response.msg, 0);
				
				var usuariosActualizados = JSON.parse(response.usuarios);
				
				$.each(usuariosActualizados, function(key, value){
					
					console.log(value);
					
					$("#" + value._userId).find("#jefeTMXUser").val(value._jefe);
					$("#" + value._userId).find("#jefeTMXUser").change();
				});
			}
			else {
				showMessageError('#modal-asignar-usuarios .modal-header', response.msg, 0);
			}
			
			console.log(data);
		});
});

function generaInfoDependientes() {
	
	var arrayInfo = [];
	
	var tablaPersonal = $('#tableAsignarUsuarios').DataTable();
	
	$.each(tablaPersonal.$("input:checked"), function(key, value){
		
		/*
		var objAuxPersonal = new Object();
		objAuxPersonal.idDependiente = $(this).val();
		*/
		
		arrayInfo.push($(this).val());
	});
	
	return arrayInfo;
}

	
function mostrarUsuarios(userId) {
	
	$('#modal-usuarios').modal('hide');
	showLoader();
	
	$.post(verUsuariosURL, {
		userId: userId
	})
		.done(function(data) {
		
			var response = JSON.parse(data);
			var tabla = $("#tableVerUsuarios").DataTable();
			
			tabla.clear();
			
			$.each(response, function a(i, persona) {
				tabla.row.add([
					persona.usuarioWindows,
					persona.nombreCompleto
				]);
			});
			
			tabla.destroy();
			
			$('#tableVerUsuarios').DataTable({
				pageLength: 10
			});
			
			hideLoader();
			$('#modal-usuarios').modal('show');
		});
}

function agregarUsuarios(userId) {
	$('#modal-asignar-usuarios').modal('hide');
	showLoader();
	$.post(obtenerUsuariosAsignarURL, {
		userId: userId,
		areaId: $("#"+userId).find("#areaTMXUser").val()
	})
			.done(
					function(data) {
						//console.log(data);
						var response = JSON.parse(data);
						var tabla = $('#tableAsignarUsuarios').DataTable();
						
						tabla.clear();
						
						$.each(response, function a(i, persona) {
							tabla.row.add([
								'<div class=\"form-check form-check-inline\"><input type=\"checkbox\" class=\"form-check-input\" id=\"agregaUsuarios-'+i+'\" value="' + persona.idUsuario + '" ' + (persona.dependiente ? 'checked' : '') + '><label class=\"form-check-label\" for=\"agregaUsuarios-'+i+'\">&nbsp;</label></div>',
								persona.usuarioWindows,
								persona.nombreCompleto
							]);
						});
						
						tabla.destroy();
						
						$('#tableAsignarUsuarios').DataTable({
							pageLength: 10
						});
						
						hideLoader();
						$('#modal-asignar-usuarios').modal('show');
					});
	
	$('#modal-asignar-usuarios #userIdPersonal').val(userId);
}

$(".textPerfil").click(function(){
	
	$(this).closest('td').find('#modal-perfiles').modal('show');
});

$(".textArea").click(function(){
	
	$(this).closest('td').find('#modal-areas').modal('show');
});

$(".textJefe").click(function(){
	
	$(this).closest('td').find('#modal-jefe').modal('show');
});


function selectDestroy(objeto, enabled) {
    $(objeto).prop("disabled", enabled);
    $(objeto).materialSelect('destroy');
    $(objeto).materialSelect();
}