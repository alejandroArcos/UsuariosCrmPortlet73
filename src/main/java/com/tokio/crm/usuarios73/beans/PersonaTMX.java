package com.tokio.crm.usuarios73.beans;

public class PersonaTMX {

	private boolean accesoCrm;
	private String usuarioWindows;
	private String nombreCompleto;
	private String correo;
	private int area;
	private int idUsuario;
	private int oficina;
	private int perfil;
	private int jefeDirecto;
	private boolean dependiente;
	private int isManager;
	private int isEjecutivo;

	public PersonaTMX() {

	}

	public PersonaTMX(String usuario, String nombre, String correo, int area, boolean accesoCRM) {	
		this.accesoCrm = accesoCRM;
		this.usuarioWindows = usuario;
		this.nombreCompleto = nombre;
		this.correo = correo;
		this.area = area;		
	}

	public boolean isAccesoCrm() {
		return accesoCrm;
	}

	public void setAccesoCrm(boolean accesoCrm) {
		this.accesoCrm = accesoCrm;
	}

	public String getUsuarioWindows() {
		return usuarioWindows;
	}

	public void setUsuarioWindows(String usuarioWindows) {
		this.usuarioWindows = usuarioWindows;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public int getOficina() {
		return oficina;
	}

	public void setOficina(int oficina) {
		this.oficina = oficina;
	}

	public int getPerfil() {
		return perfil;
	}

	public void setPerfil(int perfil) {
		this.perfil = perfil;
	}

	public int getJefeDirecto() {
		return jefeDirecto;
	}

	public void setJefeDirecto(int jefeDirecto) {
		this.jefeDirecto = jefeDirecto;
	}

	public boolean isDependiente() {
		return dependiente;
	}

	public void setDependiente(boolean dependiente) {
		this.dependiente = dependiente;
	}

	public int getIsManager() {
		return isManager;
	}

	public void setIsManager(int isManager) {
		this.isManager = isManager;
	}

	public int getIsEjecutivo() {
		return isEjecutivo;
	}

	public void setIsEjecutivo(int isEjecutivo) {
		this.isEjecutivo = isEjecutivo;
	}
}
