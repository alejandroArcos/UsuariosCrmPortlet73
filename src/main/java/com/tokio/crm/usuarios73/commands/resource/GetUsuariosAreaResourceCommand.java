package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;
import com.tokio.crm.usuarios73.beans.PersonaTMX;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property =
			{
				"javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
				"mvc.command.name=/crm/usuarios/getUsuariosArea"
			},
		service = MVCResourceCommand.class
	)

public class GetUsuariosAreaResourceCommand extends BaseMVCResourceCommand {

	@Reference
	User_CrmLocalService _User_CrmLocalService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		Gson gson = new Gson();
		
		int idArea = ParamUtil.getInteger(resourceRequest, "idArea");
		
		System.out.println(idArea);
		
		List<User_Crm> usuariosAux = _User_CrmLocalService.getUsers_CrmByArea(idArea);
		
		
		List<User_Crm> usersCrmRelevoManager = _User_CrmLocalService.getUsers_CrmByPerfil(16);
		List<User_Crm> usersCrmRelevoManagerTI = _User_CrmLocalService.getUsers_CrmByPerfil(19);
		
		
		List<PersonaTMX> usuarios = new ArrayList<>();
		
		for(User_Crm u : usuariosAux) {
			
			User usuarioAux = UserLocalServiceUtil.getUser(u.getUserId());
			
			PersonaTMX personaAux = new PersonaTMX();
			
			personaAux.setIdUsuario(u.getUserId());
			personaAux.setNombreCompleto(usuarioAux.getFullName());
			personaAux.setPerfil(u.getPerfilId());
			
			if(u.getAccesoCRM()) {
				usuarios.add(personaAux);
			}
		}
		
		/*
		for(User_Crm u : usersCrmRelevoManager) {
			
			User usuarioAux = UserLocalServiceUtil.getUser(u.getUserId());
			
			PersonaTMX personaAux = new PersonaTMX();
			
			personaAux.setIdUsuario(u.getUserId());
			personaAux.setNombreCompleto(usuarioAux.getFullName());
			personaAux.setPerfil(u.getPerfilId());
			
			if(u.getAccesoCRM()) {
				usuarios.add(personaAux);
			}
		}

		for(User_Crm u : usersCrmRelevoManagerTI) {
			
			User usuarioAux = UserLocalServiceUtil.getUser(u.getUserId());
			
			PersonaTMX personaAux = new PersonaTMX();
			
			personaAux.setIdUsuario(u.getUserId());
			personaAux.setNombreCompleto(usuarioAux.getFullName());
			personaAux.setPerfil(u.getPerfilId());
			
			if(u.getAccesoCRM()) {
				usuarios.add(personaAux);
			}
		}
		*/
		
		JsonObject response = new JsonObject();
		
		response.addProperty("manager", getManagerPerfil(idArea));
		response.addProperty("usuarios", gson.toJson(usuarios));
		
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(gson.toJson(response));
	}
	
	public int getManagerPerfil(int idArea) {
		
		
		switch(idArea) {
			
			case 1:
				return 16;
			case 2:
				return 11;
			case 3:
				return 16;
			case 4:
				return 14;
			case 5:
				return 17;
			case 6:
				return 18;
			case 7:
				return 10;
			case 8:
				return 19;
			case 9:
				return 13;
			case 10:
				return 12;
			case 11:
				return 21;
			default:
				return 19;
		}
	}

}
