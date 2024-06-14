package assembler;

import java.io.FileNotFoundException;

import assembler.codeGenerator.CodeGenerator;
import assembler.lexicalAnalyzer.LexicalAnalyzer;
import assembler.linker.Linker;
import assembler.parser.Parser;

public class Assembler {
	private LexicalAnalyzer lexical_analyzer;
	private Parser parser;
	private CodeGenerator code_generator;
	private Linker linker;
	
	public Assembler() {
		this.lexical_analyzer = new LexicalAnalyzer(System.getProperty("user.dir")+ "/source/test.txt");
		this.parser = new Parser(this.lexical_analyzer);
		this.code_generator = new CodeGenerator(this.parser, CodeGenerator._64_BIT_OS);
		this.linker = new Linker();
		
		this.lexical_analyzer.associate(this.parser.getSymbolTable());
		this.code_generator.associate(this.parser.getSymbolTable());
		this.linker.associate(this.parser.getSymbolTable());
	}
	//method
	public void run() {
		try {
			this.parser.parse(null);
			this.code_generator.generateCode();
			this.linker.generateExe(this.code_generator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initialize() throws FileNotFoundException {
		this.lexical_analyzer.initialize();
	}

	public void finish() {
		
	}
}
