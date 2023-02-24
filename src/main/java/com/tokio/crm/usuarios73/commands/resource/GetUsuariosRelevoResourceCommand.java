package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
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

@Component(immediate = true, property = { "javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
"mvc.command.name=/crm/usuarios/obtenerUsuariosRelevo" }, service = MVCResourceCommand.class)

public class GetUsuariosRelevoResourceCommand extends BaseMVCResourceCommand {

	@Reference
	User_CrmLocalService _User_CrmLocalService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		Gson gson = new Gson();
		
		int userId = ParamUtil.getInteger(resourceRequest, "userId");
		int areaId = ParamUtil.getInteger(resourceRequest, "areaId");
		
		User_Crm usuario = _User_CrmLocalService.getUser_Crm(userId);
		
		List<User_Crm> usersCrmRelevo = _User_CrmLocalService.getUsers_CrmByPerfil(usuario.getPerfilId());
		
		
		
		List<PersonaTMX> listaPersonas = new ArrayList<>();
		
		for(User_Crm user : usersCrmRelevo) {
			
			PersonaTMX persona = new PersonaTMX();
			
			User u = UserLocalServiceUtil.getUser(user.getUserId());
			
			persona.setIdUsuario((int)u.getUserId());
			persona.setNombreCompleto(u.getFullName());
			persona.setUsuarioWindows(u.getScreenName());
			
			if((usuario.getArea() == user.getArea()) && user.getAccesoCRM()) {
				listaPersonas.add(persona);
			}
		}
		
		if(listaPersonas.size() == 0) {
			
			List<User_Crm> usersCrmRelevoManager = _User_CrmLocalService.getUsers_CrmByPerfil(16);
			List<User_Crm> usersCrmRelevoManagerTI = _User_CrmLocalService.getUsers_CrmByPerfil(19);
			
			for(User_Crm user : usersCrmRelevoManager) {
				
				PersonaTMX persona = new PersonaTMX();
				
				User u = UserLocalServiceUtil.getUser(user.getUserId());
				
				persona.setIdUsuario((int)u.getUserId());
				persona.setNombreCompleto(u.getFullName());
				persona.setUsuarioWindows(u.getScreenName());
				
				if(user.getAccesoCRM()) {
					listaPersonas.add(persona);
				}
			}

			for(User_Crm user : usersCrmRelevoManagerTI) {
				
				PersonaTMX persona = new PersonaTMX();
				
				User u = UserLocalServiceUtil.getUser(user.getUserId());
				
				persona.setIdUsuario((int)u.getUserId());
				persona.setNombreCompleto(u.getFullName());
				persona.setUsuarioWindows(u.getScreenName());
				
				if(user.getAccesoCRM()) {
					listaPersonas.add(persona);
				}
			}
		}
		
		String responseString = gson.toJson(listaPersonas);
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(responseString);
	}

}
