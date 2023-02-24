package com.tokio.crm.usuarios73.commands.resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import com.tokio.crm.servicebuilder73.exception.NoSuchUser_CrmException;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.Permisos_Perfiles;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.Permisos_PerfilesLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;
import com.tokio.crm.usuarios73.beans.PersonaTMX;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import com.tokio.crm.usuarios73.utils.SendMail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(
		immediate = true,
		property =
			{
				"javax.portlet.init-param.copy-request-parameters=true",
				"javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
				"mvc.command.name=/crm/usuarios/agregaUsuario"
			},
		service = MVCActionCommand.class
)

public class GuardaUsuariosActionCommand extends BaseMVCActionCommand {
	
	private static final Log _log = LogFactoryUtil.getLog(GuardaUsuariosActionCommand.class);
	@Reference
	User_CrmLocalService _User_CrmLocalService;

	@Reference
	Permisos_PerfilesLocalService _Permisos_PerfilesLocalService;

	@Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;

	@Reference
	AgenteLocalService _AgenteLocalService;
	SendMail envioCorreos;

	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		
		HttpServletRequest originalRequest = PortalUtil
				.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
		
		Gson gson = new Gson();
		
		envioCorreos = new SendMail();
		
		String arregloUsuariosString = originalRequest.getParameter("usuarios");
		
		JsonArray arregloUsuariosJson = gson.fromJson(arregloUsuariosString, JsonArray.class);
		
		for(int i = 0; i < arregloUsuariosJson.size(); i++) {
			
			JsonObject objAux = (JsonObject) arregloUsuariosJson.get(i);
			
			int auxIdUser = objAux.get("idUsuario").getAsInt();
			boolean auxAcceso = objAux.get("acceso").getAsBoolean();
			int auxArea = objAux.get("area").getAsInt();
			int auxOficina = objAux.get("oficina").getAsInt();
			int auxPerfil = objAux.get("perfil").getAsInt();
			int auxJefe = objAux.get("jefe").getAsInt();
			int auxRelevo = objAux.get("relevo").getAsInt();
			int auxRelevoCartera = objAux.get("relevoCartera").getAsInt();
			
			boolean actualizar = false;
			String actualizaciones = "";
			
			try {
				
				User_Crm usuarioCrm = _User_CrmLocalService.getUser_Crm(auxIdUser);
				
				 
				
				if(auxAcceso != usuarioCrm.getAccesoCRM()) {
					if(auxAcceso) {
						usuarioCrm.setAccesoCRM(auxAcceso);
						usuarioCrm.setArea(auxArea);
						usuarioCrm.setOficina(auxOficina);
						usuarioCrm.setPerfilId(auxPerfil);
						usuarioCrm.setJefe(auxJefe);
						
						try {
							
							ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
						            .getAttribute(WebKeys.THEME_DISPLAY);
						
							System.out.println(themeDisplay.getScopeGroup().getName());
							
							Group g = GroupLocalServiceUtil.getGroup(themeDisplay.getScopeGroup().getGroupId());
							GroupLocalServiceUtil.addUserGroup(auxIdUser, g);
						}
						catch(Exception group) {
							group.printStackTrace();
						}
						
						List<Role> rolesAux = RoleLocalServiceUtil.getUserRoles(auxIdUser);
						
						if(validaUserAdmin(auxIdUser)) {
							try {
								RoleLocalServiceUtil.deleteUserRoles(auxIdUser, rolesAux);
							}
							catch(Exception roleExcepcion){
								_log.info("No se pueden borrar roles");
								roleExcepcion.printStackTrace();
							}
						}
						
						List<Permisos_Perfiles> permisos = _Permisos_PerfilesLocalService.getPermisosByPerfilId(auxPerfil);
						
						for(Permisos_Perfiles p : permisos) {
							
							Catalogo_Detalle entrada = _Catalogo_DetalleLocalService.getCatalogo_Detalle(p.getModuloId());
							
							if(!p.getRestringido()) {
								RoleLocalServiceUtil.addUserRole(auxIdUser, entrada.getValorN());
							}
						}
						
						sendEmailAlta(usuarioCrm);
					}
					else {
						usuarioCrm.setAccesoCRM(auxAcceso);
						
						List<User_Crm> personal = _User_CrmLocalService.getUsers_CrmByJefe(auxIdUser);
						
						if(personal != null) {
						
							for(User_Crm userAux : personal) {
								
								if(auxRelevo != -1) {
									
									userAux.setJefe(auxRelevo);
								
									_User_CrmLocalService.updateUser_Crm(userAux);
								}
							}
						}
						
						/*
						List<Personal_A_Cargo> personal = Personal_A_CargoLocalServiceUtil.getPersonal_A_CargoByUserId(auxIdUser);
						
						if(personal != null) {
							
							for(Personal_A_Cargo auxPersonal : personal) {
								try {
									Personal_A_Cargo nuevaEntrada = Personal_A_CargoLocalServiceUtil.addPersonal_A_Cargo(auxRelevo, auxPersonal.getDependientesId());
								}
								catch(Exception dependienteDuplicado) {
									_log.error("El dependiente ya está a cargo del relevo");
								}
								Personal_A_CargoLocalServiceUtil.deletePersonal_A_Cargo(auxPersonal);
							}
						}
						*/
						
						if(auxRelevoCartera != -1) {
							
							List<Agente> agentesEjecutivo = _AgenteLocalService.findByEjecutivoId(auxIdUser);
							
							if(agentesEjecutivo != null) {
							
								for(Agente agenteAux : agentesEjecutivo) {
									
									agenteAux.setEjecutivo(auxRelevoCartera);
									
									_AgenteLocalService.updateAgente(agenteAux);
								}
							}
							
							List<Agente> agenteCreacion = _AgenteLocalService.findByUserCreacion(auxIdUser);
							
							if(agenteCreacion != null) {
							
								for(Agente agenteAux : agenteCreacion) {
									
									agenteAux.setUserCreacion(auxRelevoCartera);
									
									_AgenteLocalService.updateAgente(agenteAux);
								}
							}
						}
						
						sendEmailBaja(usuarioCrm);
					}
					
					try {
						_User_CrmLocalService.updateUser_Crm(usuarioCrm);
					} catch (Exception altaBaja) {
						_log.error(altaBaja.getMessage());
					}
				}
				else {
					
					if(usuarioCrm.getAccesoCRM()) {
						try {
							
							if(auxArea != usuarioCrm.getArea()) {
								actualizar = true;
								actualizaciones += "Área actualizada<br/>";
								usuarioCrm.setArea(auxArea);
								
								List<User_Crm> personal = _User_CrmLocalService.getUsers_CrmByJefe(auxIdUser);
								
								if(personal != null) {
								
									for(User_Crm userAux : personal) {
										/*
										if(auxRelevo != -1) {
											
											userAux.setJefe(auxRelevo);
										
											_User_CrmLocalService.updateUser_Crm(userAux);
										}
										*/
									}
								}
							}
							
							if(auxOficina != usuarioCrm.getOficina()) {
								actualizar = true;
								actualizaciones += "Oficina actualizada<br/>";
								usuarioCrm.setOficina(auxOficina);
							}
							
							if(auxPerfil != usuarioCrm.getPerfilId()) {
								actualizar = true;
								actualizaciones += "Perfil actualizado<br/>";
								usuarioCrm.setPerfilId(auxPerfil);
								
								List<Role> rolesAux = RoleLocalServiceUtil.getUserRoles(auxIdUser);
								
								if(validaUserAdmin(auxIdUser)) {
									RoleLocalServiceUtil.deleteUserRoles(auxIdUser, rolesAux);
								}
								
								List<Permisos_Perfiles> permisos = _Permisos_PerfilesLocalService.getPermisosByPerfilId(auxPerfil);
								
								for(Permisos_Perfiles p : permisos) {
									
									Catalogo_Detalle entrada = _Catalogo_DetalleLocalService.getCatalogo_Detalle(p.getModuloId());
									
									if(!p.getRestringido()) {
										RoleLocalServiceUtil.addUserRole(auxIdUser, entrada.getValorN());
									}
								}
								
								List<User_Crm> personal = _User_CrmLocalService.getUsers_CrmByJefe(auxIdUser);
								
								if(personal != null) {
								
									for(User_Crm userAux : personal) {
										/*
										if(auxRelevo != -1) {
										
											userAux.setJefe(auxRelevo);
										
											_User_CrmLocalService.updateUser_Crm(userAux);
										}
										*/
									}
								}
							}
							
							if(auxJefe != usuarioCrm.getJefe()) {
									actualizar = true;
									actualizaciones += "Jefe Directo actualizado<br/>";
									usuarioCrm.setJefe(auxJefe);
							}
							
							if(auxRelevoCartera != -1) {
								
								List<Agente> agentesEjecutivo = _AgenteLocalService.findByEjecutivoId(auxIdUser);
								
								if(agentesEjecutivo != null) {
								
									for(Agente agenteAux : agentesEjecutivo) {
										
										agenteAux.setEjecutivo(auxRelevoCartera);
										
										_AgenteLocalService.updateAgente(agenteAux);
									}
								}
								
								List<Agente> agenteCreacion = _AgenteLocalService.findByUserCreacion(auxIdUser);
								
								if(agenteCreacion != null) {
								
									for(Agente agenteAux : agenteCreacion) {
										
										agenteAux.setUserCreacion(auxRelevoCartera);
										
										_AgenteLocalService.updateAgente(agenteAux);
									}
								}
							}
							
							
							if(actualizar) {
								_User_CrmLocalService.updateUser_Crm(usuarioCrm);
								sendEmailActualizacion(usuarioCrm, actualizaciones);
							}
							
						} catch (Exception update) {
							_log.error(update.getMessage(),update);
							update.printStackTrace();
						}
					}
				}
				
				
			} catch (NoSuchUser_CrmException e) {
				
				_log.error("Hay un error en: " + e.getMessage());
				e.printStackTrace();
				
				User_Crm usuarioNuevo = _User_CrmLocalService.addUser_Crm(
						auxIdUser,
						auxAcceso,
						auxPerfil,
						auxArea,
						auxOficina,
						auxJefe);
				
				try {
					
					ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				            .getAttribute(WebKeys.THEME_DISPLAY);
				
					System.out.println(themeDisplay.getScopeGroup().getName());
					
					Group g = GroupLocalServiceUtil.getGroup(themeDisplay.getScopeGroup().getGroupId());
					GroupLocalServiceUtil.addUserGroup(auxIdUser, g);
				}
				catch(Exception group) {
					group.printStackTrace();
				}
				
				List<Permisos_Perfiles> permisos = _Permisos_PerfilesLocalService.getPermisosByPerfilId(auxPerfil);
				
				for(Permisos_Perfiles p : permisos) {
					
					Catalogo_Detalle entrada = _Catalogo_DetalleLocalService.getCatalogo_Detalle(p.getModuloId());
					
					if(!p.getRestringido()) {
						RoleLocalServiceUtil.addUserRole(auxIdUser, entrada.getValorN());
					}
				}
				
				if(auxAcceso) {
					sendEmailAlta(usuarioNuevo);
				}
			}
			catch(Exception e) {
				_log.error("Hay un error en: " + e.getMessage());
			}
			
		}
		
		for(int i = 0; i < arregloUsuariosJson.size(); i++) {
			
			JsonObject objAux = (JsonObject) arregloUsuariosJson.get(i);
			
			int auxIdUser = objAux.get("idUsuario").getAsInt();
			boolean auxAcceso = objAux.get("acceso").getAsBoolean();
			int auxArea = objAux.get("area").getAsInt();
			int auxOficina = objAux.get("oficina").getAsInt();
			int auxPerfil = objAux.get("perfil").getAsInt();
			int auxJefe = objAux.get("jefe").getAsInt();
			int auxRelevo = objAux.get("relevo").getAsInt();
			int auxRelevoCartera = objAux.get("relevoCartera").getAsInt();
			
			List<User_Crm> personal = _User_CrmLocalService.getUsers_CrmByJefe(auxIdUser);
			
			if(personal != null) {
			
				for(User_Crm userAux : personal) {
					
					if(auxRelevo != -1) {
						
						if(auxRelevo != userAux.getUserId()) {
							_log.info("Asignando como jefe de " + userAux.getUserId() + " a " + auxRelevo);
							userAux.setJefe(auxRelevo);
						}
						else {
							userAux.setJefe(-1);
						}
						
						_User_CrmLocalService.updateUser_Crm(userAux);
					}
				}
			}
		}
		
		long companyId = CompanyThreadLocal.getCompanyId();
		
		List<User> usuarios = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		List<PersonaTMX> usuariosCRM = new ArrayList<>();
		
		for(User u : usuarios) {
			
			long auxUserId = u.getUserId();
			
			PersonaTMX persona = new PersonaTMX();
			
			try {
				User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) auxUserId);
				persona.setJefeDirecto(usuario.getJefe());
				persona.setArea(usuario.getArea());
				persona.setOficina(usuario.getOficina());
				persona.setAccesoCrm(usuario.getAccesoCRM());
				persona.setPerfil(usuario.getPerfilId());
			} catch(Exception e) {
			}
			
			persona.setUsuarioWindows(u.getScreenName());
			persona.setNombreCompleto(u.getFullName());
			persona.setCorreo(u.getEmailAddress());
			persona.setIdUsuario((int) auxUserId);
			
			usuariosCRM.add(persona);
		}
		
		
		actionRequest.setAttribute("mostrarGuardar", true);
		//actionRequest.setAttribute("usuarios", usuariosCRM);
		
	}
	
	public boolean validaUserAdmin(int userId) {
		
		switch(userId) {
			case 1:
				return false;
			case 32868:
				return false;
			case 405176:
				return false;
			case 1940132:
				return false;
				/*
			case 408292:
				return false;
				*/
			default:
				return true;
		}
		
	}
	
	public void sendEmailAlta(User_Crm usuarioNuevo) {

		try {
			
			User usuarioLiferay = UserLocalServiceUtil.getUser(usuarioNuevo.getUserId());
			
			String subject = "Bienvenido al CRM de Tokio Marine";
			String body = "<!DOCTYPE html> <html>   <head>  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /></head><body><section><p>Hola ${nombreUsuario}</p><p>Bienvenido al CRM de Tokio Marine; te confirmamos que tu acceso al sistema se ha habilitado de forma correcta. Para ingresar, en el siguiente link debes usar tu usuario de Windows y la misma contraseña de inicio de sesión que usas para encender tu computadora:" + 
					"</p><p><a href=\"https://crm.tokiomarine.corp\">crm.tokiomarine.corp</a></p><p>Cualquier duda o comentario, por favor acércate al equipo de Ventas.</p></section></body></html>";
			
			body = StringUtil.replace(body,
					new String[] { "${nombreUsuario}"},
					new String[] { usuarioLiferay.getFullName() });
			
			envioCorreos.SendMAil(new String[] {usuarioLiferay.getEmailAddress()}, body, subject);
			
		} catch (PortalException e) {
			e.printStackTrace();
		}
	}
	
	public void sendEmailBaja(User_Crm usuarioBaja) {
		
		try {
			
			User usuarioLiferay = UserLocalServiceUtil.getUser(usuarioBaja.getUserId());
			
			String subject = "Acceso deshabilitado";
			String body = "<!DOCTYPE html> <html>   <head>  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /></head><body><section><p>Hola ${nombreUsuario}</p><p>Te confirmamos que tu acceso al CRM de Tokio Marine se ha deshabilitado." + 
					"</p><p>Cualquier duda o comentario, por favor acércate al equipo de Ventas.</p></section></body></html>";
			
			body = StringUtil.replace(body,
					new String[] { "${nombreUsuario}"},
					new String[] { usuarioLiferay.getFullName() });
			
			envioCorreos.SendMAil(new String[] {usuarioLiferay.getEmailAddress()}, body, subject);
			
		} catch (PortalException e) {
			e.printStackTrace();
		}
	}
	
	public void sendEmailActualizacion(User_Crm usuarioUpdate, String actualizaciones) {
		
		try {
			
			User usuarioLiferay = UserLocalServiceUtil.getUser(usuarioUpdate.getUserId());
			
			String subject = "Perfil Actualizado";
			
			String body = "<!DOCTYPE html> <html>   <head>  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /></head><body><section><p>Hola ${nombreUsuario}</p><p>Te confirmamos que tu perfil en el CRM de Tokio Marine se ha actualizado:<br/>" + 
					actualizaciones + "</p><p>Cualquier duda o comentario, por favor acércate al equipo de Ventas.</p></section></body></html>";
			
			body = StringUtil.replace(body,
					new String[] { "${nombreUsuario}"},
					new String[] { usuarioLiferay.getFullName() });
			
			envioCorreos.SendMAil(new String[] {usuarioLiferay.getEmailAddress()}, body, subject);
			
		} catch (PortalException e) {
			e.printStackTrace();
		}
	}
}
