package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.PrintWriter;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.tokio.crm.servicebuilder73.model.Perfil_Crm;
import com.tokio.crm.servicebuilder73.service.Perfil_CrmLocalService;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		immediate = true,
		property =
			{
				"javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
				"mvc.command.name=/crm/usuarios/getPerfilesArea"
			},
		service = MVCResourceCommand.class
	)

public class GetPerfilesAreaResourceCommand extends BaseMVCResourceCommand {
	@Reference
	Perfil_CrmLocalService _Perfil_CrmLocalService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		Gson gson = new Gson();
		
		int idArea = ParamUtil.getInteger(resourceRequest, "idArea");
		
		System.out.println(idArea);
		
		if(idArea == 3) {
			idArea = 1;
		}
		
		List<Perfil_Crm> perfiles = _Perfil_CrmLocalService.getPerfilesByArea(idArea);
		
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(gson.toJson(perfiles));
	}

}
