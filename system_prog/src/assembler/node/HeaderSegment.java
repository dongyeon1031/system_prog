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
		// 다음 토큰 읽어서 다음 토큰을 반환하는 방식으로 하자.
		// 지금 for문 수정하기 -> enum에 catagory 만들어야 한다. (segment head type이 나오기 전까지 루프 돌리기)
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
