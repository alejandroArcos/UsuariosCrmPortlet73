<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<c:set var="version" scope="session" value="DEV.21102021.1630" />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<style>
    .UsuariosCrmPortlet {
    .btn-usuarios {
        min-width: 15px !important;
        margin: 0px !important;
        padding: 5px !important;
        font-size: 20px !important;
        color: #1976d2 !important;
        box-shadow: none !important;
    }

    .dataTables_filter {
        display: none;
    }

    .buttons-excel {
        display: none;
    }


    .nuevo{
        color:red;
    }
    .switch label input[type=checkbox]:checked + .lever:after{
        background-color: #4285f4 !important;
    }

    .switch label input[type=checkbox]:checked + .lever{
        background-color: #c7e0f1 !important;
    }
    }
</style>