package assembler.node;

import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.symbolTable.EType;
import assembler.symbolTable.Token;

public class Instruction extends Node{
	public enum ECommand{
		eAdd("add", 2),
		eMove("move", 2),
		eCmp("cmp", 2),
		eJmp("jmp", 1),
		eGe("ge", 1),
		eInt("int", 1),
		ePush("push", 1),
		ePop("pop", 1),
		eCall("fcall", 1),	// 함수 호출 -> 반환 주소 push + jump로 구성
		eHalt("halt", 0),
		eRet("fret", 0),	// 함수 반환 -> r로 시작하지 않게 하기 위해 fret로 명령어 구성..
//		eEnd(".end")	//end 없앨 수 있는지 보자
		;
		private String text;
		private int operandNum;
		
		private ECommand(String text, int operandNum) {
			this.text = text;
			this.operandNum = operandNum;
		}
		public String getText() {
			return this.text;
		}
		public int numOperand() {
			return this.operandNum;
		}
		public static ECommand myValueOf(String text) {
			for(ECommand e : ECommand.values()) {
				if(e.text.equals(text)) {
					return e;
				}
			}
			return null;
		}
	}
	private static final int LIMIT_OPERAND = 2;
	private ECommand eCommand;
	private Token[] operand;
	public ECommand getOpcode() {
		return this.eCommand;
	}
	public Token[] getOperand() {
		return this.operand;
	}
	public Instruction(LexicalAnalyzer lexical_analyzer) {
		super(lexical_analyzer);
		this.operand = new Token[LIMIT_OPERAND];
	}
	@Override
	public Token parse(Token command) throws Exception {
		this.eCommand = ECommand.myValueOf(command.getName());
		Token nextToken = super.lexical_analyzer.getToken(); //operand

		for(int i=0; nextToken.getType() != EType.eOpcode && nextToken.getType() != EType.eSegmentHead && i<this.eCommand.operandNum; i++) {
			this.operand[i] = nextToken;
			nextToken = super.lexical_analyzer.getToken();
		}

		return nextToken;
	}

}
