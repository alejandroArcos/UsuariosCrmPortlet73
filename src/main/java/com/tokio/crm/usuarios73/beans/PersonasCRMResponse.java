package com.tokio.crm.usuarios73.beans;

import java.util.ArrayList;
import java.util.List;

public class PersonasCRMResponse extends CRMResponse{

	private List<PersonaTMX> personas= new ArrayList<PersonaTMX>();

	public List<PersonaTMX> getPersonas() {
		return personas;
	}

	public void setPersonas(List<PersonaTMX> personas) {
		this.personas = personas;
	}
		
}
