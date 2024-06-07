package assembler.linker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import OS.Kernal;
import assembler.codeGenerator.CodeGenerator;
import assembler.node.Program;
import assembler.symbolTable.SymbolTable;

public class Linker {
	// attribute
	private static final String PROCESS_FILE_NAME = "a.out.txt";
	
	// associate
	private SymbolTable symbol_table;
	
	public void associate(SymbolTable symbol_table) {
		this.symbol_table = symbol_table;
	}
	
	public void generateExe(CodeGenerator code_generator) throws IOException {
		// 일단 절대경로로 path 고정
		BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/binary/" + PROCESS_FILE_NAME));
		this.writeMagicNumber(writer);
		this.writeHeader(writer);
		
		for (String s : code_generator.getInstructions()) {
			writer.write(s);
			writer.newLine();
		}
		writer.close();
	}
	private void writeMagicNumber(BufferedWriter writer) throws IOException {
		writer.write(String.format("%08x", Kernal.KDY_FILE_MAGIC_NUMBER));
		writer.newLine();
		return;
	}
	private void writeHeader(BufferedWriter writer) throws IOException {
		writer.write(String.format("%08x", this.symbol_table.getSymbol("heap").getSize()));
		writer.newLine();
		writer.write(String.format("%08x", this.symbol_table.getSymbol("stack").getSize()));
		writer.newLine();
		writer.write(String.format("%08x", this.symbol_table.getSymbol("data").getSize()));
		writer.newLine();
		writer.write(String.format("%08x", this.symbol_table.getSymbol("code").getSize()));
		writer.newLine();
	}
}
