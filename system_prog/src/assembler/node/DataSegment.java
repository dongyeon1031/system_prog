package assembler.node;

import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.symbolTable.EType;
import assembler.symbolTable.Token;

public class DataSegment extends Node {
	
	public DataSegment(LexicalAnalyzer lexical_analyzer) {
		super(lexical_analyzer);
	}

	@Override
	public Token parse(Token token) throws Exception {
		Token name = super.lexical_analyzer.getToken();
		
		while(name.getType() != EType.eSegmentHead) {
			name.setType(EType.eVariable);
			Token size = super.lexical_analyzer.getToken();
			name.setSize(size.getInitialValue());
//			Program.addSymbol(name);
			
			name = super.lexical_analyzer.getToken();
		}
		return name;
	}

}
