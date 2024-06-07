package assembler.codeGenerator;

import java.util.Vector;

import assembler.node.Instruction;
import assembler.parser.Parser;
import assembler.symbolTable.EType;
import assembler.symbolTable.SymbolTable;
import assembler.symbolTable.Token;
import component.InstructionMappingTable;

public class CodeGenerator {
	public static final int _8_BIT_OS = 8;
	public static final int _32_BIT_OS = 32;
	public int bit_type = _8_BIT_OS;	// 비트 수가 먼저 정해지고 instruction의 길이가 정해져야 하나?
	private Vector<String> instruction_set;
	private Parser parser;
	private SymbolTable symbol_table;
	public CodeGenerator(Parser parser) {
		this.instruction_set = new Vector<>();
		this.parser = parser;
	}
	public Vector<String> getInstructions(){
		return this.instruction_set;
	}

	public void generateCode() throws Exception {
		Vector<Instruction> assembly_code = parser.getInstruction();
		
		this.judgeOffset();
		
		/*
		 * 2번 돌리는 이유
		 * 
		 * 코드 생성을 해 봐야 label이 몇 번째 줄인지 알 수 있다.
		 * 그런데 label 선언보다 먼저 label을 사용하는 instruction이 나오면
		 * 해당 label의 주소를 설정하기 전에 instruction이 만들어지기 때문에 주소값이 정상적으로 들어가지 않는다. 
		 * 따라서 첫 번째 generation에서는 label의 주소 확정, 두 번째 generation에서는 채워진 주소로 instruction 확정을 한다.
		 * 
		 */
		for(int i=0; i<2; i++) {
			for(int index=0; index< assembly_code.size(); index++) {
				Instruction instruction = assembly_code.get(index);
				
				// assembly code debug print
				System.out.print(instruction.getOpcode().getText()+"\t: ");
				for(Token t : instruction.getOperand()) {
					if(t != null)
						System.out.print(t.getName()+' ');
				}
				System.out.println();
				
				if(i==0) {	// symbol 위치 확정
					for(Token t : this.symbol_table.getSymbolTable()) {
						if(t.getType() == EType.eLabel && index == t.getOffset() && !t.isBinded()) {
							t.setOffset(this.instruction_set.size());
							t.binding();
						}
					}
				}
				
				Vector<String> instructions = new Vector<>();
				switch(instruction.getOpcode()) {
				case eAdd:
					instructions = InstructionMappingTable.makeAdd(instruction.getOperand()[0], instruction.getOperand()[1]);
					break;
				case eMove:
					instructions = InstructionMappingTable.makeMove(instruction.getOperand()[0], instruction.getOperand()[1]);
					break;
				case eCmp:
					instructions = InstructionMappingTable.makeCmp(instruction.getOperand()[0], instruction.getOperand()[1]);
					break;
				case eJmp:
					instructions = InstructionMappingTable.makeJmp(instruction.getOperand()[0]);
					break;
				case eGe:
					instructions = InstructionMappingTable.makeGe(instruction.getOperand()[0]);
					break;
				case eHalt:
					instructions = InstructionMappingTable.makeHalt();
					break;
				case eInt:
					instructions = InstructionMappingTable.makeInt(instruction.getOperand()[0]);
					break;
				case eRet:
					instructions = InstructionMappingTable.makeRet();
					break;
				case ePop:
					instructions = InstructionMappingTable.makePop(instruction.getOperand()[0]);
					break;
				case ePush:
					instructions = InstructionMappingTable.makePush(instruction.getOperand()[0]);
					break;
				case eCall:
					// 현재 코드의 다음 시작 주소를 push해야 한다. -> 파라미터는 모르니까 현재 주소 넘겨주기 
					instructions = InstructionMappingTable.makeCall(instruction.getOperand()[0], 
							this.instruction_set.size()-1+this.symbol_table.getSymbol("code").getOffset());
					break;
				}
				this.instruction_set.addAll(instructions);

				// instruction set debug print
				if(i==0) {
					for(String s : instructions) {
						System.out.println(s);
					}	
				}
			}
			if(i==0) {
				// code segment 크기 설정.
				this.symbol_table.getSymbol("code").setSize(this.instruction_set.size() * this.bit_type / 8);
				//label offset 설정
				for(Token t : this.symbol_table.getSymbolTable()) {
					if(t.getType() == EType.eLabel) {
						t.setOffset(this.symbol_table.getSymbol("code").getOffset()+t.getOffset());
					}
				}
				this.instruction_set.clear();
			}
		}
		
		for(Token t : this.symbol_table.getSymbolTable()) {
			System.out.println(t.getName()+" "+t.getType()+ " "+t.getOffset()+ " "+t.getSize());
		}
	}
	private void judgeOffset() {
		//data, code segment 추가
		Token data = new Token("data");
		data.setType(EType.eSegmentSize);
		this.symbol_table.addSymbol(data);
		Token code = new Token("code");
		code.setType(EType.eSegmentSize);
		this.symbol_table.addSymbol(code);
		
		//segment offset 결정.
		int offset = 0;
		this.symbol_table.getSymbol("heap").setOffset(offset);
		offset += this.symbol_table.getSymbol("heap").getSize();
		this.symbol_table.getSymbol("stack").setOffset(offset);
		offset += this.symbol_table.getSymbol("stack").getSize();
		this.symbol_table.getSymbol("data").setOffset(offset);
		
		// 각 변수의 offset 결정.
		int datasize = 0;
		for(Token t : this.symbol_table.getSymbolTable()) {
			if(t.getType() == EType.eVariable) {
				t.setOffset(datasize+data.getOffset());
				datasize += t.getSize();
			}
		}
		// data segment 크기 설정.
		data.setSize(datasize);
		offset += datasize;
		code.setOffset(offset);
		
		return;
	}
	public void associate(SymbolTable symbol_table) {
		this.symbol_table = symbol_table;
		
	}
	
	
}
