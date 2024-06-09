package assembler.lexicalAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import assembler.node.Instruction;
import assembler.symbolTable.EType;
import assembler.symbolTable.SymbolTable;
import assembler.symbolTable.Token;
import component.InstructionMappingTable;

public class LexicalAnalyzer {
//	private enum WordType {
//		//여기서 identifier면 심볼 테이블에 넣어버리기
//		/*
//		 * keyword 종류
//		 * 
//		 * 1. segment head	: .으로 시작
//		 * 2. opcode		: 이미 정의됨.
//		 * 3. register		: 첫글자 n, 두번째글자 숫자
//		 */
//		
//		eIdentifier,	// 모르는 단어
//		eKeyword,		// 아는 단어
//		eConstant		// 상수
//	}
	// component
	private Scanner scanner;
	// attribute
	public static final String NOP = "nop";
	private static final String COMMENT_SYMBOL = ";";
	private String fileName;
	// associate
	private SymbolTable symbol_table;

	public LexicalAnalyzer(String fileName) {
		this.fileName = fileName;
	}
	
	public void associate(SymbolTable symbolTable) {
		this.symbol_table = symbolTable;
		
	}

	public void initialize() throws FileNotFoundException {
		this.scanner = new Scanner(new File(this.fileName));
	}

	public void fianlize() {
		this.scanner.close();
	}

	private EType judgeType(String name) {
		// 상수 판단부
		if (name.startsWith("#")) {
			return EType.eConstant;
		}else if(name.startsWith("[")) { //주소 참조 연산자
			return EType.eAddress;
		}
		// 키워드 판단부
		if (name.startsWith(".")) { // segment head
			return EType.eSegmentHead;
		} else if (name.startsWith("r")) {
			return EType.eRegister;
		} else if (Instruction.ECommand.myValueOf(name) != null) {
			return EType.eOpcode;
		}
		// identifier
		if (name.endsWith(":")) {
			return EType.eLabel;
		}
		return null;
	}
	private String commentFilter(String name) {
		if(name.startsWith(COMMENT_SYMBOL)) {
			while(name.startsWith(COMMENT_SYMBOL)) {
				this.scanner.nextLine();
				name = scanner.next();
			}
			return name;
		}
		return name;
	}

	public Token getToken() throws Exception {
		/*
		 * 역할
		 * 
		 * 1. 띄워쓰기 단위로 문장을 쪼갠다. 
		 * 2. 단어가 'nop'면 건너뛴다. 
		 * 3. keyword / identifier / constant로 단어를 분류한다. 
		 * 4. 단어에 타입을 정의할 수 있는 경우에 이를 정의한다. 
		 * 5. identifier면 symbol table에 추가한다.
		 * 6. 토큰을 반환한다.
		 */
		if (this.scanner.hasNext()) {
			String name = this.scanner.next();
			name = commentFilter(name);
			while(true) {
				if (name.equals(NOP)) {
					name = scanner.next();
				}else {
					break;
				}
			}
			Token token = this.symbol_table.getSymbol(name); 
			if(token == null) { // 심볼 테이블에 정의되지 않은 토큰
				
				EType type = this.judgeType(name);
				
				token = new Token(name);
				if (type == null) { // identifier인 경우 (label 제외)
					this.symbol_table.addSymbol(token);
				} else {
					token.setType(type);
					switch(type) {
					case eLabel:	//label type
						this.symbol_table.addSymbol(token);
						break;
					case eConstant: // constant type
						token.setInitialValue(Integer.parseInt(name.substring(1)));
						break;
					case eAddress:	// address type (주소 참조 연산자)
						token = this.setAddress(token);
						break;
					default: // keyword type -> 타입과 이름만 설
						break;
					}

				}				
			}
			
			return token;
		}
		return null;
	}

	private Token setAddress(Token token) throws Exception {
		
		String name = this.scanner.next();
		Token retToken = new Token(name);
		retToken.setType(EType.eAddress);
		
		String next = this.scanner.next();
		
		boolean isAddsign = false;
		while(!next.equals("]")) {
			if(next.equals("+")) {
				isAddsign = true;
			}else if(next.equals("-")) {
				isAddsign = false;
			}else {
				EType type = this.judgeType(next);
				
				if(type == null || type != EType.eConstant) { // 레지스터, 상수, +/- sign 말고는 전부 exception
					throw new Exception();
				}
				int constant = Integer.parseInt(next.substring(1));
				if(!isAddsign) {
					constant *= -1;
				}
				retToken.setInitialValue(constant);
				
			}
			
			next = this.scanner.next();
		}

		return retToken;
	}
}
