package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.tokio.crm.usuarios73.beans.PersonaTMX;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, property = { "javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
		"mvc.command.name=/crm/usuarios/obtenerUsuariosTmx" }, service = MVCResourceCommand.class)

public class ObtenerUsuarioADCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		
		long companyId = CompanyThreadLocal.getCompanyId();
		
		List<User> usuarios = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		List<PersonaTMX> usuariosCRM = new ArrayList<>();
		
		for(User u : usuarios) {
			
			System.out.println(u.getScreenName() + " - " + u.getCompanyId() + " - " + u.getExpandoBridge().getAttribute("Oficina"));
			
			PersonaTMX persona = new PersonaTMX();
			persona.setAccesoCrm(validaAccesoCrm(u.getUserId()));
			persona.setUsuarioWindows(u.getScreenName());
			persona.setNombreCompleto(u.getFullName());
			persona.setCorreo(u.getEmailAddress());
			//persona.setArea("Ejecutivo Ventas");
			
			usuariosCRM.add(persona);
		}

		Gson gson = new Gson();
		JsonObject response = new JsonObject();
		response.add("personas", gson.fromJson(gson.toJson(usuariosCRM, ArrayList.class), JsonArray.class));
		
		/*
		PersonaService ps = new PersonaService();
		PersonasCRMResponse response = ps.obtenerPersonasAD();
		*/
		
		String responseString = gson.toJson(response);
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(responseString);

	}
	
	public boolean validaAccesoCrm(long userId) {
		
		return false;
	}

}
