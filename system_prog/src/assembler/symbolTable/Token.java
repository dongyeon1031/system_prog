package assembler.symbolTable;

public class Token{
	// symbol table은 code segment에도 존재할 수 있기 때문에 바깥으로 빼야 한다.
	public static final int NON = -1;
	private EType type;
	private int size;
	private int offset = NON;			// 상대주소
	private int initial_value=0;		// 0으로 초기화 
	private boolean isBinded=false;
//	private int line;			// 
	private final String name;	// 
	public Token(String name) {
		this.name = name;
	}
	public void binding() {
		this.isBinded = true;
	}
	public boolean isBinded() {
		return this.isBinded;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getInitialValue() {
		return initial_value;
	}
	public void setInitialValue(int initial_value) {
		this.initial_value = initial_value;
	}
	public EType getType() {
		return type;
	}
	public void setType(EType type) {
		this.type = type;
	}
//	public int getLine() {
//		return line;
//	}
//	public void setLine(int line) {
//		this.line = line;
//	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getSize() {
		return size;
	}
	public String getName() {
		return name;
	}
}