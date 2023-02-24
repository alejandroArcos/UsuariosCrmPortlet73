<%@ include file="./init.jsp"%>

<div class="modal" id="modal-usuarios" tabindex="-1" role="dialog" aria-labelledby="usuariosLabel" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="usuariosLabel">
					<liferay-ui:message key="usuarioscrm.usuarios.table.titleView" />
				</h5>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="form-wrapper">
					<div class="row">
						<div class="col">
							<table id="tableVerUsuarios"
								class="table simple-data-table table-striped table-bordered"
								style="width: 100%;">
								<thead>
									<tr>
										<th><liferay-ui:message
												key="usuarioscrm.usuarios.table.title.usuario" /></th>
										<th><liferay-ui:message
												key="usuarioscrm.usuarios.table.title.nombre" /></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
					<div class="row">
						<div class="col text-center">
							<button class="btn btn-blue pull-center" data-dismiss="modal">
								<liferay-ui:message key="usuarioscrm.usuarios.modal.regresar" />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal" id="modal-asignar-usuarios" tabindex="-1" role="dialog" aria-labelledby="usuariosLabel" aria-hidden="true">
	<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="usuariosLabel">
					<liferay-ui:message key="usuarioscrm.usuarios.table.titleAdd" />
				</h5>
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<div class="form-wrapper">
					<div class="row">
						<div class="col">
							<table id="tableAsignarUsuarios"
								class="table simple-data-table table-striped table-bordered"
								style="width: 100%;">
								<thead>
									<tr>
										<th><liferay-ui:message
												key="usuarioscrm.usuarios.table.title.seleccionar" /></th>
										<th><liferay-ui:message
												key="usuarioscrm.usuarios.table.title.usuario" /></th>
										<th><liferay-ui:message
												key="usuarioscrm.usuarios.table.title.nombre" /></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
					<div class="row">
						<div class="col text-center">
							<button class="btn btn-blue pull-center" data-dismiss="modal">
								<liferay-ui:message key="usuarioscrm.usuarios.modal.regresar" />
							</button>
						</div>
						<div class="col text-center">
							<button class="btn btn-blue pull-center" id="guardaPersonal">
								<liferay-ui:message key="usuarioscrm.usuarios.modal.guardar" />
							</button>
						</div>
					</div>
				</div>
				<input type="hidden" id="userIdPersonal" name="userIdPersonal" />
			</div>
		</div>
	</div>
</div>