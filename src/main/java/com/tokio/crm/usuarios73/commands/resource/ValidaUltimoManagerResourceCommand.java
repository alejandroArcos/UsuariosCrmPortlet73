package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.PrintWriter;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
		"mvc.command.name=/crm/usuarios/validaManager"
	},
	service = MVCResourceCommand.class
)

public class ValidaUltimoManagerResourceCommand extends BaseMVCResourceCommand {

	@Reference
	User_CrmLocalService _User_CrmLocalService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		int userId = ParamUtil.getInteger(resourceRequest, "userId");
		
		Gson gson = new Gson();
		
		User_Crm manager = _User_CrmLocalService.getUser_Crm(userId);
		
		int auxPerfil = manager.getPerfilId();
		
		List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByPerfil(auxPerfil);
		
		int auxUsuarios = 0;
		
		for(User_Crm managerAux : managers) {
			
			if(managerAux.getArea() == manager.getArea()) {
				if(managerAux.getUserId() != manager.getUserId() && managerAux.getAccesoCRM()) {
					auxUsuarios++;
				}
			}
		}
		
		
		JsonObject response = new JsonObject();
		
		if(auxUsuarios > 0) {
			response.addProperty("code", 0);
			response.addProperty("msg", "Ok");
		}
		else {
			response.addProperty("code", 2);
			response.addProperty("msg", "No se puede dar de baja ya que es el único Manager del área,"
					+ " favor de dar de alta al nuevo manager antes de dar de baja.");
		}
		
		String responseString = gson.toJson(response);
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(responseString);
		
	}

}
