package assembler.symbolTable;

public enum EType {
	// keyword
	eSegmentHead,
	eOpcode,
	eRegister, 	//
	// constants
	eConstant, 	//
	eAddress, 	// 주소를 나타냄. (주소 참조 연산자용)
	// identifier
	eSegmentSize,	// identifier로 symbol table에 일단 넣고 head segment에서 값을 채우자.
	eProgramName,	// "
	eVariable,		// "
	eLabel,			// lexical analyzer에서 이해할 수 있기 때문에 타입까지 채우고 넘기자.
	;

	public boolean isIdentifier(EType type) {
		if(type == eSegmentSize|type == eProgramName|type == eVariable|type == eLabel) {
			return true;
		}
		return false;
	}
}
