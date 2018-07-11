package dataobject;

public class Professor {

	private String profName;
	
	public Professor() {}
	
	/*public Professor(String profName) {
		this.profName = profName;
	}*/

	public String getProfName() {
		return profName;
	}

	public void setProfName(String profName) {
		this.profName = profName;
	}

	public static Professor parseProfessor(String name) {
		Professor prof = new Professor();
		prof.setProfName(name);
		return prof;
	}
	@Override
	public String toString() {
		return "Professor [profName=" + profName + "]";
	}
	
}
