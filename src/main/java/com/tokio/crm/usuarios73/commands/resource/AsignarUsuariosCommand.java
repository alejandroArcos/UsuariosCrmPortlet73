package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.tokio.crm.servicebuilder73.model.Personal_A_Cargo;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.Personal_A_CargoLocalServiceUtil;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;
import com.tokio.crm.usuarios73.beans.PersonaTMX;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRM,
		"mvc.command.name=/crm/usuarios/obtenerUsuariosAsignar" }, service = MVCResourceCommand.class)

public class AsignarUsuariosCommand extends BaseMVCResourceCommand {

	@Reference
	User_CrmLocalService _User_CrmLocalService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		Gson gson = new Gson();
		
		int userId = ParamUtil.getInteger(resourceRequest, "userId");
		int areaId = ParamUtil.getInteger(resourceRequest, "areaId");
		
		List<Personal_A_Cargo> listadoPersonas = Personal_A_CargoLocalServiceUtil.getPersonal_A_CargoByUserId(userId);
		
		long companyId = CompanyThreadLocal.getCompanyId();
		
		List<PersonaTMX> listaPersonas = new ArrayList<>();
		
		try {
			User_Crm usuario = _User_CrmLocalService.getUser_Crm(userId);
		
			//List<User> usuarios = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			List<User_Crm> usuariosCrm = _User_CrmLocalService.getUsers_CrmByArea(areaId);		
			
			for(User_Crm user : usuariosCrm) {
				
				/*if(user.getPerfilId() != usuario.getPerfilId()) { */
				
				if(user.getUserId() != usuario.getUserId()) {
				
					PersonaTMX persona = new PersonaTMX();
					
					User u = UserLocalServiceUtil.getUser(user.getUserId());
					
					/*
					for(Personal_A_Cargo p : listadoPersonas) {
						if(p.getDependientesId() == u.getUserId()) {
							persona.setDependiente(true);
						}
					}
					*/
					
					if(user.getJefe() == usuario.getUserId()) {
						persona.setDependiente(true);
					}
					
					persona.setIdUsuario((int)u.getUserId());
					persona.setNombreCompleto(u.getFullName());
					persona.setUsuarioWindows(u.getScreenName());
					
					
					listaPersonas.add(persona);
				}
				/*}*/
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		String responseString = gson.toJson(listaPersonas);
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(responseString);
	}

}
