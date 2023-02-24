package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.PrintWriter;
import java.util.ArrayList;
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
	property =
		{
			"javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
			"mvc.command.name=/crm/usuarios/guardaPersonalACargo"
		},
	service = MVCResourceCommand.class
)

public class GuardaPersonalACargoResourceCommand extends BaseMVCResourceCommand {

	@Reference
	User_CrmLocalService _User_CrmLocalService;

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		
		Gson gson = new Gson();
		
		int userId = ParamUtil.getInteger(resourceRequest, "userId");
		String auxDependientesString = ParamUtil.getString(resourceRequest, "dependientes");
		
		JsonArray auxDependientesArray = gson.fromJson(auxDependientesString, JsonArray.class);
		
		System.out.println("userId: " + userId);
		System.out.println("String dependientes: " + auxDependientesString);
		
		/*
		List<Personal_A_Cargo> personalTemp = Personal_A_CargoLocalServiceUtil.getPersonal_A_CargoByUserId(userId);
		
		for(Personal_A_Cargo auxEntrada : personalTemp) {
			Personal_A_CargoLocalServiceUtil.deletePersonal_A_Cargo(auxEntrada);
		}
		*/
		
		List<User_Crm> usuariosDependientes = _User_CrmLocalService.getUsers_CrmByJefe(userId);
		ArrayList<User_Crm> usuariosEliminados = new ArrayList<>();
		
		for(User_Crm aux : usuariosDependientes) {
			aux.setJefe(-1);
			_User_CrmLocalService.updateUser_Crm(aux);
			usuariosEliminados.add(aux);
		}
		
		ArrayList<User_Crm> usuariosActualizados = new ArrayList<>();
		
		
		JsonObject response = new JsonObject();
		
		try {
			
			for(int i = 0; i < auxDependientesArray.size(); i++) {
				int dependienteId = auxDependientesArray.get(i).getAsInt();
				
				//Personal_A_CargoLocalServiceUtil.addPersonal_A_Cargo(userId, dependienteId);
				
				User_Crm userAux = _User_CrmLocalService.getUser_Crm(dependienteId);
				
				userAux.setJefe(userId);
				
				_User_CrmLocalService.updateUser_Crm(userAux);
				
				usuariosActualizados.add(userAux);
			}
			
			for(User_Crm auxElim : usuariosEliminados) {
				
				boolean actualizado = false;
				
				for(User_Crm auxActualizado : usuariosActualizados) {
					if(auxActualizado.getUserId() == auxElim.getUserId()) {
						actualizado = true;
					}
				}
				
				if(!actualizado) {
					usuariosActualizados.add(auxElim);
				}
			}
			
			response.addProperty("code", 0);
			response.addProperty("msg", "La información se ha actualizado exitosamente");
			response.addProperty("usuarios", gson.toJson(usuariosActualizados));
		}
		catch(Exception e) {
			response.addProperty("code", 2);
			response.addProperty("msg", "Ocurrió un error durante la actualización de la información");
		}
		
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(gson.toJson(response));
	}

}
