package assembler.node;

import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.symbolTable.Token;

public abstract class Node {
	// node는 terminal / non-terminal이다.
	// 즉, 글자를 가져와서 파싱할 능력이 있어야 한다.
	protected LexicalAnalyzer lexical_analyzer;
	public Node(LexicalAnalyzer lexical_analyzer) {
		this.lexical_analyzer = lexical_analyzer;
	}
	public abstract Token parse(Token token) throws Exception; //node의 문법적 구조가 끝날때까지 문장을 해석한다.
}
