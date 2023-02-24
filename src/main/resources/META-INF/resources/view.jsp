<%@ include file="./init.jsp"%>
<jsp:include page="modales.jsp" />

<portlet:actionURL var="actionUsuarios" name="/crm/actionUsuarios" />
<portlet:actionURL var="actionGuardarUsuarios" name="/crm/usuarios/agregaUsuario" />

<portlet:resourceURL id="/crm/usuarios/obtenerUsuariosAsignar" var="obtenerUsuariosAsignarURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/usuarios/obtenerUsuariosRelevo" var="obtenerUsuariosRelevoURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/usuarios/verUsuarios" var="verUsuariosURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/usuarios/obtenerUsuariosTmx" var="obtenerUsuariosTmxURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/usuarios/guardaPersonalACargo" var="guardaPersonalURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/usuarios/getPerfilesArea" var="obtenerPerfilesAreaURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/usuarios/getUsuariosArea" var="obtenerUsuariosAreaURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/usuarios/validaManager" var="validaManagerURL" cacheability="FULL" />


<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css?v=${version}&browserId=other" />

<div id="customAlertJS"></div>
<section class="upper-case-all UsuariosCrmPortlet">
	<div class="section-heading">
		<div class="container-fluid">
			<div class="row">
				<div class="col-md-12">
					<p class="font-weight-bold h1-responsive center-block mt-4 mb-4 animated zoomInDown animation-delay-5">
						<liferay-ui:message key="usuarioscrm.title" />
					</p>
				</div>
			</div>
			<div class="form-wrapper">
				<div class="row">
					<div class="col">
						<div class="row">
							<div class="col">
								<div class="md-form form-group">
									<input id="usuarioTMX" type="text" name="usuarioTXM" value="${usuario}" class="form-control">
									<label for="usuarioTMX" style="color: #02579b;">
										<liferay-ui:message key="usuarioscrm.filter.usuario" />
									</label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<select name="areaTMX" class="mdb-select" id="areaTMX">
										<option value="-1">Selecciona Area</option>
										<c:forEach items="${listaAreas}" var="option">
											<option value="${option.id}" ${ option.id ==  area ? 'selected' : ''}>${option.valor}</option>
										</c:forEach>
									</select>
									<label for="areaTMX" style="color: #02579b;">
										<liferay-ui:message key="usuarioscrm.filter.area" />
									</label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<select name="perfilTMX" class="mdb-select" id="perfilTMX">
										<option value="-1">Seleccionar Perfil</option>
										<c:forEach items="${listaPerfiles}" var="option">
											<option value="${option.perfilId}" ${ option.perfilId ==  perfil ? 'selected' : ''}>${option.descripcion}</option>
										</c:forEach>
									</select>
									<label for="perfilTMX" style="color: #02579b;">
										<liferay-ui:message key="usuarioscrm.filter.perfil" />
									</label>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<form action="${actionUsuarios}" method="post" id="buscar_form">
							<input type="hidden" id="area" name="area" />
							<input type="hidden" id="usuario" name="usuario" />
							<input type="hidden" id="perfil" name="perfil" />
						</form>
						<button class="btn btn-blue pull-center" id="buscarPersonasCrm">
							<liferay-ui:message key="usuarioscrm.filter.buscar" />
						</button>
					</div>
				</div>
			</div>
			<div class="table-wrapper">
				<table class="table data-table table-bordered"
					   style="width: 100%;" id="tableUsuariosCrm">
					<!--  tablaAgente -->
					<thead style="color: #FFFFFF; background-color: #43aee9">
					<tr>
						<th><liferay-ui:message key="usuarioscrm.table.title.acceso" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.usuario" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.nombre" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.correo" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.area" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.perfil" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.jefedirecto" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.asignar" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.oficina" /></th>
						<th><liferay-ui:message key="usuarioscrm.table.title.cartera" /></th>
					</tr>
					</thead>
					<tbody>
					<c:forEach items="${usuarios}" var="user">
						<tr id="${user.idUsuario}" class="rowUsuario">
							<td >
								<div class="switch" style="min-width: 110px">
									<label>No<input class="checkAcceso" id="chktoggle-persona" type="checkbox" ${user.accesoCrm ? 'checked' : ''} ${ (user.idUsuario == 405176 || user.idUsuario == 408292) ? 'disabled' : '' }><span class="lever"></span>Si</label>
								</div>
							</td>
							<td>${user.usuarioWindows}</td>
							<td>${user.nombreCompleto}</td>
							<td>${user.correo}</td>
							<td>
									<span id="areaTMxUserText">
										<c:forEach items="${listaAreas}" var="option">
											<c:if test="${ user.area == option.id }">
												<a style="color: blue; cursor: pointer; text-decoration: underline;"
												   class="textArea">
														${option.valor}
												</a>
											</c:if>
										</c:forEach>
										<c:if test="${ user.area == -1 || user.area == 0}">
											<a style="color: blue; cursor: pointer; text-decoration: underline;"
											   class="textArea">
												Seleccionar Una Opcion
											</a>
										</c:if>
									</span>
								<div class="modal" id="modal-areas" tabindex="-1" role="dialog" aria-labelledby="usuariosLabel" aria-hidden="true">
									<div class="modal-dialog modal-dialog-centered" role="document">
										<div class="modal-content">
											<div class="modal-header">
												<h5 class="modal-title" id="usuariosLabel">
													<liferay-ui:message key="usuarioscrm.table.title.area" />
												</h5>
												<button type="button" class="close" data-dismiss="modal"
														aria-label="Close">
													<span aria-hidden="true">&times;</span>
												</button>
											</div>
											<div class="modal-body">
												<select name="areaTMXUser" class="mdb-select area" id="areaTMXUser">
													<option value="-1">Selecciona Area</option>
													<c:forEach items="${listaAreas}" var="option">
														<option value="${option.id}" ${ user.area == option.id ? 'selected' : '' } >${option.valor}</option>
													</c:forEach>
												</select>
											</div>
											<div class="modal-footer">
												<button class="btn btn-blue pull-center" data-dismiss="modal">
													Aceptar
												</button>
											</div>
										</div>
									</div>
								</div>
							</td>
							<td>
									<span id="perfilTmxUserText">
										<c:forEach items="${listaPerfiles}" var="option">
											<c:if test="${ user.perfil == option.perfilId }">
												<a style="color: blue; cursor: pointer; text-decoration: underline;"
												   class="textPerfil">
														${option.descripcion}
												</a>
											</c:if>
										</c:forEach>
										<c:if test="${ user.perfil == -1 || user.perfil == 0}">
											<a style="color: blue; cursor: pointer; text-decoration: underline;"
											   class="textPerfil">
												Seleccionar Una Opcion
											</a>
										</c:if>
									</span>
								<div class="modal" id="modal-perfiles" tabindex="-1" role="dialog" aria-labelledby="usuariosLabel" aria-hidden="true">
									<div class="modal-dialog modal-dialog-centered" role="document">
										<div class="modal-content">
											<div class="modal-header">
												<h5 class="modal-title" id="usuariosLabel">
													<liferay-ui:message key="usuarioscrm.table.title.perfil" />
												</h5>
												<button type="button" class="close" data-dismiss="modal"
														aria-label="Close">
													<span aria-hidden="true">&times;</span>
												</button>
											</div>
											<div class="modal-body">
												<input type="hidden" id="isManager" name="isManager" value="${user.isManager}" />
												<input type="hidden" id="isEjecutivo" name="isEjecutivo" value="${user.isEjecutivo}" />
												<input type="hidden" id="inputPerfil" name="inputPerfil" value="${user.perfil}" />
												<select name="perfilTMXUser" class="mdb-select perfil" id="perfilTMXUser">
													<option value="-1">Selecciona Perfil</option>
													<c:forEach items="${listaPerfiles}" var="option">
														<c:if test="${(user.area == option.areaId) || (user.area == 3 ? (option.areaId == 1 ? true : false) : false)}">
															<option value="${option.perfilId}" ${ user.perfil == option.perfilId ? 'selected' : '' } >${option.descripcion}</option>
														</c:if>
													</c:forEach>
												</select>
											</div>
											<div class="modal-footer">
												<button class="btn btn-blue pull-center" data-dismiss="modal">
													Aceptar
												</button>
											</div>
										</div>
									</div>
								</div>
							</td>
							<td>
									<span id="jefeTmxUserText">
										<c:forEach items="${listaUsuarios}" var="option">
											<c:if test="${ user.jefeDirecto == option.idUsuario }">
												<a style="color: blue; cursor: pointer; text-decoration: underline;"
												   class="textJefe">
														${option.nombreCompleto}
												</a>
											</c:if>
										</c:forEach>
										<c:if test="${ user.jefeDirecto == -1 || user.jefeDirecto == 0}">
											<a style="color: blue; cursor: pointer; text-decoration: underline;"
											   class="textJefe">
												Seleccionar Una Opcion
											</a>
										</c:if>
									</span>
								<div class="modal" id="modal-jefe" tabindex="-1" role="dialog" aria-labelledby="usuariosLabel" aria-hidden="true">
									<div class="modal-dialog modal-dialog-centered" role="document">
										<div class="modal-content">
											<div class="modal-header">
												<h5 class="modal-title" id="usuariosLabel">
													<liferay-ui:message key="usuarioscrm.table.title.jefedirecto" />
												</h5>
												<button type="button" class="close" data-dismiss="modal"
														aria-label="Close">
													<span aria-hidden="true">&times;</span>
												</button>
											</div>
											<div class="modal-body">
												<select name="jefeTMXUser" class="mdb-select jefe" id="jefeTMXUser">
													<option value="-1">Selecciona Jefe</option>
													<c:forEach items="${listaUsuarios}" var="option">
														<c:if test="${((user.area == option.area) && (user.idUsuario != option.idUsuario) && (option.isManager == 1) && (option.accesoCrm))}">
															<option value="${option.idUsuario}" ${ user.jefeDirecto == option.idUsuario ? 'selected' : '' }>${option.nombreCompleto}</option>
														</c:if>
													</c:forEach>
												</select>
											</div>
											<div class="modal-footer">
												<button class="btn btn-blue pull-center" data-dismiss="modal">
													Aceptar
												</button>
											</div>
										</div>
									</div>
								</div>
							</td>
							<td>
								<div class="md-form form-group">
										<span id="analista" class='d-none'>
											<select name="manager-select-'+i+'"class="mdb-select">
												<option value="-1">Seleccionar Manager</option>
												<option>Luis Ortiz</option>
												<option>Jaime Duarte</option>
											</select>
										</span>
									<span id="manager" class='d-none'>
											<button class="btn btn-usuarios verUsuarios waves-effect waves-light" onclick="mostrarUsuarios(${user.idUsuario});">
												<i class="fa fa-users" aria-hidden="true"></i>
											</button>
											<button class="btn btn-usuarios agregarUsuarios waves-effect waves-light"onclick="agregarUsuarios(${user.idUsuario});">
												<i class="fa fa-user-plus" aria-hidden="true"></i>
											</button>
										</span>
									<span id="relevo" class='d-none'>
											<select name="relevoBaja" class="mdb-select" id="relevoBaja">
												<option value="-1">Selecciona Relevo</option>
												<c:forEach items="${listaUsuarios}" var="option">
													<c:if test="${(user.area == option.area) && (user.idUsuario != option.idUsuario)}">
														<option value="${option.idUsuario}">${option.nombreCompleto}</option>
													</c:if>
												</c:forEach>
											</select>
										</span>
								</div>
							</td>
							<td>
								<select name="oficinaTmxUser" class="mdb-select" id="oficinaTmxUser">
									<option value="-1">Selecciona Oficina</option>
									<c:forEach items="${listaOficinas}" var="option">
										<option value="${option.catalogoDetalleId}" ${ user.oficina == option.catalogoDetalleId ? 'selected' : '' }>${option.descripcion}</option>
									</c:forEach>
								</select>
							</td>
							<td>
									<span id="cartera" class="d-none">
										<select name="cambioCartera" class="mdb-select" id="cambioCartera">
										 	<option value="-1">Selecciona Relevo</option>
											<c:forEach items="${listaUsuarios}" var="option">
												<c:if test="${(user.perfil == option.perfil) && (user.idUsuario != option.idUsuario) && (option.accesoCrm) && (user.area == option.area)}">
													<option value="${option.idUsuario}">${option.nombreCompleto}</option>
												</c:if>
											</c:forEach>
										</select>
									</span>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				<div class="row ${ permisoEscritura ? '' : 'd-none' }" id="guardarTodo">
					<div class="col text-center">
						<form action="${actionGuardarUsuarios}" method="post" id="guardar_form">
							<input type="hidden" id="usuarios" name="usuarios" />
						</form>
						<button class="btn btn-blue pull-center" id="guardarPersonasCrm">
							<liferay-ui:message key="usuarioscrm.table.guardar" />
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<script src="<%=request.getContextPath()%>/js/main.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/tooltip/popper.min.js?v=${version}"></script>
<script src="<%=request.getContextPath()%>/js/tooltip/tippy-bundle.umd.js?v=${version}"></script>

<script>
	var obtenerUsuariosAsignarURL = '${obtenerUsuariosAsignarURL}';
	var verUsuariosURL = '${verUsuariosURL}';
	var obtenerUsuariosTmxURL = '${obtenerUsuariosTmxURL}';
	var agregaUsuarioURL = '${agregaUsuarioURL}';
	var guardaPersonalURL = '${guardaPersonalURL}';
	var obtenerPerfilesAreaURL = '${obtenerPerfilesAreaURL}';
	var obtenerUsuariosAreaURL = '${obtenerUsuariosAreaURL}';
	var obtenerUsuariosRelevoURL = '${obtenerUsuariosRelevoURL}';
	var validaManagerURL = '${validaManagerURL}';

	var permisoEscritura = ${permisoEscritura};

	var error = 0;

	var idArea = "${idArea}";
	var isManager = ${isManager};
	var isManagerVentas = ${isManagerVentas};
	let listaOficinas = "${listaOficinas}";
</script>
