package assembler.parser;

import java.util.Vector;

import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.node.Instruction;
import assembler.node.Node;
import assembler.node.Program;
import assembler.symbolTable.EType;
import assembler.symbolTable.SymbolTable;
import assembler.symbolTable.Token;

public class Parser extends Node{
	private Program program;
	private SymbolTable symbol_table;
	public Parser(LexicalAnalyzer lexical_analyzer) {
		super(lexical_analyzer);
		this.symbol_table = new SymbolTable(); 
	}

	@Override
	public Token parse(Token token) throws Exception {
		token = super.lexical_analyzer.getToken();
		if(token.getType() == EType.eSegmentHead) { // keyword가 먼저 나오고 나서 파싱을 해야 한다.
			// 즉, 먼저 판단하고 파싱한다.
			this.program = new Program(this.lexical_analyzer);
			return this.program.parse(token);
//			return program;
		}
		return null;
	}

	public Vector<Instruction> getInstruction(){
		return this.program.getInstruction();
	}
	
	public SymbolTable getSymbolTable() {
		return this.symbol_table;
	}
}
