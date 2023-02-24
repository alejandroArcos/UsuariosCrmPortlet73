package com.tokio.crm.usuarios73.commands.resource;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;
import com.tokio.crm.usuarios73.beans.PersonaTMX;
import com.tokio.crm.usuarios73.constants.UsuariosCrmPortlet73PortletKeys;
import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Reference;


@Component(
        immediate = true,
        property = {
                "javax.portlet.init-param.copy-request-parameters=true",
                "javax.portlet.name=" + UsuariosCrmPortlet73PortletKeys.USUARIOSCRMPORTLET73,
                "mvc.command.name=/crm/actionUsuarios"
        },
        service = {MVCActionCommand.class}
)

public class BuscarUsuariosActionCommand extends BaseMVCActionCommand {
    @Reference
    User_CrmLocalService _User_CrmLocalService;

    private static final Log _log = LogFactoryUtil.getLog(BuscarUsuariosActionCommand.class);

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        // TODO Auto-generated method stub

        HttpServletRequest originalRequest = PortalUtil
                .getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));

        String filtroUser = originalRequest.getParameter("usuario");
        int filtroArea = Integer.parseInt(originalRequest.getParameter("area"));
        int filtroPerfil = Integer.parseInt(originalRequest.getParameter("perfil"));

        long companyId = CompanyThreadLocal.getCompanyId();

        List<PersonaTMX> usuariosCRM = new ArrayList<>();

        if(filtroArea != -1 || filtroPerfil != -1 || filtroUser != "") {
            if(filtroUser != "") {

                _log.info("Filtro por usuario: " + filtroUser);

                List<User> usuariosAux = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

                for(User u : usuariosAux) {

                    _log.info("Nombre usuario: " + u.getFullName());

                    if(u.getFullName().toLowerCase().contains(filtroUser.toLowerCase())) {

                        long auxUserId = u.getUserId();

                        PersonaTMX persona = new PersonaTMX();

                        try {
                            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) auxUserId);
                            persona.setJefeDirecto(usuario.getJefe());
                            persona.setAccesoCrm(usuario.getAccesoCRM());
                            persona.setArea(usuario.getArea());
                            persona.setOficina(usuario.getOficina());
                            persona.setPerfil(usuario.getPerfilId());
                            persona.setOficina(usuario.getOficina());
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
                }
            }
            else {
                if(filtroArea != -1 ) {
                    if(filtroPerfil != -1) {
                        _log.info("Filtro por area y perfil");

                        try {

                            List<User_Crm> usuariosArea = _User_CrmLocalService.getUsers_CrmByAreaPerfil(filtroArea, filtroPerfil);

                            for(User_Crm u : usuariosArea) {
                                PersonaTMX persona = new PersonaTMX();
                                persona.setAccesoCrm(u.getAccesoCRM());
                                persona.setArea(u.getArea());
                                persona.setIdUsuario(u.getUserId());
                                persona.setJefeDirecto(u.getJefe());
                                persona.setPerfil(u.getPerfilId());
                                persona.setOficina(u.getOficina());
                                persona.setIsManager(validaManager(u.getPerfilId()));
                                persona.setIsEjecutivo(validaEjectivo(u.getPerfilId()));

                                try {
                                    User usuarioLiferay = UserLocalServiceUtil.getUser(u.getUserId());

                                    persona.setNombreCompleto(usuarioLiferay.getFullName());
                                    persona.setCorreo(usuarioLiferay.getEmailAddress());
                                    persona.setUsuarioWindows(usuarioLiferay.getScreenName());

                                    usuariosCRM.add(persona);
                                }
                                catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else {

                        _log.info("Filtro por area");

                        try {

                            List<User_Crm> usuariosArea = _User_CrmLocalService.getUsers_CrmByArea(filtroArea);

                            for(User_Crm u : usuariosArea) {
                                PersonaTMX persona = new PersonaTMX();
                                persona.setAccesoCrm(u.getAccesoCRM());
                                persona.setArea(u.getArea());
                                persona.setIdUsuario(u.getUserId());
                                persona.setJefeDirecto(u.getJefe());
                                persona.setPerfil(u.getPerfilId());
                                persona.setOficina(u.getOficina());
                                persona.setIsManager(validaManager(u.getPerfilId()));
                                persona.setIsEjecutivo(validaEjectivo(u.getPerfilId()));

                                try {
                                    User usuarioLiferay = UserLocalServiceUtil.getUser(u.getUserId());

                                    persona.setNombreCompleto(usuarioLiferay.getFullName());
                                    persona.setCorreo(usuarioLiferay.getEmailAddress());
                                    persona.setUsuarioWindows(usuarioLiferay.getScreenName());

                                    usuariosCRM.add(persona);
                                }
                                catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    _log.info("Filtro por perfil");

                    try {

                        List<User_Crm> usuariosArea = _User_CrmLocalService.getUsers_CrmByPerfil(filtroPerfil);

                        for(User_Crm u : usuariosArea) {
                            PersonaTMX persona = new PersonaTMX();
                            persona.setAccesoCrm(u.getAccesoCRM());
                            persona.setArea(u.getArea());
                            persona.setIdUsuario(u.getUserId());
                            persona.setJefeDirecto(u.getJefe());
                            persona.setPerfil(u.getPerfilId());
                            persona.setOficina(u.getOficina());
                            persona.setIsManager(validaManager(u.getPerfilId()));
                            persona.setIsEjecutivo(validaEjectivo(u.getPerfilId()));

                            try {
                                User usuarioLiferay = UserLocalServiceUtil.getUser(u.getUserId());

                                persona.setNombreCompleto(usuarioLiferay.getFullName());
                                persona.setCorreo(usuarioLiferay.getEmailAddress());
                                persona.setUsuarioWindows(usuarioLiferay.getScreenName());

                                usuariosCRM.add(persona);
                            }
                            catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {

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
        }

        actionRequest.setAttribute("mostrarGuardar", true);
        actionRequest.setAttribute("usuarios", usuariosCRM);
        actionRequest.setAttribute("usuario", filtroUser);
        actionRequest.setAttribute("perfil", filtroPerfil);
        actionRequest.setAttribute("area", filtroArea);
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

    public int validaEjectivo(int idPerfil) {

        if(idPerfil == 9) {
            return 1;
        }
        else {
            return 0;
        }
    }
}

