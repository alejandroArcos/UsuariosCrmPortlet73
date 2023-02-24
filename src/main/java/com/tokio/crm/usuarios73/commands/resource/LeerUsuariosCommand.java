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

import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;
import com.tokio.crm.usuarios73.beans.PersonaTMX;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
		"mvc.command.name=/crm/usuarios/verUsuarios" }, service = MVCResourceCommand.class)

public class LeerUsuariosCommand extends BaseMVCResourceCommand {

	@Reference
	User_CrmLocalService _User_CrmLocalService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		int userId = ParamUtil.getInteger(resourceRequest, "userId");
		
		Gson gson = new Gson();
				
		long companyId = CompanyThreadLocal.getCompanyId();
		
		//List<Personal_A_Cargo> listadoPersonas = Personal_A_CargoLocalServiceUtil.getPersonal_A_CargoByUserId(userId);
		
		List<User_Crm> listadoPersonas = _User_CrmLocalService.getUsers_CrmByJefe(userId);
		
		List<PersonaTMX> listaPersonas = new ArrayList<PersonaTMX>();
		
		for(User_Crm dependiente : listadoPersonas) {
			
			PersonaTMX persona = new PersonaTMX();
			
			User u = UserLocalServiceUtil.getUser(dependiente.getUserId());
			
			persona.setIdUsuario((int)u.getUserId());
			persona.setNombreCompleto(u.getFullName());
			persona.setUsuarioWindows(u.getScreenName());
			
			listaPersonas.add(persona);
		}
		
		String responseString = gson.toJson(listaPersonas);
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(responseString);
		
	}

}
