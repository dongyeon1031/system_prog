package assembler.node;

import java.util.Vector;

import assembler.lexicalAnalyzer.EKeyword;
import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.symbolTable.EType;
import assembler.symbolTable.Token;

public class Program extends Node {
	private HeaderSegment headerSegment;
	private CodeSegment codeSegment;
	private DataSegment dataSegment;

	public Program(LexicalAnalyzer lexical_analyzer) {
		super(lexical_analyzer);
	}

	@Override
	public Token parse(Token token) throws Exception {
		token = lexical_analyzer.getToken();
		token.setType(EType.eProgramName);
		
		token = lexical_analyzer.getToken();
		// token은 section head가 된다.
		while (true) {
			if (token.getType() == EType.eSegmentHead) {
				if(token.getName().equals(EKeyword.eHeader.getKeyword())) {
					headerSegment = new HeaderSegment(super.lexical_analyzer);
					token = headerSegment.parse(token);
				}else if(token.getName().equals(EKeyword.eData.getKeyword())) {
					dataSegment = new DataSegment(super.lexical_analyzer);
					token = dataSegment.parse(token);
				}else if(token.getName().equals(EKeyword.eCode.getKeyword())) {
					codeSegment = new CodeSegment(super.lexical_analyzer);
					token = codeSegment.parse(token);
				}else if(token.getName().equals(EKeyword.eEnd.getKeyword())) {
					return null;
				}
			} else {
				throw new Exception();
			}
		}
	}

	public Vector<Instruction> getInstruction() {
		return this.codeSegment.getInstruction();
	}

}
