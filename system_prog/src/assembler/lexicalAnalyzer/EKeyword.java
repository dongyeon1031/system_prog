package assembler.lexicalAnalyzer;

public enum EKeyword{
	eProgram(".program"),
	eHeader(".header"), 
	eStack("stack"), 
	eHeap("heap"),
	eData(".data"),
	eCode(".code"),
	eEnd(".end");

	private final String text;
	EKeyword(String text) {
		this.text = text;
	}
	public String getKeyword() {
		return this.text;
	}
}