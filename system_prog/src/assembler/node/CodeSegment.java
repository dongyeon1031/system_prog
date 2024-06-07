package assembler.node;

import java.util.Vector;

import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.symbolTable.EType;
import assembler.symbolTable.Token;

public class CodeSegment extends Node {
	private Vector<Instruction> instructions;
	public CodeSegment(LexicalAnalyzer lexical_analyzer) {
		super(lexical_analyzer);
		this.instructions = new Vector<>();
	}
	
	private Token setLabel(Token token) throws Exception {
		while(true) { // label인 경우 offset 채워주기
			if(token.getType() == EType.eLabel) {
				token.setOffset(this.instructions.size());
				token = super.lexical_analyzer.getToken();
			}else {
				break;
			}
		}
		return token;
	}
	
	@Override
	public Token parse(Token token) throws Exception {
		Token command = super.lexical_analyzer.getToken();

		command = this.setLabel(command);
		
		while(command.getType() == EType.eOpcode) {
			Instruction instruction = new Instruction(super.lexical_analyzer);
			command = instruction.parse(command);
			this.instructions.add(instruction);
			
			command = this.setLabel(command);
		}
		
//		for(Instruction i : this.instructions) {
//			System.out.print(i.getOpcode().getText()+" : ");
//			for(Token t : i.getOperand()) {
//				if(t != null)
//					System.out.print(t.getName()+' '+t.getType()+" "+t.getInitialValue()+"\t");
//			}
//			System.out.println();
//		}
		return command;
	}

	public Vector<Instruction> getInstruction() {
		return this.instructions;
	}
}
