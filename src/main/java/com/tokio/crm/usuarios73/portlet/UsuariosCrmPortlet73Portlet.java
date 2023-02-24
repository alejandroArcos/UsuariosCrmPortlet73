package com.tokio.crm.usuarios73.portlet;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.crmservices73.Bean.ListaRegistro;
import com.tokio.crm.crmservices73.Constants.CrmServiceKey;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.Perfil_Crm;
import com.tokio.crm.servicebuilder73.model.Permisos_Perfiles;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.Perfil_CrmLocalService;
import com.tokio.crm.servicebuilder73.service.Permisos_PerfilesLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;
import com.tokio.crm.usuarios73.beans.PersonaTMX;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author urielfloresvaldovinos
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=UsuariosCrmPortlet73",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"com.liferay.portlet.private-request-attributes=false"
	},
	service = Portlet.class
)
public class UsuariosCrmPortlet73Portlet extends MVCPortlet {
	private static final Log _log = LogFactoryUtil.getLog(UsuariosCrmPortlet73Portlet.class);
	@Reference
	CrmGenerico _CrmGenericoService;

	User usuario;

	@Reference
	private User_CrmLocalService _User_CrmLocalService;

	@Reference
	private Perfil_CrmLocalService _Perfil_CrmLocalService;

	@Reference
	private Permisos_PerfilesLocalService _Permisos_PerfilesLocalService;

	@Reference
	private Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		usuario = (User) renderRequest.getAttribute(WebKeys.USER);

		cargaCatalogos(renderRequest, renderResponse);

		Enumeration<String> en = renderRequest.getParameterNames();

		int params = 0;

		while (en.hasMoreElements()) {
			Object objOri = en.nextElement();
			String param = (String) objOri;
			String value = renderRequest.getParameter(param);
			System.out.println("[ ---> " +param + " : " + value );
			params++;
		}

		if(params == 0) {
			//cargaInformacionEntrada(renderRequest, renderResponse);
		}

		renderRequest.setAttribute("permisoEscritura", validaPermisos(renderRequest));
		renderRequest.setAttribute("idArea", getAreaUsuario(usuario));
		renderRequest.setAttribute("isManager", isManager(usuario));
		renderRequest.setAttribute("isManagerVentas", isManagerVentas(usuario));

		super.doView(renderRequest, renderResponse);
	}

	public boolean validaPermisos(RenderRequest renderRequest) {

		User user = (User) renderRequest.getAttribute(WebKeys.USER);

		try {
			User_Crm usuario = _User_CrmLocalService.getUser_Crm((int)user.getUserId());

			List<Permisos_Perfiles> permisos = _Permisos_PerfilesLocalService.getPermisosByPerfilId(usuario.getPerfilId());

			for(Permisos_Perfiles p : permisos) {
				if(p.getModuloId() == 56) {
					return p.getEscritura();
				}
			}

			return false;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void cargaInformacionEntrada(RenderRequest renderRequest,
										RenderResponse renderResponse) {

		long companyId = CompanyThreadLocal.getCompanyId();

		List<PersonaTMX> usuariosCRM = new ArrayList<PersonaTMX>();

		List<User> usuarios = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);


		for(User u : usuarios) {

			long auxUserId = u.getUserId();

			PersonaTMX persona = new PersonaTMX();

			try {
				User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) auxUserId);
				persona.setJefeDirecto(usuario.getJefe());
				persona.setAccesoCrm(usuario.getAccesoCRM());
				persona.setArea(usuario.getArea());
				persona.setOficina(usuario.getOficina());
				persona.setPerfil(usuario.getPerfilId());
				persona.setIsManager(validaManager(usuario.getPerfilId()));
				persona.setIsEjecutivo(validaEjectivo(usuario.getPerfilId()));
			} catch(Exception e) {
			}

			persona.setUsuarioWindows(u.getScreenName());
			persona.setNombreCompleto(u.getFullName());
			persona.setCorreo(u.getEmailAddress());
			persona.setIdUsuario((int) auxUserId);


			usuariosCRM.add(persona);
		}

		renderRequest.setAttribute("mostrarGuardar", true);
		renderRequest.setAttribute("usuarios", usuariosCRM);
	}

	public void cargaCatalogos(RenderRequest renderRequest, RenderResponse renderResponse) {

		try {

			ListaRegistro listaAreas = _CrmGenericoService.getCatalogo(
					CrmServiceKey.TMX_CTE_ROW_TODOS,
					CrmServiceKey.TMX_CTE_TRANSACCION_GET,
					CrmServiceKey.LIST_CAT_AREA,
					CrmServiceKey.TMX_CTE_CAT_ACTIVOS,
					usuario.getScreenName(),
					UsuariosCrmPortlet73PortletKeys.USUARIOSCRM
			);

			ListaRegistro listaOficinas = _CrmGenericoService.getCatalogo(
					CrmServiceKey.TMX_CTE_ROW_TODOS,
					CrmServiceKey.TMX_CTE_TRANSACCION_GET,
					CrmServiceKey.LIST_CAT_OFICINA,
					CrmServiceKey.TMX_CTE_CAT_ACTIVOS,
					usuario.getScreenName(),
					UsuariosCrmPortlet73PortletKeys.USUARIOSCRM
			);

			List<Catalogo_Detalle> listaOf = _Catalogo_DetalleLocalService.findByCodigo("CATOFICINA");
			_log.error(listaOf);

			List<Perfil_Crm> listaPerfiles = new ArrayList<Perfil_Crm>();
			listaPerfiles = _Perfil_CrmLocalService.getPerfil_Crms(QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			long companyId = CompanyThreadLocal.getCompanyId();
			List<User> usuarios = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			List<PersonaTMX> usuariosCRM = new ArrayList<PersonaTMX>();

			for(User u : usuarios) {

				long auxUserId = u.getUserId();


				PersonaTMX persona = new PersonaTMX();
				persona.setNombreCompleto(u.getFullName());
				persona.setIdUsuario((int) auxUserId);

				try {
					User_Crm userCrm = _User_CrmLocalService.getUser_Crm((int)auxUserId);
					persona.setArea(userCrm.getArea());
					persona.setPerfil(userCrm.getPerfilId());
					persona.setAccesoCrm(userCrm.getAccesoCRM());
					persona.setIsManager(validaManager(userCrm.getPerfilId()));
				}
				catch (Exception e) {
					persona.setArea(-1);
					persona.setPerfil(-1);
					persona.setAccesoCrm(false);
				}


				usuariosCRM.add(persona);
			}

			renderRequest.setAttribute("listaAreas", listaAreas.getLista());
			renderRequest.setAttribute("listaOficinas", listaOf);
			renderRequest.setAttribute("listaPerfiles", listaPerfiles);
			renderRequest.setAttribute("listaUsuarios", usuariosCRM);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int getAreaUsuario(User usuario) {

		User_Crm usuarioCrm = null;

		try {
			usuarioCrm = _User_CrmLocalService.getUser_Crm((int) usuario.getUserId());
		}
		catch(Exception e) {
			return 0;
		}

		return usuarioCrm.getArea();
	}

	public int validaManager(int idPerfil) {

		switch(idPerfil) {
			case 10:
				return 1;
			case 11:
				return 1;
			case 12:
				return 1;
			case 13:
				return 1;
			case 14:
				return 1;
			case 16:
				return 1;
			case 17:
				return 1;
			case 18:
				return 1;
			case 19:
				return 1;
			case 21:
				return 1;
			default:
				return 0;
		}

	}

	public boolean isManager(User usuario) {

		User_Crm usuarioCrm = null;

		try {
			usuarioCrm = _User_CrmLocalService.getUser_Crm((int) usuario.getUserId());

			switch(usuarioCrm.getPerfilId()) {

				case 10:
					return true;
				case 11:
					return true;
				case 12:
					return true;
				case 13:
					return true;
				case 14:
					return true;
				case 16:
					return true;
				case 17:
					return true;
				case 18:
					return true;
				case 19:
					return true;
				case 21:
					return true;
				default:
					return false;
			}
		}
		catch(Exception e) {
			return false;
		}
	}

	public boolean isManagerVentas(User usuario) {

		User_Crm usuarioCrm = null;

		try {
			usuarioCrm = _User_CrmLocalService.getUser_Crm((int) usuario.getUserId());

			switch(usuarioCrm.getPerfilId()) {

				case 16:
					return true;
				case 19:
					return true;
				default:
					return false;
			}
		}
		catch(Exception e) {
			return false;
		}

	}

	public int validaEjectivo(int idPerfil) {

		if(idPerfil == 9) {
			return 1;
		}
		else {
			return 0;
		}
	}
}