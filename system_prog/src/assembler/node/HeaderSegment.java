package assembler.node;

import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.symbolTable.EType;
import assembler.symbolTable.Token;

public class HeaderSegment extends Node{
	//= tree
	//tree의 자식
	// 자식의 구조가 정형화되어있지 않아도 상관없다. (다형성 구조)
	public HeaderSegment(LexicalAnalyzer lexical_analyzer) {
		super(lexical_analyzer);
	}

	@Override
	public Token parse(Token token) throws Exception {
		Token keyword = super.lexical_analyzer.getToken();
		
		while(keyword.getType() != EType.eSegmentHead) {
			keyword.setType(EType.eSegmentSize);
			Token size = super.lexical_analyzer.getToken();
			keyword.setSize(size.getInitialValue());
			keyword = super.lexical_analyzer.getToken();
		}

		return keyword;
	}

}
