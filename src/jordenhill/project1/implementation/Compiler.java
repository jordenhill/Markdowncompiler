package jordenhill.project1.implementation;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

//A compiler for the Markdown language
//Takes in text from a single source file with a mkd extension
//Lexical analyzer reads text and creates tokens and reports lexical errors
//Syntax analyzer checks sequence of tokens for syntax errors and creates a parse tree
//Parse tree is sent to semantic analyzer, which checks semantics of parse tree and reports any semantic errors
//Semantic analyzer also translates the text into html, which is then written to an html file
//After html file is created, it is opened in the user's default browser
public class Compiler {
    public static String currentToken;
    public static LexicalAnalyzer lexer;
    public static SyntaxAnalyzer parser;
    public static SemanticAnalyzer semanticAnalyzer;

    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Error: No arguments. Please enter a source file.");
            System.exit(1);
        }
        else if(args.length > 1) {
            System.out.println("Error: Please enter only one source file");
        }
        if(!args[0].substring(args[0].length() - 4).equals(".mkd")) {
            System.out.println("Error: Source file must have an \".mkd\" extension.");
            System.exit(2);
        }

        try {
            String source = new String(Files.readAllBytes(Paths.get(args[0])));
            lexer = new LexicalAnalyzer(source);
            Compiler.lexer.getNextToken();

            parser = new SyntaxAnalyzer();
            parser.markdown();


            Stack<String> buffer = new Stack<>();
            Stack<String> stack = parser.getParseTree();

            while(!stack.isEmpty()){
                buffer.push(stack.pop());
            }

            semanticAnalyzer = new SemanticAnalyzer(buffer);
            semanticAnalyzer.write(args[0].substring(0, args[0].length() - 4)+".html");
            openHTMLFileInBrowswer(args[0].substring(0, args[0].length()-4)+".html");
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + " does not exist");
        }
        catch (CompilerException e) {
            System.out.println(e.getErrorMessage());
        }
    }

    public static void openHTMLFileInBrowswer(String htmlFileStr) {
        File file= new File(htmlFileStr.trim());
        if(!file.exists()){
            System.err.println("File "+ htmlFileStr +" does not exist.");
            return;
        }
        try{
            Desktop.getDesktop().browse(file.toURI());
        }
        catch(IOException ioe){
            System.err.println("Failed to open file");
            ioe.printStackTrace();
        }
    }
}
